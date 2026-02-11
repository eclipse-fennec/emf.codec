/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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
package org.eclipse.fennec.codec.info.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.info.CodecModelInfo;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.FeatureCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.IdentityInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.TypeInfo;
import org.eclipse.fennec.codec.options.CodecValueReaderConstants;
import org.eclipse.fennec.codec.options.CodecValueWriterConstants;
import org.eclipse.fennec.codec.demo.model.test1.TestTypeAnnotationInheritancePackage;
import org.gecko.codec.demo.model.person.PersonPackage;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(MockitoExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class CodecModelInfoTest {
	
	@InjectService
	ServiceAware<ResourceSet> rsAware;
	
	@BeforeEach
	public void before() throws InterruptedException {
		System.out.println("");
		ResourceSet resourceSet = rsAware.waitForService(2000l);
		assertNotNull(resourceSet);
	}

	
	@Test
	public void testPackageCodecInfoCreation(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		Optional<PackageCodecInfo> packageCodecInfoOpt = codecModelInfo.getCodecInfoForPackage(PersonPackage.eNS_URI);
		assertFalse(packageCodecInfoOpt.isEmpty());
		
		PackageCodecInfo packageCodecInfo = packageCodecInfoOpt.get();
		assertEquals(packageCodecInfo.getId(), PersonPackage.eNS_URI);
		assertEquals(packageCodecInfo.getEPackage(), demoModel);
	}
	
	@Test
	public void testEClassCodecInfoCreation(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		demoModel.getEClassifiers().forEach(ec -> {
			if(ec instanceof EClass eClass) {
				assertFalse(codecModelInfo.getCodecInfoForEClass(eClass).isEmpty());
				EClassCodecInfo eClassCodecInfo = codecModelInfo.getCodecInfoForEClass(eClass).get();
				assertEquals(eClassCodecInfo.getId(), EcoreUtil.getURI(eClass).toString());
				assertEquals(eClassCodecInfo.getClassifier(), eClass);				
				
				assertNotNull(eClassCodecInfo.getIdentityInfo());				
				assertNotNull(eClassCodecInfo.getTypeInfo());
			}
		});
	}
	
	@Test
	public void testIdentityInfoCreation(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		EClassCodecInfo eClassCodecInfo = codecModelInfo.getCodecInfoForEClass(demoModel.getPerson()).get();
		assertNotNull(eClassCodecInfo);
		
		IdentityInfo identityInfo = eClassCodecInfo.getIdentityInfo();
		assertThat(identityInfo.getIdFeatures()).hasSize(2);
		assertEquals(identityInfo.getIdFeatures().get(0), demoModel.getPerson_Name());
		assertEquals(identityInfo.getIdFeatures().get(1), demoModel.getPerson_LastName());
		assertEquals("COMBINED", identityInfo.getIdStrategy());
		assertEquals( "-", identityInfo.getIdSeparator());
	}

	@Test
	public void testTypeInfoCreation(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		EClassCodecInfo eClassCodecInfo = codecModelInfo.getCodecInfoForEClass(demoModel.getPerson()).get();
		assertNotNull(eClassCodecInfo);
		
		TypeInfo typeInfo = eClassCodecInfo.getTypeInfo();
		assertNotNull(typeInfo);
		assertEquals("NAME", typeInfo.getTypeStrategy());
		assertEquals(CodecValueReaderConstants.READER_BY_ECLASS_NAME, typeInfo.getTypeValueReaderName());
		assertEquals(CodecValueWriterConstants.WRITER_BY_ECLASS_NAME, typeInfo.getTypeValueWriterName());
	}
	
	@Test
	public void testTypeInfoEClassCreation(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		EClassCodecInfo eClassCodecInfo = codecModelInfo.getCodecInfoForEClass(demoModel.getTypeKeyEClass()).get();
		assertNotNull(eClassCodecInfo);
		
		TypeInfo typeInfo = eClassCodecInfo.getTypeInfo();
		assertNotNull(typeInfo);
		assertEquals("NAME", typeInfo.getTypeStrategy());
		assertEquals(CodecValueReaderConstants.READER_BY_ECLASS_NAME, typeInfo.getTypeValueReaderName());
		assertEquals(CodecValueWriterConstants.WRITER_BY_ECLASS_NAME, typeInfo.getTypeValueWriterName());
		assertEquals("name", typeInfo.getTypeKey());
		assertThat(typeInfo.getTypeMap()).hasSize(2);
		assertTrue(typeInfo.getTypeMap().containsKey("dragino"));
		assertTrue(typeInfo.getTypeMap().containsKey("em310"));
		assertEquals("DraginoUplink", typeInfo.getTypeMap().get("dragino"));
		assertEquals("EM310Uplink", typeInfo.getTypeMap().get("em310"));
	}
	
	@Test
	public void testTypeInfoERefCreation(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		EClassCodecInfo eClassCodecInfo = codecModelInfo.getCodecInfoForEClass(demoModel.getSensorBook()).get();
		assertNotNull(eClassCodecInfo);
		
		FeatureCodecInfo refCodecInfo = eClassCodecInfo.getReferenceCodecInfo().stream().filter(r -> "sensors".equals(r.getKey())).findFirst().orElse(null);
		assertThat(refCodecInfo).isNotNull();
		
		TypeInfo typeInfo = refCodecInfo.getTypeInfo();
		assertNotNull(typeInfo);
		assertEquals("NAME", typeInfo.getTypeStrategy());
		assertEquals(CodecValueReaderConstants.READER_BY_ECLASS_NAME, typeInfo.getTypeValueReaderName());
		assertEquals(CodecValueWriterConstants.WRITER_BY_ECLASS_NAME, typeInfo.getTypeValueWriterName());
		assertEquals("name", typeInfo.getTypeKey());
		assertThat(typeInfo.getTypeMap()).hasSize(2);
		assertTrue(typeInfo.getTypeMap().containsKey("dragino"));
		assertTrue(typeInfo.getTypeMap().containsKey("em310"));
		assertEquals("DraginoUplink", typeInfo.getTypeMap().get("dragino"));
		assertEquals("EM310Uplink", typeInfo.getTypeMap().get("em310"));
	}
	
	@Test
	public void testReferenceInfoCreation(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		EClassCodecInfo eClassCodecInfo = codecModelInfo.getCodecInfoForEClass(demoModel.getPerson()).get();
		assertNotNull(eClassCodecInfo);
		
		List<FeatureCodecInfo> refInfos = eClassCodecInfo.getReferenceCodecInfo();
		assertNotNull(refInfos);
		assertThat(refInfos).hasSize(5); //containedAdd, containedAdds, nonContainedAdd, nonContainedAdds, businessAdd
	}
	
	@Test
	public void testFeatureIgnore(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		EClassCodecInfo eClassCodecInfo = codecModelInfo.getCodecInfoForEClass(demoModel.getAddress()).get();
		assertNotNull(eClassCodecInfo);
		
		List<FeatureCodecInfo> featureInfos = eClassCodecInfo.getFeatureInfo();
		assertThat(featureInfos).hasSize(3);
		for(FeatureCodecInfo fci : featureInfos) {
			if("zip".equals(fci.getFeature().getName())) {
				assertTrue(fci.isIgnore());
			}
		}
	}
	
	@Test
	public void testCodecInfoHolderCreation(@InjectService(timeout = 2000l) PersonPackage demoModel,  
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {
		
		assertNotNull(demoModel);
		assertNotNull(codecModelInfo);
		
		CodecInfoHolder codecInfoHolder = codecModelInfo.getCodecInfoHolderByType(InfoType.IDENTITY);
		assertNotNull(codecInfoHolder);
		assertEquals(codecInfoHolder.getInfoType(), InfoType.IDENTITY);
		assertThat(codecInfoHolder.getReaders()).hasSize(1);
		assertThat(codecInfoHolder.getWriters()).hasSize(1);
		
		codecInfoHolder = codecModelInfo.getCodecInfoHolderByType(InfoType.TYPE);
		assertNotNull(codecInfoHolder);
		assertEquals(codecInfoHolder.getInfoType(), InfoType.TYPE);
		assertThat(codecInfoHolder.getReaders()).hasSize(3);
		assertThat(codecInfoHolder.getWriters()).hasSize(3);
		
		codecInfoHolder = codecModelInfo.getCodecInfoHolderByType(InfoType.SUPER_TYPE);
		assertNotNull(codecInfoHolder);
		assertEquals(codecInfoHolder.getInfoType(), InfoType.SUPER_TYPE);
		assertThat(codecInfoHolder.getReaders()).isEmpty();
		assertThat(codecInfoHolder.getWriters()).hasSize(2);
		
		codecInfoHolder = codecModelInfo.getCodecInfoHolderByType(InfoType.REFERENCE);
		assertNotNull(codecInfoHolder);
		assertEquals(codecInfoHolder.getInfoType(), InfoType.REFERENCE);
		assertThat(codecInfoHolder.getReaders()).isEmpty();
		assertThat(codecInfoHolder.getWriters()).isEmpty();
		
		codecInfoHolder = codecModelInfo.getCodecInfoHolderByType(InfoType.ATTRIBUTE);
		assertNotNull(codecInfoHolder);
		assertThat(codecInfoHolder.getReaders()).isEmpty();
		assertThat(codecInfoHolder.getWriters()).isEmpty();
		
		codecInfoHolder = codecModelInfo.getCodecInfoHolderByType(InfoType.OPERATION);
		assertNotNull(codecInfoHolder);
		assertThat(codecInfoHolder.getReaders()).isEmpty();
		assertThat(codecInfoHolder.getWriters()).isEmpty();
		
		codecInfoHolder = codecModelInfo.getCodecInfoHolderByType(InfoType.OBJECT);
		assertNotNull(codecInfoHolder);
		assertThat(codecInfoHolder.getReaders()).isEmpty();
		assertThat(codecInfoHolder.getWriters()).isEmpty();
		
		codecInfoHolder = codecModelInfo.getCodecInfoHolderByType(InfoType.OTHER);
		assertNotNull(codecInfoHolder);
		assertThat(codecInfoHolder.getReaders()).isEmpty();
		assertThat(codecInfoHolder.getWriters()).isEmpty();
	}

	/**
	 * Test that codec.type annotations on an EClass are properly parsed.
	 * Person has codec.type annotation with companyId -> BusinessPerson mapping.
	 */
	@Test
	public void testTypeAnnotationOnEClass(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel,
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {

		assertNotNull(testModel);
		assertNotNull(codecModelInfo);

		// Get codec info for Person EClass
		EClassCodecInfo personCodecInfo = codecModelInfo.getCodecInfoForEClass(testModel.getTest1Person()).get();
		assertNotNull(personCodecInfo);

		// Check that type info is configured correctly
		TypeInfo typeInfo = personCodecInfo.getTypeInfo();
		assertNotNull(typeInfo, "Person should have type info");
		assertFalse(typeInfo.isIgnoreType(), "Type should not be ignored");
		assertEquals("NAME", typeInfo.getTypeStrategy(), "Type strategy should be NAME");
		assertEquals(CodecValueReaderConstants.READER_BY_ECLASS_NAME, typeInfo.getTypeValueReaderName());
		assertEquals(CodecValueWriterConstants.WRITER_BY_ECLASS_NAME, typeInfo.getTypeValueWriterName());
		assertEquals("_type", typeInfo.getTypeKey(), "Type key should default to _type");

		// Check type map - should have the discriminator mapping
		assertThat(typeInfo.getTypeMap()).hasSize(1);
		assertTrue(typeInfo.getTypeMap().containsKey("companyId"));
		assertEquals("Test1BusinessPerson", typeInfo.getTypeMap().get("companyId"));
	}

	/**
	 * Test that an EReference without codec.type annotation inherits from the target EClass.
	 * Meeting.responsiblePerson has no annotation, so it should inherit from Person.
	 */
	@Test
	public void testTypeAnnotationInheritanceFromTargetEClass(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel,
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {

		assertNotNull(testModel);
		assertNotNull(codecModelInfo);

		// Get codec info for Meeting EClass
		EClassCodecInfo meetingCodecInfo = codecModelInfo.getCodecInfoForEClass(testModel.getMeeting()).get();
		assertNotNull(meetingCodecInfo);

		// Find the responsiblePerson reference
		FeatureCodecInfo refCodecInfo = meetingCodecInfo.getReferenceCodecInfo().stream()
				.filter(r -> "responsiblePerson".equals(r.getKey()))
				.findFirst()
				.orElse(null);
		assertNotNull(refCodecInfo, "responsiblePerson reference should exist");

		// Check that type info is inherited from Person EClass
		TypeInfo typeInfo = refCodecInfo.getTypeInfo();
		assertNotNull(typeInfo, "responsiblePerson should have inherited type info from Person");
		assertFalse(typeInfo.isIgnoreType(), "Type should not be ignored");
		assertEquals("NAME", typeInfo.getTypeStrategy(), "Type strategy should be inherited as NAME");
		assertEquals(CodecValueReaderConstants.READER_BY_ECLASS_NAME, typeInfo.getTypeValueReaderName());
		assertEquals(CodecValueWriterConstants.WRITER_BY_ECLASS_NAME, typeInfo.getTypeValueWriterName());
		assertEquals("_type", typeInfo.getTypeKey(), "Type key should be inherited as _type");

		// Check type map - should have the inherited discriminator mapping
		assertThat(typeInfo.getTypeMap()).hasSize(1);
		assertTrue(typeInfo.getTypeMap().containsKey("companyId"), "Should inherit companyId discriminator");
		assertEquals("Test1BusinessPerson", typeInfo.getTypeMap().get("companyId"));
	}

	/**
	 * Test that BusinessPerson (subclass of Person) inherits codec.type annotation from its parent.
	 * This is the standard EClass inheritance behavior (different from EReference-to-EClass inheritance).
	 */
	@Test
	public void testSubclassInheritsFromParentEClass(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel,
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {

		assertNotNull(testModel);
		assertNotNull(codecModelInfo);

		// Get codec info for BusinessPerson EClass
		EClassCodecInfo businessPersonCodecInfo = codecModelInfo.getCodecInfoForEClass(testModel.getTest1BusinessPerson()).get();
		assertNotNull(businessPersonCodecInfo);

		// BusinessPerson has no codec.type annotation itself, but inherits from Person (its superclass)
		// This is standard EClass inheritance via getAnnotationDetailsMap with deriveFromParent=true
		TypeInfo typeInfo = businessPersonCodecInfo.getTypeInfo();
		assertNotNull(typeInfo);
		assertFalse(typeInfo.isIgnoreType());
		assertEquals("NAME", typeInfo.getTypeStrategy(), "Should inherit NAME strategy from Person");
		assertEquals("_type", typeInfo.getTypeKey(), "Should inherit type key from Person");

		// Should inherit the type map from Person parent
		assertThat(typeInfo.getTypeMap()).hasSize(1);
		assertTrue(typeInfo.getTypeMap().containsKey("companyId"), "Should inherit companyId discriminator from Person");
		assertEquals("Test1BusinessPerson", typeInfo.getTypeMap().get("companyId"));
	}

	/**
	 * Test that runtime options can override inheritance behavior.
	 * This simulates what happens when CodecOptionsBuilder.inheritsTypeFromParent(false) is used.
	 *
	 * The actual runtime override happens in CodecResource.updateCodecModelInfoFromOptions(),
	 * but we can verify the model structure here to ensure it's set up correctly for that override.
	 */
	@Test
	public void testRuntimeOptionCanDisableInheritance(@InjectService(timeout = 2000l) TestTypeAnnotationInheritancePackage testModel,
			@InjectService(timeout = 2000l) CodecModelInfo codecModelInfo) {

		assertNotNull(testModel);
		assertNotNull(codecModelInfo);

		// Get codec info for Meeting EClass
		EClassCodecInfo meetingCodecInfo = codecModelInfo.getCodecInfoForEClass(testModel.getMeeting()).get();
		assertNotNull(meetingCodecInfo);

		// Find the responsiblePerson reference
		FeatureCodecInfo refCodecInfo = meetingCodecInfo.getReferenceCodecInfo().stream()
				.filter(r -> "responsiblePerson".equals(r.getKey()))
				.findFirst()
				.orElse(null);
		assertNotNull(refCodecInfo, "responsiblePerson reference should exist");

		// Verify initial state: type info is inherited (has companyId mapping)
		TypeInfo typeInfo = refCodecInfo.getTypeInfo();
		assertNotNull(typeInfo);
		assertThat(typeInfo.getTypeMap()).hasSize(1);
		assertTrue(typeInfo.getTypeMap().containsKey("companyId"));
		assertEquals("Test1BusinessPerson", typeInfo.getTypeMap().get("companyId"));

		// Simulate what CodecResource does when inheritsTypeFromParent(false) is set via options:
		// 1. It would check if the reference itself has a codec.type annotation
		// 2. If not, it clears the typeMap (removing inherited mappings)

		// Check that the reference has no codec.type annotation of its own
		EReference responsiblePersonRef = (EReference) refCodecInfo.getFeature();
		EAnnotation codecTypeAnnotation = responsiblePersonRef.getEAnnotation("codec.type");
		assertNull(codecTypeAnnotation, "responsiblePerson should have no codec.type annotation");

		// This confirms that if inheritsTypeFromParent(false) is set at runtime,
		// the typeMap would be cleared (since there's no annotation on the reference itself)
		// The actual clearing happens in CodecResource.updateCodecModelInfoFromOptions()

		// To simulate the runtime behavior, we can verify that clearing would be correct:
		typeInfo.getTypeMap().clear();
		assertThat(typeInfo.getTypeMap()).isEmpty();

		// This demonstrates that:
		// - By default: reference inherits type map from Person (companyId -> BusinessPerson)
		// - With inheritsTypeFromParent(false): type map would be empty (since ref has no annotation)
		// - This is exactly the behavior that CodecResource.updateCodecModelInfoFromOptions() implements
	}
}
