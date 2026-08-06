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
package org.eclipse.fennec.codec.tests.tck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Abstract TCK for supertype serialization tests.
 */
public abstract class AbstractSuperTypeTCK {

    private static final String TEST_ECORE = "/org/eclipse/fennec/codec/tests/tck/test-tck-supertype.ecore";

    private EcoreHelper ecoreHelper;
    private EPackage testPackage;
    private MetadataWhiteboard metadataService;

    private EClass companyClass;
    private EClass baseEntityClass;
    private EClass employeeClass;
    private EClass managerClass;
    private EReference staffRef;

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    @BeforeEach
    void setUp() throws IOException {
        ecoreHelper = new EcoreHelper();
        testPackage = ecoreHelper.loadEcore(TEST_ECORE, AbstractSuperTypeTCK.class);
        EPackage.Registry.INSTANCE.put(testPackage.getNsURI(), testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);

        companyClass = EcoreHelper.getEClass(testPackage, "Company");
        baseEntityClass = EcoreHelper.getEClass(testPackage, "BaseEntity");
        employeeClass = EcoreHelper.getEClass(testPackage, "Employee");
        managerClass = EcoreHelper.getEClass(testPackage, "Manager");
        staffRef = (EReference) EcoreHelper.getFeature(companyClass, "staff");
    }

    @AfterEach
    void tearDown() {
        EPackage.Registry.INSTANCE.remove(testPackage.getNsURI());
        ecoreHelper.releaseAll();
    }

    @Test
    @DisplayName("supertype serialization with ALL strategy")
    @SuppressWarnings("unchecked")
    void superTypeSerializeAll() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.builder()
                .serializeSuperTypes(true)
                .build();

        EObject company = testPackage.getEFactoryInstance().create(companyClass);
        company.eSet(EcoreHelper.getFeature(companyClass, "name"), "Acme Corp");

        EObject manager = testPackage.getEFactoryInstance().create(managerClass);
        manager.eSet(EcoreHelper.getFeature(baseEntityClass, "id"), "mgr-1");
        manager.eSet(EcoreHelper.getFeature(baseEntityClass, "name"), "Alice");
        manager.eSet(EcoreHelper.getFeature(employeeClass, "department"), "Engineering");
        manager.eSet(EcoreHelper.getFeature(managerClass, "level"), 3);

        ((List<EObject>) company.eGet(staffRef)).add(manager);

        EObject loaded = roundTrip(company, companyClass, config);

        assertNotNull(loaded);
        assertEquals("Acme Corp", loaded.eGet(EcoreHelper.getFeature(companyClass, "name")));

        List<EObject> loadedStaff = (List<EObject>) loaded.eGet(staffRef);
        assertEquals(1, loadedStaff.size());

        EObject loadedManager = loadedStaff.get(0);
        assertTrue(managerClass.isInstance(loadedManager), "Should be Manager type");
        assertEquals("mgr-1", loadedManager.eGet(EcoreHelper.getFeature(baseEntityClass, "id")));
        assertEquals("Alice", loadedManager.eGet(EcoreHelper.getFeature(baseEntityClass, "name")));
        assertEquals("Engineering", loadedManager.eGet(EcoreHelper.getFeature(employeeClass, "department")));
        assertEquals(3, loadedManager.eGet(EcoreHelper.getFeature(managerClass, "level")));
    }

    @Test
    @DisplayName("supertype disabled by default still preserves polymorphic type")
    @SuppressWarnings("unchecked")
    void superTypeDisabledByDefault() throws IOException {
        ConfigurationResolver config = ConfigurationResolver.defaults();

        EObject company = testPackage.getEFactoryInstance().create(companyClass);
        company.eSet(EcoreHelper.getFeature(companyClass, "name"), "Beta Inc");

        EObject employee = testPackage.getEFactoryInstance().create(employeeClass);
        employee.eSet(EcoreHelper.getFeature(baseEntityClass, "id"), "emp-1");
        employee.eSet(EcoreHelper.getFeature(baseEntityClass, "name"), "Bob");
        employee.eSet(EcoreHelper.getFeature(employeeClass, "department"), "Sales");

        ((List<EObject>) company.eGet(staffRef)).add(employee);

        EObject loaded = roundTrip(company, companyClass, config);

        assertNotNull(loaded);
        List<EObject> loadedStaff = (List<EObject>) loaded.eGet(staffRef);
        assertEquals(1, loadedStaff.size());

        EObject loadedEmployee = loadedStaff.get(0);
        assertTrue(employeeClass.isInstance(loadedEmployee), "Should be Employee type");
        assertEquals("emp-1", loadedEmployee.eGet(EcoreHelper.getFeature(baseEntityClass, "id")));
        assertEquals("Bob", loadedEmployee.eGet(EcoreHelper.getFeature(baseEntityClass, "name")));
        assertEquals("Sales", loadedEmployee.eGet(EcoreHelper.getFeature(employeeClass, "department")));
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private CodecResource createResource(ConfigurationResolver config) {
        return new CodecResource(
                URI.createURI("test://supertype." + getFileExtension()),
                metadataService, config,
                null, null, createFormatProvider());
    }

    private EObject roundTrip(EObject object, EClass rootEClass, ConfigurationResolver config) throws IOException {
        CodecResource saveResource = createResource(config);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, Collections.emptyMap());

        CodecResource loadResource = createResource(config);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootEClass);
        loadResource.load(new ByteArrayInputStream(out.toByteArray()), options);
        assertNoDiagnostics(loadResource);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    /**
     * Fails when a round trip reported problems (issue #131).
     * <p>
     * Deserialization catches, logs and continues, so a load succeeds even when a value was
     * dropped. The diagnostics are the only trace - a test that ignores them cannot tell a
     * clean round trip from a lossy one.
     * </p>
     */
    private static void assertNoDiagnostics(CodecResource resource) {
        assertTrue(resource.getErrors().isEmpty(),
                "round trip reported errors: " + resource.getErrors());
        assertTrue(resource.getWarnings().isEmpty(),
                "round trip reported warnings: " + resource.getWarnings());
    }
}
