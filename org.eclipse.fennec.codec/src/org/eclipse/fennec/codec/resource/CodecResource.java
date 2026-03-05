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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.config.ConfigProperty;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.config.bridge.AspectToPropertiesConverter;
import org.eclipse.fennec.codec.config.effective.EffectiveCodecConfig;
import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.format.impl.FormatDelegateGenerator;
import org.eclipse.fennec.codec.format.impl.FormatDelegateParser;
import org.eclipse.fennec.codec.format.FormatReaderDelegate;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorReader;
import org.eclipse.fennec.codec.metadata.type.TypeDiscriminatorService;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.context.ContextHelper;
import org.eclipse.fennec.codec.deser.DeserializationState.UnresolvedReference;
import org.eclipse.fennec.codec.diagnostic.DiagnosticCollector;
import org.eclipse.fennec.codec.jackson.CodecJsonFactory;
import org.eclipse.fennec.codec.jackson.CodecJsonReadContext;
import org.eclipse.fennec.codec.module.CodecModule;
import org.eclipse.fennec.codec.util.CodecResourceHelper;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.model.metadata.PackageMetadata;
import org.eclipse.fennec.model.metadata.api.MetadataService;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * EMF Resource implementation for JSON codec serialization.
 * <p>
 * This resource uses {@link MetadataService} to access pre-computed metadata
 * and codec aspects, and {@link ConfigurationResolver} for spec-compliant
 * configuration resolution.
 * </p>
 *
 * <h2>Configuration Hierarchy</h2>
 * Settings are resolved in this order (highest priority first):
 * <ol>
 *   <li>Load/Save options passed to save()/load()</li>
 *   <li>ResourceFactory default options</li>
 *   <li>ConfigurationResolver (module config + EAnnotations via MetadataService)</li>
 *   <li>Built-in defaults</li>
 * </ol>
 *
 * @see <a href="docs/codec-v2-spec/13-resource.md">Spec 13: Resource</a>
 * @author Mark Hoffmann
 * @since 2026-02-01
 */
public class CodecResource extends ResourceImpl {

    private static final Logger LOGGER = Logger.getLogger(CodecResource.class.getName());

    /** Option key for specifying the root EClass during deserialization */
    public static final String CODEC_ROOT_TYPE = "CODEC_ROOT_TYPE";

    /**
     * Option key for specifying the context schema URI during deserialization.
     */
    public static final String CODEC_ROOT_SCHEMA = "CODEC_ROOT_SCHEMA";

    private final MetadataService metadataService;
    private final ConfigurationResolver resolver;
    private final CodecValueRegistry valueRegistry;
    private final JsonMapper.Builder mapperBuilder;
    private final CodecResourceHelper helper;
    private final CodecFormatProvider<?, ?> formatProvider;
    private final TypeDiscriminatorReader typeDiscriminatorReader;

    private ObjectMapper mapper;

    /**
     * Creates a new CodecResource.
     *
     * @param uri the resource URI
     * @param metadataService the metadata service
     * @param resolver the configuration resolver
     * @param mapperBuilder pre-configured mapper builder (null for default)
     */
    public CodecResource(URI uri, MetadataService metadataService, ConfigurationResolver resolver,
            JsonMapper.Builder mapperBuilder) {
        this(uri, metadataService, resolver, null, mapperBuilder, null);
    }

    /**
     * Creates a new CodecResource with custom value registry.
     *
     * @param uri the resource URI
     * @param metadataService the metadata service
     * @param resolver the configuration resolver
     * @param valueRegistry custom value readers/writers registry
     * @param mapperBuilder pre-configured mapper builder (null for default)
     */
    public CodecResource(URI uri, MetadataService metadataService, ConfigurationResolver resolver,
            CodecValueRegistry valueRegistry, JsonMapper.Builder mapperBuilder) {
        this(uri, metadataService, resolver, valueRegistry, mapperBuilder, null);
    }

    /**
     * Creates a new CodecResource with a format provider for non-JSON formats.
     * <p>
     * When a format provider is set, serialization and deserialization use
     * {@link FormatDelegateGenerator} and {@link FormatDelegateParser} to bridge
     * between Jackson's ObjectMapper and the format-specific delegates.
     *
     * @param uri the resource URI
     * @param metadataService the metadata service
     * @param resolver the configuration resolver
     * @param valueRegistry custom value readers/writers registry (null for default)
     * @param mapperBuilder pre-configured mapper builder (null for default)
     * @param formatProvider the format provider (null for default JSON via CodecJsonFactory)
     */
    public CodecResource(URI uri, MetadataService metadataService, ConfigurationResolver resolver,
            CodecValueRegistry valueRegistry, JsonMapper.Builder mapperBuilder,
            CodecFormatProvider<?, ?> formatProvider) {
        this(uri, metadataService, resolver, valueRegistry, mapperBuilder, formatProvider, null);
    }

    /**
     * Creates a new CodecResource with an externally managed TypeDiscriminatorReader.
     * <p>
     * When a {@code typeDiscriminatorReader} is provided, it is used directly instead of
     * creating a fresh {@link TypeDiscriminatorService} on every save()/load() call.
     * This is the recommended approach in OSGi where the TypeDiscriminatorService is
     * managed as a MetadataHandler on the MetadataWhiteboard.
     * </p>
     *
     * @param uri the resource URI
     * @param metadataService the metadata service
     * @param resolver the configuration resolver
     * @param valueRegistry custom value readers/writers registry (null for default)
     * @param mapperBuilder pre-configured mapper builder (null for default)
     * @param formatProvider the format provider (null for default JSON via CodecJsonFactory)
     * @param typeDiscriminatorReader externally managed discriminator reader (null to create fresh per operation)
     */
    public CodecResource(URI uri, MetadataService metadataService, ConfigurationResolver resolver,
            CodecValueRegistry valueRegistry, JsonMapper.Builder mapperBuilder,
            CodecFormatProvider<?, ?> formatProvider, TypeDiscriminatorReader typeDiscriminatorReader) {
        super(uri);
        this.metadataService = requireNonNull(metadataService, "metadataService must not be null");
        this.resolver = enrichWithAnnotations(resolver, metadataService);
        this.valueRegistry = valueRegistry;
        this.mapperBuilder = mapperBuilder;
        this.helper = new CodecResourceHelper(metadataService);
        this.formatProvider = formatProvider;
        this.typeDiscriminatorReader = typeDiscriminatorReader;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public MetadataService getMetadataService() {
        return metadataService;
    }

    public ConfigurationResolver getResolver() {
        return resolver;
    }

    public CodecFormatProvider<?, ?> getFormatProvider() {
        return formatProvider;
    }

    // ========================================================================
    // Save Implementation
    // ========================================================================

    @Override
    protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
        if (getContents().isEmpty()) {
            LOGGER.warning(() -> String.format("No content to save for resource %s", getURI()));
            return;
        }

        EObject rootObject = getContents().get(0);
        EClass eClass = rootObject.eClass();
        EPackage ePackage = eClass.getEPackage();

        requirePackageRegistered(ePackage);

        Map<String, Object> effectiveOptions = (Map<String, Object>) mergeOptions(options);

        // Enrich resolver with save options (highest priority in config hierarchy)
        ConfigurationResolver operationResolver = enrichWithOptions(resolver, effectiveOptions);

        mapper = createObjectMapper(effectiveOptions, operationResolver);

        if (formatProvider != null) {
            doSaveWithFormat(outputStream, effectiveOptions);
            LOGGER.fine(() -> String.format("Saved %s to %s (format: %s)",
                    eClass.getName(), getURI(), formatProvider.getFormatId()));
            return;
        }

        // Check if we have custom value writer configurations
        Object valueWriterInstancesOption = effectiveOptions.get(CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES);
        Object valueWritersOption = effectiveOptions.get(CodecOptions.CODEC_FEATURE_VALUE_WRITERS);
        boolean hasCustomWriterConfig = (valueWriterInstancesOption instanceof Map<?, ?>)
                || (valueWritersOption instanceof Map<?, ?>);

        if (hasCustomWriterConfig) {
            // Prepare writer with value writer configurations
            var writer = mapper.writerFor(EObject.class);

            // Set feature value writer instances if provided (highest priority - direct binding)
            if (valueWriterInstancesOption instanceof Map<?, ?> instancesMap) {
                writer = writer.withAttribute(ContextHelper.FEATURE_VALUE_WRITER_INSTANCES, instancesMap);
            }

            // Set feature value writers by name if provided
            if (valueWritersOption instanceof Map<?, ?> valueWritersMap) {
                writer = writer.withAttribute(ContextHelper.FEATURE_VALUE_WRITERS, valueWritersMap);
            }

            if (getContents().size() == 1) {
                writer.writeValue(outputStream, rootObject);
            } else {
                // For arrays, need to write each element
                mapper.writeValue(outputStream, getContents().toArray(new EObject[0]));
            }
        } else if (getContents().size() == 1) {
            mapper.writeValue(outputStream, rootObject);
        } else {
            mapper.writeValue(outputStream, getContents().toArray(new EObject[0]));
        }

        LOGGER.fine(() -> String.format("Saved %s to %s", eClass.getName(), getURI()));
    }

    // ========================================================================
    // Load Implementation
    // ========================================================================

    @Override
    protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
        Map<?, ?> effectiveOptions = isNull(options) ? Collections.emptyMap() : options;

        EClass rootEClassHint = helper.resolveRootEClass(effectiveOptions);

        if (nonNull(rootEClassHint)) {
            EPackage ePackage = rootEClassHint.getEPackage();
            requirePackageRegistered(ePackage);
        }

        Map<String, Object> mergedOptions = (Map<String, Object>) mergeOptions(effectiveOptions);

        // Enrich resolver with load options (highest priority in config hierarchy)
        ConfigurationResolver operationResolver = enrichWithOptions(resolver, mergedOptions);

        mapper = createObjectMapper(mergedOptions, operationResolver);

        if (formatProvider != null) {
            doLoadWithFormat(inputStream, mergedOptions, rootEClassHint, operationResolver);
            return;
        }

        // Create EffectiveCodecConfig for the codec factory
        DiagnosticCollector diagnosticCollector = new DiagnosticCollector();
        EffectiveCodecConfig effectiveConfig = EffectiveCodecConfig.builder()
                .resolver(operationResolver)
                .diagnostics(diagnosticCollector)
                .metadataService(metadataService)
                .valueRegistry(valueRegistry != null ? valueRegistry : new CodecValueRegistry())
                .customProperties(extractCustomProperties(mergedOptions))
                .build();

        CodecJsonFactory codecFactory = new CodecJsonFactory(effectiveConfig);

        List<UnresolvedReference> unresolvedReferences = new ArrayList<>();

        var reader = mapper.readerFor(EObject.class)
                .withAttribute(ContextHelper.UNRESOLVED_REFERENCES, unresolvedReferences)
                .withAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR, diagnosticCollector)
                .without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

        if (nonNull(rootEClassHint)) {
            reader = reader.withAttribute(ContextHelper.EXPECTED_TYPE, rootEClassHint);
        }

        String contextSchemaUri = resolveContextSchema(effectiveOptions, rootEClassHint);
        if (nonNull(contextSchemaUri)) {
            reader = reader.withAttribute(ContextHelper.CONTEXT_SCHEMA_URI, contextSchemaUri);
        }

        // Set feature type hints if provided
        Object typeHintsOption = mergedOptions.get(CodecOptions.CODEC_FEATURE_TYPE_HINTS);
        if (typeHintsOption instanceof Map<?, ?> typeHintsMap) {
            @SuppressWarnings("unchecked")
            Map<EStructuralFeature, EClass> typeHints = (Map<EStructuralFeature, EClass>) typeHintsMap;
            reader = reader.withAttribute(ContextHelper.FEATURE_TYPE_HINTS, typeHints);
        }

        // Set feature value reader instances if provided (highest priority - direct binding)
        Object valueReaderInstancesOption = mergedOptions.get(CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES);
        if (valueReaderInstancesOption instanceof Map<?, ?> instancesMap) {
            reader = reader.withAttribute(ContextHelper.FEATURE_VALUE_READER_INSTANCES, instancesMap);
        }

        // Set feature value readers by name if provided
        Object valueReadersOption = mergedOptions.get(CodecOptions.CODEC_FEATURE_VALUE_READERS);
        if (valueReadersOption instanceof Map<?, ?> valueReadersMap) {
            reader = reader.withAttribute(ContextHelper.FEATURE_VALUE_READERS, valueReadersMap);
        }

        // Set deserialization mode if provided
        Object deserializationModeOption = mergedOptions.get(CodecOptions.CODEC_DESERIALIZATION_MODE);
        if (deserializationModeOption != null) {
            // Accept both String and DeserializationMode enum
            String modeString = deserializationModeOption.toString();
            reader = reader.withAttribute(ContextHelper.DESERIALIZATION_MODE, modeString);
        }

        try (JsonParser parser = codecFactory.createParser(ObjectReadContext.empty(), inputStream)) {
            if (parser.streamReadContext() instanceof CodecJsonReadContext ctx) {
                ctx.setResource(this);
                if (nonNull(rootEClassHint)) {
                    ctx.setCurrentTypeHint(rootEClassHint);
                }
                if (nonNull(contextSchemaUri)) {
                    ctx.setContextSchemaUri(contextSchemaUri);
                }
            }

            JsonToken firstToken = parser.nextToken();

            if (firstToken == JsonToken.START_ARRAY) {
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    EObject result = reader.readValue(parser);
                    if (nonNull(result)) {
                        getContents().add(result);
                    }
                }
            } else if (firstToken == JsonToken.START_OBJECT) {
                EObject result = reader.readValue(parser);
                if (nonNull(result)) {
                    getContents().add(result);
                }
            } else if (firstToken != null) {
                LOGGER.warning(() -> String.format("Unexpected token at root: %s", firstToken));
            }
        }

        if (!unresolvedReferences.isEmpty()) {
            resolveReferences(unresolvedReferences, diagnosticCollector);
        }

        diagnosticCollector.addToResource(this);

        LOGGER.fine(() -> String.format("Loaded %d objects from %s (errors=%d, warnings=%d)",
            getContents().size(), getURI(),
            getErrors().size(), getWarnings().size()));
    }

    // ========================================================================
    // Format Provider Methods
    // ========================================================================

    @SuppressWarnings("unchecked")
    private <T> void doSaveWithFormat(OutputStream outputStream,
            Map<String, Object> effectiveOptions) throws IOException {
        CodecFormatProvider<?, T> provider = (CodecFormatProvider<?, T>) formatProvider;
        FormatDelegate<T> delegate = provider.createWriter((T) outputStream);

        IOContext ioCtxt = new IOContext(
                StreamReadConstraints.defaults(),
                StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                new BufferRecycler(),
                ContentReference.unknown(), false, JsonEncoding.UTF8);

        try (FormatDelegateGenerator<T> gen = FormatDelegateGenerator.create(
                ObjectWriteContext.empty(), ioCtxt, delegate)) {

            var writer = mapper.writerFor(EObject.class);

            // Set feature value writer instances if provided
            Object valueWriterInstancesOption = effectiveOptions.get(
                    CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES);
            if (valueWriterInstancesOption instanceof Map<?, ?> instancesMap) {
                writer = writer.withAttribute(ContextHelper.FEATURE_VALUE_WRITER_INSTANCES, instancesMap);
            }

            // Set feature value writers by name if provided
            Object valueWritersOption = effectiveOptions.get(CodecOptions.CODEC_FEATURE_VALUE_WRITERS);
            if (valueWritersOption instanceof Map<?, ?> valueWritersMap) {
                writer = writer.withAttribute(ContextHelper.FEATURE_VALUE_WRITERS, valueWritersMap);
            }

            if (getContents().size() == 1) {
                writer.writeValue(gen, getContents().get(0));
            } else if (!provider.supportsArrayRoot()) {
                throw new IOException(String.format(
                        "Format '%s' does not support multiple root objects (array root). "
                        + "Resource %s contains %d root objects.",
                        provider.getFormatId(), getURI(), getContents().size()));
            } else {
                gen.writeStartArray();
                for (EObject content : getContents()) {
                    writer.writeValue(gen, content);
                }
                gen.writeEndArray();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <S> void doLoadWithFormat(InputStream inputStream, Map<String, Object> mergedOptions,
            EClass rootEClassHint, ConfigurationResolver operationResolver) throws IOException {

        CodecFormatProvider<S, ?> provider = (CodecFormatProvider<S, ?>) formatProvider;
        FormatReaderDelegate<S> delegate = provider.createReader((S) inputStream);

        IOContext ioCtxt = new IOContext(
                StreamReadConstraints.defaults(),
                StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                new BufferRecycler(),
                ContentReference.unknown(), true, JsonEncoding.UTF8);

        DiagnosticCollector diagnosticCollector = new DiagnosticCollector();
        List<UnresolvedReference> unresolvedReferences = new ArrayList<>();

        var reader = mapper.readerFor(EObject.class)
                .withAttribute(ContextHelper.UNRESOLVED_REFERENCES, unresolvedReferences)
                .withAttribute(ContextHelper.DIAGNOSTIC_COLLECTOR, diagnosticCollector)
                .withAttribute(ContextHelper.RESOURCE, this)
                .without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

        if (nonNull(rootEClassHint)) {
            reader = reader.withAttribute(ContextHelper.EXPECTED_TYPE, rootEClassHint);
        }

        String contextSchemaUri = resolveContextSchema(mergedOptions, rootEClassHint);
        if (nonNull(contextSchemaUri)) {
            reader = reader.withAttribute(ContextHelper.CONTEXT_SCHEMA_URI, contextSchemaUri);
        }

        // Set feature type hints if provided
        Object typeHintsOption = mergedOptions.get(CodecOptions.CODEC_FEATURE_TYPE_HINTS);
        if (typeHintsOption instanceof Map<?, ?> typeHintsMap) {
            Map<EStructuralFeature, EClass> typeHints = (Map<EStructuralFeature, EClass>) typeHintsMap;
            reader = reader.withAttribute(ContextHelper.FEATURE_TYPE_HINTS, typeHints);
        }

        // Set feature value reader instances if provided
        Object valueReaderInstancesOption = mergedOptions.get(CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES);
        if (valueReaderInstancesOption instanceof Map<?, ?> instancesMap) {
            reader = reader.withAttribute(ContextHelper.FEATURE_VALUE_READER_INSTANCES, instancesMap);
        }

        // Set feature value readers by name if provided
        Object valueReadersOption = mergedOptions.get(CodecOptions.CODEC_FEATURE_VALUE_READERS);
        if (valueReadersOption instanceof Map<?, ?> valueReadersMap) {
            reader = reader.withAttribute(ContextHelper.FEATURE_VALUE_READERS, valueReadersMap);
        }

        // Set deserialization mode if provided
        Object deserializationModeOption = mergedOptions.get(CodecOptions.CODEC_DESERIALIZATION_MODE);
        if (deserializationModeOption != null) {
            String modeString = deserializationModeOption.toString();
            reader = reader.withAttribute(ContextHelper.DESERIALIZATION_MODE, modeString);
        }

        try (FormatDelegateParser<S> parser = FormatDelegateParser.create(
                ObjectReadContext.empty(), ioCtxt, delegate)) {

            JsonToken firstToken = parser.nextToken();

            if (firstToken == JsonToken.START_ARRAY) {
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    EObject result = reader.readValue(parser);
                    if (nonNull(result)) {
                        getContents().add(result);
                    }
                }
            } else if (firstToken == JsonToken.START_OBJECT) {
                EObject result = reader.readValue(parser);
                if (nonNull(result)) {
                    getContents().add(result);
                }
            } else if (firstToken != null) {
                LOGGER.warning(() -> String.format("Unexpected token at root: %s", firstToken));
            }
        }

        if (!unresolvedReferences.isEmpty()) {
            resolveReferences(unresolvedReferences, diagnosticCollector);
        }

        diagnosticCollector.addToResource(this);

        LOGGER.fine(() -> String.format("Loaded %d objects from %s (format: %s, errors=%d, warnings=%d)",
            getContents().size(), getURI(), formatProvider.getFormatId(),
            getErrors().size(), getWarnings().size()));
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private PackageMetadata requirePackageRegistered(EPackage ePackage) throws IOException {
        PackageMetadata metadata = metadataService.getPackageMetadata(ePackage.getNsURI());
        if (isNull(metadata)) {
            throw new IOException(String.format(
                "Package '%s' is not registered with MetadataService. " +
                "Register it via MetadataWhiteboard.registerPackage() before using CodecResource.",
                ePackage.getNsURI()));
        }
        return metadata;
    }

    private String resolveContextSchema(Map<?, ?> options, EClass rootEClassHint) {
        Object schemaOption = options.get(CODEC_ROOT_SCHEMA);
        if (schemaOption instanceof String schemaUri && !schemaUri.isEmpty()) {
            LOGGER.fine(() -> "Using explicit CODEC_ROOT_SCHEMA: " + schemaUri);
            return schemaUri;
        }

        if (nonNull(rootEClassHint)) {
            EPackage ePackage = rootEClassHint.getEPackage();
            if (nonNull(ePackage)) {
                String schemaUri = ePackage.getNsURI();
                LOGGER.fine(() -> "Using context schema from CODEC_ROOT_TYPE: " + schemaUri);
                return schemaUri;
            }
        }

        return null;
    }

    private Map<String, Object> mergeOptions(Map<?, ?> options) {
        Map<String, Object> merged = new HashMap<>();
        if (nonNull(options)) {
            options.forEach((key, value) -> {
                if (key instanceof String) {
                    merged.put((String) key, value);
                }
            });
        }
        return merged;
    }

    /**
     * Enriches a resolver with annotation properties extracted from MetadataService.
     * <p>
     * Preserves any existing resolver properties (module, resource, options, etc.)
     * while adding annotation properties derived from MetadataService's parsed codec aspects.
     */
    private static ConfigurationResolver enrichWithAnnotations(ConfigurationResolver resolver,
            MetadataService metadataService) {
        ConfigurationResolver base = isNull(resolver) ? ConfigurationResolver.defaults() : resolver;
        Map<String, Object> annotationProps = AspectToPropertiesConverter.buildAnnotationProperties(metadataService);
        if (annotationProps.isEmpty()) {
            return base;
        }
        return base.toBuilder()
                .annotationProperties(annotationProps)
                .build();
    }

    /**
     * Enriches a resolver with load/save options as highest priority.
     * <p>
     * This implements the spec's configuration hierarchy where load/save options
     * have the highest priority (Level 1), overriding all other configuration sources:
     * OPTIONS → RESOURCE → FACTORY → MODULE → ANNOTATION → DEFAULT
     *
     * @param resolver the base resolver (with module/annotation/etc. properties)
     * @param options the load/save options map
     * @return a new resolver with options set as highest priority
     */
    private static ConfigurationResolver enrichWithOptions(ConfigurationResolver resolver,
            Map<String, Object> options) {
        if (isNull(options) || options.isEmpty()) {
            return resolver;
        }
        // Merge load/save options with existing options properties.
        // The load/save options take precedence over previously configured options.
        ConfigurationResolver.Builder builder = resolver.toBuilder();
        Map<String, Object> existingOptions = resolver.getOptionsProperties();
        if (nonNull(existingOptions) && !existingOptions.isEmpty()) {
            Map<String, Object> merged = new HashMap<>(existingOptions);
            merged.putAll(options);
            builder.optionsProperties(merged);
        } else {
            builder.optionsProperties(options);
        }
        return builder.build();
    }

    private ObjectMapper createObjectMapper(Map<String, Object> options, ConfigurationResolver operationResolver) {
        // Use externally managed TypeDiscriminatorReader or create fresh one
        TypeDiscriminatorReader typeService = typeDiscriminatorReader != null
                ? typeDiscriminatorReader
                : TypeDiscriminatorService.fromMetadataService(metadataService);

        // Extract global properties from the operation resolver (includes load/save options)
        List<String> ignoreFeatures = operationResolver.getGlobalProperty(ConfigProperty.IGNORE_FEATURES);
        boolean smartCompression = operationResolver.getGlobalProperty(ConfigProperty.SMART_COMPRESSION);
        boolean useNamesFromExtendedMetaData = operationResolver.getGlobalProperty(ConfigProperty.USE_NAMES_FROM_EXTENDED_METADATA);

        // Extract expand properties from the operation resolver
        boolean expandGlobal = operationResolver.getGlobalProperty(ConfigProperty.EXPAND_GLOBAL);
        int expandDepth = operationResolver.getGlobalProperty(ConfigProperty.EXPAND_DEPTH);
        boolean expandIgnoreBidirectional = operationResolver.getGlobalProperty(ConfigProperty.EXPAND_IGNORE_BIDIRECTIONAL);

        // Extract expand references list (may contain EReference objects or String names)
        // Note: EXPAND property returns null when not set (unlike EXPAND_GLOBAL which returns false)
        List<Object> expandList = operationResolver.getGlobalProperty(ConfigProperty.EXPAND);

        Map<String, Object> customProperties = extractCustomProperties(options);

        CodecModule.Builder moduleBuilder = CodecModule.builder()
                .resolver(operationResolver)
                .metadataService(metadataService)
                .typeDiscriminatorService(typeService)
                .globalIgnoreFeatures(ignoreFeatures)
                .smartCompression(smartCompression)
                .useNamesFromExtendedMetaData(useNamesFromExtendedMetaData)
                .customProperties(customProperties)
                .expandGlobal(expandGlobal)
                .expandDepth(expandDepth)
                .expandIgnoreBidirectional(expandIgnoreBidirectional)
                .expandList(expandList);

        if (valueRegistry != null) {
            moduleBuilder.valueRegistry(valueRegistry);
        }

        CodecModule codecModule = moduleBuilder.build();

        JsonMapper.Builder builder = isNull(mapperBuilder) ? JsonMapper.builder() : mapperBuilder;
        return builder
                .addModule(codecModule)
                .build();
    }

    @SuppressWarnings("unchecked")
    private void resolveReferences(List<UnresolvedReference> unresolvedReferences,
            DiagnosticCollector diagnosticCollector) {
        Map<String, EObject> proxyCache = new HashMap<>();

        for (UnresolvedReference unresolved : unresolvedReferences) {
            String targetUri = unresolved.getTargetUri();
            EObject target = resolveReference(targetUri);

            if (isNull(target)) {
                target = proxyCache.computeIfAbsent(targetUri, uri -> createProxy(unresolved, diagnosticCollector));
                if (isNull(target)) {
                    String msg = String.format(
                            "Could not resolve or create proxy for reference %s -> %s",
                            unresolved.getReference().getName(),
                            targetUri);
                    LOGGER.warning(msg);
                    diagnosticCollector.addWarning(msg, "CodecResource");
                    continue;
                }
            }

            EObject source = unresolved.getSource();
            EReference reference = unresolved.getReference();

            if (unresolved.isMultiValued()) {
                List<EObject> list = (List<EObject>) source.eGet(reference);
                int index = unresolved.getIndex();
                if (index < list.size()) {
                    list.set(index, target);
                } else {
                    list.add(target);
                }
            } else {
                source.eSet(reference, target);
            }
        }
    }

    private EObject createProxy(UnresolvedReference unresolved, DiagnosticCollector diagnosticCollector) {
        EClass eClass = unresolved.getEffectiveType();

        if (isNull(eClass) || eClass.isAbstract() || eClass.isInterface()) {
            String msg = String.format(
                    "Cannot create proxy for reference %s: type %s is abstract or interface",
                    unresolved.getReference().getName(),
                    eClass != null ? eClass.getName() : "null");
            LOGGER.warning(msg);
            diagnosticCollector.addWarning(msg, "CodecResource");
            return null;
        }

        try {
            EObject proxy = EcoreUtil.create(eClass);
            if (proxy instanceof InternalEObject internalObject) {
                URI proxyUri = resolveProxyUri(unresolved.getTargetUri());
                internalObject.eSetProxyURI(proxyUri);
            }
            return proxy;
        } catch (Exception e) {
            String msg = String.format(
                    "Error creating proxy for reference %s -> %s: %s",
                    unresolved.getReference().getName(),
                    unresolved.getTargetUri(),
                    e.getMessage());
            LOGGER.warning(msg);
            diagnosticCollector.addWarning(msg, "CodecResource");
            return null;
        }
    }

    private URI resolveProxyUri(String uriString) {
        URI targetUri = URI.createURI(uriString);
        if (targetUri.isRelative() && nonNull(getURI())) {
            targetUri = targetUri.resolve(getURI());
        }
        return targetUri;
    }

    /** Runtime-only codec options that are NOT ConfigProperty keys and should NOT be treated as custom properties. */
    private static final Set<String> KNOWN_RUNTIME_OPTIONS = Set.of(
            CodecOptions.CODEC_ROOT_TYPE,
            CodecOptions.CODEC_ROOT_SCHEMA,
            CodecOptions.CODEC_FEATURE_TYPE_HINTS,
            CodecOptions.CODEC_TYPE_HINT_MODE,
            CodecOptions.CODEC_DESERIALIZATION_MODE,
            CodecOptions.CODEC_VALUE_READERS,
            CodecOptions.CODEC_VALUE_WRITERS,
            CodecOptions.CODEC_FEATURE_VALUE_READERS,
            CodecOptions.CODEC_FEATURE_VALUE_WRITERS,
            CodecOptions.CODEC_FEATURE_VALUE_READER_INSTANCES,
            CodecOptions.CODEC_FEATURE_VALUE_WRITER_INSTANCES,
            CodecOptions.CODEC_FAIL_FAST,
            CodecOptions.CODEC_SUPPRESS_WARNINGS,
            CodecOptions.CODEC_SUPPRESS_WARNING_SOURCES,
            CodecOptions.CODEC_DIAGNOSTIC_HANDLER,
            CodecOptions.CODEC_ECLASS_CONFIG,
            CodecOptions.CODEC_EREFERENCE_CONFIG,
            CodecOptions.CODEC_EATTRIBUTE_CONFIG
    );

    private static final Set<String> KNOWN_CONFIG_PROPERTY_KEYS = Arrays.stream(ConfigProperty.values())
            .map(ConfigProperty::getPropertyKey)
            .collect(Collectors.toUnmodifiableSet());

    /**
     * Extracts custom properties from options — all {@code codec.*} keys that are not
     * known {@link ConfigProperty} keys and not known runtime-only options.
     * <p>
     * Subclasses (e.g., {@code JsonSchemaResourceImpl}) can use this to extract
     * format-specific options from load/save option maps.
     * </p>
     *
     * @param options the raw options map (may be null)
     * @return an immutable map of custom properties, never null
     */
    protected static Map<String, Object> extractCustomProperties(Map<String, Object> options) {
        if (options == null || options.isEmpty()) {
            return Map.of();
        }
        Map<String, Object> custom = new HashMap<>();
        for (Map.Entry<String, Object> entry : options.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(CodecOptions.CODEC_PREFIX)
                    && !KNOWN_CONFIG_PROPERTY_KEYS.contains(key)
                    && !KNOWN_RUNTIME_OPTIONS.contains(key)) {
                custom.put(key, entry.getValue());
            }
        }
        return custom.isEmpty() ? Map.of() : Map.copyOf(custom);
    }

    private EObject resolveReference(String uri) {
        if (isNull(uri) || uri.isEmpty()) {
            return null;
        }

        try {
            if (uri.startsWith("#")) {
                String fragment = uri.substring(1);
                return getEObject(fragment);
            } else if (uri.startsWith("//")) {
                return getEObject(uri);
            } else if (uri.contains("#")) {
                URI emfUri = URI.createURI(uri);
                if (nonNull(getResourceSet())) {
                    return getResourceSet().getEObject(emfUri, true);
                } else {
                    String fragment = emfUri.fragment();
                    if (nonNull(fragment)) {
                        return getEObject(fragment);
                    }
                }
            } else {
                return getEObject(uri);
            }
        } catch (Exception e) {
            LOGGER.warning(() -> String.format(
                    "Error resolving reference URI '%s': %s", uri, e.getMessage()));
        }

        return null;
    }
}
