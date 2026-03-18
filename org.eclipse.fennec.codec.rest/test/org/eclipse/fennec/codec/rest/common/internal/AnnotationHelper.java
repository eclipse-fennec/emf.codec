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
package org.eclipse.fennec.codec.rest.common.internal;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import org.eclipse.fennec.codec.rest.annotations.json.CodecConfig;
import org.eclipse.fennec.codec.rest.annotations.json.RootElement;

/**
 * Helper for creating annotation instances in tests.
 */
final class AnnotationHelper {

	private AnnotationHelper() {}

	static RootElement rootElement(String rootType, String rootSchema) {
		return new RootElement() {
			@Override public Class<? extends Annotation> annotationType() { return RootElement.class; }
			@Override public String rootType() { return rootType; }
			@Override public String rootSchema() { return rootSchema; }
		};
	}

	static CodecConfig codecConfigDefaults() {
		return codecConfig(b -> {});
	}

	static CodecConfig codecConfig(Consumer<CodecConfigBuilder> customizer) {
		CodecConfigBuilder builder = new CodecConfigBuilder();
		customizer.accept(builder);
		return builder.build();
	}

	static class CodecConfigBuilder {
		// Feature
		private String dateFormat = "";
		private boolean serializeDefaultValues = false;
		private boolean serializeEmptyValues = false;
		private boolean serializeNullValues = false;
		private String enumSerialization = "LITERAL";
		private String valueReaderName = "";
		private String valueWriterName = "";
		// Type
		private String typeStrategy = "URI";
		private String typeFormat = "PLAIN";
		private String typeKey = "";
		private boolean typeInclude = true;
		private String typeNameKey = "";
		private String typeSchemaKey = "";
		// ID
		private String idStrategy = "ID_FIELD";
		private String idFormat = "PLAIN";
		private String idKey = "";
		private String idValueKey = "";
		private String idKeyMode = "ID_ONLY";
		private boolean idOnTop = false;
		// Reference
		private String refFormat = "STRUCTURED";
		private String refKey = "";
		private String refTypeKey = "";
		// SuperType
		private boolean superTypeSerialize = false;
		private String superTypeKey = "";
		private String superTypeStrategy = "ALL";
		// Global
		private boolean smartCompression = false;

		CodecConfigBuilder dateFormat(String v) { this.dateFormat = v; return this; }
		CodecConfigBuilder serializeDefaultValues(boolean v) { this.serializeDefaultValues = v; return this; }
		CodecConfigBuilder serializeEmptyValues(boolean v) { this.serializeEmptyValues = v; return this; }
		CodecConfigBuilder serializeNullValues(boolean v) { this.serializeNullValues = v; return this; }
		CodecConfigBuilder enumSerialization(String v) { this.enumSerialization = v; return this; }
		CodecConfigBuilder valueReaderName(String v) { this.valueReaderName = v; return this; }
		CodecConfigBuilder valueWriterName(String v) { this.valueWriterName = v; return this; }
		CodecConfigBuilder typeStrategy(String v) { this.typeStrategy = v; return this; }
		CodecConfigBuilder typeFormat(String v) { this.typeFormat = v; return this; }
		CodecConfigBuilder typeKey(String v) { this.typeKey = v; return this; }
		CodecConfigBuilder typeInclude(boolean v) { this.typeInclude = v; return this; }
		CodecConfigBuilder typeNameKey(String v) { this.typeNameKey = v; return this; }
		CodecConfigBuilder typeSchemaKey(String v) { this.typeSchemaKey = v; return this; }
		CodecConfigBuilder idStrategy(String v) { this.idStrategy = v; return this; }
		CodecConfigBuilder idFormat(String v) { this.idFormat = v; return this; }
		CodecConfigBuilder idKey(String v) { this.idKey = v; return this; }
		CodecConfigBuilder idValueKey(String v) { this.idValueKey = v; return this; }
		CodecConfigBuilder idKeyMode(String v) { this.idKeyMode = v; return this; }
		CodecConfigBuilder idOnTop(boolean v) { this.idOnTop = v; return this; }
		CodecConfigBuilder refFormat(String v) { this.refFormat = v; return this; }
		CodecConfigBuilder refKey(String v) { this.refKey = v; return this; }
		CodecConfigBuilder refTypeKey(String v) { this.refTypeKey = v; return this; }
		CodecConfigBuilder superTypeSerialize(boolean v) { this.superTypeSerialize = v; return this; }
		CodecConfigBuilder superTypeKey(String v) { this.superTypeKey = v; return this; }
		CodecConfigBuilder superTypeStrategy(String v) { this.superTypeStrategy = v; return this; }
		CodecConfigBuilder smartCompression(boolean v) { this.smartCompression = v; return this; }

		CodecConfig build() {
			return new CodecConfig() {
				@Override public Class<? extends Annotation> annotationType() { return CodecConfig.class; }
				// Feature
				@Override public String dateFormat() { return dateFormat; }
				@Override public boolean serializeDefaultValues() { return serializeDefaultValues; }
				@Override public boolean serializeEmptyValues() { return serializeEmptyValues; }
				@Override public boolean serializeNullValues() { return serializeNullValues; }
				@Override public String enumSerialization() { return enumSerialization; }
				@Override public String valueReaderName() { return valueReaderName; }
				@Override public String valueWriterName() { return valueWriterName; }
				// Type
				@Override public String typeStrategy() { return typeStrategy; }
				@Override public String typeFormat() { return typeFormat; }
				@Override public String typeKey() { return typeKey; }
				@Override public boolean typeInclude() { return typeInclude; }
				@Override public String typeNameKey() { return typeNameKey; }
				@Override public String typeSchemaKey() { return typeSchemaKey; }
				// ID
				@Override public String idStrategy() { return idStrategy; }
				@Override public String idFormat() { return idFormat; }
				@Override public String idKey() { return idKey; }
				@Override public String idValueKey() { return idValueKey; }
				@Override public String idKeyMode() { return idKeyMode; }
				@Override public boolean idOnTop() { return idOnTop; }
				// Reference
				@Override public String refFormat() { return refFormat; }
				@Override public String refKey() { return refKey; }
				@Override public String refTypeKey() { return refTypeKey; }
				// SuperType
				@Override public boolean superTypeSerialize() { return superTypeSerialize; }
				@Override public String superTypeKey() { return superTypeKey; }
				@Override public String superTypeStrategy() { return superTypeStrategy; }
				// Global
				@Override public boolean smartCompression() { return smartCompression; }
			};
		}
	}
}
