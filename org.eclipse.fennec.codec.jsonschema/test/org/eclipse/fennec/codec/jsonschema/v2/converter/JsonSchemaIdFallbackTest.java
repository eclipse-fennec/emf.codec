/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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
package org.eclipse.fennec.codec.jsonschema.v2.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Issue #213 - the derived {@code $id} fallback must be a valid JSON Schema identifier.
 * <p>
 * JSON Schema 2019-09 §8.2.1 (and 2020-12 §8.2.1) require that the URI-reference in
 * {@code $id} does not contain a non-empty fragment. The generator used to fall back to
 * {@code nsURI + "#" + eClass.getName()}, which is exactly such a fragment. The valid
 * shape is a path segment: {@code nsURI + "/" + eClass.getName()}.
 * </p>
 */
@DisplayName("Issue #213 - derived $id is a valid JSON Schema identifier")
class JsonSchemaIdFallbackTest {

	private static final String JSONSCHEMA_ANNOTATION_SOURCE = "http://fennec.eclipse.org/jsonschema";

	@Test
	@DisplayName("derived $id appends the class name as a path segment, not as a fragment")
	void derivedIdUsesPathSegment() throws IOException {
		EClass person = eClassInPackage("Person", "http://example.org/test");

		JsonNode schema = convertEClass(person);

		JsonNode id = schema.get("$id");
		assertNotNull(id, "$id should be written when the package has an nsURI");
		assertEquals("http://example.org/test/Person", id.asString());
	}

	@Test
	@DisplayName("derived $id never carries a non-empty fragment")
	void derivedIdHasNoFragment() throws IOException {
		EClass person = eClassInPackage("Person", "http://example.org/test");

		JsonNode schema = convertEClass(person);

		String id = schema.get("$id").asString();
		assertFalse(hasNonEmptyFragment(id),
				"$id must not contain a non-empty fragment (JSON Schema 2019-09 §8.2.1), was: " + id);
	}

	@Test
	@DisplayName("a trailing slash on the nsURI is not doubled")
	void trailingSlashIsNotDoubled() throws IOException {
		EClass person = eClassInPackage("Person", "http://example.org/test/");

		JsonNode schema = convertEClass(person);

		assertEquals("http://example.org/test/Person", schema.get("$id").asString());
	}

	@Test
	@DisplayName("a trailing empty fragment on the nsURI is dropped, not turned into a fragment")
	void trailingHashIsDropped() throws IOException {
		EClass person = eClassInPackage("Person", "http://example.org/test#");

		JsonNode schema = convertEClass(person);

		String id = schema.get("$id").asString();
		assertFalse(hasNonEmptyFragment(id), "$id must not contain a non-empty fragment, was: " + id);
		assertEquals("http://example.org/test/Person", id);
	}

	@Test
	@DisplayName("an explicit id annotation still wins over the derived fallback")
	void explicitAnnotationWins() throws IOException {
		EClass person = eClassInPackage("Person", "http://example.org/test");
		EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
		annotation.setSource(JSONSCHEMA_ANNOTATION_SOURCE);
		annotation.getDetails().put("id", "https://schemas.example.com/person.json");
		person.getEAnnotations().add(annotation);

		JsonNode schema = convertEClass(person);

		assertEquals("https://schemas.example.com/person.json", schema.get("$id").asString());
	}

	@Test
	@DisplayName("no $id is written when the class has no package and no annotation")
	void noPackageNoId() throws IOException {
		EClass detached = EcoreFactory.eINSTANCE.createEClass();
		detached.setName("Loose");

		JsonNode schema = convertEClass(detached);

		assertFalse(schema.has("$id"), "$id must be omitted when no valid identifier can be derived");
	}

	/**
	 * A URI-reference carries a non-empty fragment when something follows the first '#'.
	 */
	private static boolean hasNonEmptyFragment(String uri) {
		int hash = uri.indexOf('#');
		return hash >= 0 && hash < uri.length() - 1;
	}

	private static EClass eClassInPackage(String className, String nsURI) {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("test");
		ePackage.setNsPrefix("test");
		ePackage.setNsURI(nsURI);

		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(className);

		EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		eClass.getEStructuralFeatures().add(name);

		ePackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private static JsonNode convertEClass(EClass eClass) throws IOException {
		EClassToJsonSchemaConverter converter = new EClassToJsonSchemaConverter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		converter.convert(eClass, baos, false, Map.of());
		return JsonMapper.builder().build().readTree(baos.toByteArray());
	}
}
