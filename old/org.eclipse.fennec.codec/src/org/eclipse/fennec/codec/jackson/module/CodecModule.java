/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.jackson.module;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.CodecEMFDeserializers;
import org.eclipse.fennec.codec.CodecEMFSerializers;
import org.eclipse.fennec.codec.CodecProxyFactory;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.jackson.databind.deser.ReferenceCodecInfoDeserializer;
import org.eclipse.fennec.codec.jackson.utils.BaseURIHandler;
import org.eclipse.fennec.codec.jackson.utils.URIHandler;

import tools.jackson.core.Version;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.deser.Deserializers;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.Serializers;

/**
 * Extension of EMFModule which allows to set codec specific options
 * 
 * @author mark
 * @since 02.08.2024
 */
public class CodecModule extends SimpleModule {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private String codecType;
	private String codecModuleName;
	private boolean serializeDefaultValue;
	private boolean serializeEmptyValue;
	private boolean serializeNullValue;
	private boolean useNamesFromExtendedMetaData;
	private boolean useId;
	private boolean idOnTop;
	private boolean serializeIdField;
	private boolean idFeatureAsPrimaryKey;
	private boolean deserializeType;
	private boolean serializeType;
	private boolean serializeSuperTypes;
	private boolean serializeAllSuperTypes;
	private boolean serializeSuperTypesAsArray;
	private String superTypeKey;
	private String refKey;
	private String proxyKey;
	private String timestampKey;
	private boolean writeEnumLiterals;
	private boolean sortPropertiesAlphabetically;
	private List<String> globalIgnoreFeatureNames;

	private CodecEMFSerializers serializers;
	private CodecEMFDeserializers deserializers;
	
	private ValueSerializer<EObject> referenceSerializer;
	private ValueDeserializer<EObject> referenceDeserializer;
	private URIHandler handler;

	private PackageCodecInfo codecModelInfo;
	private CodecModelInfo codecModelInfoService;

	private CodecProxyFactory codecProxyFactory;

	public String getCodecType() {
		return codecType;
	}

	public String getCodecModuleName() {
		return codecModuleName;
	}

	public boolean isSerializeDefaultValue() {
		return serializeDefaultValue;
	}

	public boolean isSerializeEmptyValue() {
		return serializeEmptyValue;
	}

	public boolean isSerializeNullValue() {
		return serializeNullValue;
	}

	public boolean isUseNamesFromExtendedMetaData() {
		return useNamesFromExtendedMetaData;
	}

	public boolean isUseId() {
		return useId;
	}

	public boolean isIdOnTop() {
		return idOnTop;
	}

	public boolean isSerializeIdField() {
		return serializeIdField;
	}

	public boolean isIdFeatureAsPrimaryKey() {
		return idFeatureAsPrimaryKey;
	}

	public boolean isDeserializeType() {
		return deserializeType;
	}

	public boolean isSerializeType() {
		return serializeType;
	}

	public boolean isSerializeSuperTypes() {
		return serializeSuperTypes;
	}

	public boolean isSerializeAllSuperTypes() {
		return serializeAllSuperTypes;
	}

	public boolean isSerializeSuperTypesAsArray() {
		return serializeSuperTypesAsArray;
	}

	public String getSuperTypeKey() {
		return superTypeKey;
	}

	public String getRefKey() {
		return refKey;
	}

	public String getProxyKey() {
		return proxyKey;
	}

	public String getTimestampKey() {
		return timestampKey;
	}
	
	public boolean isWriteEnumLiterals() {
		return writeEnumLiterals;
	}
	
	public boolean isSortPropertiesAlphabetically() {
		return sortPropertiesAlphabetically;
	}

	public List<String> getGlobalIgnoreFeatureNames() {
		return globalIgnoreFeatureNames != null ? globalIgnoreFeatureNames : Collections.emptyList();
	}

	public PackageCodecInfo getCodecModelInfo() {
		return codecModelInfo;
	}

	public CodecModelInfo getCodecModelInfoService() {
		return codecModelInfoService;
	}

	public CodecProxyFactory getProxyFactory() {
		return codecProxyFactory;
	}


	public CodecModule(Builder builder) {
		this.codecType = builder.codecType;
		this.codecModuleName = builder.codecModuleName;
		this.serializeSuperTypesAsArray = builder.serializeSuperTypesAsArray;
		this.serializeDefaultValue = builder.serializeDefaultValue;
		this.serializeEmptyValue = builder.serializeEmptyValue;
		this.serializeNullValue = builder.serializeNullValue;
		this.serializeSuperTypes = builder.serializeSuperTypes;
		this.serializeAllSuperTypes = builder.serializeAllSuperTypes;
		this.serializeType = builder.serializeType;
		this.deserializeType = builder.deserializeType;
		this.idFeatureAsPrimaryKey = builder.idFeatureAsPrimaryKey;
		this.superTypeKey = builder.superTypeKey;
		this.timestampKey = builder.timestampKey;
		this.proxyKey = builder.proxyKey;
		this.refKey = builder.refKey;
		this.useId = builder.useId;
		this.useNamesFromExtendedMetaData = builder.useNamesFromExtendedMetaData;
		this.idOnTop = builder.idOnTop;
		this.serializeIdField = builder.serializeIdField;
		this.codecModelInfo = builder.codecModelInfo;
		this.codecModelInfoService = builder.codecModelInfoService;
		this.writeEnumLiterals = builder.writeEnumLiterals;
		this.codecProxyFactory = builder.codecProxyFactory;
		this.setReferenceDeserializer(builder.referenceDeserializer);
		this.setUriHandler(builder.handler);
		this.setReferenceSerializer(builder.referenceSerializer);
		this.sortPropertiesAlphabetically = builder.sortPropertiesAlphabetically;
		this.globalIgnoreFeatureNames = builder.globalIgnoreFeatureNames;
		this.serializers = builder.serializers;
		this.deserializers = builder.deserializers;
	}

	
	
	/* 
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.module.SimpleModule#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return this.codecModuleName;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emfcloud.jackson.module.EMFModule#version()
	 */
	@Override
	public Version version() {
		return new Version(1, 0, 0, "SNAPSHOT", "org.eclipse.fennec", "org.eclipse.fennec.codec");
	}
	
	
	public void setReferenceDeserializer(final ValueDeserializer<EObject> deserializer) {
	      this.referenceDeserializer = deserializer;
	   }
	
	public ValueDeserializer<EObject> getReferenceDeserializer() { return referenceDeserializer; }
	
	public void setUriHandler(final URIHandler handler) { this.handler = handler; }

	public URIHandler getUriHandler() { return handler; }
	
	public ValueSerializer<EObject> getReferenceSerializer() {
		return this.referenceSerializer;
	}
	
	public void setReferenceSerializer(ValueSerializer<EObject> referenceSerializer) {
		this.referenceSerializer = referenceSerializer;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.module.SimpleModule#setupModule(com.fasterxml.jackson.databind.Module.SetupContext)
	 */
	@Override
	public void setupModule(final SetupContext context) {
		super.setupModule(context);
		
		if(referenceDeserializer == null) {
			referenceDeserializer = new ReferenceCodecInfoDeserializer(this, codecModelInfoService, null);
		}
		if(handler == null) {
	         handler = new BaseURIHandler();
	      }

//		CodecEMFSerializers serializers = new CodecEMFSerializers(this);
		serializers.bindCodecModule(this);
		context.addSerializers((Serializers) serializers);
		
//		CodecEMFDeserializers deserializers = new CodecEMFDeserializers(this);
		
		deserializers.bindCodecModule(this);
		context.addDeserializers((Deserializers) deserializers);
		
		if(codecProxyFactory == null) {
			codecProxyFactory = new CodecProxyFactory() {
				public EObject createProxy(EClass eClass, URI uri) {
					EObject object = EcoreUtil.create(eClass);
				
					if (object instanceof InternalEObject) {
						((InternalEObject) object).eSetProxyURI(uri);
					}
					return object;
				}
			};
		}
	}

	public static class Builder {

		private CodecProxyFactory codecProxyFactory;
		private PackageCodecInfo codecModelInfo;
		private CodecModelInfo codecModelInfoService;

		private String codecType = "json";
		private String codecModuleName = "gecko-codec-module";
		private boolean serializeDefaultValue = false;
		private boolean serializeEmptyValue = false;
		private boolean serializeNullValue = false;
		private boolean useNamesFromExtendedMetaData = true;
		private boolean useId = true;
		private boolean idOnTop = true;
		private boolean serializeIdField = false;
		private boolean idFeatureAsPrimaryKey = true;
		private boolean serializeType = true;
		private boolean deserializeType = false;
		private boolean serializeSuperTypes = false;
		private boolean serializeAllSuperTypes = false;
		private boolean serializeSuperTypesAsArray = true;
		private String superTypeKey = "_supertype";
		private String refKey = "$ref";
		private String proxyKey = "_proxy";
		private String timestampKey = "_timestamp";
		private boolean writeEnumLiterals = false;
		private ValueDeserializer<EObject> referenceDeserializer;
		private URIHandler handler;
		private ValueSerializer<EObject> referenceSerializer;
		private boolean sortPropertiesAlphabetically = false;
		private List<String> globalIgnoreFeatureNames = null;
		private CodecEMFSerializers serializers = null;
		private CodecEMFDeserializers deserializers = null;

		public Builder() {

		}

		public Builder withCodecType(String codecType) {
			this.codecType = codecType;
			return this;
		}

		public Builder withCodecModuleName(String codecModuleName) {
			this.codecModuleName = codecModuleName;
			return this;
		}
		
		public Builder withCodecEMFSerializers(CodecEMFSerializers serializers) {
			this.serializers = serializers;
			return this;
		}
		
		public Builder withCodecEMFDeserializers(CodecEMFDeserializers deserializers) {
			this.deserializers = deserializers;
			return this;
		}

		public Builder withSuperTypeKey(String superTypeKey) {
			this.superTypeKey = superTypeKey;
			return this;
		}

		public Builder withProxyKey(String proxyKey) {
			this.proxyKey = proxyKey;
			return this;
		}

		public Builder withRefKey(String refKey) {
			this.refKey = refKey;
			return this;
		}

		public Builder withTimestampKey(String timestampKey) {
			this.timestampKey = timestampKey;
			return this;
		}

		public Builder withSerializeDefaultValue(boolean serializeDefaultValue) {
			this.serializeDefaultValue = serializeDefaultValue;
			return this;
		}

		public Builder withSerializeEmptyValue(boolean serializeEmptyValue) {
			this.serializeEmptyValue = serializeEmptyValue;
			return this;
		}

		public Builder withSerializeNullValue(boolean serializeNullValue) {
			this.serializeNullValue = serializeNullValue;
			return this;
		}

		public Builder withUseNamesFromExtendedMetaData(boolean useNamesFromExtendedMetaData) {
			this.useNamesFromExtendedMetaData = useNamesFromExtendedMetaData;
			return this;
		}
		public Builder withUseId(boolean useId) {
			this.useId = useId;
			return this;
		}
		public Builder withIdOnTop(boolean idOnTop) {
			this.idOnTop = idOnTop;
			return this;
		}

		public Builder withSerializeIdField(boolean serializeIdField) {
			this.serializeIdField = serializeIdField;
			return this;
		}

		public Builder withIdFeatureAsPrimaryKey(boolean idFeatureAsPrimaryKey) {
			this.idFeatureAsPrimaryKey = idFeatureAsPrimaryKey;
			return this;
		}

		public Builder withDeserializeType(boolean deserializeType) {
			this.deserializeType = deserializeType;
			return this;
		}
		
		public Builder withSerializeType(boolean serializeType) {
			this.serializeType = serializeType;
			return this;
		}

		public Builder withSerializeSuperTypes(boolean serializeSuperTypes) {
			this.serializeSuperTypes = serializeSuperTypes;
			return this;
		}

		public Builder withSerializeAllSuperTypes(boolean serializeAllSuperTypes) {
			this.serializeAllSuperTypes = serializeAllSuperTypes;
			return this;
		}

		public Builder withSerailizeSuperTypesAsArray(boolean serializeSuperTypesAsArray) {
			this.serializeSuperTypesAsArray = serializeSuperTypesAsArray;
			return this;
		}
		
		public Builder withWriteEnumLiterals(boolean writeEnumLiterals) {
			this.writeEnumLiterals = writeEnumLiterals;
			return this;
		}
		
		public Builder withSortPropertiesAlphabetically(boolean sortPropertiesAlphabetically) {
			this.sortPropertiesAlphabetically = sortPropertiesAlphabetically;
			return this;
		}

		public Builder withGlobalIgnoreFeatureNames(List<String> globalIgnoreFeatureNames) {
			this.globalIgnoreFeatureNames = globalIgnoreFeatureNames;
			return this;
		}

		public Builder bindCodecModelInfo(PackageCodecInfo codecModelInfo) {
			this.codecModelInfo = codecModelInfo;
			return this;
		}

		public Builder bindCodecModelInfoService(CodecModelInfo codecModelInfoService) {
			this.codecModelInfoService = codecModelInfoService;
			return this;
		}
	
		public Builder bindCodecProxyFactory(CodecProxyFactory codecProxyFactory) {
			this.codecProxyFactory = codecProxyFactory;
			return this;
		}
		public Builder bindReferenceDeserializer(ValueDeserializer<EObject> referenceDeserializer) {
			this.referenceDeserializer = referenceDeserializer;
			return this;			
		}
		
		public Builder bindURIHandler(URIHandler handler) {
			this.handler = handler;
			return this;
		}
		
		public Builder bindReferenceSerializer(ValueSerializer<EObject> referenceSerializer) {
			this.referenceSerializer = referenceSerializer;
			return this;
		}
		
		

		public CodecModule build() {
			return new CodecModule(this);
		}

	}

}
