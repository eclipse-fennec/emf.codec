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
package org.eclipse.fennec.codec.openapi.oas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.diagnostic.CodecDiagnostic;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.openapi.SchemasPackageIdentity;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.model.oas.Info;
import org.eclipse.fennec.model.oas.OpenAPI;

/**
 * Resource for one OpenAPI version of the {@code org.eclipse.fennec.model.oas} model, in JSON or
 * another syntax such as YAML (issues #299, #300).
 * <p>
 * After loading, the document's {@code openapi} member is checked against the resource's version
 * (another covered version: a warning; one the model does not cover: an error, the load fails), the
 * schemas package gets its identity (#311) and the description is linked in the model (#313).
 * </p>
 */
public class OasResourceImpl extends CodecResource {

	private static final String SOURCE = "OasResource";
	private static final String YAML_MINIMIZE_QUOTES = "codec.yaml.minimizeQuotes";
	private static final String YAML_LITERAL_BLOCK_STYLE = "codec.yaml.literalBlockStyle";

	private final OasVersion version;
	private final boolean yaml;

	/**
	 * @param uri the resource URI
	 * @param metadataService the metadata service, which has to know the oas model
	 * @param valueRegistry the registry with the OpenAPI value handlers
	 * @param version the OpenAPI version this resource reads and writes
	 * @param formatProvider the syntax, {@code null} for JSON
	 */
	public OasResourceImpl(URI uri, MetadataService metadataService, CodecValueRegistry valueRegistry,
			OasVersion version, CodecFormatProvider<?, ?> formatProvider) {
		super(uri, metadataService, ConfigurationResolver.builder().typeInclude(false).build(), valueRegistry, null,
				formatProvider);
		this.version = version;
		this.yaml = formatProvider != null && "yaml".equals(formatProvider.getFormatId());
	}

	/** @return the OpenAPI version of this resource */
	public OasVersion getVersion() {
		return version;
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		super.doLoad(inputStream, options);
		for (EObject content : getContents()) {
			if (content instanceof OpenAPI api) {
				checkVersion(api);
				List<String> problems = new ArrayList<>();
				OasSchemas.convert(api, problems);
				Info info = api.getInfo();
				if (api.getComponents() != null) {
					SchemasPackageIdentity.apply(api.getComponents().getSchemas(), info == null ? null : info.getTitle(),
							info == null ? null : info.getVersion(), options);
				}
				problems.addAll(OasLinker.link(api));
				for (String problem : problems) {
					getWarnings().add(CodecDiagnostic.warning(problem, SOURCE));
				}
			}
		}
	}

	private void checkVersion(OpenAPI api) throws IOException {
		OasVersion documentVersion = OasVersion.of(api.getOpenapi());
		if (documentVersion == null) {
			String message = "OpenAPI '" + api.getOpenapi() + "' is not supported: the model covers 3.0, 3.1 and 3.2";
			getErrors().add(CodecDiagnostic.error(message, SOURCE));
			throw new IOException(message);
		}
		if (documentVersion != version) {
			getWarnings().add(CodecDiagnostic.warning("the document is OpenAPI " + api.getOpenapi()
					+ ", read by a resource for " + version.version(), SOURCE));
		}
	}

	/** YAML as OpenAPI descriptions are written by hand: minimal quotes, literal blocks; the caller's options win. */
	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		Map<Object, Object> effective = options == null ? new HashMap<>() : new HashMap<>(options);
		if (yaml) {
			effective.putIfAbsent(YAML_MINIMIZE_QUOTES, Boolean.TRUE);
			effective.putIfAbsent(YAML_LITERAL_BLOCK_STYLE, Boolean.TRUE);
		}
		// inline schemas are converted once per save, from the model as it is now
		SchemaReferenceValueWriter.InlineCache.reset();
		try {
			super.doSave(outputStream, effective);
		} finally {
			SchemaReferenceValueWriter.InlineCache.reset();
		}
	}
}
