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
package org.eclipse.fennec.codec.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * YAML anchors and aliases (issue #297): an alias reads as a copy of the anchored node - an object,
 * a sequence or a scalar - and the expansion is bounded against "billion laughs".
 */
@DisplayName("YAML anchors and aliases")
class YamlAliasTest {

	private EPackage testPackage;
	private MetadataWhiteboard metadataService;
	private EClass docClass;
	private EAttribute name;
	private EAttribute tags;
	private EReference child;
	private EReference children;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("alias");
		testPackage.setNsPrefix("alias");
		testPackage.setNsURI("http://test.org/alias/YAML/1.0");
		docClass = EcoreFactory.eINSTANCE.createEClass();
		docClass.setName("Doc");
		testPackage.getEClassifiers().add(docClass);
		name = attribute("name", 1);
		tags = attribute("tags", -1);
		child = reference("child", 1);
		children = reference("children", -1);
		EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);
		metadataService = MetadataServiceFactory.create();
		metadataService.registerPackage(testPackage);
	}

	@AfterEach
	void tearDown() {
		EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
	}

	@Test
	@DisplayName("an alias of an object reads as a copy of it")
	void objectAlias() throws IOException {
		EObject doc = load("""
				name: root
				child: &shared
				  name: shared
				  tags: [a, b]
				children:
				  - *shared
				  - name: own
				""");

		EObject original = (EObject) doc.eGet(child);
		List<?> list = (List<?>) doc.eGet(children);
		EObject copy = (EObject) list.get(0);
		assertEquals("shared", copy.eGet(name));
		assertEquals(List.of("a", "b"), copy.eGet(tags));
		assertNotSame(original, copy);
		assertEquals("own", ((EObject) list.get(1)).eGet(name));
	}

	@Test
	@DisplayName("an alias of a scalar reads as its value")
	void scalarAlias() throws IOException {
		EObject doc = load("""
				name: &n root
				child:
				  name: *n
				""");

		assertEquals("root", ((EObject) doc.eGet(child)).eGet(name));
	}

	@Test
	@DisplayName("an alias of a sequence reads as its items")
	void sequenceAlias() throws IOException {
		EObject doc = load("""
				name: root
				tags: &t [x, y]
				child:
				  name: c
				  tags: *t
				""");

		assertEquals(List.of("x", "y"), ((EObject) doc.eGet(child)).eGet(tags));
	}

	@Test
	@DisplayName("aliases inside an anchored node and nested anchors resolve")
	void nested() throws IOException {
		EObject doc = load("""
				name: &n root
				child: &outer
				  name: *n
				  child: &inner
				    name: inner
				children:
				  - *outer
				  - *inner
				""");

		List<?> list = (List<?>) doc.eGet(children);
		EObject outer = (EObject) list.get(0);
		assertEquals("root", outer.eGet(name));
		assertEquals("inner", ((EObject) outer.eGet(child)).eGet(name));
		assertEquals("inner", ((EObject) list.get(1)).eGet(name));
	}

	@Test
	@DisplayName("the property after an alias is read in its own place")
	void contextAfterAlias() throws IOException {
		EObject doc = load("""
				child: &c
				  name: c
				children:
				  - *c
				name: last
				""");

		assertEquals("last", doc.eGet(name));
		assertEquals(1, ((List<?>) doc.eGet(children)).size());
	}

	@Test
	@DisplayName("an alias without anchor is refused")
	void undefinedAlias() {
		assertThrows(IOException.class, () -> load("""
				name: root
				child: *missing
				"""));
	}

	@Test
	@DisplayName("a billion-laughs document is refused by default")
	void billionLaughs() {
		StringBuilder yaml = new StringBuilder("name: root\na0: &a0 [x, x, x, x, x, x, x, x, x, x]\n");
		for (int i = 1; i < 9; i++) {
			yaml.append('a').append(i).append(": &a").append(i).append(" [");
			for (int j = 0; j < 10; j++) {
				yaml.append(j == 0 ? "" : ", ").append("*a").append(i - 1);
			}
			yaml.append("]\n");
		}
		yaml.append("tags: *a8\n");

		IOException e = assertThrows(IOException.class, () -> load(yaml.toString()));
		assertTrue(String.valueOf(e.getMessage()).contains(YamlFormatProvider.OPTION_MAX_ALIAS_TOKENS)
				|| String.valueOf(e.getCause()).contains(YamlFormatProvider.OPTION_MAX_ALIAS_TOKENS), e.toString());
	}

	@Test
	@DisplayName("the alias budget is an option")
	void budgetOption() {
		String yaml = """
				child: &c
				  name: c
				  tags: [a, b, c, d]
				children:
				  - *c
				  - *c
				name: root
				""";

		assertThrows(IOException.class, () -> load(yaml, Map.of(YamlFormatProvider.OPTION_MAX_ALIAS_TOKENS, 10)));
	}

	private EObject load(String yaml) throws IOException {
		return load(yaml, Map.of());
	}

	private EObject load(String yaml, Map<String, Object> extra) throws IOException {
		CodecResource resource = new CodecResource(URI.createURI("test://alias.yaml"), metadataService,
				ConfigurationResolver.defaults(), null, null, new YamlFormatProvider());
		Map<String, Object> options = new HashMap<>(extra);
		options.put(CodecResource.CODEC_ROOT_TYPE, docClass);
		resource.load(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), options);
		return resource.getContents().get(0);
	}

	private EAttribute attribute(String featureName, int upper) {
		EAttribute a = EcoreFactory.eINSTANCE.createEAttribute();
		a.setName(featureName);
		a.setEType(EcorePackage.Literals.ESTRING);
		a.setUpperBound(upper);
		docClass.getEStructuralFeatures().add(a);
		return a;
	}

	private EReference reference(String featureName, int upper) {
		EReference r = EcoreFactory.eINSTANCE.createEReference();
		r.setName(featureName);
		r.setEType(docClass);
		r.setContainment(true);
		r.setUpperBound(upper);
		docClass.getEStructuralFeatures().add(r);
		return r;
	}
}
