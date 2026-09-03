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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import org.eclipse.fennec.codec.prefix.CodecPrefixReader;
import org.eclipse.fennec.codec.prefix.CodecPrefixRegistry;
import org.eclipse.fennec.codec.prefix.CodecPrefixWriter;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Shared model, handlers and helpers for the prefix reader/writer tests (issue #193).
 * <p>
 * Model: {@code Order(id, orderNo, items: Item[*], archived: Item[*])},
 * {@code Item(id, label, tenant)} - {@code tenant} exists so a prefix key can clash with a feature.
 * </p>
 */
abstract class PrefixTestSupport {

    static final String NS_URI = "urn:codec:prefix:test";
    static final JsonMapper JSON = JsonMapper.builder().build();

    EPackage testPackage;
    MetadataWhiteboard metadataService;
    EClass orderClass;
    EClass itemClass;
    EAttribute orderId;
    EAttribute orderNo;
    EAttribute itemId;
    EAttribute itemLabel;
    EAttribute itemTenant;
    EReference items;
    EReference archived;

    void buildModel() {
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
        items = containment(f, orderClass, "items");
        archived = containment(f, orderClass, "archived");

        testPackage = f.createEPackage();
        testPackage.setName("prefixtest");
        testPackage.setNsURI(NS_URI);
        testPackage.setNsPrefix("prefixtest");
        testPackage.getEClassifiers().add(itemClass);
        testPackage.getEClassifiers().add(orderClass);

        new ResourceImpl(URI.createURI(NS_URI)).getContents().add(testPackage);
        EPackage.Registry.INSTANCE.put(NS_URI, testPackage);

        metadataService = MetadataServiceFactory.create();
        metadataService.registerPackage(testPackage);
    }

    void releaseModel() {
        EPackage.Registry.INSTANCE.remove(NS_URI);
    }

    private static EAttribute attribute(EcoreFactory f, EClass owner, String name, boolean id) {
        EAttribute a = f.createEAttribute();
        a.setName(name);
        a.setEType(EcorePackage.Literals.ESTRING);
        a.setID(id);
        owner.getEStructuralFeatures().add(a);
        return a;
    }

    private EReference containment(EcoreFactory f, EClass owner, String name) {
        EReference r = f.createEReference();
        r.setName(name);
        r.setEType(itemClass);
        r.setContainment(true);
        r.setUpperBound(-1);
        owner.getEStructuralFeatures().add(r);
        return r;
    }

    // ------------------------------------------------------------------ objects

    EObject order(String id, String no, String... itemIds) {
        EObject order = testPackage.getEFactoryInstance().create(orderClass);
        order.eSet(orderId, id);
        order.eSet(orderNo, no);
        for (String itemIdValue : itemIds) {
            @SuppressWarnings("unchecked")
            List<EObject> list = (List<EObject>) order.eGet(items);
            list.add(item(itemIdValue, "label-" + itemIdValue));
        }
        return order;
    }

    EObject item(String id, String label) {
        EObject item = testPackage.getEFactoryInstance().create(itemClass);
        item.eSet(itemId, id);
        item.eSet(itemLabel, label);
        item.eSet(itemTenant, "feat-" + id);
        return item;
    }

    static String idOf(EObject object) {
        EAttribute idAttr = object.eClass().getEIDAttribute();
        return idAttr == null ? null : String.valueOf(object.eGet(idAttr));
    }

    // ------------------------------------------------------------------ handlers

    /** Writes a string value per key, or declines when the function yields null. */
    static final class KeyedWriter implements CodecPrefixWriter {
        private final Map<String, Function<EObject, String>> values;

        KeyedWriter(Map<String, Function<EObject, String>> values) {
            this.values = values;
        }

        @Override
        public boolean write(String key, EObject object, org.eclipse.fennec.codec.prefix.CodecPrefixWriterContext ctx)
                throws IOException {
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

    /** The two standard keys: {@code _tenant} everywhere, {@code _owner} for contained objects only. */
    static KeyedWriter standardWriter() {
        return new KeyedWriter(Map.of(
                "_tenant", o -> "t1",
                "_owner", o -> o.eContainer() == null ? null : idOf(o.eContainer())));
    }

    /**
     * Records {@code key@TargetClass=value} and the target itself. The target exists when the
     * reader runs, but is not necessarily complete: a prefix key met before {@code _id} is
     * replayed as soon as the type is known, so the id is not part of the record.
     */
    static final class RecordingReader implements CodecPrefixReader {
        final List<String> seen = new ArrayList<>();
        final List<EObject> targets = new ArrayList<>();

        @Override
        public void read(String key, EObject target, org.eclipse.fennec.codec.prefix.CodecPrefixReaderContext ctx)
                throws IOException {
            JsonNode value = ctx.getJacksonContext().readTree(ctx.getParser());
            seen.add(key + "@" + target.eClass().getName() + "="
                    + (value.isString() ? value.stringValue() : value.toString()));
            targets.add(target);
        }

        /** The records in a stable order - deferred replay order is not document order. */
        List<String> sorted() {
            return seen.stream().sorted().toList();
        }
    }

    static CodecPrefixRegistry registryWith(CodecPrefixWriter writer, CodecPrefixReader reader, String... keys) {
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

    // ------------------------------------------------------------------ resources

    CodecResource resource(CodecPrefixRegistry registry, ConfigurationResolver resolver) {
        return new CodecResource(URI.createURI("test://prefix.json"), metadataService,
                resolver != null ? resolver : ConfigurationResolver.defaults(), null, registry, null, null, null);
    }

    String save(CodecResource resource, EObject root, Map<String, Object> options) throws IOException {
        resource.getContents().add(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resource.save(out, options);
        return out.toString(StandardCharsets.UTF_8);
    }

    EObject load(CodecResource resource, String json, Map<String, Object> options) throws IOException {
        resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);
        return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
    }

    static JsonNode tree(String json) {
        return JSON.readTree(json);
    }

    static List<String> keys(JsonNode object) {
        return new ArrayList<>(object.propertyNames());
    }

    static int pos(JsonNode object, String key) {
        return keys(object).indexOf(key);
    }

    static List<String> warnings(CodecResource resource) {
        List<String> messages = new ArrayList<>();
        resource.getWarnings().forEach(d -> messages.add(d.getMessage()));
        return messages;
    }

    static List<String> errors(CodecResource resource) {
        List<String> messages = new ArrayList<>();
        resource.getErrors().forEach(d -> messages.add(d.getMessage()));
        return messages;
    }

    static String type(String className) {
        return "\"_type\":\"" + NS_URI + "#//" + className + "\"";
    }
}
