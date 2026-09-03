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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.prefix.CodecPrefixReader;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriter;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.emf.osgi.helper.EcoreHelper;
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
 * The whiteboard side of prefix readers/writers (issue #193 / #198, spec 14-custom-values.md
 * §13.7): handler services carrying {@code codec.prefix.key} land in the shared
 * {@link CodecPrefixRegistry}, and a resource created by the JSON factory writes the key.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("Prefix registry whiteboard")
public class PrefixRegistryOSGiTest {

    private static final String ECORE = "/org/eclipse/fennec/codec/osgi/tests/example-customvalue.ecore";

    @InjectService
    CodecPrefixRegistry registry;

    @InjectService(filter = "(emf.contentType=application/json)")
    Resource.Factory jsonFactory;

    private BundleContext ctx;
    private EcoreHelper ecoreHelper;
    private EPackage pkg;
    private ServiceRegistration<EPackage> pkgReg;
    private ServiceRegistration<?> reg1;
    private ServiceRegistration<?> reg2;

    @BeforeEach
    void setUp(@InjectBundleContext BundleContext ctx) throws IOException {
        this.ctx = ctx;
        ecoreHelper = new EcoreHelper();
        pkg = ecoreHelper.loadEcore(ECORE, PrefixRegistryOSGiTest.class);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
        pkgReg = ctx.registerService(EPackage.class, pkg, null);
    }

    @AfterEach
    void tearDown() {
        for (ServiceRegistration<?> r : new ServiceRegistration<?>[] { reg1, reg2 }) {
            if (r != null) {
                try {
                    r.unregister();
                } catch (IllegalStateException alreadyGone) {
                    // fine
                }
            }
        }
        reg1 = reg2 = null;
        pkgReg.unregister();
        EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
        ecoreHelper.releaseAll();
        await(() -> !registry.hasWriter("_tenant") && !registry.hasWriter("_owner") && !registry.hasReader("_tenant"));
    }

    @Test
    @DisplayName("a writer service with two keys is registered under both, and leaves with the service")
    void writerServiceWithTwoKeys() {
        CodecPrefixWriter writer = (key, object, c) -> false;
        reg1 = ctx.registerService(CodecPrefixWriter.class, writer, keys("_tenant", "_owner"));

        await(() -> registry.hasWriter("_tenant") && registry.hasWriter("_owner"));
        assertSame(writer, registry.getWriter("_tenant").orElseThrow());
        assertSame(writer, registry.getWriter("_owner").orElseThrow());

        reg1.unregister();
        reg1 = null;
        await(() -> !registry.hasWriter("_tenant") && !registry.hasWriter("_owner"));
    }

    @Test
    @DisplayName("a service without the codec.prefix.key property is not registered")
    void serviceWithoutKeyIsIgnored() {
        List<String> before = registry.writerKeys();
        reg1 = ctx.registerService(CodecPrefixWriter.class, (CodecPrefixWriter) (k, o, c) -> false, null);

        pause();
        assertEquals(before, registry.writerKeys(), "nothing to register it under");
    }

    @Test
    @DisplayName("a second service for a taken key is ignored, the first stays")
    void duplicateKeyFirstStays() {
        CodecPrefixWriter first = (key, object, c) -> false;
        CodecPrefixWriter second = (key, object, c) -> true;
        reg1 = ctx.registerService(CodecPrefixWriter.class, first, keys("_tenant"));
        await(() -> registry.hasWriter("_tenant"));

        reg2 = ctx.registerService(CodecPrefixWriter.class, second, keys("_tenant"));
        pause();

        assertSame(first, registry.getWriter("_tenant").orElseThrow(), "no first-wins-by-ranking, the first registration stands");
    }

    @Test
    @DisplayName("a resource from the JSON factory writes the key of a whiteboard writer and reads it back with a whiteboard reader")
    void factoryResourceUsesTheRegistry() throws IOException {
        CodecPrefixWriter writer = (key, object, c) -> {
            c.getGenerator().writeName(key);
            c.getGenerator().writeString("t1");
            return true;
        };
        List<String> seen = new java.util.concurrent.CopyOnWriteArrayList<>();
        CodecPrefixReader reader = (key, target, c) -> seen.add(key + "=" + c.getParser().getString());
        reg1 = ctx.registerService(CodecPrefixWriter.class, writer, keys("_tenant"));
        reg2 = ctx.registerService(CodecPrefixReader.class, reader, keys("_tenant"));
        await(() -> registry.hasWriter("_tenant") && registry.hasReader("_tenant"));

        EClass eventClass = EcoreHelper.getEClass(pkg, "Event");
        EObject event = pkg.getEFactoryInstance().create(eventClass);
        event.eSet((EAttribute) EcoreHelper.getFeature(eventClass, "eventId"), "e1");
        event.eSet((EAttribute) EcoreHelper.getFeature(eventClass, "title"), "Hello");

        Resource saving = jsonFactory.createResource(URI.createURI("test://prefix.json"));
        assertInstanceOf(CodecResource.class, saving);
        saving.getContents().add(event);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saving.save(out, null);
        String json = out.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("\"_tenant\":\"t1\""), "the whiteboard writer ran: " + json);

        Resource loading = jsonFactory.createResource(URI.createURI("test://prefix2.json"));
        loading.load(new java.io.ByteArrayInputStream(out.toByteArray()), null);
        assertEquals(List.of("_tenant=t1"), seen);
        assertTrue(loading.getWarnings().isEmpty(), "a key with a reader is known: " + loading.getWarnings());
        assertFalse(loading.getContents().isEmpty());
    }

    private static Dictionary<String, Object> keys(String... keys) {
        Dictionary<String, Object> props = new Hashtable<>();
        props.put(CodecPrefixRegistry.SERVICE_PROPERTY_KEY, keys.length == 1 ? keys[0] : keys);
        return props;
    }

    /** DS binds dynamic references asynchronously in some frameworks; give it a moment. */
    private static void await(BooleanSupplier condition) {
        long deadline = System.currentTimeMillis() + 5_000;
        while (!condition.getAsBoolean() && System.currentTimeMillis() < deadline) {
            pause();
        }
        assertTrue(condition.getAsBoolean(), "condition not met within 5s");
    }

    private static void pause() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
