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
package org.eclipse.fennec.codec.util;

import java.io.IOException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.metadata.service.MetadataServiceImpl;
import org.eclipse.fennec.model.metadata.utils.EcoreHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for {@link CodecResourceHelper} tests.
 * <p>
 * Provides common setup and teardown for loading test ecore model
 * and initializing the helper with a MetadataService.
 * </p>
 */
abstract class CodecResourceHelperTestBase {

    protected static final String TEST_ECORE = "test-resource-helper.ecore";

    protected MetadataWhiteboard metadataService;
    protected CodecResourceHelper helper;
    protected EcoreHelper ecoreHelper;
    protected EPackage testPackage;
    protected EClass personClass;
    protected EClass employeeClass;
    protected EClass addressClass;
    protected EClass abstractEntityClass;
    protected EClass namedInterface;

    @SuppressWarnings("restriction")
	@BeforeEach
    void setUp() throws IOException {
        metadataService = new MetadataServiceImpl();
        helper = new CodecResourceHelper(metadataService);

        ecoreHelper = new EcoreHelper(CodecResourceHelperTestBase.class);
        testPackage = ecoreHelper.loadEcore(TEST_ECORE);

        personClass = ecoreHelper.getEClass(testPackage, "Person");
        employeeClass = ecoreHelper.getEClass(testPackage, "Employee");
        addressClass = ecoreHelper.getEClass(testPackage, "Address");
        abstractEntityClass = ecoreHelper.getEClass(testPackage, "AbstractEntity");
        namedInterface = ecoreHelper.getEClass(testPackage, "Named");

        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDown() {
        ecoreHelper.releaseAll();
    }
}
