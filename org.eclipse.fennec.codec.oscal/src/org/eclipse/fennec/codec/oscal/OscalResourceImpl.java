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
package org.eclipse.fennec.codec.oscal;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

import gov.nist.csrc.ns.oscal.DocumentRoot;
import gov.nist.csrc.ns.oscal.OSCALPackage;

/**
 * Codec resource for OSCAL JSON documents.
 * <p>
 * An OSCAL JSON document is an object with a single member that names the model, e.g.
 * {@code { "catalog": { ... } }}, optionally next to {@code $schema}. The resource reads it into
 * the {@link DocumentRoot} of the OSCAL model: its containment references ({@code catalog},
 * {@code profile}, {@code component-definition}, ...) are exactly these members, and
 * {@code $schema} goes to {@link DocumentRoot#getSchema()}. A loaded resource therefore always
 * holds one {@code DocumentRoot}.
 * </p>
 * <p>
 * For saving as JSON, the content may also be a bare OSCAL model object such as a {@code Catalog}.
 * It is written inside its document member, so the output is an OSCAL document either way. Any
 * other object is rejected: without the member the output would not be OSCAL. In another format
 * (YAML) the content has to be the {@code DocumentRoot}.
 * </p>
 * <p>
 * Markup ({@code markup-line}, {@code markup-multiline}) is a Markdown string in OSCAL JSON and a
 * {@code String} in the model, so it is kept exactly as written.
 * </p>
 *
 * @author Mark Hoffmann
 * @since 1.0
 */
public class OscalResourceImpl extends CodecResource {

	/** Whether the document is JSON; only then can a bare model object be wrapped while writing. */
	private final boolean json;

	/**
	 * Creates a resource on a metadata service that knows the OSCAL model.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service
	 */
	public OscalResourceImpl(URI uri, MetadataService metadataService) {
		this(uri, metadataService, null);
	}

	/**
	 * Creates a resource on a metadata service that knows the OSCAL model, with custom value
	 * readers and writers.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service
	 * @param valueRegistry the value reader/writer registry, may be {@code null}
	 */
	public OscalResourceImpl(URI uri, MetadataService metadataService, CodecValueRegistry valueRegistry) {
		this(uri, metadataService, valueRegistry, null);
	}

	/**
	 * Creates a resource for OSCAL in another format than JSON, e.g. YAML through
	 * {@code org.eclipse.fennec.codec.yaml.YamlFormatProvider}. OSCAL defines YAML as the same
	 * document structure as JSON.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service
	 * @param valueRegistry the value reader/writer registry, may be {@code null}
	 * @param formatProvider the format, {@code null} for JSON
	 */
	public OscalResourceImpl(URI uri, MetadataService metadataService, CodecValueRegistry valueRegistry,
			CodecFormatProvider<?, ?> formatProvider) {
		super(uri, metadataService, createResolver(), valueRegistry, null, formatProvider);
		this.json = formatProvider == null;
	}

	private static ConfigurationResolver createResolver() {
		OSCALPackage pkg = OSCALPackage.eINSTANCE;
		EStructuralFeature[] members = documentMembers().toArray(new EStructuralFeature[0]);
		return ConfigurationResolver.builder()
				// the OSCAL JSON names: ExtendedMetaData carries the XML names, the codec key
				// annotations of the model the JSON names where they differ
				.useNamesFromExtendedMetaData(true)
				// OSCAL JSON has no type or id members; every type follows from the reference
				.typeInclude(false)
				.useId(false)
				// XML-only machinery of the document root
				.globalIgnoreFeatures(pkg.getDocumentRoot_Mixed(), pkg.getDocumentRoot_XMLNSPrefixMap(),
						pkg.getDocumentRoot_XSISchemaLocation())
				// the document members are derived from the mixed feature map and $schema is
				// transient, so the codec skips them unless told otherwise
				.forceWrite(members)
				.forceRead(members)
				.build();
	}

	/**
	 * The members of the document object: one derived containment reference per OSCAL model and
	 * {@code $schema}.
	 */
	private static List<EStructuralFeature> documentMembers() {
		OSCALPackage pkg = OSCALPackage.eINSTANCE;
		List<EStructuralFeature> members = new ArrayList<>(modelMembers());
		members.add(pkg.getDocumentRoot_Schema());
		return members;
	}

	/** The document root references, one per OSCAL model ({@code catalog}, {@code profile}, ...). */
	private static List<EReference> modelMembers() {
		List<EReference> members = new ArrayList<>();
		for (EReference reference : OSCALPackage.eINSTANCE.getDocumentRoot().getEReferences()) {
			if (reference.isContainment() && reference.isDerived()) {
				members.add(reference);
			}
		}
		return members;
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		Map<Object, Object> effectiveOptions = new HashMap<>();
		if (options != null) {
			options.forEach(effectiveOptions::put);
		}
		effectiveOptions.putIfAbsent(CodecOptions.CODEC_ROOT_TYPE, OSCALPackage.eINSTANCE.getDocumentRoot());
		super.doLoad(inputStream, effectiveOptions);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		if (getContents().isEmpty() || getContents().get(0) instanceof DocumentRoot) {
			super.doSave(outputStream, options);
			return;
		}
		EObject root = getContents().get(0);
		if (!json) {
			throw new IOException("Cannot save " + root.eClass().getName() + " to " + getURI()
					+ ": outside JSON the content must be a DocumentRoot that holds the OSCAL model");
		}
		EReference member = memberFor(root.eClass());
		if (member == null) {
			throw new IOException("Cannot save " + root.eClass().getName() + " as OSCAL document to " + getURI()
					+ ": the content must be a DocumentRoot or one of the OSCAL models (catalog, profile, ...)");
		}
		// {"catalog": <the object as the codec writes it>}
		String name = ExtendedMetaData.INSTANCE.getName(member);
		outputStream.write(("{\"" + name + "\":").getBytes(StandardCharsets.UTF_8));
		super.doSave(new NonClosingOutputStream(outputStream), options);
		outputStream.write('}');
		outputStream.flush();
	}

	/** Keeps the target open when the codec closes its generator, so the wrapper can be completed. */
	private static final class NonClosingOutputStream extends FilterOutputStream {

		NonClosingOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}

		@Override
		public void close() throws IOException {
			flush();
		}
	}

	private static EReference memberFor(EClass eClass) {
		for (EReference member : modelMembers()) {
			if (member.getEReferenceType() == eClass) {
				return member;
			}
		}
		return null;
	}
}
