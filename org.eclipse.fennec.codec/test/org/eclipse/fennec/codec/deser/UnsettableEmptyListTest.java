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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * An unsettable list with serializeEmpty: an empty array that was read is written again, an absent
 * member stays absent (spec 11 #277, issue #319).
 */
@DisplayName("Unsettable lists: empty versus absent")
class UnsettableEmptyListTest {

	private EPackage pkg;
	private EClass holder;
	private MetadataWhiteboard metadata;

	@BeforeEach
	void setUp() {
		EcoreFactory f = EcoreFactory.eINSTANCE;
		pkg = f.createEPackage();
		pkg.setName("unsettablelist");
		pkg.setNsPrefix("unsettablelist");
		pkg.setNsURI("http://test.org/unsettablelist");
		EClass item = f.createEClass();
		item.setName("Item");
		EAttribute name = f.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		item.getEStructuralFeatures().add(name);
		holder = f.createEClass();
		holder.setName("Holder");
		EAttribute tags = f.createEAttribute();
		tags.setName("tags");
		tags.setEType(EcorePackage.Literals.ESTRING);
		EReference items = f.createEReference();
		items.setName("items");
		items.setEType(item);
		items.setContainment(true);
		for (EStructuralFeature feature : List.of(tags, items)) {
			feature.setUpperBound(-1);
			feature.setUnsettable(true);
			EcoreUtil.setAnnotation(feature, AnnotationSources.CODEC, "serializeEmpty", "true");
			holder.getEStructuralFeatures().add(feature);
		}
		pkg.getEClassifiers().addAll(List.of(item, holder));
		for (var c : pkg.getEClassifiers()) {
			EcoreUtil.setAnnotation(c, AnnotationSources.CODEC, "typeStrategy", "NONE");
		}
		EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
		metadata = MetadataServiceFactory.create();
		metadata.registerPackage(pkg);
	}

	@AfterEach
	void tearDown() {
		EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
	}

	@ParameterizedTest
	@ValueSource(strings = { "{\"tags\":[],\"items\":[]}", "{}", "{\"tags\":[\"a\"]}", "{\"items\":[]}" })
	@DisplayName("is written as it was read")
	void roundTrip(String json) throws IOException {
		CodecResource resource = new CodecResource(URI.createURI("test://u.json"), metadata,
				ConfigurationResolver.defaults(), null);
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, holder);
		resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());

		assertEquals(json, out.toString(StandardCharsets.UTF_8));
	}
}
