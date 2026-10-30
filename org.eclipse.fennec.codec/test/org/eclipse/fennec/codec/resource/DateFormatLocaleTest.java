/*
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("codec.dateFormat does not depend on the JVM default locale (issue #287)")
class DateFormatLocaleTest {

    private static final String NS_URI = "http://test.example.org/dateformat/1.0";
    private static final String ZONE_NAME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'zzz";
    /** 2026-01-07T11:07:08 in Berlin, which is CET in English and MEZ in German. */
    private static final Date WINTER_MORNING = new Date(1_767_780_428_000L);

    private Locale defaultLocale;
    private TimeZone defaultZone;
    private EPackage stampPackage;
    private EClass stampClass;
    private EAttribute at;
    private MetadataWhiteboard metadataService;

    @BeforeEach
    void germanJvmInBerlin() {
        defaultLocale = Locale.getDefault();
        defaultZone = TimeZone.getDefault();
        Locale.setDefault(Locale.GERMANY);
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));

        stampPackage = EcoreFactory.eINSTANCE.createEPackage();
        stampPackage.setName("dateformat");
        stampPackage.setNsURI(NS_URI);
        stampPackage.setNsPrefix("df");
        stampClass = EcoreFactory.eINSTANCE.createEClass();
        stampClass.setName("Stamp");
        stampPackage.getEClassifiers().add(stampClass);
        at = EcoreFactory.eINSTANCE.createEAttribute();
        at.setName("at");
        at.setEType(EcorePackage.Literals.EDATE);
        stampClass.getEStructuralFeatures().add(at);
        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(stampPackage);

        EPackage.Registry.INSTANCE.put(NS_URI, stampPackage);
        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(stampPackage);
    }

    @AfterEach
    void restoreJvmDefaults() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
        Locale.setDefault(defaultLocale);
        TimeZone.setDefault(defaultZone);
    }

    @Test
    @DisplayName("writes English zone names under a German default locale")
    void writesEnglishZoneNames() throws IOException {
        EObject stamp = stampPackage.getEFactoryInstance().create(stampClass);
        stamp.eSet(at, WINTER_MORNING);
        CodecResource resource = resource();
        resource.getContents().add(stamp);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        resource.save(out, Map.of(CodecOptions.CODEC_DATE_FORMAT, ZONE_NAME_PATTERN));

        String json = out.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("2026-01-07T11:07:08.000ZCET"), json);
    }

    @Test
    @DisplayName("reads English zone names under a German default locale")
    void readsEnglishZoneNames() throws IOException {
        // a zone other than the JVM default: EMF's own patterns would read 05:07 as Berlin time
        String json = """
            {"_type": "%s#//Stamp", "at": "2026-01-07T05:07:08.000ZEST"}
            """.formatted(NS_URI);
        CodecResource resource = resource();

        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
                Map.of(CodecOptions.CODEC_DATE_FORMAT, ZONE_NAME_PATTERN));

        assertTrue(resource.getErrors().isEmpty(), resource.getErrors().toString());
        assertEquals(WINTER_MORNING, resource.getContents().get(0).eGet(at));
    }

    private CodecResource resource() {
        return new CodecResource(URI.createURI("test://dateformat.json"), metadataService,
                ConfigurationResolver.defaults(), null);
    }
}
