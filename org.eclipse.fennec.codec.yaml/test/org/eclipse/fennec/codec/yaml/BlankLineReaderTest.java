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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Lines of only spaces and tabs are emptied before parsing (issue #298): snakeyaml-engine refuses
 * a block scalar whose only line is blank but deeper indented than the line after it, which YAML
 * 1.2.2 §8.1.1.1 allows (the OpenAI description has one).
 */
@DisplayName("Blank lines before parsing")
class BlankLineReaderTest {

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
			"a\\n   \\nb | a\\n\\nb",
			"a\\r\\n \\t \\r\\nb | a\\r\\n\\r\\nb",
			"'   ' | ''",
			"'  x \\n  y' | '  x \\n  y'",
			"'a\\n    ' | 'a\\n'",
			"\\n\\n | '\\n\\n'" })
	@DisplayName("whitespace-only lines become empty, everything else is kept")
	void filter(String in, String out) throws IOException {
		assertEquals(unescape(out), read(new BlankLineReader(new StringReader(unescape(in)))));
	}

	@Test
	@DisplayName("reads in small chunks the same")
	void chunks() throws IOException {
		String text = "key: |+\n            \n  next: 1\n   \t \nend\n";
		BlankLineReader reader = new BlankLineReader(new StringReader(text));
		StringBuilder out = new StringBuilder();
		char[] buf = new char[3];
		int n;
		while ((n = reader.read(buf, 0, buf.length)) >= 0) {
			out.append(buf, 0, n);
		}
		assertEquals("key: |+\n\n  next: 1\n\nend\n", out.toString());
	}

	@Test
	@DisplayName("a keep block scalar with only a blank line reads as a line break")
	void openAiCase() throws IOException {
		EPackage pkg = EcoreFactory.eINSTANCE.createEPackage();
		pkg.setName("blank");
		pkg.setNsPrefix("blank");
		pkg.setNsURI("http://test.org/blank/YAML/1.0");
		EClass doc = EcoreFactory.eINSTANCE.createEClass();
		doc.setName("Doc");
		pkg.getEClassifiers().add(doc);
		EAttribute example = EcoreFactory.eINSTANCE.createEAttribute();
		example.setName("example");
		example.setEType(EcorePackage.Literals.ESTRING);
		doc.getEStructuralFeatures().add(example);
		EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		doc.getEStructuralFeatures().add(name);
		EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
		try {
			MetadataWhiteboard metadata = MetadataServiceFactory.create();
			metadata.registerPackage(pkg);
			CodecResource resource = new CodecResource(URI.createURI("test://blank.yaml"), metadata,
					ConfigurationResolver.defaults(), null, null, new YamlFormatProvider());
			Map<String, Object> options = new HashMap<>();
			options.put(CodecResource.CODEC_ROOT_TYPE, doc);
			String yaml = "example: |+\n            \nname: after\n";
			resource.load(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), options);

			EObject loaded = resource.getContents().get(0);
			assertEquals("\n", loaded.eGet(example));
			assertEquals("after", loaded.eGet(name));
		} finally {
			EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
		}
	}

	private static String read(Reader reader) throws IOException {
		StringWriter out = new StringWriter();
		reader.transferTo(out);
		return out.toString();
	}

	private static String unescape(String s) {
		return s.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t");
	}
}
