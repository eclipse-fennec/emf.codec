/********************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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
package org.eclipse.fennec.codec.openapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.diagnostic.CodecDiagnostic;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.openapi.value.OpenApiSchemasValueWriter;
import org.eclipse.fennec.codec.openapi.value.OperationValueReader;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;

/**
 * EMF Resource implementation for OpenAPI documents.
 * <p>
 * The {@code components/schemas} JSON key is handled entirely by the
 * {@code schemasPackage} EReference via codec annotations:
 * <ul>
 *   <li>Read: {@code EPackageValueReader} ("jsonSchemaToEPackage") converts the
 *       raw JSON schemas object directly to an {@code EPackage}.</li>
 *   <li>Write: {@code OpenApiSchemasValueWriter} ("ePackageToOpenApiSchemas") converts
 *       the {@code EPackage} back to a flat JSON schemas map.</li>
 * </ul>
 * The legacy {@code schemas} EMap feature is ignored by the codec ({@code ignore=true})
 * and will be removed in a future release.
 * </p>
 * <p>
 * The required readers/writers ({@link OperationValueReader},
 * {@link OpenApiSchemasValueWriter}) are OSGi {@code @Component} services collected
 * into the injected {@link CodecValueRegistry}.
 * </p>
 *
 * @author Data In Motion
 * @since 1.0
 */
public class OpenApiResourceImpl extends CodecResource {

	/**
	 * Save options of {@code org.eclipse.fennec.codec.yaml.YamlFormatProvider}, named here to keep
	 * the YAML packages off this bundle's imports: quote only where needed, literal blocks for
	 * multi-line strings (issue #297).
	 */
	static final String YAML_MINIMIZE_QUOTES = "codec.yaml.minimizeQuotes";
	static final String YAML_LITERAL_BLOCK_STYLE = "codec.yaml.literalBlockStyle";

	private final boolean yaml;

	/**
	 * Creates an OpenAPI resource.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service for codec configuration
	 * @param valueRegistry registry containing the OpenAPI-specific readers/writers
	 */
	public OpenApiResourceImpl(URI uri, MetadataService metadataService, CodecValueRegistry valueRegistry) {
		this(uri, metadataService, valueRegistry, null);
	}

	/**
	 * Creates an OpenAPI resource in another syntax than JSON, e.g. YAML through
	 * {@code org.eclipse.fennec.codec.yaml.YamlFormatProvider} (issue #296). The model and the
	 * mapping are the same as for JSON; only the token stream comes from the format provider.
	 *
	 * @param uri the resource URI
	 * @param metadataService the metadata service for codec configuration
	 * @param valueRegistry registry containing the OpenAPI-specific readers/writers
	 * @param formatProvider the syntax, {@code null} for JSON
	 * @since 1.1
	 */
	public OpenApiResourceImpl(URI uri, MetadataService metadataService, CodecValueRegistry valueRegistry,
			CodecFormatProvider<?, ?> formatProvider) {
		super(uri, metadataService, createResolver(), valueRegistry, null, formatProvider);
		this.yaml = formatProvider != null && "yaml".equals(formatProvider.getFormatId());
	}

	/**
	 * After loading, gives the package built from {@code components/schemas} an identity (issue #311)
	 * and links the schemas' {@code $ref}s to their classifiers (issue #312); an internal reference
	 * without target is a warning on the resource.
	 */
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		super.doLoad(inputStream, options);
		for (EObject content : getContents()) {
			if (content instanceof OpenAPI api) {
				SchemasPackageIdentity.apply(api, options);
				for (String ref : OpenApiReferences.link(api)) {
					getWarnings().add(CodecDiagnostic.warning("$ref '" + ref + "' has no target in this document",
							"OpenApiReferences"));
				}
			}
		}
	}

	/**
	 * Writes YAML the way OpenAPI descriptions are written by hand (issue #297): strings unquoted
	 * unless YAML would read them as something else, multi-line descriptions as literal blocks. A
	 * caller's own value for either option wins.
	 */
	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		if (!yaml) {
			super.doSave(outputStream, options);
			return;
		}
		Map<Object, Object> effective = options == null ? new HashMap<>() : new HashMap<>(options);
		effective.putIfAbsent(YAML_MINIMIZE_QUOTES, Boolean.TRUE);
		effective.putIfAbsent(YAML_LITERAL_BLOCK_STYLE, Boolean.TRUE);
		super.doSave(outputStream, effective);
	}

	private static ConfigurationResolver createResolver() {
		return ConfigurationResolver.builder()
				.typeInclude(false)  // OpenAPI doesn't use _type for root
				.build();
	}
}
