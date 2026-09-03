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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.prefix.CodecPrefixReader;
import org.eclipse.fennec.codec.prefix.CodecPrefixReaderContext;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriter;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriterContext;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;

/**
 * TCK for prefix readers/writers (issue #193 / #199, spec 14-custom-values.md §13): the matrix
 * reader/writer × registered/absent, unknown keys, clashes, several keys and writers, options,
 * STRUCTURED and {@code idOnTop} variants - format-agnostic, so it runs against every
 * {@link CodecFormatProvider}. Field positions are not asserted here (a binary format has no
 * observable order from outside); the JSON tests in the codec bundle pin them.
 * <p>
 * Model: {@code Order(id, orderNo, items: Item[*])}, {@code Item(id, label, tenant)} - the
 * {@code tenant} feature exists so a prefix key can clash with a feature name.
 * </p>
 */
public abstract class AbstractPrefixTCK {

    private static final String NS_URI = "urn:codec:tck:prefix";

    protected abstract CodecFormatProvider<?, ?> createFormatProvider();

    protected abstract String getFileExtension();

    private EPackage testPackage;
    private MetadataWhiteboard metadataService;
    private EClass orderClass;
    private EClass itemClass;
    private EAttribute orderId;
    private EAttribute orderNo;
    private EAttribute itemId;
    private EAttribute itemLabel;
    private EAttribute itemTenant;
    private EReference items;

    @BeforeEach
    void setUpModel() {
        EcoreFactory f = EcoreFactory.eINSTANCE;
        itemClass = f.createEClass();
        itemClass.setName("Item");
        itemId = attribute(f, itemClass, "id", true);
        itemLabel = attribute(f, itemClass, "label", false);
        itemTenant = attribute(f, itemClass, "tenant", false);

        orderClass = f.createEClass();
        orderClass.setName("Order");
        orderId = attribute(f, orderClass, "id", true);
        orderNo = attribute(f, orderClass, "orderNo", false);
        items = f.createEReference();
        items.setName("items");
        items.setEType(itemClass);
        items.setContainment(true);
        items.setUpperBound(-1);
        orderClass.getEStructuralFeatures().add(items);

        testPackage = f.createEPackage();
        testPackage.setName("prefixtck");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("prefixtck");
        testPackage.getEClassifiers().add(itemClass);
        testPackage.getEClassifiers().add(orderClass);
        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(testPackage);
        EPackage.Registry.INSTANCE.put(NS_URI, testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    @AfterEach
    void tearDownModel() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    // ------------------------------------------------------------------ the matrix

    @Test
    @DisplayName("reader and writer: write → read → write reproduces the document, the reader saw every key")
    void readerAndWriter() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecPrefixRegistry registry = pair(standardWriter(), reader);

        byte[] first = save(registry, null, order("o1", "i1", "i2"), Map.of());
        CodecResource loaded = resource(registry, null);
        EObject root = load(loaded, first, Map.of());
        byte[] second = save(registry, null, root, Map.of());

        assertArrayEquals(first, second, "the document survives the round trip byte for byte");
        assertEquals(List.of("_owner@Item=o1", "_owner@Item=o1", "_tenant@Item=t1", "_tenant@Item=t1", "_tenant@Order=t1"),
                reader.sorted());
        assertTrue(warnings(loaded).isEmpty(), "known keys are not reported: " + warnings(loaded));
    }

    @Test
    @DisplayName("writer only: the keys are written, and unknown on read - a warning, a failure under strictOnUnknown")
    void writerOnly() throws IOException {
        byte[] document = save(pair(standardWriter(), null), null, order("o1", "i1"), Map.of());

        CodecResource lenient = resource(new CodecPrefixRegistry(), null);
        EObject root = load(lenient, document, Map.of());

        assertNotNull(root);
        assertEquals("i1", ((EObject) ((List<?>) root.eGet(items)).get(0)).eGet(itemId), "the model is intact");
        List<String> unknown = warnings(lenient).stream().filter(w -> w.contains("Unknown feature")).toList();
        assertTrue(unknown.stream().anyMatch(w -> w.contains("'_tenant'")), "was: " + unknown);
        assertTrue(unknown.stream().anyMatch(w -> w.contains("'_owner'")), "was: " + unknown);

        CodecResource strict = resource(new CodecPrefixRegistry(), strictOnUnknown());
        assertThrows(Exception.class, () -> load(strict, document, Map.of()));
    }

    @Test
    @DisplayName("reader only: a foreign document with the keys loads silently, also under strictOnUnknown, and saving it writes none")
    void readerOnly() throws IOException {
        byte[] foreign = save(pair(standardWriter(), null), null, order("o1", "i1", "i2"), Map.of());
        RecordingReader reader = new RecordingReader();
        CodecPrefixRegistry readerOnly = pair(null, reader);

        CodecResource loaded = resource(readerOnly, strictOnUnknown());
        EObject root = load(loaded, foreign, Map.of());

        assertEquals(5, reader.seen.size(), reader.seen.toString());
        assertTrue(warnings(loaded).isEmpty() && errors(loaded).isEmpty(),
                "was: " + warnings(loaded) + " / " + errors(loaded));

        byte[] written = save(readerOnly, null, root, Map.of());
        CodecResource check = resource(new CodecPrefixRegistry(), strictOnUnknown());
        load(check, written, Map.of());
        assertTrue(warnings(check).isEmpty(), "nothing registered for writing, so no prefix keys: " + warnings(check));
    }

    @Test
    @DisplayName("neither: an empty registry and no registry produce the same document and read the same")
    void neither() throws IOException {
        byte[] withEmpty = save(new CodecPrefixRegistry(), null, order("o1", "i1"), Map.of());
        byte[] withNone = save(null, null, order("o1", "i1"), Map.of());

        assertArrayEquals(withEmpty, withNone);
        CodecResource loaded = resource(null, strictOnUnknown());
        assertNotNull(load(loaded, withNone, Map.of()));
        assertTrue(warnings(loaded).isEmpty());
    }

    // ------------------------------------------------------------------ variations

    @Test
    @DisplayName("several writers under several keys, one of them declining for the root")
    void severalWritersSeveralKeys() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecPrefixRegistry registry = pair(standardWriter(), reader);
        registry.register("_region", new KeyedWriter(Map.of("_region", o -> "eu")));
        registry.register("_region", reader);

        byte[] document = save(registry, null, order("o1", "i1", "i2"), Map.of());
        load(resource(registry, null), document, Map.of());

        assertEquals(List.of("_owner@Item=o1", "_owner@Item=o1", "_region@Item=eu", "_region@Item=eu",
                "_region@Order=eu", "_tenant@Item=t1", "_tenant@Item=t1", "_tenant@Order=t1"), reader.sorted(),
                "_owner declined for the root, everything else on every object");
    }

    @Test
    @DisplayName("an unknown key beside registered ones: only the unknown one is reported")
    void unknownKeyBesideRegisteredOnes() throws IOException {
        RecordingReader reader = new RecordingReader();
        CodecPrefixRegistry registry = pair(standardWriter(), reader);
        CodecPrefixWriter stray = new KeyedWriter(Map.of("_stray", o -> "s"));

        byte[] document = save(registry, null, order("o1", "i1"),
                Map.of(CodecOptions.CODEC_PREFIX_WRITER_INSTANCES, Map.of("_stray", stray)));
        CodecResource loaded = resource(registry, null);
        load(loaded, document, Map.of());

        List<String> unknown = warnings(loaded).stream().filter(w -> w.contains("Unknown feature")).toList();
        assertEquals(2, unknown.size(), "root and item carry _stray, nothing else is unknown: " + warnings(loaded));
        assertTrue(unknown.stream().allMatch(w -> w.contains("'_stray'")), unknown.toString());
        assertEquals(3, reader.seen.size(), reader.seen.toString());
    }

    @Test
    @DisplayName("a prefix key equal to a feature name: the feature wins on write and on read, one warning per class")
    void featureWinsTheClash() throws IOException {
        // write side: the writer would put "PREFIX" under 'tenant'; the feature carries "feat-<id>"
        CodecPrefixWriter clashing = new KeyedWriter(Map.of("tenant", o -> "PREFIX", "_owner", o -> "x"));
        CodecResource saving = resource(pair(clashing, null, "tenant", "_owner"), null);
        saving.getContents().add(order("o1", "i1", "i2"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        saving.save(out, Map.of());
        assertEquals(1, warnings(saving).stream().filter(w -> w.contains("'tenant'") && w.contains("Item")).count(),
                "once per class, not per item: " + warnings(saving));

        EObject plain = load(resource(new CodecPrefixRegistry(), null), out.toByteArray(), Map.of());
        for (Object o : (List<?>) plain.eGet(items)) {
            EObject item = (EObject) o;
            assertEquals("feat-" + item.eGet(itemId), item.eGet(itemTenant), "the feature value was written");
        }

        // read side: a reader under 'tenant' is skipped, the feature is set
        RecordingReader reader = new RecordingReader();
        CodecResource reading = resource(pair(null, reader, "tenant", "_owner"), null);
        EObject root = load(reading, out.toByteArray(), Map.of());
        for (Object o : (List<?>) root.eGet(items)) {
            EObject item = (EObject) o;
            assertEquals("feat-" + item.eGet(itemId), item.eGet(itemTenant));
        }
        // Order has no 'tenant' feature: there the key is a plain prefix key and is read as such
        assertTrue(reader.seen.stream().noneMatch(s -> s.startsWith("tenant@Item")), reader.seen.toString());
        assertEquals(List.of("_owner@Item=x", "_owner@Item=x", "_owner@Order=x", "tenant@Order=PREFIX"), reader.sorted());
        assertEquals(1, warnings(reading).stream().filter(w -> w.contains("'tenant'") && w.contains("Item")).count(),
                warnings(reading).toString());
    }

    @Test
    @DisplayName("instances bound through the options beat the registry for their key, on both sides")
    void optionInstancesBeatTheRegistry() throws IOException {
        RecordingReader registryReader = new RecordingReader();
        RecordingReader optionReader = new RecordingReader();
        CodecPrefixRegistry registry = pair(standardWriter(), registryReader);
        CodecPrefixWriter optionWriter = new KeyedWriter(Map.of("_tenant", o -> "opt"));

        byte[] document = save(registry, null, order("o1", "i1"),
                Map.of(CodecOptions.CODEC_PREFIX_WRITER_INSTANCES, Map.of("_tenant", optionWriter)));
        load(resource(registry, null), document,
                Map.of(CodecOptions.CODEC_PREFIX_READER_INSTANCES, Map.of("_tenant", optionReader)));

        assertEquals(List.of("_tenant@Item=opt", "_tenant@Order=opt"), optionReader.sorted());
        assertEquals(List.of("_owner@Item=o1"), registryReader.seen, "keys not overridden stay with the registry");
    }

    @Test
    @DisplayName("STRUCTURED type/id and idOnTop, both ways: the round trip holds in every combination")
    void structuredAndIdOnTopVariants() throws IOException {
        for (boolean idOnTop : new boolean[] { true, false }) {
            for (String format : new String[] { "PLAIN", "STRUCTURED" }) {
                ConfigurationResolver resolver = ConfigurationResolver.builder()
                        .moduleProperties(Map.of("idOnTop", idOnTop, "typeFormat", format, "idFormat", format))
                        .build();
                RecordingReader reader = new RecordingReader();
                CodecPrefixRegistry registry = pair(standardWriter(), reader);
                String variant = "idOnTop=" + idOnTop + ", format=" + format;

                byte[] first = save(registry, resolver, order("o1", "i1"), Map.of());
                CodecResource loaded = resource(registry, resolver);
                EObject root = load(loaded, first, Map.of());
                byte[] second = save(registry, resolver, root, Map.of());

                assertArrayEquals(first, second, variant);
                assertEquals(List.of("_owner@Item=o1", "_tenant@Item=t1", "_tenant@Order=t1"), reader.sorted(), variant);
                assertTrue(warnings(loaded).isEmpty(), variant + ": " + warnings(loaded));
            }
        }
    }

    @Test
    @DisplayName("a reader that throws: lenient reports an error and reads on, STRICT fails the load")
    void readerThrows() throws IOException {
        byte[] document = save(pair(standardWriter(), null), null, order("o1", "i1"), Map.of());
        CodecPrefixReader broken = (key, target, ctx) -> {
            if ("_owner".equals(key)) {
                throw new IOException("boom");
            }
            ctx.getParser().skipChildren();
        };

        CodecResource lenient = resource(pair(null, broken), null);
        EObject root = load(lenient, document, Map.of());
        assertNotNull(root);
        assertEquals("label-i1", ((EObject) ((List<?>) root.eGet(items)).get(0)).eGet(itemLabel), "the rest is read");
        assertTrue(errors(lenient).stream().anyMatch(e -> e.contains("_owner") && e.contains("boom")), errors(lenient).toString());

        CodecResource strict = resource(pair(null, broken), null);
        assertThrows(Exception.class,
                () -> load(strict, document, Map.of(CodecOptions.CODEC_DESERIALIZATION_MODE, "STRICT")));
    }

    // ------------------------------------------------------------------ helpers

    private static EAttribute attribute(EcoreFactory f, EClass owner, String name, boolean id) {
        EAttribute a = f.createEAttribute();
        a.setName(name);
        a.setEType(EcorePackage.Literals.ESTRING);
        a.setID(id);
        owner.getEStructuralFeatures().add(a);
        return a;
    }

    private EObject order(String id, String... itemIds) {
        EObject order = testPackage.getEFactoryInstance().create(orderClass);
        order.eSet(orderId, id);
        order.eSet(orderNo, "no-" + id);
        for (String itemIdValue : itemIds) {
            EObject item = testPackage.getEFactoryInstance().create(itemClass);
            item.eSet(itemId, itemIdValue);
            item.eSet(itemLabel, "label-" + itemIdValue);
            item.eSet(itemTenant, "feat-" + itemIdValue);
            @SuppressWarnings("unchecked")
            List<EObject> list = (List<EObject>) order.eGet(items);
            list.add(item);
        }
        return order;
    }

    private static String idOf(EObject object) {
        EAttribute idAttr = object.eClass().getEIDAttribute();
        return idAttr == null ? null : String.valueOf(object.eGet(idAttr));
    }

    /** Writes a string per key, or declines when the function yields null. */
    protected static final class KeyedWriter implements CodecPrefixWriter {
        private final Map<String, Function<EObject, String>> values;

        protected KeyedWriter(Map<String, Function<EObject, String>> values) {
            this.values = values;
        }

        @Override
        public boolean write(String key, EObject object, CodecPrefixWriterContext ctx) throws IOException {
            Function<EObject, String> fn = values.get(key);
            String value = fn == null ? null : fn.apply(object);
            if (value == null) {
                return false;
            }
            ctx.getGenerator().writeName(key);
            ctx.getGenerator().writeString(value);
            return true;
        }
    }

    /** {@code _tenant} everywhere, {@code _owner} for contained objects only. */
    private static KeyedWriter standardWriter() {
        return new KeyedWriter(Map.of(
                "_tenant", o -> "t1",
                "_owner", o -> o.eContainer() == null ? null : idOf(o.eContainer())));
    }

    /** Records {@code key@TargetClass=value}; the target exists but is not necessarily complete. */
    protected static final class RecordingReader implements CodecPrefixReader {
        final List<String> seen = new ArrayList<>();

        @Override
        public void read(String key, EObject target, CodecPrefixReaderContext ctx) throws IOException {
            JsonNode value = ctx.getJacksonContext().readTree(ctx.getParser());
            seen.add(key + "@" + target.eClass().getName() + "="
                    + (value.isString() ? value.stringValue() : value.toString()));
        }

        List<String> sorted() {
            return seen.stream().sorted().toList();
        }
    }

    private static CodecPrefixRegistry pair(CodecPrefixWriter writer, CodecPrefixReader reader) {
        return pair(writer, reader, "_tenant", "_owner");
    }

    private static CodecPrefixRegistry pair(CodecPrefixWriter writer, CodecPrefixReader reader, String... keys) {
        CodecPrefixRegistry registry = new CodecPrefixRegistry();
        for (String key : keys) {
            if (writer != null) {
                registry.register(key, writer);
            }
            if (reader != null) {
                registry.register(key, reader);
            }
        }
        return registry;
    }

    private static ConfigurationResolver strictOnUnknown() {
        return ConfigurationResolver.builder().moduleProperties(Map.of("strictOnUnknown", true)).build();
    }

    private CodecResource resource(CodecPrefixRegistry registry, ConfigurationResolver resolver) {
        return new CodecResource(URI.createURI("test://prefix." + getFileExtension()), metadataService,
                resolver != null ? resolver : ConfigurationResolver.defaults(), null, registry, null,
                createFormatProvider(), null);
    }

    private byte[] save(CodecPrefixRegistry registry, ConfigurationResolver resolver, EObject root,
            Map<String, Object> options) throws IOException {
        CodecResource resource = resource(registry, resolver);
        resource.getContents().add(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toByteArray();
    }

    private EObject load(CodecResource resource, byte[] document, Map<String, Object> options) throws IOException {
        resource.load(new ByteArrayInputStream(document), options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    private static List<String> warnings(CodecResource resource) {
        List<String> messages = new ArrayList<>();
        resource.getWarnings().forEach(d -> messages.add(d.getMessage()));
        return messages;
    }

    private static List<String> errors(CodecResource resource) {
        List<String> messages = new ArrayList<>();
        resource.getErrors().forEach(d -> messages.add(d.getMessage()));
        return messages;
    }
}
