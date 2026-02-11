/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.jsonschema.writers;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.jackson.databind.EMFCodecContext;
import org.eclipse.fennec.codec.jackson.databind.ser.CodecEObjectSerializer;
import org.eclipse.fennec.codec.jackson.module.CodecModule;
import org.eclipse.fennec.codec.options.CodecResourceOptions;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * 
 * @author ilenia
 * @since Aug 11, 2025
 */
public class SmartJsonSchemaSerializer extends ValueSerializer<EObject> {
	
	private CodecModule codecModule;
	private CodecModelInfo codecModelInfoService;

	public SmartJsonSchemaSerializer(CodecModule codecModule, CodecModelInfo codecModelInfoService) {
		this.codecModule = codecModule;
		this.codecModelInfoService = codecModelInfoService;
	}
	
	private EClassCodecInfo extractModelInfo(EClass type) {
		PackageCodecInfo codecModelInfo = codecModule.getCodecModelInfo();
		EClassCodecInfo eObjCodecInfo = null;
		if(type != null) {
			for(EClassCodecInfo eci : codecModelInfo.getEClassCodecInfo()) {
				if(eci.getClassifier().equals(type)) {
					eObjCodecInfo = eci;
					break;
				}
			}
		}
//		we look in other packages
		if(eObjCodecInfo == null) {
			eObjCodecInfo = codecModelInfoService.getCodecInfoForEClass(type).orElse(null);
		}
		return eObjCodecInfo;
	}
	
	private boolean isRootObject(TokenStreamContext codecContext) {
		if(codecContext.getParent() == null) return true;
		if(codecContext.inObject() && codecContext.getParent().inRoot()) return true; //if we are in a Resource then the root object has as parent the Resource ctxt
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see tools.jackson.databind.ValueSerializer#serialize(java.lang.Object, tools.jackson.core.JsonGenerator, tools.jackson.databind.SerializationContext)
	 */
	@Override
	public void serialize(EObject value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
		
		if(gen.streamWriteContext() instanceof EMFCodecContext codecCtxt) {
			if(isRootObject(gen.streamWriteContext()) && codecCtxt.getCurrentFeature() == null) {
    			if(ctxt.getAttribute(CodecResourceOptions.CODEC_ROOT_OBJECT) != null) {
    				EClass type  = (EClass) ctxt.getAttribute(CodecResourceOptions.CODEC_ROOT_OBJECT);
    				EClassCodecInfo eObjCodecInfo = extractModelInfo(type);
    				if(eObjCodecInfo.getCodecExtraProperties().containsKey("jsonschema") && value instanceof EPackage ePackage) {
        				if(eObjCodecInfo.getCodecExtraProperties().containsKey("jsonschema.feature.key")) new EnhancedEPackageToJsonSchemaSerializer(eObjCodecInfo.getCodecExtraProperties().get("jsonschema.feature.key")).serialize(ePackage, gen, ctxt);
        				else new EnhancedEPackageToJsonSchemaSerializer().serialize(ePackage, gen, ctxt);
        			} else {
        				new CodecEObjectSerializer(codecModule, codecModelInfoService).serialize(value, gen, ctxt);
        			}
    			} else {
    				throw new IllegalArgumentException("No CODEC_ROOT_OBJECT option found for root object! Something is wrong!");
    			}
    		}  else {
    			EStructuralFeature currentFeature = codecCtxt.getCurrentFeature();
    			if(currentFeature != null) {
    				EClassCodecInfo eObjCodecInfo = extractModelInfo(currentFeature.getEContainingClass());
        			FeatureCodecInfo featureCodecInfo = eObjCodecInfo.getReferenceCodecInfo().stream().filter(r -> r.getFeature().getName().equals(currentFeature.getName())).findFirst().orElse(null);
        			if(featureCodecInfo == null) {
        				throw new IllegalArgumentException(String.format("Cannot retrieve FeatureCodecInfo for current EStructuralFeature %s. Something went wrong!", currentFeature.getName()));
        			}
        			if(featureCodecInfo.getCodecExtraProperties().containsKey("jsonschema") && value instanceof EPackage ePackage) {
        				if(featureCodecInfo.getCodecExtraProperties().containsKey("jsonschema.feature.key")) new EnhancedEPackageToJsonSchemaSerializer(featureCodecInfo.getCodecExtraProperties().get("jsonschema.feature.key")).serialize(ePackage, gen, ctxt);
        				else new EnhancedEPackageToJsonSchemaSerializer().serialize(ePackage, gen, ctxt);
        			} else {
        				new CodecEObjectSerializer(codecModule, codecModelInfoService).serialize(value, gen, ctxt);
        			}
    			} else {
    				new CodecEObjectSerializer(codecModule, codecModelInfoService).serialize(value, gen, ctxt);
    			}
    		}
		}
			 else {
	    			throw new IllegalArgumentException("No EMFCodecContext found. Something is wrong!");
	    		
	    		}
    		
			
			
		
	}

}
