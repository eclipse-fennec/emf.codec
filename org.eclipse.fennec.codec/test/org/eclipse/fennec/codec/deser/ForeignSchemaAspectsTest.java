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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

/**
 * Codec aspects for payloads of foreign schemas (spec 21 §6.2, §6.3): a catch-all map for
 * undeclared members (issue #278) and the subclass inferred from the members present (issue #279).
 */
@DisplayName("Foreign schema aspects (#278, #279)")
class ForeignSchemaAspectsTest {

	private static final String NS_URI = "https://example.org/foreign/1.0.0";

	private final EcoreFactory factory = EcoreFactory.eINSTANCE;
	private final JsonMapper mapper = JsonMapper.builder().build();
	private EPackage ePackage;
	private MetadataWhiteboard metadataService;

	@BeforeEach
	void setUp() {
		ePackage = factory.createEPackage();
		ePackage.setName("foreign");
		ePackage.setNsPrefix("foreign");
		ePackage.setNsURI(NS_URI);

		EClass item = eClass("Item");
		attribute(item, "name", EcorePackage.Literals.ESTRING, 0);
		mapFeature(item, "extra", EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY);
		codec(item, "additionalPropertiesFeature", "extra");

		EClass itemEntry = eClass("ItemEntry");
		itemEntry.setInstanceClassName("java.util.Map$Entry");
		attribute(itemEntry, "key", EcorePackage.Literals.ESTRING, 0);
		EReference itemValue = reference(itemEntry, "value", item, true);
		itemValue.setLowerBound(0);
		EClass bag = eClass("Bag");
		attribute(bag, "name", EcorePackage.Literals.ESTRING, 0);
		mapFeature(bag, "items", itemEntry);
		codec(bag, "additionalPropertiesFeature", "items");

		EClass shape = eClass("Shape");
		shape.setAbstract(true);
		codec(shape, "typeInference", "STRUCTURAL");
		attribute(shape, "label", EcorePackage.Literals.ESTRING, 0);
		EClass circle = eClass("Circle");
		circle.getESuperTypes().add(shape);
		attribute(circle, "radius", EcorePackage.Literals.EDOUBLE_OBJECT, 1);
		EClass rect = eClass("Rect");
		rect.getESuperTypes().add(shape);
		attribute(rect, "width", EcorePackage.Literals.EDOUBLE_OBJECT, 1);
		attribute(rect, "height", EcorePackage.Literals.EDOUBLE_OBJECT, 1);
		EClass square = eClass("Square");
		square.getESuperTypes().add(shape);
		attribute(square, "width", EcorePackage.Literals.EDOUBLE_OBJECT, 1);
		EClass holder = eClass("Holder");
		reference(holder, "shape", shape, true);

		for (EClassifier classifier : ePackage.getEClassifiers()) {
			codec(classifier, "typeStrategy", "NONE");
		}
		new ResourceImpl(URI.createURI(NS_URI)).getContents().add(ePackage);
		metadataService = MetadataServiceFactory.create();
		metadataService.registerPackage(ePackage);
	}

	@Nested
	@DisplayName("catch-all map (#278)")
	class CatchAllMap {

		@Test
		@DisplayName("undeclared members go into the map as raw JSON and are written again")
		void rawJsonValues() throws IOException {
			String payload = """
					{ "name": "a", "color": "red", "size": { "w": 1, "h": [2, 3] }, "n": 12.5, "flag": null }
					""";
			CodecResource resource = load("Item", payload);
			EObject item = resource.getContents().get(0);

			EMap<String, String> extra = map(item, "extra");
			assertEquals("\"red\"", extra.get("color"));
			assertEquals("null", extra.get("flag"));
			assertEquals(4, extra.size());
			assertTrue(resource.getWarnings().isEmpty(), resource.getWarnings().toString());
			assertEquals(mapper.readTree(payload), mapper.readTree(save(resource)));
		}

		@Test
		@DisplayName("the map feature itself is not written as a key")
		void mapIsNotAKey() throws IOException {
			CodecResource resource = load("Item", "{ \"name\": \"a\" }");
			assertFalse(save(resource).contains("extra"));
		}

		@Test
		@DisplayName("a map of a class reads every undeclared member as an object of that class")
		void objectValues() throws IOException {
			String payload = """
					{ "name": "b", "x": { "name": "i1", "k": "v" }, "y": { "name": "i2" } }
					""";
			CodecResource resource = load("Bag", payload);
			EObject bag = resource.getContents().get(0);

			EMap<String, EObject> items = map(bag, "items");
			assertEquals(2, items.size());
			EObject x = items.get("x");
			assertEquals("i1", x.eGet(x.eClass().getEStructuralFeature("name")));
			assertEquals("\"v\"", map(x, "extra").get("k"));
			assertEquals(mapper.readTree(payload), mapper.readTree(save(resource)));
		}
	}

	@Nested
	@DisplayName("structural type inference (#279)")
	class TypeInference {

		@Test
		@DisplayName("the subclass whose required members are present is chosen")
		void infersSubclass() throws IOException {
			assertEquals("Circle", shapeOf("{ \"shape\": { \"radius\": 2 } }").eClass().getName());
			assertEquals("Rect", shapeOf("{ \"shape\": { \"width\": 1, \"height\": 2 } }").eClass().getName());
		}

		@Test
		@DisplayName("with several candidates the one declaring most present members wins")
		void mostSpecificWins() throws IOException {
			// Square and Rect both accept width; only Rect also declares height
			assertEquals("Rect", shapeOf("{ \"shape\": { \"width\": 1, \"height\": 2, \"label\": \"r\" } }")
					.eClass().getName());
			// Rect needs height, so only Square fits
			assertEquals("Square", shapeOf("{ \"shape\": { \"width\": 1 } }").eClass().getName());
		}

		@Test
		@DisplayName("an inferred object is written without type information")
		void roundTrip() throws IOException {
			String payload = "{ \"shape\": { \"width\": 1.0, \"height\": 2.0 } }";
			assertEquals(mapper.readTree(payload), mapper.readTree(save(load("Holder", payload))));
		}

		@Test
		@DisplayName("no matching subclass is an error")
		void noCandidate() throws IOException {
			CodecResource resource = load("Holder", "{ \"shape\": { \"label\": \"?\", \"depth\": 3 } }");
			assertFalse(resource.getErrors().isEmpty());
		}

		private EObject shapeOf(String payload) throws IOException {
			CodecResource resource = load("Holder", payload);
			assertTrue(resource.getErrors().isEmpty(), resource.getErrors().toString());
			EObject holder = resource.getContents().get(0);
			return (EObject) holder.eGet(holder.eClass().getEStructuralFeature("shape"));
		}
	}

	private CodecResource load(String rootClass, String payload) throws IOException {
		CodecResource resource = new CodecResource(URI.createURI("test://payload.json"), metadataService,
				ConfigurationResolver.defaults(), null);
		resource.load(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)),
				Map.of(CodecResource.CODEC_ROOT_TYPE, ePackage.getEClassifier(rootClass)));
		return resource;
	}

	private String save(CodecResource resource) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, null);
		return out.toString(StandardCharsets.UTF_8);
	}

	@SuppressWarnings("unchecked")
	private static <V> EMap<String, V> map(EObject eObject, String feature) {
		return (EMap<String, V>) eObject.eGet(eObject.eClass().getEStructuralFeature(feature));
	}

	private EClass eClass(String name) {
		EClass eClass = factory.createEClass();
		eClass.setName(name);
		ePackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private void attribute(EClass owner, String name, EClassifier type, int lowerBound) {
		EAttribute attribute = factory.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		attribute.setLowerBound(lowerBound);
		owner.getEStructuralFeatures().add(attribute);
	}

	private EReference reference(EClass owner, String name, EClass type, boolean containment) {
		EReference reference = factory.createEReference();
		reference.setName(name);
		reference.setEType(type);
		reference.setContainment(containment);
		owner.getEStructuralFeatures().add(reference);
		return reference;
	}

	private void mapFeature(EClass owner, String name, EClass entryClass) {
		EReference map = reference(owner, name, entryClass, true);
		map.setUpperBound(-1);
	}

	private void codec(EModelElement element, String key, String value) {
		EAnnotation annotation = element.getEAnnotation(AnnotationSources.CODEC);
		if (annotation == null) {
			annotation = factory.createEAnnotation();
			annotation.setSource(AnnotationSources.CODEC);
			element.getEAnnotations().add(annotation);
		}
		annotation.getDetails().put(key, value);
	}
}
