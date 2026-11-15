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
package org.eclipse.fennec.model.oas.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;

import org.eclipse.fennec.model.oas.ApiKeyLocation;
import org.eclipse.fennec.model.oas.Callback;
import org.eclipse.fennec.model.oas.Components;
import org.eclipse.fennec.model.oas.Contact;
import org.eclipse.fennec.model.oas.Encoding;
import org.eclipse.fennec.model.oas.Example;
import org.eclipse.fennec.model.oas.ExternalDocumentation;
import org.eclipse.fennec.model.oas.Header;
import org.eclipse.fennec.model.oas.Info;
import org.eclipse.fennec.model.oas.License;
import org.eclipse.fennec.model.oas.Link;
import org.eclipse.fennec.model.oas.MediaType;
import org.eclipse.fennec.model.oas.OAuthFlow;
import org.eclipse.fennec.model.oas.OAuthFlows;
import org.eclipse.fennec.model.oas.OasFactory;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.Parameter;
import org.eclipse.fennec.model.oas.ParameterLocation;
import org.eclipse.fennec.model.oas.ParameterStyle;
import org.eclipse.fennec.model.oas.PathItem;
import org.eclipse.fennec.model.oas.RequestBody;
import org.eclipse.fennec.model.oas.Response;
import org.eclipse.fennec.model.oas.SecurityRequirement;
import org.eclipse.fennec.model.oas.SecurityScheme;
import org.eclipse.fennec.model.oas.SecuritySchemeType;
import org.eclipse.fennec.model.oas.Server;
import org.eclipse.fennec.model.oas.ServerVariable;
import org.eclipse.fennec.model.oas.Tag;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OasPackageImpl extends EPackageImpl implements OasPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass openAPIEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass infoEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass contactEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass licenseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serverEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serverVariableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass componentsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pathItemEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass operationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass externalDocumentationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass requestBodyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mediaTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass encodingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass responseEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass callbackEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass exampleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass linkEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass headerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tagEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass securitySchemeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass oAuthFlowsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass oAuthFlowEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass securityRequirementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pathItemEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass operationEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass responseEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass exampleEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass requestBodyEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass headerEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass securitySchemeEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass linkEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass callbackEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mediaTypeEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass encodingEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serverVariableEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass anyEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass extensionEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass securityRequirementEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass schemaEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum parameterLocationEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum parameterStyleEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum securitySchemeTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum apiKeyLocationEEnum = null;

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
	 * @see org.eclipse.fennec.model.oas.OasPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private OasPackageImpl() {
		super(eNS_URI, OasFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link OasPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static OasPackage init() {
		if (isInited) return (OasPackage)EPackage.Registry.INSTANCE.getEPackage(OasPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredOasPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		OasPackageImpl theOasPackage = registeredOasPackage instanceof OasPackageImpl ? (OasPackageImpl)registeredOasPackage : new OasPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		JsonSchemaPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theOasPackage.createPackageContents();

		// Initialize created meta-data
		theOasPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theOasPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(OasPackage.eNS_URI, theOasPackage);
		return theOasPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOpenAPI() {
		return openAPIEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOpenAPI_Openapi() {
		return (EAttribute)openAPIEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOpenAPI_Self() {
		return (EAttribute)openAPIEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_Info() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOpenAPI_JsonSchemaDialect() {
		return (EAttribute)openAPIEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_Servers() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_Paths() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_Webhooks() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_Components() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_Security() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_Tags() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_ExternalDocs() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOpenAPI_Extensions() {
		return (EReference)openAPIEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getInfo() {
		return infoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInfo_Title() {
		return (EAttribute)infoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInfo_Summary() {
		return (EAttribute)infoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInfo_Description() {
		return (EAttribute)infoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInfo_TermsOfService() {
		return (EAttribute)infoEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInfo_Contact() {
		return (EReference)infoEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInfo_License() {
		return (EReference)infoEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getInfo_Version() {
		return (EAttribute)infoEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getInfo_Extensions() {
		return (EReference)infoEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getContact() {
		return contactEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContact_Name() {
		return (EAttribute)contactEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContact_Url() {
		return (EAttribute)contactEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getContact_Email() {
		return (EAttribute)contactEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getContact_Extensions() {
		return (EReference)contactEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLicense() {
		return licenseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLicense_Name() {
		return (EAttribute)licenseEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLicense_Identifier() {
		return (EAttribute)licenseEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLicense_Url() {
		return (EAttribute)licenseEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLicense_Extensions() {
		return (EReference)licenseEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getServer() {
		return serverEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getServer_Url() {
		return (EAttribute)serverEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getServer_Description() {
		return (EAttribute)serverEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getServer_Name() {
		return (EAttribute)serverEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServer_Variables() {
		return (EReference)serverEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServer_Extensions() {
		return (EReference)serverEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getServerVariable() {
		return serverVariableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getServerVariable_Enum() {
		return (EAttribute)serverVariableEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getServerVariable_Default() {
		return (EAttribute)serverVariableEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getServerVariable_Description() {
		return (EAttribute)serverVariableEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServerVariable_Extensions() {
		return (EReference)serverVariableEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getComponents() {
		return componentsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_Schemas() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_SchemasPackage() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_Responses() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_Parameters() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_Examples() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_RequestBodies() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_Headers() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_SecuritySchemes() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_Links() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_Callbacks() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_PathItems() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_MediaTypes() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getComponents_Extensions() {
		return (EReference)componentsEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPathItem() {
		return pathItemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPathItem_Ref() {
		return (EAttribute)pathItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Target() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPathItem_Summary() {
		return (EAttribute)pathItemEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPathItem_Description() {
		return (EAttribute)pathItemEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Get() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Put() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Post() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Delete() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Options() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Head() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Patch() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Trace() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Query() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_AdditionalOperations() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Servers() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Parameters() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_AllOperations() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItem_Extensions() {
		return (EReference)pathItemEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOperation() {
		return operationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_Tags() {
		return (EAttribute)operationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_Summary() {
		return (EAttribute)operationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_Description() {
		return (EAttribute)operationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_ExternalDocs() {
		return (EReference)operationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_OperationId() {
		return (EAttribute)operationEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_Parameters() {
		return (EReference)operationEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_RequestBody() {
		return (EReference)operationEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_Responses() {
		return (EReference)operationEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_Callbacks() {
		return (EReference)operationEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_Deprecated() {
		return (EAttribute)operationEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_Security() {
		return (EReference)operationEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_Servers() {
		return (EReference)operationEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperation_Method() {
		return (EAttribute)operationEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperation_Extensions() {
		return (EReference)operationEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExternalDocumentation() {
		return externalDocumentationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExternalDocumentation_Description() {
		return (EAttribute)externalDocumentationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExternalDocumentation_Url() {
		return (EAttribute)externalDocumentationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExternalDocumentation_Extensions() {
		return (EReference)externalDocumentationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getParameter() {
		return parameterEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Ref() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameter_Target() {
		return (EReference)parameterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Name() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_In() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Summary() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Description() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Required() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Deprecated() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_AllowEmptyValue() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Style() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Explode() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_AllowReserved() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameter_Schema() {
		return (EReference)parameterEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Example() {
		return (EAttribute)parameterEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameter_Examples() {
		return (EReference)parameterEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameter_Content() {
		return (EReference)parameterEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameter_Extensions() {
		return (EReference)parameterEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRequestBody() {
		return requestBodyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRequestBody_Ref() {
		return (EAttribute)requestBodyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequestBody_Target() {
		return (EReference)requestBodyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRequestBody_Summary() {
		return (EAttribute)requestBodyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRequestBody_Description() {
		return (EAttribute)requestBodyEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequestBody_Content() {
		return (EReference)requestBodyEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRequestBody_Required() {
		return (EAttribute)requestBodyEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequestBody_Extensions() {
		return (EReference)requestBodyEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMediaType() {
		return mediaTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMediaType_Ref() {
		return (EAttribute)mediaTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaType_Target() {
		return (EReference)mediaTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMediaType_Summary() {
		return (EAttribute)mediaTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMediaType_Description() {
		return (EAttribute)mediaTypeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaType_Schema() {
		return (EReference)mediaTypeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaType_ItemSchema() {
		return (EReference)mediaTypeEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMediaType_Example() {
		return (EAttribute)mediaTypeEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaType_Examples() {
		return (EReference)mediaTypeEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaType_Encoding() {
		return (EReference)mediaTypeEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaType_PrefixEncoding() {
		return (EReference)mediaTypeEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaType_ItemEncoding() {
		return (EReference)mediaTypeEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaType_Extensions() {
		return (EReference)mediaTypeEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEncoding() {
		return encodingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEncoding_ContentType() {
		return (EAttribute)encodingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEncoding_Headers() {
		return (EReference)encodingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEncoding_Style() {
		return (EAttribute)encodingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEncoding_Explode() {
		return (EAttribute)encodingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEncoding_AllowReserved() {
		return (EAttribute)encodingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEncoding_Encoding() {
		return (EReference)encodingEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEncoding_PrefixEncoding() {
		return (EReference)encodingEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEncoding_ItemEncoding() {
		return (EReference)encodingEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEncoding_Extensions() {
		return (EReference)encodingEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResponse() {
		return responseEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResponse_Ref() {
		return (EAttribute)responseEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponse_Target() {
		return (EReference)responseEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResponse_Summary() {
		return (EAttribute)responseEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResponse_Description() {
		return (EAttribute)responseEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponse_Headers() {
		return (EReference)responseEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponse_Content() {
		return (EReference)responseEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponse_Links() {
		return (EReference)responseEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponse_Extensions() {
		return (EReference)responseEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCallback() {
		return callbackEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCallback_Ref() {
		return (EAttribute)callbackEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallback_Target() {
		return (EReference)callbackEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallback_Expressions() {
		return (EReference)callbackEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExample() {
		return exampleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExample_Ref() {
		return (EAttribute)exampleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExample_Target() {
		return (EReference)exampleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExample_Summary() {
		return (EAttribute)exampleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExample_Description() {
		return (EAttribute)exampleEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExample_Value() {
		return (EAttribute)exampleEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExample_DataValue() {
		return (EAttribute)exampleEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExample_SerializedValue() {
		return (EAttribute)exampleEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExample_ExternalValue() {
		return (EAttribute)exampleEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExample_Extensions() {
		return (EReference)exampleEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLink() {
		return linkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLink_Ref() {
		return (EAttribute)linkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLink_Target() {
		return (EReference)linkEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLink_OperationRef() {
		return (EAttribute)linkEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLink_OperationId() {
		return (EAttribute)linkEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLink_Parameters() {
		return (EReference)linkEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLink_RequestBody() {
		return (EAttribute)linkEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLink_Summary() {
		return (EAttribute)linkEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLink_Description() {
		return (EAttribute)linkEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLink_Server() {
		return (EReference)linkEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLink_Extensions() {
		return (EReference)linkEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getHeader() {
		return headerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_Ref() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getHeader_Target() {
		return (EReference)headerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_Summary() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_Description() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_Required() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_Deprecated() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_AllowEmptyValue() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_Style() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_Explode() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_AllowReserved() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getHeader_Schema() {
		return (EReference)headerEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeader_Example() {
		return (EAttribute)headerEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getHeader_Examples() {
		return (EReference)headerEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getHeader_Content() {
		return (EReference)headerEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getHeader_Extensions() {
		return (EReference)headerEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTag() {
		return tagEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTag_Name() {
		return (EAttribute)tagEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTag_Summary() {
		return (EAttribute)tagEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTag_Description() {
		return (EAttribute)tagEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTag_ExternalDocs() {
		return (EReference)tagEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTag_Parent() {
		return (EAttribute)tagEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTag_Kind() {
		return (EAttribute)tagEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTag_ParentTag() {
		return (EReference)tagEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTag_Extensions() {
		return (EReference)tagEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSecurityScheme() {
		return securitySchemeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_Ref() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecurityScheme_Target() {
		return (EReference)securitySchemeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_Type() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_Summary() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_Description() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_Name() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_In() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_Scheme() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_BearerFormat() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecurityScheme_Flows() {
		return (EReference)securitySchemeEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_OpenIdConnectUrl() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_Oauth2MetadataUrl() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityScheme_Deprecated() {
		return (EAttribute)securitySchemeEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecurityScheme_Extensions() {
		return (EReference)securitySchemeEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOAuthFlows() {
		return oAuthFlowsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOAuthFlows_Implicit() {
		return (EReference)oAuthFlowsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOAuthFlows_Password() {
		return (EReference)oAuthFlowsEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOAuthFlows_ClientCredentials() {
		return (EReference)oAuthFlowsEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOAuthFlows_AuthorizationCode() {
		return (EReference)oAuthFlowsEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOAuthFlows_DeviceAuthorization() {
		return (EReference)oAuthFlowsEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOAuthFlows_Extensions() {
		return (EReference)oAuthFlowsEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOAuthFlow() {
		return oAuthFlowEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOAuthFlow_AuthorizationUrl() {
		return (EAttribute)oAuthFlowEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOAuthFlow_DeviceAuthorizationUrl() {
		return (EAttribute)oAuthFlowEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOAuthFlow_TokenUrl() {
		return (EAttribute)oAuthFlowEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOAuthFlow_RefreshUrl() {
		return (EAttribute)oAuthFlowEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOAuthFlow_Scopes() {
		return (EReference)oAuthFlowEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOAuthFlow_Extensions() {
		return (EReference)oAuthFlowEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSecurityRequirement() {
		return securityRequirementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecurityRequirement_Schemes() {
		return (EReference)securityRequirementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPathItemEntry() {
		return pathItemEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPathItemEntry_Key() {
		return (EAttribute)pathItemEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPathItemEntry_Value() {
		return (EReference)pathItemEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getOperationEntry() {
		return operationEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getOperationEntry_Key() {
		return (EAttribute)operationEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getOperationEntry_Value() {
		return (EReference)operationEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResponseEntry() {
		return responseEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResponseEntry_Key() {
		return (EAttribute)responseEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResponseEntry_Value() {
		return (EReference)responseEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getParameterEntry() {
		return parameterEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getParameterEntry_Key() {
		return (EAttribute)parameterEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getParameterEntry_Value() {
		return (EReference)parameterEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExampleEntry() {
		return exampleEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExampleEntry_Key() {
		return (EAttribute)exampleEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getExampleEntry_Value() {
		return (EReference)exampleEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRequestBodyEntry() {
		return requestBodyEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRequestBodyEntry_Key() {
		return (EAttribute)requestBodyEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRequestBodyEntry_Value() {
		return (EReference)requestBodyEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getHeaderEntry() {
		return headerEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getHeaderEntry_Key() {
		return (EAttribute)headerEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getHeaderEntry_Value() {
		return (EReference)headerEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSecuritySchemeEntry() {
		return securitySchemeEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecuritySchemeEntry_Key() {
		return (EAttribute)securitySchemeEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSecuritySchemeEntry_Value() {
		return (EReference)securitySchemeEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLinkEntry() {
		return linkEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLinkEntry_Key() {
		return (EAttribute)linkEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLinkEntry_Value() {
		return (EReference)linkEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCallbackEntry() {
		return callbackEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCallbackEntry_Key() {
		return (EAttribute)callbackEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCallbackEntry_Value() {
		return (EReference)callbackEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMediaTypeEntry() {
		return mediaTypeEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMediaTypeEntry_Key() {
		return (EAttribute)mediaTypeEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMediaTypeEntry_Value() {
		return (EReference)mediaTypeEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEncodingEntry() {
		return encodingEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEncodingEntry_Key() {
		return (EAttribute)encodingEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEncodingEntry_Value() {
		return (EReference)encodingEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getServerVariableEntry() {
		return serverVariableEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getServerVariableEntry_Key() {
		return (EAttribute)serverVariableEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServerVariableEntry_Value() {
		return (EReference)serverVariableEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStringEntry() {
		return stringEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringEntry_Key() {
		return (EAttribute)stringEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringEntry_Value() {
		return (EAttribute)stringEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAnyEntry() {
		return anyEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnyEntry_Key() {
		return (EAttribute)anyEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAnyEntry_Value() {
		return (EAttribute)anyEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExtensionEntry() {
		return extensionEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtensionEntry_Key() {
		return (EAttribute)extensionEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExtensionEntry_Value() {
		return (EAttribute)extensionEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSecurityRequirementEntry() {
		return securityRequirementEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityRequirementEntry_Key() {
		return (EAttribute)securityRequirementEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSecurityRequirementEntry_Value() {
		return (EAttribute)securityRequirementEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSchemaEntry() {
		return schemaEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSchemaEntry_Key() {
		return (EAttribute)schemaEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSchemaEntry_Value() {
		return (EReference)schemaEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getParameterLocation() {
		return parameterLocationEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getParameterStyle() {
		return parameterStyleEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getSecuritySchemeType() {
		return securitySchemeTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getApiKeyLocation() {
		return apiKeyLocationEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OasFactory getOasFactory() {
		return (OasFactory)getEFactoryInstance();
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
		openAPIEClass = createEClass(OPEN_API);
		createEAttribute(openAPIEClass, OPEN_API__OPENAPI);
		createEAttribute(openAPIEClass, OPEN_API__SELF);
		createEReference(openAPIEClass, OPEN_API__INFO);
		createEAttribute(openAPIEClass, OPEN_API__JSON_SCHEMA_DIALECT);
		createEReference(openAPIEClass, OPEN_API__SERVERS);
		createEReference(openAPIEClass, OPEN_API__PATHS);
		createEReference(openAPIEClass, OPEN_API__WEBHOOKS);
		createEReference(openAPIEClass, OPEN_API__COMPONENTS);
		createEReference(openAPIEClass, OPEN_API__SECURITY);
		createEReference(openAPIEClass, OPEN_API__TAGS);
		createEReference(openAPIEClass, OPEN_API__EXTERNAL_DOCS);
		createEReference(openAPIEClass, OPEN_API__EXTENSIONS);

		infoEClass = createEClass(INFO);
		createEAttribute(infoEClass, INFO__TITLE);
		createEAttribute(infoEClass, INFO__SUMMARY);
		createEAttribute(infoEClass, INFO__DESCRIPTION);
		createEAttribute(infoEClass, INFO__TERMS_OF_SERVICE);
		createEReference(infoEClass, INFO__CONTACT);
		createEReference(infoEClass, INFO__LICENSE);
		createEAttribute(infoEClass, INFO__VERSION);
		createEReference(infoEClass, INFO__EXTENSIONS);

		contactEClass = createEClass(CONTACT);
		createEAttribute(contactEClass, CONTACT__NAME);
		createEAttribute(contactEClass, CONTACT__URL);
		createEAttribute(contactEClass, CONTACT__EMAIL);
		createEReference(contactEClass, CONTACT__EXTENSIONS);

		licenseEClass = createEClass(LICENSE);
		createEAttribute(licenseEClass, LICENSE__NAME);
		createEAttribute(licenseEClass, LICENSE__IDENTIFIER);
		createEAttribute(licenseEClass, LICENSE__URL);
		createEReference(licenseEClass, LICENSE__EXTENSIONS);

		serverEClass = createEClass(SERVER);
		createEAttribute(serverEClass, SERVER__URL);
		createEAttribute(serverEClass, SERVER__DESCRIPTION);
		createEAttribute(serverEClass, SERVER__NAME);
		createEReference(serverEClass, SERVER__VARIABLES);
		createEReference(serverEClass, SERVER__EXTENSIONS);

		serverVariableEClass = createEClass(SERVER_VARIABLE);
		createEAttribute(serverVariableEClass, SERVER_VARIABLE__ENUM);
		createEAttribute(serverVariableEClass, SERVER_VARIABLE__DEFAULT);
		createEAttribute(serverVariableEClass, SERVER_VARIABLE__DESCRIPTION);
		createEReference(serverVariableEClass, SERVER_VARIABLE__EXTENSIONS);

		componentsEClass = createEClass(COMPONENTS);
		createEReference(componentsEClass, COMPONENTS__SCHEMAS);
		createEReference(componentsEClass, COMPONENTS__SCHEMAS_PACKAGE);
		createEReference(componentsEClass, COMPONENTS__RESPONSES);
		createEReference(componentsEClass, COMPONENTS__PARAMETERS);
		createEReference(componentsEClass, COMPONENTS__EXAMPLES);
		createEReference(componentsEClass, COMPONENTS__REQUEST_BODIES);
		createEReference(componentsEClass, COMPONENTS__HEADERS);
		createEReference(componentsEClass, COMPONENTS__SECURITY_SCHEMES);
		createEReference(componentsEClass, COMPONENTS__LINKS);
		createEReference(componentsEClass, COMPONENTS__CALLBACKS);
		createEReference(componentsEClass, COMPONENTS__PATH_ITEMS);
		createEReference(componentsEClass, COMPONENTS__MEDIA_TYPES);
		createEReference(componentsEClass, COMPONENTS__EXTENSIONS);

		pathItemEClass = createEClass(PATH_ITEM);
		createEAttribute(pathItemEClass, PATH_ITEM__REF);
		createEReference(pathItemEClass, PATH_ITEM__TARGET);
		createEAttribute(pathItemEClass, PATH_ITEM__SUMMARY);
		createEAttribute(pathItemEClass, PATH_ITEM__DESCRIPTION);
		createEReference(pathItemEClass, PATH_ITEM__GET);
		createEReference(pathItemEClass, PATH_ITEM__PUT);
		createEReference(pathItemEClass, PATH_ITEM__POST);
		createEReference(pathItemEClass, PATH_ITEM__DELETE);
		createEReference(pathItemEClass, PATH_ITEM__OPTIONS);
		createEReference(pathItemEClass, PATH_ITEM__HEAD);
		createEReference(pathItemEClass, PATH_ITEM__PATCH);
		createEReference(pathItemEClass, PATH_ITEM__TRACE);
		createEReference(pathItemEClass, PATH_ITEM__QUERY);
		createEReference(pathItemEClass, PATH_ITEM__ADDITIONAL_OPERATIONS);
		createEReference(pathItemEClass, PATH_ITEM__SERVERS);
		createEReference(pathItemEClass, PATH_ITEM__PARAMETERS);
		createEReference(pathItemEClass, PATH_ITEM__ALL_OPERATIONS);
		createEReference(pathItemEClass, PATH_ITEM__EXTENSIONS);

		operationEClass = createEClass(OPERATION);
		createEAttribute(operationEClass, OPERATION__TAGS);
		createEAttribute(operationEClass, OPERATION__SUMMARY);
		createEAttribute(operationEClass, OPERATION__DESCRIPTION);
		createEReference(operationEClass, OPERATION__EXTERNAL_DOCS);
		createEAttribute(operationEClass, OPERATION__OPERATION_ID);
		createEReference(operationEClass, OPERATION__PARAMETERS);
		createEReference(operationEClass, OPERATION__REQUEST_BODY);
		createEReference(operationEClass, OPERATION__RESPONSES);
		createEReference(operationEClass, OPERATION__CALLBACKS);
		createEAttribute(operationEClass, OPERATION__DEPRECATED);
		createEReference(operationEClass, OPERATION__SECURITY);
		createEReference(operationEClass, OPERATION__SERVERS);
		createEAttribute(operationEClass, OPERATION__METHOD);
		createEReference(operationEClass, OPERATION__EXTENSIONS);

		externalDocumentationEClass = createEClass(EXTERNAL_DOCUMENTATION);
		createEAttribute(externalDocumentationEClass, EXTERNAL_DOCUMENTATION__DESCRIPTION);
		createEAttribute(externalDocumentationEClass, EXTERNAL_DOCUMENTATION__URL);
		createEReference(externalDocumentationEClass, EXTERNAL_DOCUMENTATION__EXTENSIONS);

		parameterEClass = createEClass(PARAMETER);
		createEAttribute(parameterEClass, PARAMETER__REF);
		createEReference(parameterEClass, PARAMETER__TARGET);
		createEAttribute(parameterEClass, PARAMETER__NAME);
		createEAttribute(parameterEClass, PARAMETER__IN);
		createEAttribute(parameterEClass, PARAMETER__SUMMARY);
		createEAttribute(parameterEClass, PARAMETER__DESCRIPTION);
		createEAttribute(parameterEClass, PARAMETER__REQUIRED);
		createEAttribute(parameterEClass, PARAMETER__DEPRECATED);
		createEAttribute(parameterEClass, PARAMETER__ALLOW_EMPTY_VALUE);
		createEAttribute(parameterEClass, PARAMETER__STYLE);
		createEAttribute(parameterEClass, PARAMETER__EXPLODE);
		createEAttribute(parameterEClass, PARAMETER__ALLOW_RESERVED);
		createEReference(parameterEClass, PARAMETER__SCHEMA);
		createEAttribute(parameterEClass, PARAMETER__EXAMPLE);
		createEReference(parameterEClass, PARAMETER__EXAMPLES);
		createEReference(parameterEClass, PARAMETER__CONTENT);
		createEReference(parameterEClass, PARAMETER__EXTENSIONS);

		requestBodyEClass = createEClass(REQUEST_BODY);
		createEAttribute(requestBodyEClass, REQUEST_BODY__REF);
		createEReference(requestBodyEClass, REQUEST_BODY__TARGET);
		createEAttribute(requestBodyEClass, REQUEST_BODY__SUMMARY);
		createEAttribute(requestBodyEClass, REQUEST_BODY__DESCRIPTION);
		createEReference(requestBodyEClass, REQUEST_BODY__CONTENT);
		createEAttribute(requestBodyEClass, REQUEST_BODY__REQUIRED);
		createEReference(requestBodyEClass, REQUEST_BODY__EXTENSIONS);

		mediaTypeEClass = createEClass(MEDIA_TYPE);
		createEAttribute(mediaTypeEClass, MEDIA_TYPE__REF);
		createEReference(mediaTypeEClass, MEDIA_TYPE__TARGET);
		createEAttribute(mediaTypeEClass, MEDIA_TYPE__SUMMARY);
		createEAttribute(mediaTypeEClass, MEDIA_TYPE__DESCRIPTION);
		createEReference(mediaTypeEClass, MEDIA_TYPE__SCHEMA);
		createEReference(mediaTypeEClass, MEDIA_TYPE__ITEM_SCHEMA);
		createEAttribute(mediaTypeEClass, MEDIA_TYPE__EXAMPLE);
		createEReference(mediaTypeEClass, MEDIA_TYPE__EXAMPLES);
		createEReference(mediaTypeEClass, MEDIA_TYPE__ENCODING);
		createEReference(mediaTypeEClass, MEDIA_TYPE__PREFIX_ENCODING);
		createEReference(mediaTypeEClass, MEDIA_TYPE__ITEM_ENCODING);
		createEReference(mediaTypeEClass, MEDIA_TYPE__EXTENSIONS);

		encodingEClass = createEClass(ENCODING);
		createEAttribute(encodingEClass, ENCODING__CONTENT_TYPE);
		createEReference(encodingEClass, ENCODING__HEADERS);
		createEAttribute(encodingEClass, ENCODING__STYLE);
		createEAttribute(encodingEClass, ENCODING__EXPLODE);
		createEAttribute(encodingEClass, ENCODING__ALLOW_RESERVED);
		createEReference(encodingEClass, ENCODING__ENCODING);
		createEReference(encodingEClass, ENCODING__PREFIX_ENCODING);
		createEReference(encodingEClass, ENCODING__ITEM_ENCODING);
		createEReference(encodingEClass, ENCODING__EXTENSIONS);

		responseEClass = createEClass(RESPONSE);
		createEAttribute(responseEClass, RESPONSE__REF);
		createEReference(responseEClass, RESPONSE__TARGET);
		createEAttribute(responseEClass, RESPONSE__SUMMARY);
		createEAttribute(responseEClass, RESPONSE__DESCRIPTION);
		createEReference(responseEClass, RESPONSE__HEADERS);
		createEReference(responseEClass, RESPONSE__CONTENT);
		createEReference(responseEClass, RESPONSE__LINKS);
		createEReference(responseEClass, RESPONSE__EXTENSIONS);

		callbackEClass = createEClass(CALLBACK);
		createEAttribute(callbackEClass, CALLBACK__REF);
		createEReference(callbackEClass, CALLBACK__TARGET);
		createEReference(callbackEClass, CALLBACK__EXPRESSIONS);

		exampleEClass = createEClass(EXAMPLE);
		createEAttribute(exampleEClass, EXAMPLE__REF);
		createEReference(exampleEClass, EXAMPLE__TARGET);
		createEAttribute(exampleEClass, EXAMPLE__SUMMARY);
		createEAttribute(exampleEClass, EXAMPLE__DESCRIPTION);
		createEAttribute(exampleEClass, EXAMPLE__VALUE);
		createEAttribute(exampleEClass, EXAMPLE__DATA_VALUE);
		createEAttribute(exampleEClass, EXAMPLE__SERIALIZED_VALUE);
		createEAttribute(exampleEClass, EXAMPLE__EXTERNAL_VALUE);
		createEReference(exampleEClass, EXAMPLE__EXTENSIONS);

		linkEClass = createEClass(LINK);
		createEAttribute(linkEClass, LINK__REF);
		createEReference(linkEClass, LINK__TARGET);
		createEAttribute(linkEClass, LINK__OPERATION_REF);
		createEAttribute(linkEClass, LINK__OPERATION_ID);
		createEReference(linkEClass, LINK__PARAMETERS);
		createEAttribute(linkEClass, LINK__REQUEST_BODY);
		createEAttribute(linkEClass, LINK__SUMMARY);
		createEAttribute(linkEClass, LINK__DESCRIPTION);
		createEReference(linkEClass, LINK__SERVER);
		createEReference(linkEClass, LINK__EXTENSIONS);

		headerEClass = createEClass(HEADER);
		createEAttribute(headerEClass, HEADER__REF);
		createEReference(headerEClass, HEADER__TARGET);
		createEAttribute(headerEClass, HEADER__SUMMARY);
		createEAttribute(headerEClass, HEADER__DESCRIPTION);
		createEAttribute(headerEClass, HEADER__REQUIRED);
		createEAttribute(headerEClass, HEADER__DEPRECATED);
		createEAttribute(headerEClass, HEADER__ALLOW_EMPTY_VALUE);
		createEAttribute(headerEClass, HEADER__STYLE);
		createEAttribute(headerEClass, HEADER__EXPLODE);
		createEAttribute(headerEClass, HEADER__ALLOW_RESERVED);
		createEReference(headerEClass, HEADER__SCHEMA);
		createEAttribute(headerEClass, HEADER__EXAMPLE);
		createEReference(headerEClass, HEADER__EXAMPLES);
		createEReference(headerEClass, HEADER__CONTENT);
		createEReference(headerEClass, HEADER__EXTENSIONS);

		tagEClass = createEClass(TAG);
		createEAttribute(tagEClass, TAG__NAME);
		createEAttribute(tagEClass, TAG__SUMMARY);
		createEAttribute(tagEClass, TAG__DESCRIPTION);
		createEReference(tagEClass, TAG__EXTERNAL_DOCS);
		createEAttribute(tagEClass, TAG__PARENT);
		createEAttribute(tagEClass, TAG__KIND);
		createEReference(tagEClass, TAG__PARENT_TAG);
		createEReference(tagEClass, TAG__EXTENSIONS);

		securitySchemeEClass = createEClass(SECURITY_SCHEME);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__REF);
		createEReference(securitySchemeEClass, SECURITY_SCHEME__TARGET);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__TYPE);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__SUMMARY);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__DESCRIPTION);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__NAME);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__IN);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__SCHEME);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__BEARER_FORMAT);
		createEReference(securitySchemeEClass, SECURITY_SCHEME__FLOWS);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__OPEN_ID_CONNECT_URL);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__OAUTH2_METADATA_URL);
		createEAttribute(securitySchemeEClass, SECURITY_SCHEME__DEPRECATED);
		createEReference(securitySchemeEClass, SECURITY_SCHEME__EXTENSIONS);

		oAuthFlowsEClass = createEClass(OAUTH_FLOWS);
		createEReference(oAuthFlowsEClass, OAUTH_FLOWS__IMPLICIT);
		createEReference(oAuthFlowsEClass, OAUTH_FLOWS__PASSWORD);
		createEReference(oAuthFlowsEClass, OAUTH_FLOWS__CLIENT_CREDENTIALS);
		createEReference(oAuthFlowsEClass, OAUTH_FLOWS__AUTHORIZATION_CODE);
		createEReference(oAuthFlowsEClass, OAUTH_FLOWS__DEVICE_AUTHORIZATION);
		createEReference(oAuthFlowsEClass, OAUTH_FLOWS__EXTENSIONS);

		oAuthFlowEClass = createEClass(OAUTH_FLOW);
		createEAttribute(oAuthFlowEClass, OAUTH_FLOW__AUTHORIZATION_URL);
		createEAttribute(oAuthFlowEClass, OAUTH_FLOW__DEVICE_AUTHORIZATION_URL);
		createEAttribute(oAuthFlowEClass, OAUTH_FLOW__TOKEN_URL);
		createEAttribute(oAuthFlowEClass, OAUTH_FLOW__REFRESH_URL);
		createEReference(oAuthFlowEClass, OAUTH_FLOW__SCOPES);
		createEReference(oAuthFlowEClass, OAUTH_FLOW__EXTENSIONS);

		securityRequirementEClass = createEClass(SECURITY_REQUIREMENT);
		createEReference(securityRequirementEClass, SECURITY_REQUIREMENT__SCHEMES);

		pathItemEntryEClass = createEClass(PATH_ITEM_ENTRY);
		createEAttribute(pathItemEntryEClass, PATH_ITEM_ENTRY__KEY);
		createEReference(pathItemEntryEClass, PATH_ITEM_ENTRY__VALUE);

		operationEntryEClass = createEClass(OPERATION_ENTRY);
		createEAttribute(operationEntryEClass, OPERATION_ENTRY__KEY);
		createEReference(operationEntryEClass, OPERATION_ENTRY__VALUE);

		responseEntryEClass = createEClass(RESPONSE_ENTRY);
		createEAttribute(responseEntryEClass, RESPONSE_ENTRY__KEY);
		createEReference(responseEntryEClass, RESPONSE_ENTRY__VALUE);

		parameterEntryEClass = createEClass(PARAMETER_ENTRY);
		createEAttribute(parameterEntryEClass, PARAMETER_ENTRY__KEY);
		createEReference(parameterEntryEClass, PARAMETER_ENTRY__VALUE);

		exampleEntryEClass = createEClass(EXAMPLE_ENTRY);
		createEAttribute(exampleEntryEClass, EXAMPLE_ENTRY__KEY);
		createEReference(exampleEntryEClass, EXAMPLE_ENTRY__VALUE);

		requestBodyEntryEClass = createEClass(REQUEST_BODY_ENTRY);
		createEAttribute(requestBodyEntryEClass, REQUEST_BODY_ENTRY__KEY);
		createEReference(requestBodyEntryEClass, REQUEST_BODY_ENTRY__VALUE);

		headerEntryEClass = createEClass(HEADER_ENTRY);
		createEAttribute(headerEntryEClass, HEADER_ENTRY__KEY);
		createEReference(headerEntryEClass, HEADER_ENTRY__VALUE);

		securitySchemeEntryEClass = createEClass(SECURITY_SCHEME_ENTRY);
		createEAttribute(securitySchemeEntryEClass, SECURITY_SCHEME_ENTRY__KEY);
		createEReference(securitySchemeEntryEClass, SECURITY_SCHEME_ENTRY__VALUE);

		linkEntryEClass = createEClass(LINK_ENTRY);
		createEAttribute(linkEntryEClass, LINK_ENTRY__KEY);
		createEReference(linkEntryEClass, LINK_ENTRY__VALUE);

		callbackEntryEClass = createEClass(CALLBACK_ENTRY);
		createEAttribute(callbackEntryEClass, CALLBACK_ENTRY__KEY);
		createEReference(callbackEntryEClass, CALLBACK_ENTRY__VALUE);

		mediaTypeEntryEClass = createEClass(MEDIA_TYPE_ENTRY);
		createEAttribute(mediaTypeEntryEClass, MEDIA_TYPE_ENTRY__KEY);
		createEReference(mediaTypeEntryEClass, MEDIA_TYPE_ENTRY__VALUE);

		encodingEntryEClass = createEClass(ENCODING_ENTRY);
		createEAttribute(encodingEntryEClass, ENCODING_ENTRY__KEY);
		createEReference(encodingEntryEClass, ENCODING_ENTRY__VALUE);

		serverVariableEntryEClass = createEClass(SERVER_VARIABLE_ENTRY);
		createEAttribute(serverVariableEntryEClass, SERVER_VARIABLE_ENTRY__KEY);
		createEReference(serverVariableEntryEClass, SERVER_VARIABLE_ENTRY__VALUE);

		stringEntryEClass = createEClass(STRING_ENTRY);
		createEAttribute(stringEntryEClass, STRING_ENTRY__KEY);
		createEAttribute(stringEntryEClass, STRING_ENTRY__VALUE);

		anyEntryEClass = createEClass(ANY_ENTRY);
		createEAttribute(anyEntryEClass, ANY_ENTRY__KEY);
		createEAttribute(anyEntryEClass, ANY_ENTRY__VALUE);

		extensionEntryEClass = createEClass(EXTENSION_ENTRY);
		createEAttribute(extensionEntryEClass, EXTENSION_ENTRY__KEY);
		createEAttribute(extensionEntryEClass, EXTENSION_ENTRY__VALUE);

		securityRequirementEntryEClass = createEClass(SECURITY_REQUIREMENT_ENTRY);
		createEAttribute(securityRequirementEntryEClass, SECURITY_REQUIREMENT_ENTRY__KEY);
		createEAttribute(securityRequirementEntryEClass, SECURITY_REQUIREMENT_ENTRY__VALUE);

		schemaEntryEClass = createEClass(SCHEMA_ENTRY);
		createEAttribute(schemaEntryEClass, SCHEMA_ENTRY__KEY);
		createEReference(schemaEntryEClass, SCHEMA_ENTRY__VALUE);

		// Create enums
		parameterLocationEEnum = createEEnum(PARAMETER_LOCATION);
		parameterStyleEEnum = createEEnum(PARAMETER_STYLE);
		securitySchemeTypeEEnum = createEEnum(SECURITY_SCHEME_TYPE);
		apiKeyLocationEEnum = createEEnum(API_KEY_LOCATION);
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
		JsonSchemaPackage theJsonSchemaPackage = (JsonSchemaPackage)EPackage.Registry.INSTANCE.getEPackage(JsonSchemaPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(openAPIEClass, OpenAPI.class, "OpenAPI", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOpenAPI_Openapi(), ecorePackage.getEString(), "openapi", null, 1, 1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOpenAPI_Self(), ecorePackage.getEString(), "self", null, 0, 1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_Info(), this.getInfo(), null, "info", null, 1, 1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOpenAPI_JsonSchemaDialect(), ecorePackage.getEString(), "jsonSchemaDialect", null, 0, 1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_Servers(), this.getServer(), null, "servers", null, 0, -1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_Paths(), this.getPathItemEntry(), null, "paths", null, 0, -1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_Webhooks(), this.getPathItemEntry(), null, "webhooks", null, 0, -1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_Components(), this.getComponents(), null, "components", null, 0, 1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_Security(), this.getSecurityRequirement(), null, "security", null, 0, -1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_Tags(), this.getTag(), null, "tags", null, 0, -1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_ExternalDocs(), this.getExternalDocumentation(), null, "externalDocs", null, 0, 1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOpenAPI_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, OpenAPI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(infoEClass, Info.class, "Info", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getInfo_Title(), ecorePackage.getEString(), "title", null, 1, 1, Info.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getInfo_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, Info.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getInfo_Description(), ecorePackage.getEString(), "description", null, 0, 1, Info.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getInfo_TermsOfService(), ecorePackage.getEString(), "termsOfService", null, 0, 1, Info.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInfo_Contact(), this.getContact(), null, "contact", null, 0, 1, Info.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInfo_License(), this.getLicense(), null, "license", null, 0, 1, Info.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getInfo_Version(), ecorePackage.getEString(), "version", null, 1, 1, Info.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getInfo_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Info.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(contactEClass, Contact.class, "Contact", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getContact_Name(), ecorePackage.getEString(), "name", null, 0, 1, Contact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContact_Url(), ecorePackage.getEString(), "url", null, 0, 1, Contact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getContact_Email(), ecorePackage.getEString(), "email", null, 0, 1, Contact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getContact_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Contact.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(licenseEClass, License.class, "License", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLicense_Name(), ecorePackage.getEString(), "name", null, 1, 1, License.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLicense_Identifier(), ecorePackage.getEString(), "identifier", null, 0, 1, License.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLicense_Url(), ecorePackage.getEString(), "url", null, 0, 1, License.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLicense_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, License.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(serverEClass, Server.class, "Server", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getServer_Url(), ecorePackage.getEString(), "url", null, 1, 1, Server.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getServer_Description(), ecorePackage.getEString(), "description", null, 0, 1, Server.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getServer_Name(), ecorePackage.getEString(), "name", null, 0, 1, Server.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServer_Variables(), this.getServerVariableEntry(), null, "variables", null, 0, -1, Server.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServer_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Server.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(serverVariableEClass, ServerVariable.class, "ServerVariable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getServerVariable_Enum(), ecorePackage.getEString(), "enum", null, 0, -1, ServerVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getServerVariable_Default(), ecorePackage.getEString(), "default", null, 1, 1, ServerVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getServerVariable_Description(), ecorePackage.getEString(), "description", null, 0, 1, ServerVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerVariable_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, ServerVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(componentsEClass, Components.class, "Components", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComponents_Schemas(), this.getSchemaEntry(), null, "schemas", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_SchemasPackage(), ecorePackage.getEPackage(), null, "schemasPackage", null, 0, 1, Components.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_Responses(), this.getResponseEntry(), null, "responses", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_Parameters(), this.getParameterEntry(), null, "parameters", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_Examples(), this.getExampleEntry(), null, "examples", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_RequestBodies(), this.getRequestBodyEntry(), null, "requestBodies", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_Headers(), this.getHeaderEntry(), null, "headers", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_SecuritySchemes(), this.getSecuritySchemeEntry(), null, "securitySchemes", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_Links(), this.getLinkEntry(), null, "links", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_Callbacks(), this.getCallbackEntry(), null, "callbacks", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_PathItems(), this.getPathItemEntry(), null, "pathItems", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_MediaTypes(), this.getMediaTypeEntry(), null, "mediaTypes", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponents_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Components.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pathItemEClass, PathItem.class, "PathItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPathItem_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Target(), this.getPathItem(), null, "target", null, 0, 1, PathItem.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPathItem_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPathItem_Description(), ecorePackage.getEString(), "description", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Get(), this.getOperation(), null, "get", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Put(), this.getOperation(), null, "put", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Post(), this.getOperation(), null, "post", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Delete(), this.getOperation(), null, "delete", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Options(), this.getOperation(), null, "options", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Head(), this.getOperation(), null, "head", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Patch(), this.getOperation(), null, "patch", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Trace(), this.getOperation(), null, "trace", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Query(), this.getOperation(), null, "query", null, 0, 1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_AdditionalOperations(), this.getOperationEntry(), null, "additionalOperations", null, 0, -1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Servers(), this.getServer(), null, "servers", null, 0, -1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Parameters(), this.getParameter(), null, "parameters", null, 0, -1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_AllOperations(), this.getOperation(), null, "allOperations", null, 0, -1, PathItem.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getPathItem_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, PathItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(operationEClass, Operation.class, "Operation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOperation_Tags(), ecorePackage.getEString(), "tags", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOperation_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOperation_Description(), ecorePackage.getEString(), "description", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperation_ExternalDocs(), this.getExternalDocumentation(), null, "externalDocs", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOperation_OperationId(), ecorePackage.getEString(), "operationId", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperation_Parameters(), this.getParameter(), null, "parameters", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperation_RequestBody(), this.getRequestBody(), null, "requestBody", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperation_Responses(), this.getResponseEntry(), null, "responses", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperation_Callbacks(), this.getCallbackEntry(), null, "callbacks", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOperation_Deprecated(), ecorePackage.getEBooleanObject(), "deprecated", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperation_Security(), this.getSecurityRequirement(), null, "security", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperation_Servers(), this.getServer(), null, "servers", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOperation_Method(), ecorePackage.getEString(), "method", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperation_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(externalDocumentationEClass, ExternalDocumentation.class, "ExternalDocumentation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getExternalDocumentation_Description(), ecorePackage.getEString(), "description", null, 0, 1, ExternalDocumentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExternalDocumentation_Url(), ecorePackage.getEString(), "url", null, 1, 1, ExternalDocumentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getExternalDocumentation_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, ExternalDocumentation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameter_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameter_Target(), this.getParameter(), null, "target", null, 0, 1, Parameter.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Name(), ecorePackage.getEString(), "name", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_In(), this.getParameterLocation(), "in", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Description(), ecorePackage.getEString(), "description", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Required(), ecorePackage.getEBooleanObject(), "required", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Deprecated(), ecorePackage.getEBooleanObject(), "deprecated", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_AllowEmptyValue(), ecorePackage.getEBooleanObject(), "allowEmptyValue", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Style(), this.getParameterStyle(), "style", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Explode(), ecorePackage.getEBooleanObject(), "explode", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_AllowReserved(), ecorePackage.getEBooleanObject(), "allowReserved", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameter_Schema(), theJsonSchemaPackage.getSchema(), null, "schema", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Example(), ecorePackage.getEJavaObject(), "example", null, 0, 1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameter_Examples(), this.getExampleEntry(), null, "examples", null, 0, -1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameter_Content(), this.getMediaTypeEntry(), null, "content", null, 0, -1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameter_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Parameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(requestBodyEClass, RequestBody.class, "RequestBody", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRequestBody_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, RequestBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRequestBody_Target(), this.getRequestBody(), null, "target", null, 0, 1, RequestBody.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRequestBody_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, RequestBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRequestBody_Description(), ecorePackage.getEString(), "description", null, 0, 1, RequestBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRequestBody_Content(), this.getMediaTypeEntry(), null, "content", null, 0, -1, RequestBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRequestBody_Required(), ecorePackage.getEBooleanObject(), "required", null, 0, 1, RequestBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRequestBody_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, RequestBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(mediaTypeEClass, MediaType.class, "MediaType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMediaType_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaType_Target(), this.getMediaType(), null, "target", null, 0, 1, MediaType.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMediaType_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMediaType_Description(), ecorePackage.getEString(), "description", null, 0, 1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaType_Schema(), theJsonSchemaPackage.getSchema(), null, "schema", null, 0, 1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaType_ItemSchema(), theJsonSchemaPackage.getSchema(), null, "itemSchema", null, 0, 1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMediaType_Example(), ecorePackage.getEJavaObject(), "example", null, 0, 1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaType_Examples(), this.getExampleEntry(), null, "examples", null, 0, -1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaType_Encoding(), this.getEncodingEntry(), null, "encoding", null, 0, -1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaType_PrefixEncoding(), this.getEncoding(), null, "prefixEncoding", null, 0, -1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaType_ItemEncoding(), this.getEncoding(), null, "itemEncoding", null, 0, 1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaType_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, MediaType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(encodingEClass, Encoding.class, "Encoding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEncoding_ContentType(), ecorePackage.getEString(), "contentType", null, 0, 1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEncoding_Headers(), this.getHeaderEntry(), null, "headers", null, 0, -1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEncoding_Style(), this.getParameterStyle(), "style", null, 0, 1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEncoding_Explode(), ecorePackage.getEBooleanObject(), "explode", null, 0, 1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEncoding_AllowReserved(), ecorePackage.getEBooleanObject(), "allowReserved", null, 0, 1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEncoding_Encoding(), this.getEncodingEntry(), null, "encoding", null, 0, -1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEncoding_PrefixEncoding(), this.getEncoding(), null, "prefixEncoding", null, 0, -1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEncoding_ItemEncoding(), this.getEncoding(), null, "itemEncoding", null, 0, 1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEncoding_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Encoding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(responseEClass, Response.class, "Response", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getResponse_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, Response.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResponse_Target(), this.getResponse(), null, "target", null, 0, 1, Response.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getResponse_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, Response.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getResponse_Description(), ecorePackage.getEString(), "description", null, 0, 1, Response.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResponse_Headers(), this.getHeaderEntry(), null, "headers", null, 0, -1, Response.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResponse_Content(), this.getMediaTypeEntry(), null, "content", null, 0, -1, Response.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResponse_Links(), this.getLinkEntry(), null, "links", null, 0, -1, Response.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResponse_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Response.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(callbackEClass, Callback.class, "Callback", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCallback_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, Callback.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCallback_Target(), this.getCallback(), null, "target", null, 0, 1, Callback.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCallback_Expressions(), this.getPathItemEntry(), null, "expressions", null, 0, -1, Callback.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(exampleEClass, Example.class, "Example", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getExample_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, Example.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getExample_Target(), this.getExample(), null, "target", null, 0, 1, Example.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExample_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, Example.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExample_Description(), ecorePackage.getEString(), "description", null, 0, 1, Example.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExample_Value(), ecorePackage.getEJavaObject(), "value", null, 0, 1, Example.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExample_DataValue(), ecorePackage.getEJavaObject(), "dataValue", null, 0, 1, Example.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExample_SerializedValue(), ecorePackage.getEString(), "serializedValue", null, 0, 1, Example.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExample_ExternalValue(), ecorePackage.getEString(), "externalValue", null, 0, 1, Example.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getExample_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Example.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(linkEClass, Link.class, "Link", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLink_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLink_Target(), this.getLink(), null, "target", null, 0, 1, Link.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLink_OperationRef(), ecorePackage.getEString(), "operationRef", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLink_OperationId(), ecorePackage.getEString(), "operationId", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLink_Parameters(), this.getAnyEntry(), null, "parameters", null, 0, -1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLink_RequestBody(), ecorePackage.getEJavaObject(), "requestBody", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLink_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLink_Description(), ecorePackage.getEString(), "description", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLink_Server(), this.getServer(), null, "server", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLink_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(headerEClass, Header.class, "Header", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getHeader_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHeader_Target(), this.getHeader(), null, "target", null, 0, 1, Header.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_Description(), ecorePackage.getEString(), "description", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_Required(), ecorePackage.getEBooleanObject(), "required", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_Deprecated(), ecorePackage.getEBooleanObject(), "deprecated", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_AllowEmptyValue(), ecorePackage.getEBooleanObject(), "allowEmptyValue", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_Style(), this.getParameterStyle(), "style", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_Explode(), ecorePackage.getEBooleanObject(), "explode", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_AllowReserved(), ecorePackage.getEBooleanObject(), "allowReserved", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHeader_Schema(), theJsonSchemaPackage.getSchema(), null, "schema", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getHeader_Example(), ecorePackage.getEJavaObject(), "example", null, 0, 1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHeader_Examples(), this.getExampleEntry(), null, "examples", null, 0, -1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHeader_Content(), this.getMediaTypeEntry(), null, "content", null, 0, -1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHeader_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Header.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tagEClass, Tag.class, "Tag", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTag_Name(), ecorePackage.getEString(), "name", null, 1, 1, Tag.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTag_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, Tag.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTag_Description(), ecorePackage.getEString(), "description", null, 0, 1, Tag.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTag_ExternalDocs(), this.getExternalDocumentation(), null, "externalDocs", null, 0, 1, Tag.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTag_Parent(), ecorePackage.getEString(), "parent", null, 0, 1, Tag.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTag_Kind(), ecorePackage.getEString(), "kind", null, 0, 1, Tag.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTag_ParentTag(), this.getTag(), null, "parentTag", null, 0, 1, Tag.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTag_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, Tag.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(securitySchemeEClass, SecurityScheme.class, "SecurityScheme", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSecurityScheme_Ref(), ecorePackage.getEString(), "ref", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSecurityScheme_Target(), this.getSecurityScheme(), null, "target", null, 0, 1, SecurityScheme.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_Type(), this.getSecuritySchemeType(), "type", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_Summary(), ecorePackage.getEString(), "summary", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_Description(), ecorePackage.getEString(), "description", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_Name(), ecorePackage.getEString(), "name", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_In(), this.getApiKeyLocation(), "in", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_Scheme(), ecorePackage.getEString(), "scheme", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_BearerFormat(), ecorePackage.getEString(), "bearerFormat", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSecurityScheme_Flows(), this.getOAuthFlows(), null, "flows", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_OpenIdConnectUrl(), ecorePackage.getEString(), "openIdConnectUrl", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_Oauth2MetadataUrl(), ecorePackage.getEString(), "oauth2MetadataUrl", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityScheme_Deprecated(), ecorePackage.getEBooleanObject(), "deprecated", null, 0, 1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSecurityScheme_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, SecurityScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(oAuthFlowsEClass, OAuthFlows.class, "OAuthFlows", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getOAuthFlows_Implicit(), this.getOAuthFlow(), null, "implicit", null, 0, 1, OAuthFlows.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOAuthFlows_Password(), this.getOAuthFlow(), null, "password", null, 0, 1, OAuthFlows.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOAuthFlows_ClientCredentials(), this.getOAuthFlow(), null, "clientCredentials", null, 0, 1, OAuthFlows.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOAuthFlows_AuthorizationCode(), this.getOAuthFlow(), null, "authorizationCode", null, 0, 1, OAuthFlows.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOAuthFlows_DeviceAuthorization(), this.getOAuthFlow(), null, "deviceAuthorization", null, 0, 1, OAuthFlows.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOAuthFlows_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, OAuthFlows.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(oAuthFlowEClass, OAuthFlow.class, "OAuthFlow", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOAuthFlow_AuthorizationUrl(), ecorePackage.getEString(), "authorizationUrl", null, 0, 1, OAuthFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOAuthFlow_DeviceAuthorizationUrl(), ecorePackage.getEString(), "deviceAuthorizationUrl", null, 0, 1, OAuthFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOAuthFlow_TokenUrl(), ecorePackage.getEString(), "tokenUrl", null, 0, 1, OAuthFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getOAuthFlow_RefreshUrl(), ecorePackage.getEString(), "refreshUrl", null, 0, 1, OAuthFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOAuthFlow_Scopes(), this.getStringEntry(), null, "scopes", null, 0, -1, OAuthFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOAuthFlow_Extensions(), this.getExtensionEntry(), null, "extensions", null, 0, -1, OAuthFlow.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(securityRequirementEClass, SecurityRequirement.class, "SecurityRequirement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSecurityRequirement_Schemes(), this.getSecurityRequirementEntry(), null, "schemes", null, 0, -1, SecurityRequirement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pathItemEntryEClass, Map.Entry.class, "PathItemEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPathItemEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPathItemEntry_Value(), this.getPathItem(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(operationEntryEClass, Map.Entry.class, "OperationEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getOperationEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getOperationEntry_Value(), this.getOperation(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(responseEntryEClass, Map.Entry.class, "ResponseEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getResponseEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResponseEntry_Value(), this.getResponse(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterEntryEClass, Map.Entry.class, "ParameterEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameterEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getParameterEntry_Value(), this.getParameter(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(exampleEntryEClass, Map.Entry.class, "ExampleEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getExampleEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getExampleEntry_Value(), this.getExample(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(requestBodyEntryEClass, Map.Entry.class, "RequestBodyEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRequestBodyEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRequestBodyEntry_Value(), this.getRequestBody(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(headerEntryEClass, Map.Entry.class, "HeaderEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getHeaderEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHeaderEntry_Value(), this.getHeader(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(securitySchemeEntryEClass, Map.Entry.class, "SecuritySchemeEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSecuritySchemeEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSecuritySchemeEntry_Value(), this.getSecurityScheme(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(linkEntryEClass, Map.Entry.class, "LinkEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLinkEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLinkEntry_Value(), this.getLink(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(callbackEntryEClass, Map.Entry.class, "CallbackEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCallbackEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCallbackEntry_Value(), this.getCallback(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(mediaTypeEntryEClass, Map.Entry.class, "MediaTypeEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMediaTypeEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMediaTypeEntry_Value(), this.getMediaType(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(encodingEntryEClass, Map.Entry.class, "EncodingEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEncodingEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getEncodingEntry_Value(), this.getEncoding(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(serverVariableEntryEClass, Map.Entry.class, "ServerVariableEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getServerVariableEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerVariableEntry_Value(), this.getServerVariable(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringEntryEClass, Map.Entry.class, "StringEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStringEntry_Value(), ecorePackage.getEString(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(anyEntryEClass, Map.Entry.class, "AnyEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAnyEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAnyEntry_Value(), ecorePackage.getEJavaObject(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(extensionEntryEClass, Map.Entry.class, "ExtensionEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getExtensionEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getExtensionEntry_Value(), ecorePackage.getEJavaObject(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(securityRequirementEntryEClass, Map.Entry.class, "SecurityRequirementEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSecurityRequirementEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSecurityRequirementEntry_Value(), ecorePackage.getEString(), "value", null, 0, -1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(schemaEntryEClass, Map.Entry.class, "SchemaEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSchemaEntry_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSchemaEntry_Value(), theJsonSchemaPackage.getSchema(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(parameterLocationEEnum, ParameterLocation.class, "ParameterLocation");
		addEEnumLiteral(parameterLocationEEnum, ParameterLocation.NULL);
		addEEnumLiteral(parameterLocationEEnum, ParameterLocation.QUERY);
		addEEnumLiteral(parameterLocationEEnum, ParameterLocation.HEADER);
		addEEnumLiteral(parameterLocationEEnum, ParameterLocation.PATH);
		addEEnumLiteral(parameterLocationEEnum, ParameterLocation.COOKIE);
		addEEnumLiteral(parameterLocationEEnum, ParameterLocation.QUERYSTRING);

		initEEnum(parameterStyleEEnum, ParameterStyle.class, "ParameterStyle");
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.NULL);
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.MATRIX);
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.LABEL);
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.SIMPLE);
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.FORM);
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.SPACE_DELIMITED);
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.PIPE_DELIMITED);
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.DEEP_OBJECT);
		addEEnumLiteral(parameterStyleEEnum, ParameterStyle.COOKIE);

		initEEnum(securitySchemeTypeEEnum, SecuritySchemeType.class, "SecuritySchemeType");
		addEEnumLiteral(securitySchemeTypeEEnum, SecuritySchemeType.NULL);
		addEEnumLiteral(securitySchemeTypeEEnum, SecuritySchemeType.API_KEY);
		addEEnumLiteral(securitySchemeTypeEEnum, SecuritySchemeType.HTTP);
		addEEnumLiteral(securitySchemeTypeEEnum, SecuritySchemeType.MUTUAL_TLS);
		addEEnumLiteral(securitySchemeTypeEEnum, SecuritySchemeType.OAUTH2);
		addEEnumLiteral(securitySchemeTypeEEnum, SecuritySchemeType.OPEN_ID_CONNECT);

		initEEnum(apiKeyLocationEEnum, ApiKeyLocation.class, "ApiKeyLocation");
		addEEnumLiteral(apiKeyLocationEEnum, ApiKeyLocation.NULL);
		addEEnumLiteral(apiKeyLocationEEnum, ApiKeyLocation.QUERY);
		addEEnumLiteral(apiKeyLocationEEnum, ApiKeyLocation.HEADER);
		addEEnumLiteral(apiKeyLocationEEnum, ApiKeyLocation.COOKIE);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://eclipse.org/fennec/codec
		createCodecAnnotations();
		// http://eclipse.org/fennec/openapi
		createOpenapiAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://eclipse.org/fennec/codec</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createCodecAnnotations() {
		String source = "http://eclipse.org/fennec/codec";
		addAnnotation
		  (openAPIEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getOpenAPI_Self(),
		   source,
		   new String[] {
			   "key", "$self"
		   });
		addAnnotation
		  (getOpenAPI_Servers(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOpenAPI_Paths(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOpenAPI_Webhooks(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOpenAPI_Security(),
		   source,
		   new String[] {
			   "valueReaderName", "securityRequirement",
			   "valueWriterName", "securityRequirement",
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOpenAPI_Tags(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (infoEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (contactEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (licenseEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (serverEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getServer_Variables(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (serverVariableEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getServerVariable_Enum(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (componentsEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getComponents_Schemas(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_Responses(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_Parameters(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_Examples(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_RequestBodies(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_Headers(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_SecuritySchemes(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_Links(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_Callbacks(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_PathItems(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getComponents_MediaTypes(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (pathItemEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getPathItem_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getPathItem_Get(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_Put(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_Post(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_Delete(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_Options(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_Head(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_Patch(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_Trace(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_Query(),
		   source,
		   new String[] {
			   "valueReaderName", "operation"
		   });
		addAnnotation
		  (getPathItem_AdditionalOperations(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getPathItem_Servers(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getPathItem_Parameters(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (operationEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getOperation_Tags(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOperation_Parameters(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOperation_Responses(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOperation_Callbacks(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOperation_Deprecated(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getOperation_Security(),
		   source,
		   new String[] {
			   "valueReaderName", "securityRequirement",
			   "valueWriterName", "securityRequirement",
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOperation_Servers(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getOperation_Method(),
		   source,
		   new String[] {
			   "ignoreWrite", "true"
		   });
		addAnnotation
		  (externalDocumentationEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (parameterEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getParameter_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getParameter_Required(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getParameter_Deprecated(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getParameter_AllowEmptyValue(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getParameter_Explode(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getParameter_AllowReserved(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getParameter_Schema(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getParameter_Example(),
		   source,
		   new String[] {
			   "serializeNull", "true"
		   });
		addAnnotation
		  (getParameter_Examples(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getParameter_Content(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (requestBodyEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getRequestBody_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getRequestBody_Content(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getRequestBody_Required(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (mediaTypeEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getMediaType_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getMediaType_Schema(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getMediaType_ItemSchema(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getMediaType_Example(),
		   source,
		   new String[] {
			   "serializeNull", "true"
		   });
		addAnnotation
		  (getMediaType_Examples(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getMediaType_Encoding(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getMediaType_PrefixEncoding(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (encodingEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getEncoding_Headers(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getEncoding_Explode(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getEncoding_AllowReserved(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getEncoding_Encoding(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getEncoding_PrefixEncoding(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (responseEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getResponse_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getResponse_Headers(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getResponse_Content(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getResponse_Links(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (callbackEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "expressions"
		   });
		addAnnotation
		  (getCallback_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getCallback_Expressions(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (exampleEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getExample_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getExample_Value(),
		   source,
		   new String[] {
			   "serializeNull", "true"
		   });
		addAnnotation
		  (getExample_DataValue(),
		   source,
		   new String[] {
			   "serializeNull", "true"
		   });
		addAnnotation
		  (linkEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getLink_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getLink_Parameters(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getLink_RequestBody(),
		   source,
		   new String[] {
			   "serializeNull", "true"
		   });
		addAnnotation
		  (headerEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getHeader_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getHeader_Required(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getHeader_Deprecated(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getHeader_AllowEmptyValue(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getHeader_Explode(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getHeader_AllowReserved(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (getHeader_Schema(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
		addAnnotation
		  (getHeader_Example(),
		   source,
		   new String[] {
			   "serializeNull", "true"
		   });
		addAnnotation
		  (getHeader_Examples(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getHeader_Content(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (tagEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (securitySchemeEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getSecurityScheme_Ref(),
		   source,
		   new String[] {
			   "key", "$ref"
		   });
		addAnnotation
		  (getSecurityScheme_Deprecated(),
		   source,
		   new String[] {
			   "serializeDefault", "true"
		   });
		addAnnotation
		  (oAuthFlowsEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (oAuthFlowEClass,
		   source,
		   new String[] {
			   "additionalPropertiesFeature", "extensions"
		   });
		addAnnotation
		  (getOAuthFlow_Scopes(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getSecurityRequirement_Schemes(),
		   source,
		   new String[] {
			   "serializeEmpty", "true"
		   });
		addAnnotation
		  (getSchemaEntry_Value(),
		   source,
		   new String[] {
			   "valueReaderName", "jsonSchema",
			   "valueWriterName", "jsonSchema"
		   });
	}

	/**
	 * Initializes the annotations for <b>http://eclipse.org/fennec/openapi</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createOpenapiAnnotations() {
		String source = "http://eclipse.org/fennec/openapi";
		addAnnotation
		  (getOpenAPI_Self(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getOpenAPI_JsonSchemaDialect(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getOpenAPI_Webhooks(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getInfo_Summary(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getLicense_Identifier(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getServer_Name(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getComponents_PathItems(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getComponents_MediaTypes(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getPathItem_Query(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getPathItem_AdditionalOperations(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getParameter_Summary(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getRequestBody_Summary(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getMediaType_Summary(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getMediaType_Description(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getMediaType_ItemSchema(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getMediaType_PrefixEncoding(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getMediaType_ItemEncoding(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getEncoding_Encoding(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getEncoding_PrefixEncoding(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getEncoding_ItemEncoding(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getResponse_Summary(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getExample_DataValue(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getExample_SerializedValue(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getLink_Summary(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getHeader_Summary(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getTag_Summary(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getTag_Parent(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getTag_Kind(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getTag_ParentTag(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getSecurityScheme_Summary(),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
		addAnnotation
		  (getSecurityScheme_Oauth2MetadataUrl(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getSecurityScheme_Deprecated(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getOAuthFlows_DeviceAuthorization(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (getOAuthFlow_DeviceAuthorizationUrl(),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (parameterLocationEEnum.getELiterals().get(5),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (parameterStyleEEnum.getELiterals().get(8),
		   source,
		   new String[] {
			   "since", "3.2"
		   });
		addAnnotation
		  (securitySchemeTypeEEnum.getELiterals().get(3),
		   source,
		   new String[] {
			   "since", "3.1"
		   });
	}

} //OasPackageImpl
