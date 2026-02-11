/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.codec.test.helper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.helper.CodecIOHelper;
import org.gecko.codec.demo.model.person.Address;
import org.gecko.codec.demo.model.person.BusinessAddress;
import org.gecko.codec.demo.model.person.BusinessPerson;
import org.gecko.codec.demo.model.person.Person;
import org.gecko.codec.demo.model.person.PersonFactory;
import org.gecko.codec.demo.model.person.SpecificBusinessPerson;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;

/**
 * 
 * @author ilenia
 * @since Oct 18, 2024
 */
public class CodecTestHelper {
	
	public static BusinessPerson getTestBusinessPerson() {
		BusinessPerson person = PersonFactory.eINSTANCE.createBusinessPerson();
		person.setId(UUID.randomUUID().toString());
		person.setName("John");
		person.setLastName("Doe");
		person.setBirthDate(Date.from(                     // Convert from modern java.time class to troublesome old legacy class.  DO NOT DO THIS unless you must, to inter operate with old code not yet updated for java.time.
			    LocalDate.of(1990, 6, 20)                        // `LocalDate` class represents a date-only, without time-of-day and without time zone nor offset-from-UTC. 
			    .atStartOfDay(                       // Let java.time determine the first moment of the day on that date in that zone. Never assume the day starts at 00:00:00.
			        ZoneId.of( "Europe/Berlin" )  // Specify time zone using proper name in `continent/region` format, never 3-4 letter pseudo-zones such as “PST”, “CST”, “IST”. 
			    )                                    // Produce a `ZonedDateTime` object. 
			    .toInstant()                         // Extract an `Instant` object, a moment always in UTC.
			));
		person.getTitles().add("Mrs");
		person.getTitles().add("Dr");
		person.setTransientAtt(7);
		person.setCompanyIdCardNumber(UUID.randomUUID().toString());
		return person;
	}
	
	public static SpecificBusinessPerson getTestSpecificBusinessPerson() {
		SpecificBusinessPerson person = PersonFactory.eINSTANCE.createSpecificBusinessPerson();
		person.setId(UUID.randomUUID().toString());
		person.setName("John");
		person.setLastName("Doe");
		person.setBirthDate(Date.from(                     // Convert from modern java.time class to troublesome old legacy class.  DO NOT DO THIS unless you must, to inter operate with old code not yet updated for java.time.
			    LocalDate.of(1990, 6, 20)                        // `LocalDate` class represents a date-only, without time-of-day and without time zone nor offset-from-UTC. 
			    .atStartOfDay(                       // Let java.time determine the first moment of the day on that date in that zone. Never assume the day starts at 00:00:00.
			        ZoneId.of( "Europe/Berlin" )  // Specify time zone using proper name in `continent/region` format, never 3-4 letter pseudo-zones such as “PST”, “CST”, “IST”. 
			    )                                    // Produce a `ZonedDateTime` object. 
			    .toInstant()                         // Extract an `Instant` object, a moment always in UTC.
			));
		person.getTitles().add("Mrs");
		person.getTitles().add("Dr");
		person.setTransientAtt(7);
		return person;
	}
	
	public static Person getTestPerson() {
		Person person = PersonFactory.eINSTANCE.createPerson();
		person.setId(UUID.randomUUID().toString());
		person.setName("John");
		person.setLastName("Doe");
		person.setAge(42);
		person.setBirthDate(Date.from(                     // Convert from modern java.time class to troublesome old legacy class.  DO NOT DO THIS unless you must, to inter operate with old code not yet updated for java.time.
			    LocalDate.of(1990, 6, 20)                        // `LocalDate` class represents a date-only, without time-of-day and without time zone nor offset-from-UTC. 
			    .atStartOfDay(                       // Let java.time determine the first moment of the day on that date in that zone. Never assume the day starts at 00:00:00.
			        ZoneId.of( "Europe/Berlin" )  // Specify time zone using proper name in `continent/region` format, never 3-4 letter pseudo-zones such as “PST”, “CST”, “IST”. 
			    )                                    // Produce a `ZonedDateTime` object. 
			    .toInstant()                         // Extract an `Instant` object, a moment always in UTC.
			));
		person.getTitles().add("Mrs");
		person.getTitles().add("Dr");
		person.setTransientAtt(7);
		person.setHeight(1.77);
		person.setWeight(78.3f);
		return person;
	}
	
	public static Address getTestAddress() {
		Address address = PersonFactory.eINSTANCE.createAddress();
		address.setId(UUID.randomUUID().toString());
		address.setStreet(UUID.randomUUID().toString());
		address.setZip(UUID.randomUUID().toString());
		return address;
	}
	
	public static BusinessAddress getTestBusinessAddress() {
		BusinessAddress address = PersonFactory.eINSTANCE.createBusinessAddress();
		address.setId(UUID.randomUUID().toString());
		address.setStreet(UUID.randomUUID().toString());
		address.setZip(UUID.randomUUID().toString());
		return address;
	}
	
	public static final CodecValueWriter<String, String> TEST_VALUE_WRITER = new CodecValueWriter<>() {

		@Override
		public String getName() {
			return "TEST_VALUE_WRITER";
		}

		@Override
		public String writeValue(String value, SerializationContext provider) {
			if(value == null) return null;
			return "Super".concat(value);
		}
	};
	
	public static final CodecValueWriter<List<String>, List<String>> TEST_MULTI_VALUE_WRITER = new CodecValueWriter<>() {

		@Override
		public String getName() {
			return "TEST_MULTI_VALUE_WRITER";
		}

		@Override
		public List<String> writeValue(List<String> values, SerializationContext provider) {
			if(values == null || values.isEmpty()) return null;
			List<String> result = new LinkedList<>();
			values.forEach(v -> {
				result.add("Super".concat(v));
			});
			return result;
		}
	};

	public static final CodecValueWriter<EClass, String> TEST_TYPE_WRITER = new CodecValueWriter<>() {

		@Override
		public String getName() {
			return "TEST_TYPE_WRITER";
		}

		@Override
		public String writeValue(EClass value, SerializationContext provider) {
			return "test.".concat(value.getName());
		}
	};

	public static final CodecValueReader<String, String> TEST_VALUE_READER = new CodecValueReader<>() {

		@Override
		public String getName() {
			return "TEST_VALUE_READER";
		}

		@Override
		public String readValue(String value, DeserializationContext context) {
			if(value == null) return null;
			if(value.startsWith("Super")) return value.substring(5, value.length());
			return value;
		}		
	};

	public static final CodecValueReader<String, EClass> TEST_TYPE_READER = new CodecValueReader<>() {

		@Override
		public String getName() {
			return "TEST_TYPE_READER";
		}

		@Override
		public EClass readValue(String value, DeserializationContext ctxt) {
			if(value == null) return null;
			if(value.startsWith("test.")) value = value.substring(5);
			return CodecIOHelper.findEClassByName(value, null);
		}
	};
}
