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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.bson.BsonFormatProvider;
import org.eclipse.fennec.codec.cbor.CborFormatProvider;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
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
 * OSGi integration tests for format providers: JSON, YAML, CBOR, BSON.
 *
 * @see <a href="docs/codec-v2-spec/17-format-abstraction.md">Spec: Format Abstraction</a>
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("Format Provider OSGi Examples")
public class FormatProviderExample {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-basic.ecore";

    @InjectService
    MetadataService metadataService;

    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;

    private EClass personClass;
    private EAttribute personId;
    private EAttribute nameAttr;
    private EAttribute ageAttr;

    @BeforeEach
    public void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, FormatProviderExample.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        pkgReg = ctx.registerService(EPackage.class, pkg, null);

        personClass = EcoreHelper.getEClass(pkg, "Person");
        personId = (EAttribute) EcoreHelper.getFeature(personClass, "personId");
        nameAttr = (EAttribute) EcoreHelper.getFeature(personClass, "name");
        ageAttr = (EAttribute) EcoreHelper.getFeature(personClass, "age");
    }

    @AfterEach
    public void tearDown() {
        pkgReg.unregister();
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
    }

    private EObject createPerson() {
        EObject person = pkg.getEFactoryInstance().create(personClass);
        person.eSet(personId, "p1");
        person.eSet(nameAttr, "Alice");
        person.eSet(ageAttr, 30);
        return person;
    }

    private EObject roundTrip(EObject object, CodecFormatProvider<?, ?> provider) throws IOException {
        String ext = provider != null ? provider.getFileExtensions()[0] : "json";
        CodecResource saveResource = new CodecResource(
                URI.createURI("test://format." + ext), metadataService,
                ConfigurationResolver.defaults(), null, null, provider);
        saveResource.getContents().add(object);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saveResource.save(out, null);
        byte[] bytes = out.toByteArray();

        CodecResource loadResource = new CodecResource(
                URI.createURI("test://format." + ext), metadataService,
                ConfigurationResolver.defaults(), null, null, provider);
        Map<String, Object> options = new HashMap<>();
        options.put(CodecResource.CODEC_ROOT_TYPE, personClass);
        loadResource.load(new ByteArrayInputStream(bytes), options);

        return loadResource.getContents().isEmpty() ? null : loadResource.getContents().get(0);
    }

    @Test
    @DisplayName("JSON default format — null provider")
    void jsonDefaultFormat() throws IOException {
        EObject person = createPerson();
        EObject loaded = roundTrip(person, null);

        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
        assertEquals(30, loaded.eGet(ageAttr));
    }

    @Test
    @DisplayName("YAML format — YamlFormatProvider")
    void yamlFormat() throws IOException {
        EObject person = createPerson();
        EObject loaded = roundTrip(person, new YamlFormatProvider());

        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
        assertEquals(30, loaded.eGet(ageAttr));
    }

    @Test
    @DisplayName("CBOR format — CborFormatProvider")
    void cborFormat() throws IOException {
        EObject person = createPerson();
        EObject loaded = roundTrip(person, new CborFormatProvider());

        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
        assertEquals(30, loaded.eGet(ageAttr));
    }

    @Test
    @DisplayName("BSON format — BsonFormatProvider")
    void bsonFormat() throws IOException {
        EObject person = createPerson();
        EObject loaded = roundTrip(person, new BsonFormatProvider());

        assertNotNull(loaded);
        assertEquals("Alice", loaded.eGet(nameAttr));
        assertEquals(30, loaded.eGet(ageAttr));
    }
}
