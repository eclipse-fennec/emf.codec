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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.fennec.codec.deser.DeserializationState.UnresolvedReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for proxy creation during reference resolution.
 *
 * @see <a href="docs/codec-v2-spec/07-reference.md#8-deserialization">Spec: Reference Deserialization</a>
 */
@DisplayName("Proxy Creation")
class ProxyCreationTest {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/ser/test-serialization.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;

    // EClasses
    private EClass personClass;

    // EAttributes
    private EAttribute nameAttribute;

    // EReferences
    private EReference managerRef;       // non-containment, single
    private EReference colleaguesRef;    // non-containment, multi

    private MetadataService metadataService;
    private CodecResource resource;

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, ProxyCreationTest.class);

        personClass = EcoreHelper.getEClass(testPackage, "Person");

        nameAttribute = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        managerRef = (EReference) EcoreHelper.getFeature(personClass, "manager");
        colleaguesRef = (EReference) EcoreHelper.getFeature(personClass, "colleagues");

        metadataService = mock(MetadataService.class);
        resource = new CodecResource(URI.createURI("test://test.json"), metadataService, null, null);
    }

    @AfterEach
    void tearDown() {
        ecoreHelper.releaseAll();
    }

    private EObject createPerson(String name) {
        EObject person = testPackage.getEFactoryInstance().create(personClass);
        person.eSet(nameAttribute, name);
        return person;
    }

    @Nested
    @DisplayName("UnresolvedReference.getEffectiveType")
    class EffectiveTypeTests {

        @Test
        @DisplayName("returns explicit type when provided")
        void returnsExplicitTypeWhenProvided() {
            EObject source = createPerson("John");
            UnresolvedReference unresolved = new UnresolvedReference(
                    source, managerRef, "other.json#//@person", -1, personClass);

            assertEquals(personClass, unresolved.getEffectiveType());
        }

        @Test
        @DisplayName("falls back to reference type when no explicit type")
        void fallsBackToReferenceType() {
            EObject source = createPerson("John");
            UnresolvedReference unresolved = new UnresolvedReference(
                    source, managerRef, "other.json#//@person", -1);

            assertEquals(personClass, unresolved.getEffectiveType());
        }

        @Test
        @DisplayName("getTargetType returns null when not specified")
        void getTargetTypeReturnsNull() {
            EObject source = createPerson("John");
            UnresolvedReference unresolved = new UnresolvedReference(
                    source, managerRef, "other.json#//@person", -1);

            assertNull(unresolved.getTargetType());
        }

        @Test
        @DisplayName("getTargetType returns explicit type when specified")
        void getTargetTypeReturnsExplicit() {
            EObject source = createPerson("John");
            UnresolvedReference unresolved = new UnresolvedReference(
                    source, managerRef, "other.json#//@person", -1, personClass);

            assertEquals(personClass, unresolved.getTargetType());
        }
    }

    @Nested
    @DisplayName("Proxy creation for single-valued references")
    class SingleValuedProxyTests {

        @Test
        @DisplayName("creates proxy when reference cannot be resolved")
        void createsProxyWhenUnresolved() {
            EObject person = createPerson("John");
            resource.getContents().add(person);

            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, managerRef, "other.json#//@manager", -1));

            invokeResolveReferences(resource, unresolvedRefs);

            EObject manager = (EObject) person.eGet(managerRef);
            assertNotNull(manager, "Manager should not be null");
            assertTrue(manager.eIsProxy(), "Manager should be a proxy");
        }

        @Test
        @DisplayName("proxy has correct URI")
        void proxyHasCorrectUri() {
            EObject person = createPerson("John");
            resource.getContents().add(person);

            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, managerRef, "other.json#//@manager", -1));

            invokeResolveReferences(resource, unresolvedRefs);

            EObject manager = (EObject) person.eGet(managerRef);
            assertTrue(manager instanceof InternalEObject);

            URI proxyUri = ((InternalEObject) manager).eProxyURI();
            assertNotNull(proxyUri);
            assertTrue(proxyUri.toString().contains("other.json"));
            assertTrue(proxyUri.toString().contains("//@manager"));
        }

        @Test
        @DisplayName("proxy has correct type")
        void proxyHasCorrectType() {
            EObject person = createPerson("John");
            resource.getContents().add(person);

            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, managerRef, "other.json#//@manager", -1));

            invokeResolveReferences(resource, unresolvedRefs);

            EObject manager = (EObject) person.eGet(managerRef);
            assertEquals(personClass, manager.eClass());
        }
    }

    @Nested
    @DisplayName("Proxy creation for multi-valued references")
    class MultiValuedProxyTests {

        @Test
        @DisplayName("creates proxies for multi-valued references")
        void createsProxiesForMultiValued() {
            EObject person = createPerson("John");
            resource.getContents().add(person);

            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, colleaguesRef, "other.json#//@colleague1", 0));
            unresolvedRefs.add(new UnresolvedReference(
                    person, colleaguesRef, "other.json#//@colleague2", 1));

            invokeResolveReferences(resource, unresolvedRefs);

            @SuppressWarnings("unchecked")
            List<EObject> colleagues = (List<EObject>) person.eGet(colleaguesRef);
            assertEquals(2, colleagues.size());
            assertTrue(colleagues.get(0).eIsProxy());
            assertTrue(colleagues.get(1).eIsProxy());
        }
    }

    @Nested
    @DisplayName("Resolution before proxy creation")
    class ResolutionPriorityTests {

        @Test
        @DisplayName("resolves reference when target exists in same resource")
        void resolvesWhenTargetExists() {
            EObject person = createPerson("John");
            EObject manager = createPerson("Boss");
            resource.getContents().add(person);
            resource.getContents().add(manager);

            String fragment = resource.getURIFragment(manager);

            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, managerRef, fragment, -1));

            invokeResolveReferences(resource, unresolvedRefs);

            EObject resolvedManager = (EObject) person.eGet(managerRef);
            assertNotNull(resolvedManager);
            assertFalse(resolvedManager.eIsProxy(), "Should resolve, not create proxy");
            assertEquals(manager, resolvedManager);
        }
    }

    @Nested
    @DisplayName("Proxy creation mechanism")
    class ProxyCreationMechanismTests {

        @Test
        @DisplayName("creates new object instance, not resolved from elsewhere")
        void createsNewObjectInstance() {
            EObject person = createPerson("John");
            resource.getContents().add(person);

            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, managerRef, "external.json#//@manager", -1));

            invokeResolveReferences(resource, unresolvedRefs);

            EObject proxy = (EObject) person.eGet(managerRef);

            assertNotNull(proxy);
            assertFalse(resource.getContents().contains(proxy),
                    "Proxy should not be added to resource contents");
            assertNull(proxy.eResource(),
                    "Proxy should not have a resource assigned");
        }

        @Test
        @DisplayName("proxy is instanceof InternalEObject with eProxyURI set")
        void proxyIsInternalEObjectWithUriSet() {
            EObject person = createPerson("John");
            resource.getContents().add(person);

            String targetUri = "external.json#//@manager";
            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, managerRef, targetUri, -1));

            invokeResolveReferences(resource, unresolvedRefs);

            EObject proxy = (EObject) person.eGet(managerRef);

            assertNotNull(proxy, "Proxy should be created");
            assertTrue(proxy instanceof InternalEObject,
                    "Proxy must be instanceof InternalEObject");
            assertTrue(proxy.eIsProxy(),
                    "eIsProxy() must return true");

            InternalEObject internalProxy = (InternalEObject) proxy;
            URI proxyUri = internalProxy.eProxyURI();
            assertNotNull(proxyUri, "eProxyURI() must not be null");
            assertTrue(proxyUri.toString().contains("external.json"),
                    "Proxy URI should contain 'external.json', was: " + proxyUri);
            assertEquals("//@manager", proxyUri.fragment());
        }

        @Test
        @DisplayName("proxy is created using EcoreUtil.create with correct EClass")
        void proxyCreatedWithCorrectEClass() {
            EObject person = createPerson("John");
            resource.getContents().add(person);

            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, managerRef, "external.json#//@manager", -1));

            invokeResolveReferences(resource, unresolvedRefs);

            EObject proxy = (EObject) person.eGet(managerRef);

            assertNotNull(proxy);
            assertEquals(personClass, proxy.eClass(),
                    "Proxy should be created with the reference type's EClass");
            assertEquals(testPackage, proxy.eClass().getEPackage(),
                    "Proxy's EClass should belong to the correct EPackage");
        }

        @Test
        @DisplayName("proxy is not resolved - accessing features does not trigger resolution")
        void proxyNotAutoResolved() {
            EObject person = createPerson("John");
            resource.getContents().add(person);

            List<UnresolvedReference> unresolvedRefs = new ArrayList<>();
            unresolvedRefs.add(new UnresolvedReference(
                    person, managerRef, "nonexistent.json#//@manager", -1));

            invokeResolveReferences(resource, unresolvedRefs);

            EObject proxy = (EObject) person.eGet(managerRef);

            assertTrue(proxy.eIsProxy(), "Proxy should remain unresolved");

            assertTrue(proxy instanceof InternalEObject);
            assertNotNull(((InternalEObject) proxy).eProxyURI());
        }
    }

    /**
     * Invokes the private resolveReferences method using reflection.
     */
    private void invokeResolveReferences(CodecResource resource, List<UnresolvedReference> unresolvedRefs) {
        try {
            Class<?> policyClass = Class.forName(
                    "org.eclipse.fennec.codec.resource.ReferenceUriPolicy");
            Method from = policyClass.getDeclaredMethod("from", ConfigurationResolver.class);
            from.setAccessible(true);
            Object defaultPolicy = from.invoke(null, ConfigurationResolver.defaults());

            Method method = CodecResource.class.getDeclaredMethod(
                    "resolveReferences", List.class, DiagnosticCollector.class, policyClass);
            method.setAccessible(true);
            method.invoke(resource, unresolvedRefs, new DiagnosticCollector(), defaultPolicy);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke resolveReferences", e);
        }
    }
}
