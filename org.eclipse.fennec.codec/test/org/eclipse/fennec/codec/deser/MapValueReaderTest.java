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
package org.eclipse.fennec.codec.deser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.AnnotationSources;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.eclipse.fennec.codec.value.ReferenceValueWriter;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The value of a map entry is read and written by the reader / writer its value feature names
 * (issue #319): a JSON Schema map such as {@code components/schemas} holds values that may also be a
 * boolean, which only the schema reader understands.
 */
@DisplayName("Map entry values with a value reader / writer")
class MapValueReaderTest {

	private EPackage pkg;
	private EClass holder;
	private EClass item;
	private EAttribute flag;
	private MetadataWhiteboard metadata;

	@BeforeEach
	void setUp() {
		EcoreFactory f = EcoreFactory.eINSTANCE;
		pkg = f.createEPackage();
		pkg.setName("mapvalue");
		pkg.setNsPrefix("mapvalue");
		pkg.setNsURI("http://test.org/mapvalue");
		item = f.createEClass();
		item.setName("Item");
		flag = f.createEAttribute();
		flag.setName("flag");
		flag.setEType(EcorePackage.Literals.EBOOLEAN_OBJECT);
		item.getEStructuralFeatures().add(flag);
		EClass entry = f.createEClass();
		entry.setName("ItemEntry");
		entry.setInstanceClassName("java.util.Map$Entry");
		EAttribute key = f.createEAttribute();
		key.setName("key");
		key.setEType(EcorePackage.Literals.ESTRING);
		entry.getEStructuralFeatures().add(key);
		EReference value = f.createEReference();
		value.setName("value");
		value.setEType(item);
		value.setContainment(true);
		EcoreUtil.setAnnotation(value, AnnotationSources.CODEC, "valueReaderName", "booleanItem");
		EcoreUtil.setAnnotation(value, AnnotationSources.CODEC, "valueWriterName", "booleanItem");
		entry.getEStructuralFeatures().add(value);
		holder = f.createEClass();
		holder.setName("Holder");
		EReference items = f.createEReference();
		items.setName("items");
		items.setEType(entry);
		items.setContainment(true);
		items.setUpperBound(-1);
		holder.getEStructuralFeatures().add(items);
		pkg.getEClassifiers().addAll(List.of(item, entry, holder));
		for (var c : pkg.getEClassifiers()) {
			EcoreUtil.setAnnotation(c, AnnotationSources.CODEC, "typeStrategy", "NONE");
		}
		EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
		metadata = MetadataServiceFactory.create();
		metadata.registerPackage(pkg);
	}

	@AfterEach
	void tearDown() {
		EPackage.Registry.INSTANCE.remove(pkg.getNsURI());
	}

	@Test
	@DisplayName("booleans as map values are read and written by the named reader and writer")
	void readAndWrite() throws IOException {
		String json = "{\"items\":{\"a\":true,\"b\":false}}";
		CodecResource resource = resource();
		Map<String, Object> options = new HashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, holder);
		resource.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), options);

		EObject loaded = resource.getContents().get(0);
		@SuppressWarnings("unchecked")
		EMap<String, EObject> items = (EMap<String, EObject>) loaded.eGet(holder.getEStructuralFeature("items"));
		assertEquals(Boolean.TRUE, items.get("a").eGet(flag));
		assertEquals(Boolean.FALSE, items.get("b").eGet(flag));
		assertTrue(resource.getWarnings().isEmpty(), resource.getWarnings().toString());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		resource.save(out, Map.of());
		assertEquals(json, out.toString(StandardCharsets.UTF_8));
	}

	private CodecResource resource() {
		CodecValueRegistry registry = new CodecValueRegistry();
		registry.register(new BooleanItemReader());
		registry.register(new BooleanItemWriter());
		return new CodecResource(URI.createURI("test://map.json"), metadata, ConfigurationResolver.defaults(), registry, null);
	}

	/** Reads a JSON boolean into an Item. */
	class BooleanItemReader implements ReferenceValueReader<EObject> {
		@Override
		public String getName() {
			return "booleanItem";
		}

		@Override
		public boolean canHandle(EReference reference) {
			return reference.getEReferenceType() == item;
		}

		@Override
		public EObject read(CodecReaderContext ctx, EReference reference) throws IOException {
			EObject object = EcoreUtil.create(item);
			object.eSet(flag, ctx.getParser().getBooleanValue());
			return object;
		}
	}

	/** Writes an Item as a JSON boolean. */
	class BooleanItemWriter implements ReferenceValueWriter<EObject> {
		@Override
		public String getName() {
			return "booleanItem";
		}

		@Override
		public boolean canHandle(EReference reference) {
			return reference.getEReferenceType() == item;
		}

		@Override
		public void write(EObject value, EReference reference, CodecWriterContext ctx) throws IOException {
			ctx.getGenerator().writeBoolean((Boolean) value.eGet(flag));
		}
	}
}
