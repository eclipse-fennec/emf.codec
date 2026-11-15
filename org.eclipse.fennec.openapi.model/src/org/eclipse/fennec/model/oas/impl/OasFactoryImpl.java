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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.codec.jsonschema.model.Schema;

import org.eclipse.fennec.model.oas.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OasFactoryImpl extends EFactoryImpl implements OasFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static OasFactory init() {
		try {
			OasFactory theOasFactory = (OasFactory)EPackage.Registry.INSTANCE.getEFactory(OasPackage.eNS_URI);
			if (theOasFactory != null) {
				return theOasFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new OasFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OasFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case OasPackage.OPEN_API: return createOpenAPI();
			case OasPackage.INFO: return createInfo();
			case OasPackage.CONTACT: return createContact();
			case OasPackage.LICENSE: return createLicense();
			case OasPackage.SERVER: return createServer();
			case OasPackage.SERVER_VARIABLE: return createServerVariable();
			case OasPackage.COMPONENTS: return createComponents();
			case OasPackage.PATH_ITEM: return createPathItem();
			case OasPackage.OPERATION: return createOperation();
			case OasPackage.EXTERNAL_DOCUMENTATION: return createExternalDocumentation();
			case OasPackage.PARAMETER: return createParameter();
			case OasPackage.REQUEST_BODY: return createRequestBody();
			case OasPackage.MEDIA_TYPE: return createMediaType();
			case OasPackage.ENCODING: return createEncoding();
			case OasPackage.RESPONSE: return createResponse();
			case OasPackage.CALLBACK: return createCallback();
			case OasPackage.EXAMPLE: return createExample();
			case OasPackage.LINK: return createLink();
			case OasPackage.HEADER: return createHeader();
			case OasPackage.TAG: return createTag();
			case OasPackage.SECURITY_SCHEME: return createSecurityScheme();
			case OasPackage.OAUTH_FLOWS: return createOAuthFlows();
			case OasPackage.OAUTH_FLOW: return createOAuthFlow();
			case OasPackage.SECURITY_REQUIREMENT: return createSecurityRequirement();
			case OasPackage.PATH_ITEM_ENTRY: return (EObject)createPathItemEntry();
			case OasPackage.OPERATION_ENTRY: return (EObject)createOperationEntry();
			case OasPackage.RESPONSE_ENTRY: return (EObject)createResponseEntry();
			case OasPackage.PARAMETER_ENTRY: return (EObject)createParameterEntry();
			case OasPackage.EXAMPLE_ENTRY: return (EObject)createExampleEntry();
			case OasPackage.REQUEST_BODY_ENTRY: return (EObject)createRequestBodyEntry();
			case OasPackage.HEADER_ENTRY: return (EObject)createHeaderEntry();
			case OasPackage.SECURITY_SCHEME_ENTRY: return (EObject)createSecuritySchemeEntry();
			case OasPackage.LINK_ENTRY: return (EObject)createLinkEntry();
			case OasPackage.CALLBACK_ENTRY: return (EObject)createCallbackEntry();
			case OasPackage.MEDIA_TYPE_ENTRY: return (EObject)createMediaTypeEntry();
			case OasPackage.ENCODING_ENTRY: return (EObject)createEncodingEntry();
			case OasPackage.SERVER_VARIABLE_ENTRY: return (EObject)createServerVariableEntry();
			case OasPackage.STRING_ENTRY: return (EObject)createStringEntry();
			case OasPackage.ANY_ENTRY: return (EObject)createAnyEntry();
			case OasPackage.EXTENSION_ENTRY: return (EObject)createExtensionEntry();
			case OasPackage.SECURITY_REQUIREMENT_ENTRY: return (EObject)createSecurityRequirementEntry();
			case OasPackage.SCHEMA_ENTRY: return (EObject)createSchemaEntry();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case OasPackage.PARAMETER_LOCATION:
				return createParameterLocationFromString(eDataType, initialValue);
			case OasPackage.PARAMETER_STYLE:
				return createParameterStyleFromString(eDataType, initialValue);
			case OasPackage.SECURITY_SCHEME_TYPE:
				return createSecuritySchemeTypeFromString(eDataType, initialValue);
			case OasPackage.API_KEY_LOCATION:
				return createApiKeyLocationFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case OasPackage.PARAMETER_LOCATION:
				return convertParameterLocationToString(eDataType, instanceValue);
			case OasPackage.PARAMETER_STYLE:
				return convertParameterStyleToString(eDataType, instanceValue);
			case OasPackage.SECURITY_SCHEME_TYPE:
				return convertSecuritySchemeTypeToString(eDataType, instanceValue);
			case OasPackage.API_KEY_LOCATION:
				return convertApiKeyLocationToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OpenAPI createOpenAPI() {
		OpenAPIImpl openAPI = new OpenAPIImpl();
		return openAPI;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Info createInfo() {
		InfoImpl info = new InfoImpl();
		return info;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Contact createContact() {
		ContactImpl contact = new ContactImpl();
		return contact;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public License createLicense() {
		LicenseImpl license = new LicenseImpl();
		return license;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Server createServer() {
		ServerImpl server = new ServerImpl();
		return server;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServerVariable createServerVariable() {
		ServerVariableImpl serverVariable = new ServerVariableImpl();
		return serverVariable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Components createComponents() {
		ComponentsImpl components = new ComponentsImpl();
		return components;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PathItem createPathItem() {
		PathItemImpl pathItem = new PathItemImpl();
		return pathItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Operation createOperation() {
		OperationImpl operation = new OperationImpl();
		return operation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ExternalDocumentation createExternalDocumentation() {
		ExternalDocumentationImpl externalDocumentation = new ExternalDocumentationImpl();
		return externalDocumentation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Parameter createParameter() {
		ParameterImpl parameter = new ParameterImpl();
		return parameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public RequestBody createRequestBody() {
		RequestBodyImpl requestBody = new RequestBodyImpl();
		return requestBody;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MediaType createMediaType() {
		MediaTypeImpl mediaType = new MediaTypeImpl();
		return mediaType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Encoding createEncoding() {
		EncodingImpl encoding = new EncodingImpl();
		return encoding;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Response createResponse() {
		ResponseImpl response = new ResponseImpl();
		return response;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Callback createCallback() {
		CallbackImpl callback = new CallbackImpl();
		return callback;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Example createExample() {
		ExampleImpl example = new ExampleImpl();
		return example;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Link createLink() {
		LinkImpl link = new LinkImpl();
		return link;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Header createHeader() {
		HeaderImpl header = new HeaderImpl();
		return header;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Tag createTag() {
		TagImpl tag = new TagImpl();
		return tag;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SecurityScheme createSecurityScheme() {
		SecuritySchemeImpl securityScheme = new SecuritySchemeImpl();
		return securityScheme;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OAuthFlows createOAuthFlows() {
		OAuthFlowsImpl oAuthFlows = new OAuthFlowsImpl();
		return oAuthFlows;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OAuthFlow createOAuthFlow() {
		OAuthFlowImpl oAuthFlow = new OAuthFlowImpl();
		return oAuthFlow;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SecurityRequirement createSecurityRequirement() {
		SecurityRequirementImpl securityRequirement = new SecurityRequirementImpl();
		return securityRequirement;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, PathItem> createPathItemEntry() {
		PathItemEntryImpl pathItemEntry = new PathItemEntryImpl();
		return pathItemEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Operation> createOperationEntry() {
		OperationEntryImpl operationEntry = new OperationEntryImpl();
		return operationEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Response> createResponseEntry() {
		ResponseEntryImpl responseEntry = new ResponseEntryImpl();
		return responseEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Parameter> createParameterEntry() {
		ParameterEntryImpl parameterEntry = new ParameterEntryImpl();
		return parameterEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Example> createExampleEntry() {
		ExampleEntryImpl exampleEntry = new ExampleEntryImpl();
		return exampleEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, RequestBody> createRequestBodyEntry() {
		RequestBodyEntryImpl requestBodyEntry = new RequestBodyEntryImpl();
		return requestBodyEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Header> createHeaderEntry() {
		HeaderEntryImpl headerEntry = new HeaderEntryImpl();
		return headerEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, SecurityScheme> createSecuritySchemeEntry() {
		SecuritySchemeEntryImpl securitySchemeEntry = new SecuritySchemeEntryImpl();
		return securitySchemeEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Link> createLinkEntry() {
		LinkEntryImpl linkEntry = new LinkEntryImpl();
		return linkEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Callback> createCallbackEntry() {
		CallbackEntryImpl callbackEntry = new CallbackEntryImpl();
		return callbackEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, MediaType> createMediaTypeEntry() {
		MediaTypeEntryImpl mediaTypeEntry = new MediaTypeEntryImpl();
		return mediaTypeEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Encoding> createEncodingEntry() {
		EncodingEntryImpl encodingEntry = new EncodingEntryImpl();
		return encodingEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, ServerVariable> createServerVariableEntry() {
		ServerVariableEntryImpl serverVariableEntry = new ServerVariableEntryImpl();
		return serverVariableEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, String> createStringEntry() {
		StringEntryImpl stringEntry = new StringEntryImpl();
		return stringEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Object> createAnyEntry() {
		AnyEntryImpl anyEntry = new AnyEntryImpl();
		return anyEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Object> createExtensionEntry() {
		ExtensionEntryImpl extensionEntry = new ExtensionEntryImpl();
		return extensionEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, EList<String>> createSecurityRequirementEntry() {
		SecurityRequirementEntryImpl securityRequirementEntry = new SecurityRequirementEntryImpl();
		return securityRequirementEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Schema> createSchemaEntry() {
		SchemaEntryImpl schemaEntry = new SchemaEntryImpl();
		return schemaEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterLocation createParameterLocationFromString(EDataType eDataType, String initialValue) {
		ParameterLocation result = ParameterLocation.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertParameterLocationToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterStyle createParameterStyleFromString(EDataType eDataType, String initialValue) {
		ParameterStyle result = ParameterStyle.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertParameterStyleToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SecuritySchemeType createSecuritySchemeTypeFromString(EDataType eDataType, String initialValue) {
		SecuritySchemeType result = SecuritySchemeType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSecuritySchemeTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ApiKeyLocation createApiKeyLocationFromString(EDataType eDataType, String initialValue) {
		ApiKeyLocation result = ApiKeyLocation.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertApiKeyLocationToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public OasPackage getOasPackage() {
		return (OasPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static OasPackage getPackage() {
		return OasPackage.eINSTANCE;
	}

} //OasFactoryImpl
