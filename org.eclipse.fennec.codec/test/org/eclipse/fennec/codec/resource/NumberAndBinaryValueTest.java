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
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Numbers beyond {@code long}/{@code double} and binary values, read and written exactly.
 * <ul>
 * <li>Issue #258: a property read before the object exists is buffered and replayed; the buffer
 * kept floats as {@code double} and integers as {@code long}.</li>
 * <li>Issue #259: {@code BigDecimal}, {@code BigInteger}, {@code Short} and {@code Byte} were
 * written as JSON strings.</li>
 * <li>Issue #260: {@code base64Binary} and {@code hexBinary} values were read from their lexical
 * form but written as arrays of numbers.</li>
 * </ul>
 * Every load runs twice: with a type key, where the object exists before its properties, and
 * without one, where every property goes through the buffer.
 */
@DisplayName("Number and binary values")
class NumberAndBinaryValueTest {

	private static final String NS_URI = "http://test.example.org/numbers/1.0";
	private static final JsonMapper EXACT = JsonMapper.builder()
			.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
			.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
			.build();

	private static final String DECIMAL = "0.12345678901234567890123";
	private static final String BIG_INTEGER = "123456789012345678901";

	private EPackage testPackage;
	private MetadataWhiteboard metadataService;
	private EClass holder;
	private EAttribute bigDecimal;
	private EAttribute bigInteger;
	private EAttribute xmlDecimal;
	private EAttribute xmlNonNegativeInteger;
	private EAttribute bigDecimals;
	private EAttribute shortValue;
	private EAttribute byteValue;
	private EAttribute doubleValue;
	private EAttribute longValue;
	private EAttribute base64;
	private EAttribute derivedBase64;
	private EAttribute hex;
	private EAttribute byteArray;
	private EAttribute bigDecimalArray;
	private EAttribute bigIntegerArray;

	@BeforeEach
	void setUp() {
		testPackage = EcoreFactory.eINSTANCE.createEPackage();
		testPackage.setName("numbers");
		testPackage.setNsURI(NS_URI);
		testPackage.setNsPrefix("num");
		holder = EcoreFactory.eINSTANCE.createEClass();
		holder.setName("Holder");
		testPackage.getEClassifiers().add(holder);

		// an own data type derived from base64Binary, the way the XSD importer creates them
		EDataType base64Type = EcoreFactory.eINSTANCE.createEDataType();
		base64Type.setName("Base64Datatype");
		base64Type.setInstanceTypeName("byte[]");
		ExtendedMetaData.INSTANCE.setName(base64Type, "Base64Datatype");
		ExtendedMetaData.INSTANCE.setBaseType(base64Type, XMLTypePackage.Literals.BASE64_BINARY);
		testPackage.getEClassifiers().add(base64Type);

		bigDecimal = attribute("bigDecimal", EcorePackage.Literals.EBIG_DECIMAL, false);
		bigInteger = attribute("bigInteger", EcorePackage.Literals.EBIG_INTEGER, false);
		xmlDecimal = attribute("xmlDecimal", XMLTypePackage.Literals.DECIMAL, false);
		xmlNonNegativeInteger = attribute("xmlNonNegativeInteger", XMLTypePackage.Literals.NON_NEGATIVE_INTEGER, false);
		bigDecimals = attribute("bigDecimals", EcorePackage.Literals.EBIG_DECIMAL, true);
		shortValue = attribute("shortValue", EcorePackage.Literals.ESHORT_OBJECT, false);
		byteValue = attribute("byteValue", EcorePackage.Literals.EBYTE_OBJECT, false);
		doubleValue = attribute("doubleValue", EcorePackage.Literals.EDOUBLE_OBJECT, false);
		longValue = attribute("longValue", EcorePackage.Literals.ELONG_OBJECT, false);
		base64 = attribute("base64", XMLTypePackage.Literals.BASE64_BINARY, false);
		derivedBase64 = attribute("derivedBase64", base64Type, false);
		hex = attribute("hex", XMLTypePackage.Literals.HEX_BINARY, false);
		byteArray = attribute("byteArray", EcorePackage.Literals.EBYTE_ARRAY, false);
		bigDecimalArray = attribute("bigDecimalArray", arrayType("BigDecimalArray", BigDecimal[].class), false);
		bigIntegerArray = attribute("bigIntegerArray", arrayType("BigIntegerArray", BigInteger[].class), false);

		new ResourceImpl(URI.createURI(NS_URI)).getContents().add(testPackage);
		EPackage.Registry.INSTANCE.put(NS_URI, testPackage);
		metadataService = MetadataServiceFactory.create();
		metadataService.registerPackage(testPackage);
	}

	@AfterEach
	void tearDown() {
		EPackage.Registry.INSTANCE.remove(NS_URI);
	}

	private EDataType arrayType(String name, Class<?> instanceClass) {
		EDataType type = EcoreFactory.eINSTANCE.createEDataType();
		type.setName(name);
		type.setInstanceClass(instanceClass);
		testPackage.getEClassifiers().add(type);
		return type;
	}

	private EAttribute attribute(String name, EClassifier type, boolean many) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		if (many) {
			attribute.setUpperBound(-1);
		}
		holder.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private CodecResource resource(boolean typeKey) {
		ConfigurationResolver resolver = typeKey ? ConfigurationResolver.defaults()
				: ConfigurationResolver.builder().typeInclude(false).useId(false).build();
		return new CodecResource(URI.createURI("test://numbers.json"), metadataService, resolver, null);
	}

	private EObject load(String json, boolean typeKey) throws IOException {
		CodecResource resource = resource(typeKey);
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, holder);
		resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
		assertEquals(List.of(), messages(resource.getErrors()), "errors");
		assertEquals(List.of(), messages(resource.getWarnings()), "warnings");
		return resource.getContents().get(0);
	}

	private JsonNode save(EObject object, boolean typeKey) throws IOException {
		CodecResource resource = resource(typeKey);
		resource.getContents().add(object);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());
		return EXACT.readTree(out.toByteArray());
	}

	private static List<String> messages(List<Resource.Diagnostic> diagnostics) {
		return diagnostics.stream().map(Resource.Diagnostic::getMessage).toList();
	}

	/** Issue #258: the buffer must not cut a decimal to a double or an integer to a long. */
	@ParameterizedTest(name = "type key: {0}")
	@ValueSource(booleans = { true, false })
	void bigNumbersAreReadExactly(boolean typeKey) throws IOException {
		String type = typeKey ? "\"_type\":\"" + NS_URI + "#//Holder\"," : "";
		EObject loaded = load("{" + type + "\"bigDecimal\":" + DECIMAL + ",\"bigInteger\":" + BIG_INTEGER
				+ ",\"xmlDecimal\":" + DECIMAL + ",\"xmlNonNegativeInteger\":" + BIG_INTEGER
				+ ",\"bigDecimals\":[" + DECIMAL + ",1.50]" + ",\"doubleValue\":0.1,\"longValue\":9007199254740993}",
				typeKey);
		assertEquals(new BigDecimal(DECIMAL), loaded.eGet(bigDecimal));
		assertEquals(new BigInteger(BIG_INTEGER), loaded.eGet(bigInteger));
		assertEquals(new BigDecimal(DECIMAL), loaded.eGet(xmlDecimal));
		assertEquals(new BigInteger(BIG_INTEGER), loaded.eGet(xmlNonNegativeInteger));
		assertEquals(List.of(new BigDecimal(DECIMAL), new BigDecimal("1.50")), loaded.eGet(bigDecimals));
		assertEquals(0.1d, loaded.eGet(doubleValue));
		// beyond 2^53: a detour over double would give ...992
		assertEquals(9007199254740993L, loaded.eGet(longValue));
	}

	/** Issue #259: big and small number types are JSON numbers, not strings. */
	@Test
	void numbersAreWrittenAsNumbers() throws IOException {
		EObject object = EcoreUtil.create(holder);
		object.eSet(bigDecimal, new BigDecimal(DECIMAL));
		object.eSet(bigInteger, new BigInteger(BIG_INTEGER));
		object.eSet(xmlDecimal, new BigDecimal("0.85"));
		object.eSet(xmlNonNegativeInteger, new BigInteger("27017"));
		object.eSet(shortValue, (short) 7);
		object.eSet(byteValue, (byte) 3);

		JsonNode json = save(object, false);
		assertEquals(EXACT.readTree("{\"bigDecimal\":" + DECIMAL + ",\"bigInteger\":" + BIG_INTEGER
				+ ",\"xmlDecimal\":0.85,\"xmlNonNegativeInteger\":27017,\"shortValue\":7,\"byteValue\":3}"), json);
	}

	@ParameterizedTest(name = "type key: {0}")
	@ValueSource(booleans = { true, false })
	void bigNumbersRoundTrip(boolean typeKey) throws IOException {
		EObject object = EcoreUtil.create(holder);
		object.eSet(bigDecimal, new BigDecimal(DECIMAL));
		object.eSet(xmlNonNegativeInteger, new BigInteger(BIG_INTEGER));
		EObject loaded = load(EXACT.writeValueAsString(save(object, typeKey)), typeKey);
		assertEquals(new BigDecimal(DECIMAL), loaded.eGet(bigDecimal));
		assertEquals(new BigInteger(BIG_INTEGER), loaded.eGet(xmlNonNegativeInteger));
	}

	/** Issue #259: object arrays of big numbers are written as numbers and read back exactly. */
	@ParameterizedTest(name = "type key: {0}")
	@ValueSource(booleans = { true, false })
	void bigNumberArraysRoundTrip(boolean typeKey) throws IOException {
		EObject object = EcoreUtil.create(holder);
		object.eSet(bigDecimalArray, new BigDecimal[] { new BigDecimal(DECIMAL), new BigDecimal("1.50") });
		object.eSet(bigIntegerArray, new BigInteger[] { new BigInteger(BIG_INTEGER), BigInteger.TWO });

		JsonNode json = save(object, typeKey);
		assertEquals(EXACT.readTree("[" + DECIMAL + ",1.50]"), json.get("bigDecimalArray"));
		assertEquals(EXACT.readTree("[" + BIG_INTEGER + ",2]"), json.get("bigIntegerArray"));

		EObject loaded = load(EXACT.writeValueAsString(json), typeKey);
		assertArrayEquals(new BigDecimal[] { new BigDecimal(DECIMAL), new BigDecimal("1.50") },
				(BigDecimal[]) loaded.eGet(bigDecimalArray));
		assertArrayEquals(new BigInteger[] { new BigInteger(BIG_INTEGER), BigInteger.TWO },
				(BigInteger[]) loaded.eGet(bigIntegerArray));
	}

	/** Issue #260: base64Binary and hexBinary are written in their lexical form, as they are read. */
	@ParameterizedTest(name = "type key: {0}")
	@ValueSource(booleans = { true, false })
	void binaryIsWrittenInItsLexicalForm(boolean typeKey) throws IOException {
		byte[] hello = "Hello OSCAL".getBytes(StandardCharsets.UTF_8);
		EObject object = EcoreUtil.create(holder);
		object.eSet(base64, hello);
		object.eSet(derivedBase64, hello);
		object.eSet(hex, hello);
		object.eSet(byteArray, new byte[] { 1, 2, 3 });

		JsonNode json = save(object, typeKey);
		assertEquals("SGVsbG8gT1NDQUw=", json.get("base64").asString());
		assertEquals("SGVsbG8gT1NDQUw=", json.get("derivedBase64").asString());
		assertEquals("48656C6C6F204F5343414C", json.get("hex").asString());
		// EByteArray has no lexical form of its own and stays a number array (spec 11-feature.md)
		assertEquals(EXACT.readTree("[1,2,3]"), json.get("byteArray"));

		EObject loaded = load(EXACT.writeValueAsString(json), typeKey);
		assertArrayEquals(hello, (byte[]) loaded.eGet(base64));
		assertArrayEquals(hello, (byte[]) loaded.eGet(derivedBase64));
		assertArrayEquals(hello, (byte[]) loaded.eGet(hex));
		assertArrayEquals(new byte[] { 1, 2, 3 }, (byte[]) loaded.eGet(byteArray));
	}
}
