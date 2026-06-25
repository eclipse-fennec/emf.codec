/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.codec.playground;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.annotations.GogoCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.tabular.CodecTabularOptions;
import org.eclipse.fennec.codec.tabular.model.tabular.ReferenceMode;
import org.gecko.emf.osgi.example.model.basic.Address;
import org.gecko.emf.osgi.example.model.basic.BasicFactory;
import org.gecko.emf.osgi.example.model.basic.BasicPackage;
import org.gecko.emf.osgi.example.model.basic.Contact;
import org.gecko.emf.osgi.example.model.basic.ContactContextType;
import org.gecko.emf.osgi.example.model.basic.ContactType;
import org.gecko.emf.osgi.example.model.basic.Person;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

@Component(service = TabularExporterCommand.class, scope = ServiceScope.PROTOTYPE)
@GogoCommand(scope = "exporter", function = "export")
public class TabularExporterCommand {

	@Reference
	BasicPackage basicPackage;

	@Reference
	private ResourceSet resourceSet;

	private static final Path TEMP_FOLDER = Path.of(System.getProperty("java.io.tmpdir"));

	@Descriptor("Export a test object in a resource with the provided URI")
	public void export(
			@Descriptor("The type of the export. Could be CSV, ODS, XLSX or R") 
			String type, 
			@Descriptor("The mode for the exporter. Could be IGNORE, FLAT, SQL_TABLES")
			String mode
			) throws IOException {

		URI uri = null;
		ReferenceMode referenceMode = ReferenceMode.valueOf(mode);
		Map<String, Object> effective = new HashMap<>();
		effective.putIfAbsent(CodecTabularOptions.OPTION_REFERENCE_MODE,
				ReferenceMode.valueOf(mode));
		switch(type) {
		case "CSV":
			if(ReferenceMode.SQL_TABLES.equals(referenceMode)) {
				uri = URI.createURI(TEMP_FOLDER.resolve(UUID.randomUUID().toString().concat(".csvz")).toString());
			} else {
				uri = URI.createURI(TEMP_FOLDER.resolve(UUID.randomUUID().toString().concat(".csv")).toString());
			}
			break;
		case "ODS":
			uri = URI.createURI(TEMP_FOLDER.resolve(UUID.randomUUID().toString().concat(".ods")).toString());
			break;
		case "XLSX":
			uri = URI.createURI(TEMP_FOLDER.resolve(UUID.randomUUID().toString().concat(".xlsx")).toString());
			break;
		case "R":
			if(ReferenceMode.SQL_TABLES.equals(referenceMode)) {
				uri = URI.createURI(TEMP_FOLDER.resolve(UUID.randomUUID().toString().concat(".rdataz")).toString());
				effective.put("codec.rlang.dataframePerFile", true);
			} else {
				uri = URI.createURI(TEMP_FOLDER.resolve(UUID.randomUUID().toString().concat(".RData")).toString());
			}
		default:
			System.err.println(String.format("Exporter type %s not supported!", type));
		}

		Resource resource = resourceSet.createResource(uri);
		Person person1 = createPerson("John", "Doe");
		Person person2 = createPerson("Mario", "Rossi");
		resource.getContents().add(person1);
		resource.getContents().add(person2);
		
		try {
			resource.save(effective);
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(String.format("Result saved in %s", uri.toString()));

	}

	private Person createPerson(String firstName, String lastName) {
		Person person = BasicFactory.eINSTANCE.createPerson();
		person.setId(UUID.randomUUID().toString());
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setAddress(createAddress());
		person.getContact().add(createContact(ContactContextType.HOME, ContactType.MOBILE, "123456677"));
		person.getContact().add(createContact(ContactContextType.PRIVATE, ContactType.EMAIL, "i.salforr@gmail.com"));
		return person;
	}

	private Address createAddress() {
		Address address = BasicFactory.eINSTANCE.createAddress();
		address.setCity("Belluno");
		address.setId(UUID.randomUUID().toString());
		address.setZip("32037");
		return address;
	}

	private Contact createContact(ContactContextType contextType, ContactType type, String value) {
		Contact contact = BasicFactory.eINSTANCE.createContact();
		contact.setContext(contextType);
		contact.setType(type);
		contact.setValue(value);
		return contact;
	}

}
