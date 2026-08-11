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
package org.eclipse.fennec.codec.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.model.openapi.Contact;
import org.eclipse.fennec.model.openapi.Info;
import org.eclipse.fennec.model.openapi.License;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for OpenAPI info object serialization/deserialization.
 */
@DisplayName("OpenAPI Info")
class OpenApiInfoTest {

	@BeforeEach
	void setUp() {
		EPackage.Registry.INSTANCE.put(OpenApiPackage.eNS_URI, OpenApiPackage.eINSTANCE);
	}

	@Test
	@DisplayName("deserializes complete info with contact and license")
	void deserializesCompleteInfo() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": {
					"title": "Full Info Test API",
					"description": "A comprehensive API for testing",
					"termsOfService": "http://example.com/terms",
					"contact": {
						"name": "API Support Team",
						"url": "http://example.com/support",
						"email": "support@example.com"
					},
					"license": {
						"name": "Apache 2.0",
						"url": "http://www.apache.org/licenses/LICENSE-2.0.html"
					},
					"version": "1.2.3"
				}
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
		Info info = openApi.getInfo();

		assertEquals("Full Info Test API", info.getTitle());
		assertEquals("A comprehensive API for testing", info.getDescription());
		assertEquals("http://example.com/terms", info.getTermsOfService());
		assertEquals("1.2.3", info.getVersion());

		// Contact
		Contact contact = info.getContact();
		assertNotNull(contact);
		assertEquals("API Support Team", contact.getName());
		assertEquals("http://example.com/support", contact.getUrl());
		assertEquals("support@example.com", contact.getEmail());

		// License
		License license = info.getLicense();
		assertNotNull(license);
		assertEquals("Apache 2.0", license.getName());
		assertEquals("http://www.apache.org/licenses/LICENSE-2.0.html", license.getUrl());
	}

	@Test
	@DisplayName("deserializes minimal info (title and version only)")
	void deserializesMinimalInfo() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": {
					"title": "Minimal API",
					"version": "1.0.0"
				}
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
		Info info = openApi.getInfo();

		assertEquals("Minimal API", info.getTitle());
		assertEquals("1.0.0", info.getVersion());
		assertNull(info.getDescription());
		assertNull(info.getTermsOfService());
		assertNull(info.getContact());
		assertNull(info.getLicense());
	}

	@Test
	@DisplayName("deserializes info with partial contact")
	void deserializesPartialContact() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": {
					"title": "Partial Contact API",
					"version": "1.0.0",
					"contact": {
						"email": "api@example.com"
					}
				}
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		OpenAPI openApi = (OpenAPI) resource.getContents().get(0);
		Contact contact = openApi.getInfo().getContact();

		assertNotNull(contact);
		assertEquals("api@example.com", contact.getEmail());
		assertNull(contact.getName());
		assertNull(contact.getUrl());
	}

	@Test
	@DisplayName("deserializes info with license name only")
	void deserializesLicenseNameOnly() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": {
					"title": "License Test",
					"version": "1.0.0",
					"license": {
						"name": "MIT"
					}
				}
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		License license = ((OpenAPI) resource.getContents().get(0)).getInfo().getLicense();

		assertNotNull(license);
		assertEquals("MIT", license.getName());
		assertNull(license.getUrl());
	}

	@Test
	@DisplayName("round-trips complete info")
	void roundTripsCompleteInfo() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": {
					"title": "Round-trip API",
					"description": "Testing round-trip",
					"termsOfService": "http://example.com/tos",
					"contact": {
						"name": "Support",
						"url": "http://support.example.com",
						"email": "help@example.com"
					},
					"license": {
						"name": "GPL-3.0",
						"url": "https://www.gnu.org/licenses/gpl-3.0.html"
					},
					"version": "2.0.0"
				}
			}
			""";

		// Load
		OpenApiResourceImpl resource1 = createResource();
		resource1.load(toInputStream(json), loadOptions());

		// Save
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource1.save(out, null);

		// Reload
		OpenApiResourceImpl resource2 = createResource();
		resource2.load(toInputStream(out.toString(StandardCharsets.UTF_8)), loadOptions());

		OpenAPI openApi = (OpenAPI) resource2.getContents().get(0);
		Info info = openApi.getInfo();

		assertEquals("Round-trip API", info.getTitle());
		assertEquals("Testing round-trip", info.getDescription());
		assertEquals("http://example.com/tos", info.getTermsOfService());
		assertEquals("2.0.0", info.getVersion());

		Contact contact = info.getContact();
		assertNotNull(contact);
		assertEquals("Support", contact.getName());
		assertEquals("http://support.example.com", contact.getUrl());
		assertEquals("help@example.com", contact.getEmail());

		License license = info.getLicense();
		assertNotNull(license);
		assertEquals("GPL-3.0", license.getName());
		assertEquals("https://www.gnu.org/licenses/gpl-3.0.html", license.getUrl());
	}

	@Test
	@DisplayName("handles multiline description")
	void handlesMultilineDescription() throws IOException {
		String json = """
			{
				"openapi": "3.0.3",
				"info": {
					"title": "Multiline Test",
					"description": "This is a long description.\\nIt spans multiple lines.\\n\\nWith paragraphs too.",
					"version": "1.0.0"
				}
			}
			""";

		OpenApiResourceImpl resource = createResource();
		resource.load(toInputStream(json), loadOptions());

		Info info = ((OpenAPI) resource.getContents().get(0)).getInfo();
		assertEquals("This is a long description.\nIt spans multiple lines.\n\nWith paragraphs too.",
				info.getDescription());
	}

	private OpenApiResourceImpl createResource() {
		Resource.Factory factory = new OpenApiResourceFactoryImpl();
		return (OpenApiResourceImpl) factory.createResource(URI.createURI("test://openapi.json"));
	}

	private Map<String, Object> loadOptions() {
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		return options;
	}

	private ByteArrayInputStream toInputStream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}
}
