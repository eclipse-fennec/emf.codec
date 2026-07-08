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
package org.eclipse.fennec.codec.osgi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * OSGi integration tests for basic round-trip serialization.
 * <p>
 * Mirrors {@code org.eclipse.fennec.codec.examples.BasicRoundTripExample} but
 * uses OSGi service injection instead of manual wiring:
 * <ul>
 *   <li>The {@link MetadataService} is injected automatically by DS (via
 *       {@code MetadataServiceComponent}).</li>
 *   <li>The test's {@link EPackage} is registered as an OSGi service in
 *       {@code setUp}, so the {@code MetadataServiceComponent} whiteboard picks
 *       it up and applies the {@code CodecAspectProviderComponent} automatically
 *       — no manual {@code registerPackage()} call is needed.</li>
 * </ul>
 *
 * @see <a href="docs/codec-v2-spec/11-feature.md">Spec: Feature Serialization</a>
 * @see <a href="docs/codec-v2-spec/09-id.md">Spec: ID Serialization</a>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("Basic Round-Trip OSGi Examples")
public class ListOfListExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-listoflist.ecore";
    
    @InjectService
    MetadataService metadataService;

    @InjectService(filter = ("(emf.fileExtension=json)"))
    ResourceSet resourceSet;  

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;

    private EClass configClass;

    private EAttribute configInputsAttr;
   

    @BeforeEach
    public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, ListOfListExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        // Registering as OSGi service triggers MetadataServiceComponent.addEPackage()
        // which applies CodecAspectProviderComponent automatically.
        pkgReg = ctx.registerService(EPackage.class, pkg, null);

        configClass = EcoreHelper.getEClass(pkg, "Config");
        configInputsAttr = (EAttribute) EcoreHelper.getFeature(configClass, "inputs");
    }

    @AfterEach
    public void tearDown() {
        pkgReg.unregister();
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    // ========================================================================
    // Round-trip helper
    // ========================================================================

    private EObject roundTrip(EObject object, EClass rootType) throws IOException {
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://emap.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://emap.json"), metadataService,
                ConfigurationResolver.defaults(), null);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, rootType);
        loadResource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    @Test
    @DisplayName("List of List round-trip")
    @SuppressWarnings("unchecked")
    void listOfListRoundTrip() throws IOException {
        EObject config = pkg.getEFactoryInstance().create(configClass);
        String[] innerList1 = new String[] {"A", "B", "C"};
        String[] innerList2 = new String[] {"AA", "BB", "CC"};
        String[] innerList3 = new String[] {"AAA", "BBB", "CCC"};
      
        List<String[]> outerList = new ArrayList<>(2);
        outerList.add(innerList1);
        outerList.add(innerList2);
        outerList.add(innerList3);
        config.eSet(configInputsAttr, outerList);

        EObject loaded = roundTrip(config, configClass);

        assertNotNull(loaded);
        List<String[]> loadedOuterList = (List<String[]>) loaded.eGet(configInputsAttr);
        assertEquals(3, loadedOuterList.size(), "Loaded outer list should contain 3 elements");
        assertEquals(3, loadedOuterList.get(0).length, "Loaded outer list should contain a list of 3 elements at place 0");
        assertEquals(3, loadedOuterList.get(1).length, "Loaded outer list should contain a list of 3 elements at place 1");
        assertEquals(3, loadedOuterList.get(2).length, "Loaded outer list should contain a list of 3 elements at place 2");  
    }
}
