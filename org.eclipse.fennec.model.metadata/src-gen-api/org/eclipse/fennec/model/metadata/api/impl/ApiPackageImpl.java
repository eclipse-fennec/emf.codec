/**
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
 */
package org.eclipse.fennec.model.metadata.api.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.model.metadata.MetadataPackage;

import org.eclipse.fennec.model.metadata.api.ApiFactory;
import org.eclipse.fennec.model.metadata.api.ApiPackage;
import org.eclipse.fennec.model.metadata.api.AspectProvider;
import org.eclipse.fennec.model.metadata.api.MetadataHandler;
import org.eclipse.fennec.model.metadata.api.MetadataIndex;
import org.eclipse.fennec.model.metadata.api.MetadataIndexReader;
import org.eclipse.fennec.model.metadata.api.MetadataIndexWriter;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ApiPackageImpl extends EPackageImpl implements ApiPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataIndexReaderEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataIndexWriterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataIndexEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataServiceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataHandlerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataWhiteboardEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass aspectProviderEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.model.metadata.api.ApiPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ApiPackageImpl() {
		super(eNS_URI, ApiFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link ApiPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ApiPackage init() {
		if (isInited) return (ApiPackage)EPackage.Registry.INSTANCE.getEPackage(ApiPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredApiPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		ApiPackageImpl theApiPackage = registeredApiPackage instanceof ApiPackageImpl ? (ApiPackageImpl)registeredApiPackage : new ApiPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		MetadataPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theApiPackage.createPackageContents();

		// Initialize created meta-data
		theApiPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theApiPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ApiPackage.eNS_URI, theApiPackage);
		return theApiPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataIndexReader() {
		return metadataIndexReaderEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexReader__FindByInstanceClassName__String_String() {
		return metadataIndexReaderEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexReader__FindAllByInstanceClassName__String() {
		return metadataIndexReaderEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexReader__FindByClassName__String_String() {
		return metadataIndexReaderEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexReader__FindAllByClassName__String() {
		return metadataIndexReaderEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexReader__FindClassByURI__String() {
		return metadataIndexReaderEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexReader__FindFeatureByURI__String() {
		return metadataIndexReaderEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexReader__FindClassesByAnnotation__String_String_String() {
		return metadataIndexReaderEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexReader__FindFeaturesByAnnotation__String_String_String() {
		return metadataIndexReaderEClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataIndexWriter() {
		return metadataIndexWriterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexWriter__IndexPackage__PackageMetadata() {
		return metadataIndexWriterEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexWriter__IndexClass__ClassMetadata() {
		return metadataIndexWriterEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexWriter__IndexFeature__FeatureMetadata() {
		return metadataIndexWriterEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexWriter__RemovePackage__PackageMetadata() {
		return metadataIndexWriterEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexWriter__RemoveClass__ClassMetadata() {
		return metadataIndexWriterEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexWriter__RemoveFeature__FeatureMetadata() {
		return metadataIndexWriterEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataIndexWriter__Clear() {
		return metadataIndexWriterEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataIndex() {
		return metadataIndexEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataService() {
		return metadataServiceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetIndexReader() {
		return metadataServiceEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetPackageMetadata__String() {
		return metadataServiceEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetClassMetadata__EClass() {
		return metadataServiceEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetClassMetadataByURI__String() {
		return metadataServiceEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetClassMetadataByName__String_String() {
		return metadataServiceEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetFeatureMetadata__EStructuralFeature() {
		return metadataServiceEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetFeatureMetadataByURI__String() {
		return metadataServiceEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetFeatureMetadataByName__String_String_String() {
		return metadataServiceEClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetFeatureMetadataFromClass__String_ClassMetadata() {
		return metadataServiceEClass.getEOperations().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetPackageAspect__EPackage_String() {
		return metadataServiceEClass.getEOperations().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetClassAspect__EClass_String() {
		return metadataServiceEClass.getEOperations().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetFeatureAspect__EStructuralFeature_String() {
		return metadataServiceEClass.getEOperations().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetPackageProfile__EPackage_String() {
		return metadataServiceEClass.getEOperations().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetPackageProfileByNsURI__String_String() {
		return metadataServiceEClass.getEOperations().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetClassProfile__EClass_String() {
		return metadataServiceEClass.getEOperations().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetClassProfileByURI__String_String() {
		return metadataServiceEClass.getEOperations().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataService__GetRegistry() {
		return metadataServiceEClass.getEOperations().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataHandler() {
		return metadataHandlerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataHandler__OnPackageRegistered__PackageMetadata() {
		return metadataHandlerEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataHandler__OnPackageUnregistered__PackageMetadata() {
		return metadataHandlerEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataHandler__Clear() {
		return metadataHandlerEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataWhiteboard() {
		return metadataWhiteboardEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__RegisterPackage__EPackage() {
		return metadataWhiteboardEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__UnregisterPackage__EPackage() {
		return metadataWhiteboardEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__RegisterAspectProvider__AspectProvider() {
		return metadataWhiteboardEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__UnregisterAspectProvider__AspectProvider() {
		return metadataWhiteboardEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__GetAspectProviders() {
		return metadataWhiteboardEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__GetMetadataIndex() {
		return metadataWhiteboardEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__SetMetadataIndex__MetadataIndex() {
		return metadataWhiteboardEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__UnsetMetadataIndex__MetadataIndex() {
		return metadataWhiteboardEClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__AddMetadataHandler__MetadataHandler() {
		return metadataWhiteboardEClass.getEOperations().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMetadataWhiteboard__RemoveMetadataHandler__MetadataHandler() {
		return metadataWhiteboardEClass.getEOperations().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAspectProvider() {
		return aspectProviderEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAspectProvider__GetAspectTypeId() {
		return aspectProviderEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAspectProvider__BuildPackageAspect__PackageMetadata() {
		return aspectProviderEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAspectProvider__BuildClassAspect__ClassMetadata() {
		return aspectProviderEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAspectProvider__BuildFeatureAspect__FeatureMetadata() {
		return aspectProviderEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAspectProvider__BuildAttributeAspect__AttributeMetadata() {
		return aspectProviderEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAspectProvider__BuildReferenceAspect__ReferenceMetadata() {
		return aspectProviderEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAspectProvider__BuildProfiles__PackageMetadata() {
		return aspectProviderEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ApiFactory getApiFactory() {
		return (ApiFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		metadataIndexReaderEClass = createEClass(METADATA_INDEX_READER);
		createEOperation(metadataIndexReaderEClass, METADATA_INDEX_READER___FIND_BY_INSTANCE_CLASS_NAME__STRING_STRING);
		createEOperation(metadataIndexReaderEClass, METADATA_INDEX_READER___FIND_ALL_BY_INSTANCE_CLASS_NAME__STRING);
		createEOperation(metadataIndexReaderEClass, METADATA_INDEX_READER___FIND_BY_CLASS_NAME__STRING_STRING);
		createEOperation(metadataIndexReaderEClass, METADATA_INDEX_READER___FIND_ALL_BY_CLASS_NAME__STRING);
		createEOperation(metadataIndexReaderEClass, METADATA_INDEX_READER___FIND_CLASS_BY_URI__STRING);
		createEOperation(metadataIndexReaderEClass, METADATA_INDEX_READER___FIND_FEATURE_BY_URI__STRING);
		createEOperation(metadataIndexReaderEClass, METADATA_INDEX_READER___FIND_CLASSES_BY_ANNOTATION__STRING_STRING_STRING);
		createEOperation(metadataIndexReaderEClass, METADATA_INDEX_READER___FIND_FEATURES_BY_ANNOTATION__STRING_STRING_STRING);

		metadataIndexWriterEClass = createEClass(METADATA_INDEX_WRITER);
		createEOperation(metadataIndexWriterEClass, METADATA_INDEX_WRITER___INDEX_PACKAGE__PACKAGEMETADATA);
		createEOperation(metadataIndexWriterEClass, METADATA_INDEX_WRITER___INDEX_CLASS__CLASSMETADATA);
		createEOperation(metadataIndexWriterEClass, METADATA_INDEX_WRITER___INDEX_FEATURE__FEATUREMETADATA);
		createEOperation(metadataIndexWriterEClass, METADATA_INDEX_WRITER___REMOVE_PACKAGE__PACKAGEMETADATA);
		createEOperation(metadataIndexWriterEClass, METADATA_INDEX_WRITER___REMOVE_CLASS__CLASSMETADATA);
		createEOperation(metadataIndexWriterEClass, METADATA_INDEX_WRITER___REMOVE_FEATURE__FEATUREMETADATA);
		createEOperation(metadataIndexWriterEClass, METADATA_INDEX_WRITER___CLEAR);

		metadataIndexEClass = createEClass(METADATA_INDEX);

		metadataServiceEClass = createEClass(METADATA_SERVICE);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_INDEX_READER);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_PACKAGE_METADATA__STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_CLASS_METADATA__ECLASS);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_CLASS_METADATA_BY_URI__STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_CLASS_METADATA_BY_NAME__STRING_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_FEATURE_METADATA__ESTRUCTURALFEATURE);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_FEATURE_METADATA_BY_URI__STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_FEATURE_METADATA_BY_NAME__STRING_STRING_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_FEATURE_METADATA_FROM_CLASS__STRING_CLASSMETADATA);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_PACKAGE_ASPECT__EPACKAGE_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_CLASS_ASPECT__ECLASS_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_FEATURE_ASPECT__ESTRUCTURALFEATURE_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_PACKAGE_PROFILE__EPACKAGE_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_PACKAGE_PROFILE_BY_NS_URI__STRING_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_CLASS_PROFILE__ECLASS_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_CLASS_PROFILE_BY_URI__STRING_STRING);
		createEOperation(metadataServiceEClass, METADATA_SERVICE___GET_REGISTRY);

		metadataHandlerEClass = createEClass(METADATA_HANDLER);
		createEOperation(metadataHandlerEClass, METADATA_HANDLER___ON_PACKAGE_REGISTERED__PACKAGEMETADATA);
		createEOperation(metadataHandlerEClass, METADATA_HANDLER___ON_PACKAGE_UNREGISTERED__PACKAGEMETADATA);
		createEOperation(metadataHandlerEClass, METADATA_HANDLER___CLEAR);

		metadataWhiteboardEClass = createEClass(METADATA_WHITEBOARD);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___REGISTER_PACKAGE__EPACKAGE);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___UNREGISTER_PACKAGE__EPACKAGE);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___REGISTER_ASPECT_PROVIDER__ASPECTPROVIDER);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___UNREGISTER_ASPECT_PROVIDER__ASPECTPROVIDER);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___GET_ASPECT_PROVIDERS);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___GET_METADATA_INDEX);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___SET_METADATA_INDEX__METADATAINDEX);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___UNSET_METADATA_INDEX__METADATAINDEX);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___ADD_METADATA_HANDLER__METADATAHANDLER);
		createEOperation(metadataWhiteboardEClass, METADATA_WHITEBOARD___REMOVE_METADATA_HANDLER__METADATAHANDLER);

		aspectProviderEClass = createEClass(ASPECT_PROVIDER);
		createEOperation(aspectProviderEClass, ASPECT_PROVIDER___GET_ASPECT_TYPE_ID);
		createEOperation(aspectProviderEClass, ASPECT_PROVIDER___BUILD_PACKAGE_ASPECT__PACKAGEMETADATA);
		createEOperation(aspectProviderEClass, ASPECT_PROVIDER___BUILD_CLASS_ASPECT__CLASSMETADATA);
		createEOperation(aspectProviderEClass, ASPECT_PROVIDER___BUILD_FEATURE_ASPECT__FEATUREMETADATA);
		createEOperation(aspectProviderEClass, ASPECT_PROVIDER___BUILD_ATTRIBUTE_ASPECT__ATTRIBUTEMETADATA);
		createEOperation(aspectProviderEClass, ASPECT_PROVIDER___BUILD_REFERENCE_ASPECT__REFERENCEMETADATA);
		createEOperation(aspectProviderEClass, ASPECT_PROVIDER___BUILD_PROFILES__PACKAGEMETADATA);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		MetadataPackage theMetadataPackage = (MetadataPackage)EPackage.Registry.INSTANCE.getEPackage(MetadataPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		metadataIndexEClass.getESuperTypes().add(this.getMetadataIndexReader());
		metadataIndexEClass.getESuperTypes().add(this.getMetadataIndexWriter());
		metadataWhiteboardEClass.getESuperTypes().add(this.getMetadataService());

		// Initialize classes, features, and operations; add parameters
		initEClass(metadataIndexReaderEClass, MetadataIndexReader.class, "MetadataIndexReader", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		EOperation op = initEOperation(getMetadataIndexReader__FindByInstanceClassName__String_String(), theMetadataPackage.getClassMetadata(), "findByInstanceClassName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "nsURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "instanceClassName", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexReader__FindAllByInstanceClassName__String(), theMetadataPackage.getClassMetadata(), "findAllByInstanceClassName", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "instanceClassName", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexReader__FindByClassName__String_String(), theMetadataPackage.getClassMetadata(), "findByClassName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "nsURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "className", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexReader__FindAllByClassName__String(), theMetadataPackage.getClassMetadata(), "findAllByClassName", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "className", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexReader__FindClassByURI__String(), theMetadataPackage.getClassMetadata(), "findClassByURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "uri", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexReader__FindFeatureByURI__String(), theMetadataPackage.getFeatureMetadata(), "findFeatureByURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "uri", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexReader__FindClassesByAnnotation__String_String_String(), theMetadataPackage.getClassMetadata(), "findClassesByAnnotation", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "annotationSource", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "key", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "value", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexReader__FindFeaturesByAnnotation__String_String_String(), theMetadataPackage.getFeatureMetadata(), "findFeaturesByAnnotation", 0, -1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "annotationSource", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "key", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "value", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(metadataIndexWriterEClass, MetadataIndexWriter.class, "MetadataIndexWriter", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getMetadataIndexWriter__IndexPackage__PackageMetadata(), null, "indexPackage", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getPackageMetadata(), "packageMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexWriter__IndexClass__ClassMetadata(), null, "indexClass", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getClassMetadata(), "classMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexWriter__IndexFeature__FeatureMetadata(), null, "indexFeature", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getFeatureMetadata(), "featureMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexWriter__RemovePackage__PackageMetadata(), null, "removePackage", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getPackageMetadata(), "packageMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexWriter__RemoveClass__ClassMetadata(), null, "removeClass", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getClassMetadata(), "classMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataIndexWriter__RemoveFeature__FeatureMetadata(), null, "removeFeature", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getFeatureMetadata(), "featureMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getMetadataIndexWriter__Clear(), null, "clear", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(metadataIndexEClass, MetadataIndex.class, "MetadataIndex", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(metadataServiceEClass, MetadataService.class, "MetadataService", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getMetadataService__GetIndexReader(), this.getMetadataIndexReader(), "getIndexReader", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetPackageMetadata__String(), theMetadataPackage.getPackageMetadata(), "getPackageMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "nsURI", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetClassMetadata__EClass(), theMetadataPackage.getClassMetadata(), "getClassMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetClassMetadataByURI__String(), theMetadataPackage.getClassMetadata(), "getClassMetadataByURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "uri", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetClassMetadataByName__String_String(), theMetadataPackage.getClassMetadata(), "getClassMetadataByName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "className", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "nsURI", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetFeatureMetadata__EStructuralFeature(), theMetadataPackage.getFeatureMetadata(), "getFeatureMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEStructuralFeature(), "feature", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetFeatureMetadataByURI__String(), theMetadataPackage.getFeatureMetadata(), "getFeatureMetadataByURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "uri", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetFeatureMetadataByName__String_String_String(), theMetadataPackage.getFeatureMetadata(), "getFeatureMetadataByName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "featureName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "className", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "nsURI", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetFeatureMetadataFromClass__String_ClassMetadata(), theMetadataPackage.getFeatureMetadata(), "getFeatureMetadataFromClass", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "featureName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getClassMetadata(), "classMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetPackageAspect__EPackage_String(), theMetadataPackage.getPackageAspect(), "getPackageAspect", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEPackage(), "ePackage", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "aspectTypeId", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetClassAspect__EClass_String(), theMetadataPackage.getClassAspect(), "getClassAspect", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "aspectTypeId", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetFeatureAspect__EStructuralFeature_String(), theMetadataPackage.getFeatureAspect(), "getFeatureAspect", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEStructuralFeature(), "feature", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "aspectTypeId", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetPackageProfile__EPackage_String(), theMetadataPackage.getPackageProfile(), "getPackageProfile", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEPackage(), "ePackage", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "typeId", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetPackageProfileByNsURI__String_String(), theMetadataPackage.getPackageProfile(), "getPackageProfileByNsURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "nsURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "typeId", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetClassProfile__EClass_String(), theMetadataPackage.getClassProfile(), "getClassProfile", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEClass(), "eClass", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "typeId", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataService__GetClassProfileByURI__String_String(), theMetadataPackage.getClassProfile(), "getClassProfileByURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "eClassURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "typeId", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getMetadataService__GetRegistry(), theMetadataPackage.getMetadataRegistry(), "getRegistry", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(metadataHandlerEClass, MetadataHandler.class, "MetadataHandler", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getMetadataHandler__OnPackageRegistered__PackageMetadata(), null, "onPackageRegistered", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getPackageMetadata(), "packageMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataHandler__OnPackageUnregistered__PackageMetadata(), null, "onPackageUnregistered", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getPackageMetadata(), "packageMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getMetadataHandler__Clear(), null, "clear", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(metadataWhiteboardEClass, MetadataWhiteboard.class, "MetadataWhiteboard", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getMetadataWhiteboard__RegisterPackage__EPackage(), theMetadataPackage.getPackageMetadata(), "registerPackage", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEPackage(), "ePackage", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataWhiteboard__UnregisterPackage__EPackage(), null, "unregisterPackage", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEPackage(), "ePackage", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataWhiteboard__RegisterAspectProvider__AspectProvider(), null, "registerAspectProvider", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getAspectProvider(), "provider", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataWhiteboard__UnregisterAspectProvider__AspectProvider(), null, "unregisterAspectProvider", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getAspectProvider(), "provider", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getMetadataWhiteboard__GetAspectProviders(), this.getAspectProvider(), "getAspectProviders", 0, -1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getMetadataWhiteboard__GetMetadataIndex(), this.getMetadataIndex(), "getMetadataIndex", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataWhiteboard__SetMetadataIndex__MetadataIndex(), null, "setMetadataIndex", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getMetadataIndex(), "index", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataWhiteboard__UnsetMetadataIndex__MetadataIndex(), null, "unsetMetadataIndex", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getMetadataIndex(), "index", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataWhiteboard__AddMetadataHandler__MetadataHandler(), null, "addMetadataHandler", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getMetadataHandler(), "handler", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMetadataWhiteboard__RemoveMetadataHandler__MetadataHandler(), null, "removeMetadataHandler", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getMetadataHandler(), "handler", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(aspectProviderEClass, AspectProvider.class, "AspectProvider", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getAspectProvider__GetAspectTypeId(), ecorePackage.getEString(), "getAspectTypeId", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getAspectProvider__BuildPackageAspect__PackageMetadata(), theMetadataPackage.getPackageAspect(), "buildPackageAspect", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getPackageMetadata(), "packageMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getAspectProvider__BuildClassAspect__ClassMetadata(), theMetadataPackage.getClassAspect(), "buildClassAspect", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getClassMetadata(), "classMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getAspectProvider__BuildFeatureAspect__FeatureMetadata(), theMetadataPackage.getFeatureAspect(), "buildFeatureAspect", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getFeatureMetadata(), "featureMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getAspectProvider__BuildAttributeAspect__AttributeMetadata(), theMetadataPackage.getFeatureAspect(), "buildAttributeAspect", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getAttributeMetadata(), "attributeMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getAspectProvider__BuildReferenceAspect__ReferenceMetadata(), theMetadataPackage.getFeatureAspect(), "buildReferenceAspect", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getReferenceMetadata(), "referenceMetadata", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getAspectProvider__BuildProfiles__PackageMetadata(), theMetadataPackage.getPackageProfile(), "buildProfiles", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theMetadataPackage.getPackageMetadata(), "filteredMetadataCopy", 0, 1, IS_UNIQUE, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// Version
		createVersionAnnotations();
	}

	/**
	 * Initializes the annotations for <b>Version</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createVersionAnnotations() {
		String source = "Version";
		addAnnotation
		  (this,
		   source,
		   new String[] {
			   "value", "1.0"
		   });
	}

} //ApiPackageImpl
