/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.fennec.codec.configurator.CodecFactoryConfigurator;
import org.eclipse.fennec.codec.configurator.ObjectMapperConfigurator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.Property.Type;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;

//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class ObjMapperConfiguratorTest {

	
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "enableFeatures", value={"PROPAGATE_TRANSIENT_MARKER", "REQUIRE_SETTERS_FOR_GETTERS", "USE_STATIC_TYPING", "SORT_PROPERTIES_ALPHABETICALLY",
					"ACCEPT_CASE_INSENSITIVE_PROPERTIES", "ACCEPT_CASE_INSENSITIVE_ENUMS", "ACCEPT_CASE_INSENSITIVE_VALUES",
					"USE_WRAPPER_NAME_AS_PROPERTY_NAME", "ALLOW_EXPLICIT_PROPERTY_RENAMING", "ALLOW_IS_GETTERS_FOR_NON_BOOLEAN",
				    "MapperFeature.ALLOW_VOID_VALUED_PROPERTIES", "USE_BASE_TYPE_AS_DEFAULT_IMPL", "USE_BIG_DECIMAL_FOR_FLOATS"}, type = Type.Array)
	})
	@Test
	public void testObjMapperConfigEnableMapperFeature(@InjectService(timeout = 2000l) CodecFactoryConfigurator factoryConfigurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(factoryConfigurator);
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
		
		assertTrue(mapper.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER));
		assertTrue(mapper.isEnabled(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS));
		assertTrue(mapper.isEnabled(MapperFeature.USE_STATIC_TYPING));
		assertTrue(mapper.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
		assertTrue(mapper.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
		assertTrue(mapper.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
		assertTrue(mapper.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES));
		assertTrue(mapper.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME));
		assertTrue(mapper.isEnabled(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING));
		assertTrue(mapper.isEnabled(MapperFeature.ALLOW_IS_GETTERS_FOR_NON_BOOLEAN));
		assertTrue(mapper.isEnabled(MapperFeature.ALLOW_VOID_VALUED_PROPERTIES));
		assertTrue(mapper.isEnabled(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL));	
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "disableFeatures", value={"USE_ANNOTATIONS", "USE_GETTERS_AS_SETTERS", "ALLOW_FINAL_FIELDS_AS_MUTATORS", "INFER_PROPERTY_MUTATORS", "INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES", 
					"CAN_OVERRIDE_ACCESS_MODIFIERS", "OVERRIDE_PUBLIC_ACCESS_MODIFIERS", "INFER_BUILDER_TYPE_BINDINGS", "REQUIRE_TYPE_ID_FOR_SUBTYPES", "DEFAULT_VIEW_INCLUSION", 
					"SORT_CREATOR_PROPERTIES_FIRST", "ALLOW_COERCION_OF_SCALARS", "IGNORE_MERGE_FOR_UNMERGEABLE", "APPLY_DEFAULT_VALUES"}, type = Type.Array)
	})
	@Test
	public void testObjMapperConfigDisableMapperFeature(@InjectService(timeout = 2000l) CodecFactoryConfigurator factoryConfigurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(factoryConfigurator);
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
		
		assertFalse(mapper.isEnabled(MapperFeature.USE_ANNOTATIONS));
		assertFalse(mapper.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS));
		assertFalse(mapper.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS));
		assertFalse(mapper.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS));
		assertFalse(mapper.isEnabled(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES));
		assertFalse(mapper.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS));
		assertFalse(mapper.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
		assertFalse(mapper.isEnabled(MapperFeature.INFER_BUILDER_TYPE_BINDINGS));
		assertFalse(mapper.isEnabled(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES));	
		assertFalse(mapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
		assertFalse(mapper.isEnabled(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST));
		assertFalse(mapper.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS));
		assertFalse(mapper.isEnabled(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE));
		assertFalse(mapper.isEnabled(MapperFeature.APPLY_DEFAULT_VALUES));	
	}
	

	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "enableFeatures", value={"WRAP_ROOT_VALUE", "INDENT_OUTPUT", "WRITE_SELF_REFERENCES_AS_NULL", "CLOSE_CLOSEABLE",
					"WRITE_DATE_KEYS_AS_TIMESTAMPS", "WRITE_DATES_WITH_ZONE_ID", "WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS",
					"WRITE_ENUMS_USING_TO_STRING", "WRITE_ENUMS_USING_INDEX", "WRITE_ENUM_KEYS_USING_INDEX", "WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED",
					"ORDER_MAP_ENTRIES_BY_KEYS", "USE_EQUALITY_FOR_OBJECT_ID"}, type = Type.Array)
	})
	@Test
	public void testObjMapperConfigEnableSerializationFeature(@InjectService(timeout = 2000l) CodecFactoryConfigurator factoryConfigurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(factoryConfigurator);
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
		
		assertTrue(mapper.isEnabled(SerializationFeature.WRAP_ROOT_VALUE));
		assertTrue(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
		assertTrue(mapper.isEnabled(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL));
		assertTrue(mapper.isEnabled(SerializationFeature.CLOSE_CLOSEABLE));
		assertTrue(mapper.isEnabled(DateTimeFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS));
		assertTrue(mapper.isEnabled(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID));
		assertTrue(mapper.isEnabled(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID));
		assertTrue(mapper.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS));
		assertTrue(mapper.isEnabled(EnumFeature.WRITE_ENUMS_USING_TO_STRING));
		assertTrue(mapper.isEnabled(EnumFeature.WRITE_ENUMS_USING_INDEX));
		assertTrue(mapper.isEnabled(EnumFeature.WRITE_ENUM_KEYS_USING_INDEX));
		assertTrue(mapper.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED));
		assertTrue(mapper.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS));
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "disableFeatures", value={"FAIL_ON_EMPTY_BEANS", "FAIL_ON_SELF_REFERENCES", "WRAP_EXCEPTIONS", "FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS", "FLUSH_AFTER_WRITE_VALUE", 
					"WRITE_DATES_AS_TIMESTAMPS", "WRITE_DATES_WITH_CONTEXT_TIME_ZONE", "WRITE_DURATIONS_AS_TIMESTAMPS", "WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS", "EAGER_SERIALIZER_FETCH"}, type = Type.Array)
	})
	@Test
	public void testObjMapperConfigDisableSerializationFeature(@InjectService(timeout = 2000l) CodecFactoryConfigurator factoryConfigurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(factoryConfigurator);
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
		
		assertFalse(mapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
		assertFalse(mapper.isEnabled(SerializationFeature.FAIL_ON_SELF_REFERENCES));
		assertFalse(mapper.isEnabled(SerializationFeature.WRAP_EXCEPTIONS));
		assertFalse(mapper.isEnabled(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
		assertFalse(mapper.isEnabled(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS));
		assertFalse(mapper.isEnabled(DateTimeFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE));
		assertFalse(mapper.isEnabled(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS));
		assertFalse(mapper.isEnabled(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS));
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "enableFeatures", value={"USE_BIG_DECIMAL_FOR_FLOATS", "USE_BIG_INTEGER_FOR_INTS", "USE_LONG_FOR_INTS", "USE_JAVA_ARRAY_FOR_JSON_ARRAY",
					"FAIL_ON_NULL_FOR_PRIMITIVES", "FAIL_ON_NUMBERS_FOR_ENUMS", "FAIL_ON_READING_DUP_TREE_KEY",
					"FAIL_ON_IGNORED_PROPERTIES", "FAIL_ON_MISSING_CREATOR_PROPERTIES", "FAIL_ON_NULL_CREATOR_PROPERTIES", "FAIL_ON_TRAILING_TOKENS",
					"ACCEPT_SINGLE_VALUE_AS_ARRAY", "UNWRAP_SINGLE_VALUE_ARRAYS", "ACCEPT_EMPTY_STRING_AS_NULL_OBJECT", "ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT",
					"READ_ENUMS_USING_TO_STRING", "READ_UNKNOWN_ENUM_VALUES_AS_NULL", "READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE"}, type = Type.Array)
	})
	@Test
	public void testObjMapperConfigEnableDeserializationFeature(@InjectService(timeout = 2000l) CodecFactoryConfigurator factoryConfigurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(factoryConfigurator);
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
		
		assertTrue(mapper.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS));
		assertTrue(mapper.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS));
		assertTrue(mapper.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS));
		assertTrue(mapper.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY));
		assertTrue(mapper.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES));
		assertTrue(mapper.isEnabled(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS));
		assertTrue(mapper.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY));
		assertTrue(mapper.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));
		assertTrue(mapper.isEnabled(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES));
		assertTrue(mapper.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES));
		assertTrue(mapper.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));
		assertTrue(mapper.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
		assertTrue(mapper.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS));		
		assertTrue(mapper.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT));
		assertTrue(mapper.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT));
		assertTrue(mapper.isEnabled(EnumFeature.READ_ENUMS_USING_TO_STRING));
		assertTrue(mapper.isEnabled(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
		assertTrue(mapper.isEnabled(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE));
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "disableFeatures", value={"FAIL_ON_UNKNOWN_PROPERTIES", "FAIL_ON_INVALID_SUBTYPE", "FAIL_ON_UNRESOLVED_OBJECT_IDS", "FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY", "WRAP_EXCEPTIONS", 
					"ACCEPT_FLOAT_AS_INT", "READ_DATE_TIMESTAMPS_AS_NANOSECONDS", "ADJUST_DATES_TO_CONTEXT_TIME_ZONE", "EAGER_DESERIALIZER_FETCH"}, type = Type.Array)
	})
	@Test
	public void testObjMapperConfigDisableDesrializationFeature(@InjectService(timeout = 2000l) CodecFactoryConfigurator factoryConfigurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(factoryConfigurator);
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
		
		assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
		assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));
		assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS));
		assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY));
		assertFalse(mapper.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
		assertFalse(mapper.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT));
		assertFalse(mapper.isEnabled(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS));
		assertFalse(mapper.isEnabled(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE));
		assertFalse(mapper.isEnabled(DeserializationFeature.EAGER_DESERIALIZER_FETCH));

	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "enableFeatures", value={"WRITE_BIGDECIMAL_AS_PLAIN", "StreamWriteFeature.IGNORE_UNKNOWN", 
					"StreamWriteFeature.STRICT_DUPLICATE_DETECTION", "StreamWriteFeature.USE_FAST_DOUBLE_WRITER"}, type = Type.Array)
	})
	@Test
	public void testFactoryConfigEnableStreamWrite(@InjectService(timeout = 2000l) CodecFactoryConfigurator configurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
				
		assertTrue(mapper.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));
		assertTrue(mapper.isEnabled(StreamWriteFeature.USE_FAST_DOUBLE_WRITER));
		assertTrue(mapper.isEnabled(StreamWriteFeature.STRICT_DUPLICATE_DETECTION));
		assertTrue(mapper.isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN));
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "disableFeatures", value={"AUTO_CLOSE_TARGET", "StreamWriteFeature.AUTO_CLOSE_CONTENT", 
					"StreamWriteFeature.FLUSH_PASSED_TO_STREAM"}, type = Type.Array)
	})
	@Test
	public void testFactoryConfigDisableStreamWrite(@InjectService(timeout = 2000l) CodecFactoryConfigurator configurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
					
		assertFalse(mapper.isEnabled(StreamWriteFeature.AUTO_CLOSE_TARGET));
		assertFalse(mapper.isEnabled(StreamWriteFeature.AUTO_CLOSE_CONTENT));
		assertFalse(mapper.isEnabled(StreamWriteFeature.FLUSH_PASSED_TO_STREAM));	
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "enableFeatures", value={"STRICT_DUPLICATE_DETECTION", "IGNORE_UNDEFINED", 
					"INCLUDE_SOURCE_IN_LOCATION", "USE_FAST_DOUBLE_PARSER", "USE_FAST_BIG_NUMBER_PARSER"}, type = Type.Array)
	})
	@Test
	public void testFactoryConfigEnableStreamRead(@InjectService(timeout = 2000l) CodecFactoryConfigurator configurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
					
		assertTrue(mapper.isEnabled(StreamReadFeature.STRICT_DUPLICATE_DETECTION));
		assertTrue(mapper.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
		assertTrue(mapper.isEnabled(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION));
		assertTrue(mapper.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
		assertTrue(mapper.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));	
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "disableFeatures", value={"AUTO_CLOSE_SOURCE"}, type = Type.Array)
	})
	@Test
	public void testFactoryConfigDisableStreamRead(@InjectService(timeout = 2000l) CodecFactoryConfigurator configurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
					
		assertFalse(mapper.isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE));
	}
	
	@WithFactoryConfiguration(factoryPid = "DefaultCodecFactoryConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "test", properties = {
			@Property(key = "type", value="json"),
			@Property(key = "dateFormat", value="yyyy-MM-dd"),
			@Property(key = "locale", value="it-IT"),
			@Property(key = "timeZone", value="Europe/Amsterdam"),
	})
	@Test
	public void testObjMapperConfiguratorDateFormat(@InjectService(timeout = 2000l) CodecFactoryConfigurator factoryConfigurator,
			@InjectService(timeout = 2000l) ObjectMapperConfigurator mapperConfigurator
			) throws InterruptedException, IOException {
	
		assertNotNull(factoryConfigurator);
		assertNotNull(mapperConfigurator);
		
		ObjectMapper mapper = mapperConfigurator.getObjMapperBuilderFactory().createObjectMapperBuilder().build();
		assertNotNull(mapper);
		
		DateFormat mapperDF = mapper.serializationConfig().getDateFormat();
		assertEquals("1990-06-20", mapperDF.format(Date.from(                     // Convert from modern java.time class to troublesome old legacy class.  DO NOT DO THIS unless you must, to inter operate with old code not yet updated for java.time.
				    LocalDate.of(1990, 6, 20)                        // `LocalDate` class represents a date-only, without time-of-day and without time zone nor offset-from-UTC. 
				    .atStartOfDay(                       // Let java.time determine the first moment of the day on that date in that zone. Never assume the day starts at 00:00:00.
				        ZoneId.of( "Europe/Berlin" )  // Specify time zone using proper name in `continent/region` format, never 3-4 letter pseudo-zones such as “PST”, “CST”, “IST”. 
				    )                                    // Produce a `ZonedDateTime` object. 
				    .toInstant()                         // Extract an `Instant` object, a moment always in UTC.
				)));
		
		Locale loc = new Locale.Builder().setLanguageTag("it-IT").build();
		Locale mapperLoc = mapper.serializationConfig().getLocale();
		assertEquals(loc.getCountry(), mapperLoc.getCountry());
		assertEquals(loc.getLanguage(), mapperLoc.getLanguage());
		
		TimeZone tz = TimeZone.getTimeZone("Europe/Amsterdam");
		TimeZone mapperTZ = mapper.serializationConfig().getTimeZone();
		assertEquals(tz.getID(), mapperTZ.getID());
	}	
}
