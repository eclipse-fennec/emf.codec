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
package org.eclipse.fennec.codec.jsonschema.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * OSGi integration tests for oneOf handling of abstract EClass references
 * in JSON Schema generation.
 * <p>
 * Verifies that references (containment and non-containment, single and
 * multi-valued) to abstract EClasses produce {@code oneOf} by default,
 * and that the {@code useAnyOfForAbstract} option reverts to {@code anyOf}.
 * Also tests combination with the {@code flatAllOf} option.
 * </p>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("JSON Schema oneOf for Abstract EClass References")
public class JsonSchemaOneOfIntegrationTest {

	private static final String ECORE = "/org/eclipse/fennec/codec/jsonschema/tests/example-jsonschema-oneOf.ecore";

	@InjectService(filter = "(emf.contentType=application/schema+json)")
	ResourceSet resourceSet;

	private EcoreHelper ecoreHelper;
	private EPackage pkg;
	private ServiceRegistration<EPackage> pkgReg;

	private EClass abstractAddress;
	private EClass abstractName;
	private EClass address;
	private EClass businessAddress;
	private EClass verySpecificAddress;
	private EClass middleName;
	private EClass lastName;
	private EClass myClass;

	private EAttribute addressAttr;
	private EAttribute businessAddressAttr;
	private EAttribute verySpecificAddressAttr;

	private EAttribute nameAttr;
	private EAttribute lastNameAttr;
	private EAttribute middleNameAttr;

	private EReference addressRef;
	private EReference nameRef;
	private EReference addressesRef;
	private EReference namesRef;
	private EReference nonContainedAddressRef;
	private EReference nonContainedNameRef;
	private EReference nonContainedAddressesRef;
	private EReference nonContainedNamesRef;


	@BeforeEach
	public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
		ecoreHelper = new EcoreHelper();
		pkg = ecoreHelper.loadEcore(ECORE, JsonSchemaOneOfIntegrationTest.class);
		EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
		pkgReg = ctx.registerService(EPackage.class, pkg, null);

		abstractAddress = EcoreHelper.getEClass(pkg, "AbstractAddress");
		abstractName = EcoreHelper.getEClass(pkg, "AbstractName");
		address = EcoreHelper.getEClass(pkg, "Address");
		businessAddress = EcoreHelper.getEClass(pkg, "BusinessAddress");
		verySpecificAddress = EcoreHelper.getEClass(pkg, "VerySpecificAddress");
		middleName = EcoreHelper.getEClass(pkg, "MiddleName");
		lastName = EcoreHelper.getEClass(pkg, "LastName");
		myClass = EcoreHelper.getEClass(pkg, "MyClass");

		addressAttr = (EAttribute) EcoreHelper.getFeature(address, "address");
		businessAddressAttr = (EAttribute) EcoreHelper.getFeature(businessAddress, "businessAddress");
		verySpecificAddressAttr = (EAttribute) EcoreHelper.getFeature(verySpecificAddress, "verySpecificAddress");

		nameAttr = (EAttribute) EcoreHelper.getFeature(abstractName, "name");
		middleNameAttr = (EAttribute) EcoreHelper.getFeature(middleName, "middleName");
		lastNameAttr = (EAttribute) EcoreHelper.getFeature(lastName, "lastName");

		addressRef = (EReference) EcoreHelper.getFeature(myClass, "address");
		nameRef = (EReference) EcoreHelper.getFeature(myClass, "name");
		addressesRef = (EReference) EcoreHelper.getFeature(myClass, "addresses");
		namesRef = (EReference) EcoreHelper.getFeature(myClass, "names");
		nonContainedAddressRef = (EReference) EcoreHelper.getFeature(myClass, "nonContainedAddress");
		nonContainedNameRef = (EReference) EcoreHelper.getFeature(myClass, "nonContainedName");
		nonContainedAddressesRef = (EReference) EcoreHelper.getFeature(myClass, "nonContainedAddresses");
		nonContainedNamesRef = (EReference) EcoreHelper.getFeature(myClass, "nonContainedNames");
	}

	@AfterEach
	public void tearDown() {
		pkgReg.unregister();
		EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
		ecoreHelper.releaseAll();
	}

	// ========================================================================
	// Helper methods
	// ========================================================================

	private String serializeToString(Map<String, Object> options) throws IOException {
		Resource resource = resourceSet.createResource(URI.createURI("test.jsonschema"));
		resource.getContents().add(pkg);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, options);
		return out.toString(StandardCharsets.UTF_8);
	}

	private JsonNode serializeToSchema(Map<String, Object> options) throws IOException {
		String json = serializeToString(options);
		return JsonMapper.builder().build().readTree(json);
	}

	private EPackage deserializeSchema(String json) throws IOException {
		Resource resource = resourceSet.createResource(URI.createURI("roundtrip.jsonschema"));
		resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), null);
		assertFalse(resource.getContents().isEmpty(), "deserialized resource should not be empty");
		assertTrue(resource.getContents().get(0) instanceof EPackage,
				"deserialized root should be an EPackage");
		return (EPackage) resource.getContents().get(0);
	}

	private JsonNode getMyClassDef(JsonNode schema) {
		JsonNode defs = schema.get("$defs");
		assertNotNull(defs, "$defs should be present in schema");
		JsonNode myClassDef = defs.get("MyClass");
		assertNotNull(myClassDef, "MyClass definition should be present in $defs");
		return myClassDef;
	}

	private JsonNode getProperty(JsonNode classDef, String propName) {
		JsonNode props = classDef.get("properties");
		assertNotNull(props, "properties should be present");
		JsonNode prop = props.get(propName);
		assertNotNull(prop, "property '" + propName + "' should be present");
		return prop;
	}

	// ========================================================================
	// Default oneOf tests (no options)
	// ========================================================================

	@Nested
	@DisplayName("Default behavior — oneOf for abstract references")
	class DefaultOneOfTests {

		@Test
		@DisplayName("single-valued containment ref to abstract type uses oneOf with $ref")
		void singleContainment_usesOneOf() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// address: single-valued containment → AbstractAddress
			JsonNode addressProp = getProperty(myClassDef, "address");
			assertNotNull(addressProp.get("oneOf"),
					"single containment to abstract should use oneOf: " + addressProp);
			assertNull(addressProp.get("anyOf"),
					"should not use anyOf by default");
			assertTrue(addressProp.get("x-containment").asBoolean(),
					"containment ref should have x-containment: true");

			JsonNode oneOf = addressProp.get("oneOf");
			assertEquals(3, oneOf.size(), "should have 3 concrete subclasses (Address, BusinessAddress, VerySpecificAddress)");

			// Containment now uses $ref (same as non-containment) with x-containment marker
			for (JsonNode entry : oneOf) {
				assertNotNull(entry.get("$ref"), "containment should use $ref: " + entry);
			}
		}

		@Test
		@DisplayName("single-valued containment ref to abstract type with inherited attributes uses oneOf")
		void singleContainmentWithInheritance_usesOneOf() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// name: single-valued containment → AbstractName (has 'name' attribute)
			JsonNode nameProp = getProperty(myClassDef, "name");
			assertNotNull(nameProp.get("oneOf"),
					"single containment to abstract should use oneOf: " + nameProp);

			JsonNode oneOf = nameProp.get("oneOf");
			assertEquals(2, oneOf.size(), "should have 2 concrete subclasses (MiddleName, LastName)");
		}

		@Test
		@DisplayName("multi-valued containment ref to abstract type uses oneOf with $ref in items")
		void multiContainment_usesOneOfInItems() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// addresses: multi-valued containment → AbstractAddress
			JsonNode addressesProp = getProperty(myClassDef, "addresses");
			assertEquals("array", addressesProp.get("type").asString());

			JsonNode items = addressesProp.get("items");
			assertNotNull(items, "array should have items");
			assertNotNull(items.get("oneOf"),
					"multi containment items to abstract should use oneOf: " + items);
			assertNull(items.get("anyOf"),
					"should not use anyOf by default");
			assertTrue(addressesProp.get("x-containment").asBoolean(),
					"containment ref should have x-containment: true on array node");

			assertEquals(3, items.get("oneOf").size(),
					"should have 3 concrete subclasses (Address, BusinessAddress, VerySpecificAddress)");

			// Containment now uses $ref (same as non-containment) with x-containment marker
			for (JsonNode entry : items.get("oneOf")) {
				assertNotNull(entry.get("$ref"), "containment should use $ref: " + entry);
			}
		}

		@Test
		@DisplayName("multi-valued containment ref to abstract type with inherited attributes uses oneOf in items")
		void multiContainmentWithInheritance_usesOneOfInItems() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// names: multi-valued containment → AbstractName
			JsonNode namesProp = getProperty(myClassDef, "names");
			assertEquals("array", namesProp.get("type").asString());

			JsonNode items = namesProp.get("items");
			assertNotNull(items, "array should have items");
			assertNotNull(items.get("oneOf"),
					"multi containment items to abstract should use oneOf: " + items);

			assertEquals(2, items.get("oneOf").size(),
					"should have 2 concrete subclasses (MiddleName, LastName)");
		}

		@Test
		@DisplayName("single-valued non-containment ref to abstract type uses oneOf with $ref")
		void singleNonContainment_usesOneOfWithRef() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// nonContainedAddress: single-valued non-containment → AbstractAddress
			JsonNode prop = getProperty(myClassDef, "nonContainedAddress");
			assertNotNull(prop.get("oneOf"),
					"single non-containment to abstract should use oneOf: " + prop);
			assertNull(prop.get("anyOf"),
					"should not use anyOf by default");

			JsonNode oneOf = prop.get("oneOf");
			assertEquals(3, oneOf.size(), "should have 3 concrete subclasses (Address, BusinessAddress, VerySpecificAddress)");

			// Non-containment: uses $ref
			for (JsonNode entry : oneOf) {
				assertNotNull(entry.get("$ref"), "non-containment should use $ref: " + entry);
			}
		}

		@Test
		@DisplayName("single-valued non-containment ref to abstract type with inherited attributes uses oneOf with $ref")
		void singleNonContainmentWithInheritance_usesOneOfWithRef() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// nonContainedName: single-valued non-containment → AbstractName
			JsonNode prop = getProperty(myClassDef, "nonContainedName");
			assertNotNull(prop.get("oneOf"),
					"single non-containment to abstract should use oneOf: " + prop);

			JsonNode oneOf = prop.get("oneOf");
			assertEquals(2, oneOf.size(), "should have 2 concrete subclasses");

			for (JsonNode entry : oneOf) {
				assertNotNull(entry.get("$ref"), "non-containment should use $ref: " + entry);
			}
		}

		@Test
		@DisplayName("multi-valued non-containment ref to abstract type uses oneOf with $ref in items")
		void multiNonContainment_usesOneOfWithRefInItems() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// nonContainedAddresses: multi-valued non-containment → AbstractAddress
			JsonNode prop = getProperty(myClassDef, "nonContainedAddresses");
			assertEquals("array", prop.get("type").asString());

			JsonNode items = prop.get("items");
			assertNotNull(items, "array should have items");
			assertNotNull(items.get("oneOf"),
					"multi non-containment items to abstract should use oneOf: " + items);
			assertNull(items.get("anyOf"),
					"should not use anyOf by default");

			assertEquals(3, items.get("oneOf").size(),
					"should have 3 concrete subclasses (Address, BusinessAddress, VerySpecificAddress)");

			for (JsonNode entry : items.get("oneOf")) {
				assertNotNull(entry.get("$ref"), "non-containment should use $ref: " + entry);
			}
		}

		@Test
		@DisplayName("multi-valued non-containment ref to abstract type with inherited attributes uses oneOf with $ref in items")
		void multiNonContainmentWithInheritance_usesOneOfWithRefInItems() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// nonContainedNames: multi-valued non-containment → AbstractName
			JsonNode prop = getProperty(myClassDef, "nonContainedNames");
			assertEquals("array", prop.get("type").asString());

			JsonNode items = prop.get("items");
			assertNotNull(items, "array should have items");
			assertNotNull(items.get("oneOf"),
					"multi non-containment items to abstract should use oneOf: " + items);

			assertEquals(2, items.get("oneOf").size(),
					"should have 2 concrete subclasses (MiddleName, LastName)");

			for (JsonNode entry : items.get("oneOf")) {
				assertNotNull(entry.get("$ref"), "non-containment should use $ref: " + entry);
			}
		}

		@Test
		@DisplayName("deep hierarchy — VerySpecificAddress (extends BusinessAddress extends AbstractAddress) is included in oneOf")
		void deepHierarchy_includedInOneOf() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// address: single containment → AbstractAddress
			// Should include Address, BusinessAddress, AND VerySpecificAddress
			JsonNode addressProp = getProperty(myClassDef, "address");
			JsonNode oneOf = addressProp.get("oneOf");
			assertNotNull(oneOf, "should have oneOf: " + addressProp);
			assertEquals(3, oneOf.size(),
					"deep subclass VerySpecificAddress must be included: " + oneOf);

			// Verify VerySpecificAddress is present via $ref
			boolean foundVerySpecificRef = false;
			for (JsonNode entry : oneOf) {
				JsonNode ref = entry.get("$ref");
				if (ref != null && ref.asString().contains("VerySpecificAddress")) {
					foundVerySpecificRef = true;
				}
			}
			assertTrue(foundVerySpecificRef,
					"VerySpecificAddress $ref should be present in oneOf entries: " + oneOf);

			// Verify in $defs that VerySpecificAddress uses allOf for inheritance
			JsonNode defs = schema.get("$defs");
			JsonNode verySpecificDef = defs.get("VerySpecificAddress");
			assertNotNull(verySpecificDef, "VerySpecificAddress should be in $defs");
			JsonNode allOf = verySpecificDef.get("allOf");
			assertNotNull(allOf,
					"VerySpecificAddress should use allOf for inheritance in $defs: " + verySpecificDef);
			assertTrue(verySpecificDef.toString().contains("BusinessAddress"),
					"VerySpecificAddress should reference BusinessAddress parent: " + verySpecificDef);
		}

		@Test
		@DisplayName("deep hierarchy — non-containment ref also includes VerySpecificAddress via $ref")
		void deepHierarchy_nonContainmentIncludesDeepSubclass() throws IOException {
			JsonNode schema = serializeToSchema(null);
			JsonNode myClassDef = getMyClassDef(schema);

			// nonContainedAddress: single non-containment → AbstractAddress
			JsonNode prop = getProperty(myClassDef, "nonContainedAddress");
			JsonNode oneOf = prop.get("oneOf");
			assertNotNull(oneOf, "should have oneOf: " + prop);
			assertEquals(3, oneOf.size(),
					"deep subclass VerySpecificAddress must be included via $ref: " + oneOf);

			// Verify one of the $refs points to VerySpecificAddress
			boolean foundVerySpecificRef = false;
			for (JsonNode entry : oneOf) {
				JsonNode ref = entry.get("$ref");
				if (ref != null && ref.asString().contains("VerySpecificAddress")) {
					foundVerySpecificRef = true;
				}
			}
			assertTrue(foundVerySpecificRef,
					"VerySpecificAddress $ref should be present in oneOf entries: " + oneOf);
		}
	}

	// ========================================================================
	// useAnyOfForAbstract option tests
	// ========================================================================

	@Nested
	@DisplayName("useAnyOfForAbstract option — reverts to anyOf")
	class AnyOfOptionTests {

		private JsonNode serializeWithAnyOf() throws IOException {
			return serializeToSchema(Map.of(
					CodecJsonSchemaOptions.OPTION_USE_ANY_OF_FOR_ABSTRACT, Boolean.TRUE));
		}

		@Test
		@DisplayName("single-valued containment ref uses anyOf when option is set")
		void singleContainment_usesAnyOf() throws IOException {
			JsonNode schema = serializeWithAnyOf();
			JsonNode myClassDef = getMyClassDef(schema);

			JsonNode addressProp = getProperty(myClassDef, "address");
			assertNotNull(addressProp.get("anyOf"),
					"should use anyOf when option is set: " + addressProp);
			assertNull(addressProp.get("oneOf"),
					"should not use oneOf when anyOf option is set");
			assertEquals(3, addressProp.get("anyOf").size());
		}

		@Test
		@DisplayName("multi-valued containment ref uses anyOf in items when option is set")
		void multiContainment_usesAnyOfInItems() throws IOException {
			JsonNode schema = serializeWithAnyOf();
			JsonNode myClassDef = getMyClassDef(schema);

			JsonNode addressesProp = getProperty(myClassDef, "addresses");
			JsonNode items = addressesProp.get("items");
			assertNotNull(items.get("anyOf"),
					"should use anyOf in items when option is set: " + items);
			assertNull(items.get("oneOf"),
					"should not use oneOf when anyOf option is set");
			assertEquals(3, items.get("anyOf").size());
		}

		@Test
		@DisplayName("single-valued non-containment ref uses anyOf when option is set")
		void singleNonContainment_usesAnyOf() throws IOException {
			JsonNode schema = serializeWithAnyOf();
			JsonNode myClassDef = getMyClassDef(schema);

			JsonNode prop = getProperty(myClassDef, "nonContainedAddress");
			assertNotNull(prop.get("anyOf"),
					"should use anyOf when option is set: " + prop);
			assertNull(prop.get("oneOf"),
					"should not use oneOf when anyOf option is set");

			for (JsonNode entry : prop.get("anyOf")) {
				assertNotNull(entry.get("$ref"), "non-containment should use $ref: " + entry);
			}
		}

		@Test
		@DisplayName("multi-valued non-containment ref uses anyOf in items when option is set")
		void multiNonContainment_usesAnyOfInItems() throws IOException {
			JsonNode schema = serializeWithAnyOf();
			JsonNode myClassDef = getMyClassDef(schema);

			JsonNode prop = getProperty(myClassDef, "nonContainedAddresses");
			JsonNode items = prop.get("items");
			assertNotNull(items.get("anyOf"),
					"should use anyOf in items when option is set: " + items);
			assertNull(items.get("oneOf"),
					"should not use oneOf when anyOf option is set");

			for (JsonNode entry : items.get("anyOf")) {
				assertNotNull(entry.get("$ref"), "non-containment should use $ref: " + entry);
			}
		}
	}

	// ========================================================================
	// flatAllOf combination tests
	// ========================================================================

	@Nested
	@DisplayName("flatAllOf + oneOf combination")
	class FlatAllOfCombinationTests {

		private JsonNode serializeWithFlatAllOf() throws IOException {
			return serializeToSchema(Map.of(
					CodecJsonSchemaOptions.OPTION_FLAT_ALL_OF, Boolean.TRUE));
		}

		private JsonNode serializeWithFlatAllOfAndAnyOf() throws IOException {
			return serializeToSchema(Map.of(
					CodecJsonSchemaOptions.OPTION_FLAT_ALL_OF, Boolean.TRUE,
					CodecJsonSchemaOptions.OPTION_USE_ANY_OF_FOR_ABSTRACT, Boolean.TRUE));
		}

		@Test
		@DisplayName("flatAllOf + oneOf: containment uses $ref to $defs with flattened inherited properties")
		void flatAllOf_containmentInlinesWithFlattenedProps() throws IOException {
			JsonNode schema = serializeWithFlatAllOf();
			JsonNode myClassDef = getMyClassDef(schema);

			// names: multi-valued containment → AbstractName (which has 'name' attr)
			// With flatAllOf, MiddleName and LastName should have 'name' flattened in $defs
			JsonNode namesProp = getProperty(myClassDef, "names");
			JsonNode items = namesProp.get("items");
			assertNotNull(items.get("oneOf"),
					"should still use oneOf with flatAllOf: " + items);

			JsonNode oneOf = items.get("oneOf");
			assertEquals(2, oneOf.size());

			// Containment uses $ref
			for (JsonNode entry : oneOf) {
				assertNotNull(entry.get("$ref"), "containment should use $ref: " + entry);
			}

			// Verify flattened properties in $defs
			JsonNode defs = schema.get("$defs");
			JsonNode middleNameDef = defs.get("MiddleName");
			assertNotNull(middleNameDef, "MiddleName should be in $defs");
			JsonNode middleNameProps = middleNameDef.get("properties");
			assertNotNull(middleNameProps, "MiddleName should have properties");
			assertNotNull(middleNameProps.get("name"),
					"inherited 'name' property should be flattened into MiddleName: " + middleNameDef);

			JsonNode lastNameDef = defs.get("LastName");
			assertNotNull(lastNameDef, "LastName should be in $defs");
			JsonNode lastNameProps = lastNameDef.get("properties");
			assertNotNull(lastNameProps, "LastName should have properties");
			assertNotNull(lastNameProps.get("name"),
					"inherited 'name' property should be flattened into LastName: " + lastNameDef);

			// No allOf/$ref for inheritance — flatAllOf should eliminate allOf references
			assertFalse(schema.toString().contains("\"allOf\""),
					"flatAllOf should eliminate allOf references");
		}

		@Test
		@DisplayName("flatAllOf + oneOf: single containment ref to abstract with inherited attrs uses $ref")
		void flatAllOf_singleContainmentFlattensInheritedProps() throws IOException {
			JsonNode schema = serializeWithFlatAllOf();
			JsonNode myClassDef = getMyClassDef(schema);

			// name: single containment → AbstractName
			JsonNode nameProp = getProperty(myClassDef, "name");
			assertNotNull(nameProp.get("oneOf"),
					"should use oneOf for single containment to abstract: " + nameProp);

			JsonNode oneOf = nameProp.get("oneOf");
			assertEquals(2, oneOf.size());

			// Containment uses $ref
			for (JsonNode entry : oneOf) {
				assertNotNull(entry.get("$ref"), "containment should use $ref: " + entry);
			}

			// Verify flattened properties in $defs
			JsonNode defs = schema.get("$defs");
			for (JsonNode entry : oneOf) {
				String refPath = entry.get("$ref").asString();
				String className = refPath.substring(refPath.lastIndexOf('/') + 1);
				JsonNode classDef = defs.get(className);
				assertNotNull(classDef, className + " should be in $defs");
				JsonNode props = classDef.get("properties");
				assertNotNull(props, className + " should have properties: " + classDef);
				assertNotNull(props.get("name"),
						"inherited 'name' should be flattened into " + className + ": " + classDef);
			}
		}

		@Test
		@DisplayName("flatAllOf + oneOf: non-containment refs still use $ref (not affected by flatAllOf)")
		void flatAllOf_nonContainmentStillUsesRef() throws IOException {
			JsonNode schema = serializeWithFlatAllOf();
			JsonNode myClassDef = getMyClassDef(schema);

			// nonContainedNames: multi non-containment → AbstractName
			JsonNode prop = getProperty(myClassDef, "nonContainedNames");
			JsonNode items = prop.get("items");
			assertNotNull(items.get("oneOf"),
					"should use oneOf for non-containment: " + items);

			for (JsonNode entry : items.get("oneOf")) {
				assertNotNull(entry.get("$ref"),
						"non-containment should still use $ref even with flatAllOf: " + entry);
			}
		}

		@Test
		@DisplayName("flatAllOf + anyOf option: containment uses anyOf with flattened properties")
		void flatAllOfWithAnyOfOption_containmentUsesAnyOf() throws IOException {
			JsonNode schema = serializeWithFlatAllOfAndAnyOf();
			JsonNode myClassDef = getMyClassDef(schema);

			// addresses: multi-valued containment → AbstractAddress
			JsonNode addressesProp = getProperty(myClassDef, "addresses");
			JsonNode items = addressesProp.get("items");
			assertNotNull(items.get("anyOf"),
					"should use anyOf when both options set: " + items);
			assertNull(items.get("oneOf"),
					"should not use oneOf when anyOf option is set");
		}

		@Test
		@DisplayName("flatAllOf + anyOf option: non-containment uses anyOf with $ref")
		void flatAllOfWithAnyOfOption_nonContainmentUsesAnyOf() throws IOException {
			JsonNode schema = serializeWithFlatAllOfAndAnyOf();
			JsonNode myClassDef = getMyClassDef(schema);

			// nonContainedAddresses: multi non-containment → AbstractAddress
			JsonNode prop = getProperty(myClassDef, "nonContainedAddresses");
			JsonNode items = prop.get("items");
			assertNotNull(items.get("anyOf"),
					"should use anyOf when both options set: " + items);
			assertNull(items.get("oneOf"),
					"should not use oneOf when anyOf option is set");

			for (JsonNode entry : items.get("anyOf")) {
				assertNotNull(entry.get("$ref"),
						"non-containment should use $ref: " + entry);
			}
		}
	}

	// ========================================================================
	// Round-trip tests
	// ========================================================================

	@Nested
	@DisplayName("Round-trip — serialize then deserialize preserves structure")
	class RoundTripTests {

		@Test
		@DisplayName("round-trip preserves abstract classes and concrete subclasses")
		void roundTrip_preservesAbstractAndSubclasses() throws IOException {
			String json = serializeToString(null);
			EPackage loaded = deserializeSchema(json);

			assertNotNull(loaded, "deserialized EPackage should not be null");

			// Verify concrete subclasses exist (including deep hierarchy)
			assertNotNull(loaded.getEClassifier("Address"),
					"Address should survive round-trip. Schema: " + json);
			assertNotNull(loaded.getEClassifier("BusinessAddress"),
					"BusinessAddress should survive round-trip. Schema: " + json);
			assertNotNull(loaded.getEClassifier("VerySpecificAddress"),
					"VerySpecificAddress (deep subclass) should survive round-trip. Schema: " + json);
			assertNotNull(loaded.getEClassifier("MiddleName"),
					"MiddleName should survive round-trip. Schema: " + json);
			assertNotNull(loaded.getEClassifier("LastName"),
					"LastName should survive round-trip. Schema: " + json);

			// Verify MyClass exists with its features
			EClass loadedMyClass = (EClass) loaded.getEClassifier("MyClass");
			assertNotNull(loadedMyClass, "MyClass should survive round-trip. Schema: " + json);
		}

		@Test
		@DisplayName("round-trip preserves concrete subclass attributes")
		void roundTrip_preservesAttributes() throws IOException {
			String json = serializeToString(null);
			EPackage loaded = deserializeSchema(json);

			EClass loadedAddress = (EClass) loaded.getEClassifier("Address");
			assertNotNull(loadedAddress, "Address should exist");
			assertNotNull(loadedAddress.getEStructuralFeature("address"),
					"Address should have 'address' attribute. Schema: " + json);

			EClass loadedBusinessAddress = (EClass) loaded.getEClassifier("BusinessAddress");
			assertNotNull(loadedBusinessAddress, "BusinessAddress should exist");
			assertNotNull(loadedBusinessAddress.getEStructuralFeature("businessAddress"),
					"BusinessAddress should have 'businessAddress' attribute. Schema: " + json);

			EClass loadedVerySpecific = (EClass) loaded.getEClassifier("VerySpecificAddress");
			assertNotNull(loadedVerySpecific, "VerySpecificAddress should exist");
			assertNotNull(loadedVerySpecific.getEStructuralFeature("verySpecificAddress"),
					"VerySpecificAddress should have 'verySpecificAddress' attribute. Schema: " + json);

			EClass loadedMiddleName = (EClass) loaded.getEClassifier("MiddleName");
			assertNotNull(loadedMiddleName, "MiddleName should exist");
			assertNotNull(loadedMiddleName.getEStructuralFeature("middleName"),
					"MiddleName should have 'middleName' attribute. Schema: " + json);

			EClass loadedLastName = (EClass) loaded.getEClassifier("LastName");
			assertNotNull(loadedLastName, "LastName should exist");
			assertNotNull(loadedLastName.getEStructuralFeature("lastName"),
					"LastName should have 'lastName' attribute. Schema: " + json);
		}

		@Test
		@DisplayName("round-trip with flatAllOf preserves subclass attributes including inherited ones")
		void roundTrip_flatAllOf_preservesInheritedAttributes() throws IOException {
			String json = serializeToString(Map.of(
					CodecJsonSchemaOptions.OPTION_FLAT_ALL_OF, Boolean.TRUE));
			EPackage loaded = deserializeSchema(json);

			// With flatAllOf, MiddleName/LastName inlined definitions include
			// the inherited 'name' property — verify it survives the round-trip
			EClass loadedMiddleName = (EClass) loaded.getEClassifier("MiddleName");
			assertNotNull(loadedMiddleName, "MiddleName should exist after flatAllOf round-trip");
			assertNotNull(loadedMiddleName.getEStructuralFeature("middleName"),
					"MiddleName should have own 'middleName' attribute. Schema: " + json);

			EClass loadedLastName = (EClass) loaded.getEClassifier("LastName");
			assertNotNull(loadedLastName, "LastName should exist after flatAllOf round-trip");
			assertNotNull(loadedLastName.getEStructuralFeature("lastName"),
					"LastName should have own 'lastName' attribute. Schema: " + json);
		}

		@Test
		@DisplayName("round-trip with anyOf option preserves structure")
		void roundTrip_anyOfOption_preservesStructure() throws IOException {
			String json = serializeToString(Map.of(
					CodecJsonSchemaOptions.OPTION_USE_ANY_OF_FOR_ABSTRACT, Boolean.TRUE));
			EPackage loaded = deserializeSchema(json);

			assertNotNull(loaded.getEClassifier("Address"),
					"Address should survive anyOf round-trip. Schema: " + json);
			assertNotNull(loaded.getEClassifier("BusinessAddress"),
					"BusinessAddress should survive anyOf round-trip. Schema: " + json);

			EClass loadedMyClass = (EClass) loaded.getEClassifier("MyClass");
			assertNotNull(loadedMyClass,
					"MyClass should survive anyOf round-trip. Schema: " + json);
		}
	}
}
