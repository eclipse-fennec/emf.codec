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
package org.eclipse.fennec.codec.rest.common.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.constants.RootOptions;
import org.eclipse.fennec.codec.rest.annotations.json.CodecConfig;
import org.eclipse.fennec.codec.rest.annotations.json.RootElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CodecAnnotationConverter}.
 */
@DisplayName("CodecAnnotationConverter")
class CodecAnnotationConverterTest {

	private CodecAnnotationConverter converter;

	@BeforeEach
	void setUp() {
		converter = new CodecAnnotationConverter();
	}

	@Nested
	@DisplayName("canHandle")
	class CanHandle {

		@Test
		@DisplayName("handles CodecConfig annotation")
		void handlesCodecConfig() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			assertTrue(converter.canHandle(config, true));
			assertTrue(converter.canHandle(config, false));
		}

		@Test
		@DisplayName("handles RootElement annotation")
		void handlesRootElement() {
			RootElement root = AnnotationHelper.rootElement("", "");
			assertTrue(converter.canHandle(root, true));
			assertTrue(converter.canHandle(root, false));
		}

		@Test
		@DisplayName("does not handle unknown annotations")
		void doesNotHandleUnknown() {
			Annotation unknown = () -> Override.class;
			assertFalse(converter.canHandle(unknown, true));
		}
	}

	@Nested
	@DisplayName("RootElement conversion")
	class RootElementConversion {

		/*
		 * Asserted through RootOptions rather than a bare map lookup: what the annotation owes
		 * the codec is a root option the reading side finds, not one particular spelling of the
		 * key (issue #208).
		 */
		@Test
		@DisplayName("puts rootType when not blank")
		void putsRootType() throws IOException {
			RootElement root = AnnotationHelper.rootElement("http://test/1.0#//Person", "");
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(root, false, options);
			assertEquals("http://test/1.0#//Person", RootOptions.rootType(options));
		}

		@Test
		@DisplayName("puts rootSchema when not blank")
		void putsRootSchema() throws IOException {
			RootElement root = AnnotationHelper.rootElement("", "http://test/1.0");
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(root, false, options);
			assertEquals("http://test/1.0", RootOptions.rootSchema(options));
		}

		@Test
		@DisplayName("puts both rootType and rootSchema")
		void putsBoth() throws IOException {
			RootElement root = AnnotationHelper.rootElement("http://test/1.0#//Person", "http://test/1.0");
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(root, false, options);
			assertEquals("http://test/1.0#//Person", RootOptions.rootType(options));
			assertEquals("http://test/1.0", RootOptions.rootSchema(options));
		}

		@Test
		@DisplayName("skips blank rootType and rootSchema")
		void skipsBlank() {
			RootElement root = AnnotationHelper.rootElement("", "");
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(root, false, options);
			assertFalse(RootOptions.hasRootType(options));
			assertFalse(RootOptions.hasRootSchema(options));
		}
	}

	@Nested
	@DisplayName("CodecConfig conversion - Feature Configuration")
	class FeatureConfiguration {

		@Test
		@DisplayName("puts dateFormat when not blank")
		void putsDateFormat() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.dateFormat("yyyy-MM-dd"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("yyyy-MM-dd", options.get(CodecOptions.CODEC_DATE_FORMAT));
		}

		@Test
		@DisplayName("skips dateFormat when blank")
		void skipsDateFormat() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_DATE_FORMAT));
		}

		@Test
		@DisplayName("puts serializeDefaultValues")
		void putsSerializeDefaults() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.serializeDefaultValues(true));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals(true, options.get(CodecOptions.CODEC_SERIALIZE_DEFAULTS));
		}

		@Test
		@DisplayName("puts serializeEmptyValues")
		void putsSerializeEmpty() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.serializeEmptyValues(true));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals(true, options.get(CodecOptions.CODEC_SERIALIZE_EMPTY));
		}

		@Test
		@DisplayName("puts serializeNullValues")
		void putsSerializeNull() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.serializeNullValues(true));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals(true, options.get(CodecOptions.CODEC_SERIALIZE_NULL));
		}

		@Test
		@DisplayName("puts enumSerialization")
		void putsEnumSerialization() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.enumSerialization("VALUE"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("VALUE", options.get(CodecOptions.CODEC_ENUM_SERIALIZATION));
		}

		@Test
		@DisplayName("puts valueReaderName when not blank")
		void putsValueReaderName() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.valueReaderName("myReader"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, false, options);
			assertEquals("myReader", options.get(CodecOptions.CODEC_VALUE_READER_NAME));
		}

		@Test
		@DisplayName("skips valueReaderName when blank")
		void skipsValueReaderName() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, false, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_VALUE_READER_NAME));
		}

		@Test
		@DisplayName("puts valueWriterName when not blank")
		void putsValueWriterName() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.valueWriterName("myWriter"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("myWriter", options.get(CodecOptions.CODEC_VALUE_WRITER_NAME));
		}

		@Test
		@DisplayName("skips valueWriterName when blank")
		void skipsValueWriterName() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_VALUE_WRITER_NAME));
		}
	}

	@Nested
	@DisplayName("CodecConfig conversion - Type Configuration")
	class TypeConfiguration {

		@Test
		@DisplayName("puts typeStrategy")
		void putsTypeStrategy() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.typeStrategy("NAME"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("NAME", options.get(CodecOptions.CODEC_TYPE_STRATEGY));
		}

		@Test
		@DisplayName("puts typeFormat")
		void putsTypeFormat() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.typeFormat("STRUCTURED"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("STRUCTURED", options.get(CodecOptions.CODEC_TYPE_FORMAT));
		}

		@Test
		@DisplayName("puts typeKey when not blank")
		void putsTypeKey() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.typeKey("myType"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("myType", options.get(CodecOptions.CODEC_TYPE_KEY));
		}

		@Test
		@DisplayName("skips typeKey when blank")
		void skipsTypeKey() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_TYPE_KEY));
		}

		@Test
		@DisplayName("puts typeInclude")
		void putsTypeInclude() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.typeInclude(false));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals(false, options.get(CodecOptions.CODEC_TYPE_INCLUDE));
		}

		@Test
		@DisplayName("puts typeNameKey when not blank")
		void putsTypeNameKey() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.typeNameKey("name"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("name", options.get(CodecOptions.CODEC_TYPE_NAME_KEY));
		}

		@Test
		@DisplayName("skips typeNameKey when blank")
		void skipsTypeNameKey() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_TYPE_NAME_KEY));
		}

		@Test
		@DisplayName("puts typeSchemaKey when not blank")
		void putsTypeSchemaKey() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.typeSchemaKey("schema"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("schema", options.get(CodecOptions.CODEC_TYPE_SCHEMA_KEY));
		}

		@Test
		@DisplayName("skips typeSchemaKey when blank")
		void skipsTypeSchemaKey() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_TYPE_SCHEMA_KEY));
		}
	}

	@Nested
	@DisplayName("CodecConfig conversion - ID Configuration")
	class IdConfiguration {

		@Test
		@DisplayName("puts idStrategy")
		void putsIdStrategy() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.idStrategy("COMBINED"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("COMBINED", options.get(CodecOptions.CODEC_ID_STRATEGY));
		}

		@Test
		@DisplayName("puts idFormat")
		void putsIdFormat() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.idFormat("STRUCTURED"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("STRUCTURED", options.get(CodecOptions.CODEC_ID_FORMAT));
		}

		@Test
		@DisplayName("puts idKey when not blank")
		void putsIdKey() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.idKey("id"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("id", options.get(CodecOptions.CODEC_ID_KEY));
		}

		@Test
		@DisplayName("skips idKey when blank")
		void skipsIdKey() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_ID_KEY));
		}

		@Test
		@DisplayName("puts idValueKey when not blank")
		void putsIdValueKey() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.idValueKey("value"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("value", options.get(CodecOptions.CODEC_ID_VALUE_KEY));
		}

		@Test
		@DisplayName("skips idValueKey when blank")
		void skipsIdValueKey() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_ID_VALUE_KEY));
		}

		@Test
		@DisplayName("puts idKeyMode")
		void putsIdKeyMode() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.idKeyMode("BOTH"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("BOTH", options.get(CodecOptions.CODEC_ID_KEY_MODE));
		}

		@Test
		@DisplayName("puts idOnTop")
		void putsIdOnTop() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.idOnTop(true));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals(true, options.get(CodecOptions.CODEC_ID_ON_TOP));
		}
	}

	@Nested
	@DisplayName("CodecConfig conversion - Reference Configuration")
	class ReferenceConfiguration {

		@Test
		@DisplayName("puts refFormat")
		void putsRefFormat() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.refFormat("PLAIN"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("PLAIN", options.get(CodecOptions.CODEC_REF_FORMAT));
		}

		@Test
		@DisplayName("puts refKey when not blank")
		void putsRefKey() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.refKey("$ref"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("$ref", options.get(CodecOptions.CODEC_REF_KEY));
		}

		@Test
		@DisplayName("skips refKey when blank")
		void skipsRefKey() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_REF_KEY));
		}

		@Test
		@DisplayName("puts refTypeKey when not blank")
		void putsRefTypeKey() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.refTypeKey("type"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("type", options.get(CodecOptions.CODEC_REF_TYPE_KEY));
		}

		@Test
		@DisplayName("skips refTypeKey when blank")
		void skipsRefTypeKey() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_REF_TYPE_KEY));
		}
	}

	@Nested
	@DisplayName("CodecConfig conversion - SuperType Configuration")
	class SuperTypeConfiguration {

		@Test
		@DisplayName("puts superTypeSerialize")
		void putsSuperTypeSerialize() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.superTypeSerialize(true));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals(true, options.get(CodecOptions.CODEC_SUPERTYPE_SERIALIZE));
		}

		@Test
		@DisplayName("puts superTypeKey when not blank")
		void putsSuperTypeKey() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.superTypeKey("_supers"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("_supers", options.get(CodecOptions.CODEC_SUPERTYPE_KEY));
		}

		@Test
		@DisplayName("skips superTypeKey when blank")
		void skipsSuperTypeKey() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertFalse(options.containsKey(CodecOptions.CODEC_SUPERTYPE_KEY));
		}

		@Test
		@DisplayName("puts superTypeStrategy")
		void putsSuperTypeStrategy() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.superTypeStrategy("SINGLE"));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals("SINGLE", options.get(CodecOptions.CODEC_SUPERTYPE_STRATEGY));
		}
	}

	@Nested
	@DisplayName("CodecConfig conversion - Global Options")
	class GlobalOptions {

		@Test
		@DisplayName("puts smartCompression")
		void putsSmartCompression() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b.smartCompression(true));
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);
			assertEquals(true, options.get(CodecOptions.CODEC_SMART_COMPRESSION));
		}
	}

	@Nested
	@DisplayName("CodecConfig conversion - Defaults")
	class Defaults {

		@Test
		@DisplayName("default annotation produces expected option values")
		void defaultAnnotation() {
			CodecConfig config = AnnotationHelper.codecConfigDefaults();
			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);

			// Booleans with false defaults
			assertEquals(false, options.get(CodecOptions.CODEC_SERIALIZE_DEFAULTS));
			assertEquals(false, options.get(CodecOptions.CODEC_SERIALIZE_EMPTY));
			assertEquals(false, options.get(CodecOptions.CODEC_SERIALIZE_NULL));
			assertEquals(false, options.get(CodecOptions.CODEC_ID_ON_TOP));
			assertEquals(false, options.get(CodecOptions.CODEC_SUPERTYPE_SERIALIZE));
			assertEquals(false, options.get(CodecOptions.CODEC_SMART_COMPRESSION));

			// Boolean with true default
			assertEquals(true, options.get(CodecOptions.CODEC_TYPE_INCLUDE));

			// String enum defaults
			assertEquals("LITERAL", options.get(CodecOptions.CODEC_ENUM_SERIALIZATION));
			assertEquals("URI", options.get(CodecOptions.CODEC_TYPE_STRATEGY));
			assertEquals("PLAIN", options.get(CodecOptions.CODEC_TYPE_FORMAT));
			assertEquals("ID_FIELD", options.get(CodecOptions.CODEC_ID_STRATEGY));
			assertEquals("PLAIN", options.get(CodecOptions.CODEC_ID_FORMAT));
			assertEquals("ID_ONLY", options.get(CodecOptions.CODEC_ID_KEY_MODE));
			assertEquals("STRUCTURED", options.get(CodecOptions.CODEC_REF_FORMAT));
			assertEquals("ALL", options.get(CodecOptions.CODEC_SUPERTYPE_STRATEGY));

			// Blank strings should NOT be present
			assertFalse(options.containsKey(CodecOptions.CODEC_DATE_FORMAT));
			assertFalse(options.containsKey(CodecOptions.CODEC_VALUE_READER_NAME));
			assertFalse(options.containsKey(CodecOptions.CODEC_VALUE_WRITER_NAME));
			assertFalse(options.containsKey(CodecOptions.CODEC_TYPE_KEY));
			assertFalse(options.containsKey(CodecOptions.CODEC_TYPE_NAME_KEY));
			assertFalse(options.containsKey(CodecOptions.CODEC_TYPE_SCHEMA_KEY));
			assertFalse(options.containsKey(CodecOptions.CODEC_ID_KEY));
			assertFalse(options.containsKey(CodecOptions.CODEC_ID_VALUE_KEY));
			assertFalse(options.containsKey(CodecOptions.CODEC_REF_KEY));
			assertFalse(options.containsKey(CodecOptions.CODEC_REF_TYPE_KEY));
			assertFalse(options.containsKey(CodecOptions.CODEC_SUPERTYPE_KEY));
		}
	}

	@Nested
	@DisplayName("CodecConfig conversion - Full Custom")
	class FullCustom {

		@Test
		@DisplayName("all options are set with custom values")
		void allCustomValues() {
			CodecConfig config = AnnotationHelper.codecConfig(b -> b
					.dateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.serializeDefaultValues(true)
					.serializeEmptyValues(true)
					.serializeNullValues(true)
					.enumSerialization("NAME")
					.valueReaderName("customReader")
					.valueWriterName("customWriter")
					.typeStrategy("SCHEMA_AND_TYPE")
					.typeFormat("STRUCTURED")
					.typeKey("@type")
					.typeInclude(false)
					.typeNameKey("typeName")
					.typeSchemaKey("typeSchema")
					.idStrategy("COMBINED")
					.idFormat("STRUCTURED")
					.idKey("@id")
					.idValueKey("val")
					.idKeyMode("BOTH")
					.idOnTop(true)
					.refFormat("PLAIN")
					.refKey("ref")
					.refTypeKey("refType")
					.superTypeSerialize(true)
					.superTypeKey("_supers")
					.superTypeStrategy("SINGLE")
					.smartCompression(true));

			Map<Object, Object> options = new HashMap<>();
			converter.convertAnnotation(config, true, options);

			// Feature
			assertEquals("yyyy-MM-dd'T'HH:mm:ss'Z'", options.get(CodecOptions.CODEC_DATE_FORMAT));
			assertEquals(true, options.get(CodecOptions.CODEC_SERIALIZE_DEFAULTS));
			assertEquals(true, options.get(CodecOptions.CODEC_SERIALIZE_EMPTY));
			assertEquals(true, options.get(CodecOptions.CODEC_SERIALIZE_NULL));
			assertEquals("NAME", options.get(CodecOptions.CODEC_ENUM_SERIALIZATION));
			assertEquals("customReader", options.get(CodecOptions.CODEC_VALUE_READER_NAME));
			assertEquals("customWriter", options.get(CodecOptions.CODEC_VALUE_WRITER_NAME));

			// Type
			assertEquals("SCHEMA_AND_TYPE", options.get(CodecOptions.CODEC_TYPE_STRATEGY));
			assertEquals("STRUCTURED", options.get(CodecOptions.CODEC_TYPE_FORMAT));
			assertEquals("@type", options.get(CodecOptions.CODEC_TYPE_KEY));
			assertEquals(false, options.get(CodecOptions.CODEC_TYPE_INCLUDE));
			assertEquals("typeName", options.get(CodecOptions.CODEC_TYPE_NAME_KEY));
			assertEquals("typeSchema", options.get(CodecOptions.CODEC_TYPE_SCHEMA_KEY));

			// ID
			assertEquals("COMBINED", options.get(CodecOptions.CODEC_ID_STRATEGY));
			assertEquals("STRUCTURED", options.get(CodecOptions.CODEC_ID_FORMAT));
			assertEquals("@id", options.get(CodecOptions.CODEC_ID_KEY));
			assertEquals("val", options.get(CodecOptions.CODEC_ID_VALUE_KEY));
			assertEquals("BOTH", options.get(CodecOptions.CODEC_ID_KEY_MODE));
			assertEquals(true, options.get(CodecOptions.CODEC_ID_ON_TOP));

			// Reference
			assertEquals("PLAIN", options.get(CodecOptions.CODEC_REF_FORMAT));
			assertEquals("ref", options.get(CodecOptions.CODEC_REF_KEY));
			assertEquals("refType", options.get(CodecOptions.CODEC_REF_TYPE_KEY));

			// SuperType
			assertEquals(true, options.get(CodecOptions.CODEC_SUPERTYPE_SERIALIZE));
			assertEquals("_supers", options.get(CodecOptions.CODEC_SUPERTYPE_KEY));
			assertEquals("SINGLE", options.get(CodecOptions.CODEC_SUPERTYPE_STRATEGY));

			// Global
			assertEquals(true, options.get(CodecOptions.CODEC_SMART_COMPRESSION));
		}
	}
}
