/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.codec.oscal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import gov.nist.csrc.ns.oscal.Catalog;
import gov.nist.csrc.ns.oscal.Control;
import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.Property;

/**
 * Example of {@code docs/codec-oscal-guide.md} (issue #257): read the Grundschutz++ catalog and
 * list every control with its security level ({@code sec_level}).
 */
@DisplayName("Example: Grundschutz++ controls with sec_level")
@EnabledIf("org.eclipse.fennec.codec.oscal.BsiTestData#present")
class GrundschutzExample {

	@Test
	void listControlsWithSecurityLevel() throws IOException {
		Path file = BsiTestData.file("Grundschutz++-resolved_catalog.json");

		// plain Java: the factory brings its own metadata service with the OSCAL model
		Resource resource = new OscalResourceFactoryImpl().createResource(URI.createFileURI(file.toString()));
		try (InputStream in = Files.newInputStream(file)) {
			resource.load(in, Map.of());
		}
		Catalog catalog = ((DocumentRoot) resource.getContents().get(0)).getCatalog();

		List<String> lines = new ArrayList<>();
		for (TreeIterator<EObject> it = catalog.eAllContents(); it.hasNext();) {
			if (it.next() instanceof Control control) {
				String level = control.getProp().stream().filter(p -> "sec_level".equals(p.getName()))
						.map(Property::getValue).findFirst().orElse("-");
				lines.add(control.getId() + "  " + level + "  " + control.getTitle());
			}
		}
		lines.stream().limit(5).forEach(System.out::println);
		assertTrue(lines.size() > 500, lines.size() + " controls");
		assertTrue(lines.contains("GC.1.1  normal-SdT  Errichtung und Aufrechterhaltung eines ISMS"), lines.get(0));
	}
}
