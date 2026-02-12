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
package org.eclipse.fennec.model.openapi.impl;

import java.util.Map;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.model.openapi.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class OpenApiFactoryImpl extends EFactoryImpl implements OpenApiFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static OpenApiFactory init() {
		try {
			OpenApiFactory theOpenApiFactory = (OpenApiFactory)EPackage.Registry.INSTANCE.getEFactory(OpenApiPackage.eNS_URI);
			if (theOpenApiFactory != null) {
				return theOpenApiFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new OpenApiFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OpenApiFactoryImpl() {
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
			case OpenApiPackage.OPEN_API: return createOpenAPI();
			case OpenApiPackage.INFO: return createInfo();
			case OpenApiPackage.CONTACT: return createContact();
			case OpenApiPackage.LICENSE: return createLicense();
			case OpenApiPackage.SERVER: return createServer();
			case OpenApiPackage.SERVER_VARIABLE: return createServerVariable();
			case OpenApiPackage.TAG: return createTag();
			case OpenApiPackage.EXTERNAL_DOCUMENTATION: return createExternalDocumentation();
			case OpenApiPackage.PATH_ITEM: return createPathItem();
			case OpenApiPackage.OPERATION: return createOperation();
			case OpenApiPackage.PARAMETER: return createParameter();
			case OpenApiPackage.REQUEST_BODY: return createRequestBody();
			case OpenApiPackage.MEDIA_TYPE: return createMediaType();
			case OpenApiPackage.ENCODING: return createEncoding();
			case OpenApiPackage.RESPONSE: return createResponse();
			case OpenApiPackage.HEADER: return createHeader();
			case OpenApiPackage.CALLBACK: return createCallback();
			case OpenApiPackage.EXAMPLE: return createExample();
			case OpenApiPackage.LINK: return createLink();
			case OpenApiPackage.SCHEMA: return createSchema();
			case OpenApiPackage.DISCRIMINATOR: return createDiscriminator();
			case OpenApiPackage.COMPONENTS: return createComponents();
			case OpenApiPackage.SECURITY_SCHEME: return createSecurityScheme();
			case OpenApiPackage.OAUTH_FLOWS: return createOAuthFlows();
			case OpenApiPackage.OAUTH_FLOW: return createOAuthFlow();
			case OpenApiPackage.SECURITY_REQUIREMENT: return createSecurityRequirement();
			case OpenApiPackage.EXTENSION: return createExtension();
			case OpenApiPackage.STRING_ENTRY: return (EObject)createStringEntry();
			case OpenApiPackage.PATH_ENTRY: return (EObject)createPathEntry();
			case OpenApiPackage.SCHEMA_ENTRY: return (EObject)createSchemaEntry();
			case OpenApiPackage.RESPONSE_ENTRY: return (EObject)createResponseEntry();
			case OpenApiPackage.PARAMETER_ENTRY: return (EObject)createParameterEntry();
			case OpenApiPackage.EXAMPLE_ENTRY: return (EObject)createExampleEntry();
			case OpenApiPackage.REQUEST_BODY_ENTRY: return (EObject)createRequestBodyEntry();
			case OpenApiPackage.HEADER_ENTRY: return (EObject)createHeaderEntry();
			case OpenApiPackage.SECURITY_SCHEME_ENTRY: return (EObject)createSecuritySchemeEntry();
			case OpenApiPackage.LINK_ENTRY: return (EObject)createLinkEntry();
			case OpenApiPackage.CALLBACK_ENTRY: return (EObject)createCallbackEntry();
			case OpenApiPackage.MEDIA_TYPE_ENTRY: return (EObject)createMediaTypeEntry();
			case OpenApiPackage.ENCODING_ENTRY: return (EObject)createEncodingEntry();
			case OpenApiPackage.SERVER_VARIABLE_ENTRY: return (EObject)createServerVariableEntry();
			case OpenApiPackage.SECURITY_REQUIREMENT_ENTRY: return (EObject)createSecurityRequirementEntry();
			case OpenApiPackage.ANY_ENTRY: return (EObject)createAnyEntry();
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
			case OpenApiPackage.HTTP_METHOD:
				return createHttpMethodFromString(eDataType, initialValue);
			case OpenApiPackage.PARAMETER_LOCATION:
				return createParameterLocationFromString(eDataType, initialValue);
			case OpenApiPackage.PARAMETER_STYLE:
				return createParameterStyleFromString(eDataType, initialValue);
			case OpenApiPackage.SECURITY_SCHEME_TYPE:
				return createSecuritySchemeTypeFromString(eDataType, initialValue);
			case OpenApiPackage.API_KEY_LOCATION:
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
			case OpenApiPackage.HTTP_METHOD:
				return convertHttpMethodToString(eDataType, instanceValue);
			case OpenApiPackage.PARAMETER_LOCATION:
				return convertParameterLocationToString(eDataType, instanceValue);
			case OpenApiPackage.PARAMETER_STYLE:
				return convertParameterStyleToString(eDataType, instanceValue);
			case OpenApiPackage.SECURITY_SCHEME_TYPE:
				return convertSecuritySchemeTypeToString(eDataType, instanceValue);
			case OpenApiPackage.API_KEY_LOCATION:
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
	public Schema createSchema() {
		SchemaImpl schema = new SchemaImpl();
		return schema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Discriminator createDiscriminator() {
		DiscriminatorImpl discriminator = new DiscriminatorImpl();
		return discriminator;
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
	@Override
	public Extension createExtension() {
		ExtensionImpl extension = new ExtensionImpl();
		return extension;
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
	public Map.Entry<String, PathItem> createPathEntry() {
		PathEntryImpl pathEntry = new PathEntryImpl();
		return pathEntry;
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
	public Map.Entry<String, EList<String>> createSecurityRequirementEntry() {
		SecurityRequirementEntryImpl securityRequirementEntry = new SecurityRequirementEntryImpl();
		return securityRequirementEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, EObject> createAnyEntry() {
		AnyEntryImpl anyEntry = new AnyEntryImpl();
		return anyEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HttpMethod createHttpMethodFromString(EDataType eDataType, String initialValue) {
		HttpMethod result = HttpMethod.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertHttpMethodToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
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
	public OpenApiPackage getOpenApiPackage() {
		return (OpenApiPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static OpenApiPackage getPackage() {
		return OpenApiPackage.eINSTANCE;
	}

} //OpenApiFactoryImpl
