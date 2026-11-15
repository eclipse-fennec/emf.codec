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
package org.eclipse.fennec.model.oas.util;

import java.util.Map;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.fennec.model.oas.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.model.oas.OasPackage
 * @generated
 */
public class OasSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static OasPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OasSwitch() {
		if (modelPackage == null) {
			modelPackage = OasPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case OasPackage.OPEN_API: {
				OpenAPI openAPI = (OpenAPI)theEObject;
				T result = caseOpenAPI(openAPI);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.INFO: {
				Info info = (Info)theEObject;
				T result = caseInfo(info);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.CONTACT: {
				Contact contact = (Contact)theEObject;
				T result = caseContact(contact);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.LICENSE: {
				License license = (License)theEObject;
				T result = caseLicense(license);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.SERVER: {
				Server server = (Server)theEObject;
				T result = caseServer(server);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.SERVER_VARIABLE: {
				ServerVariable serverVariable = (ServerVariable)theEObject;
				T result = caseServerVariable(serverVariable);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.COMPONENTS: {
				Components components = (Components)theEObject;
				T result = caseComponents(components);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.PATH_ITEM: {
				PathItem pathItem = (PathItem)theEObject;
				T result = casePathItem(pathItem);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.OPERATION: {
				Operation operation = (Operation)theEObject;
				T result = caseOperation(operation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.EXTERNAL_DOCUMENTATION: {
				ExternalDocumentation externalDocumentation = (ExternalDocumentation)theEObject;
				T result = caseExternalDocumentation(externalDocumentation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.PARAMETER: {
				Parameter parameter = (Parameter)theEObject;
				T result = caseParameter(parameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.REQUEST_BODY: {
				RequestBody requestBody = (RequestBody)theEObject;
				T result = caseRequestBody(requestBody);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.MEDIA_TYPE: {
				MediaType mediaType = (MediaType)theEObject;
				T result = caseMediaType(mediaType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.ENCODING: {
				Encoding encoding = (Encoding)theEObject;
				T result = caseEncoding(encoding);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.RESPONSE: {
				Response response = (Response)theEObject;
				T result = caseResponse(response);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.CALLBACK: {
				Callback callback = (Callback)theEObject;
				T result = caseCallback(callback);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.EXAMPLE: {
				Example example = (Example)theEObject;
				T result = caseExample(example);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.LINK: {
				Link link = (Link)theEObject;
				T result = caseLink(link);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.HEADER: {
				Header header = (Header)theEObject;
				T result = caseHeader(header);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.TAG: {
				Tag tag = (Tag)theEObject;
				T result = caseTag(tag);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.SECURITY_SCHEME: {
				SecurityScheme securityScheme = (SecurityScheme)theEObject;
				T result = caseSecurityScheme(securityScheme);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.OAUTH_FLOWS: {
				OAuthFlows oAuthFlows = (OAuthFlows)theEObject;
				T result = caseOAuthFlows(oAuthFlows);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.OAUTH_FLOW: {
				OAuthFlow oAuthFlow = (OAuthFlow)theEObject;
				T result = caseOAuthFlow(oAuthFlow);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.SECURITY_REQUIREMENT: {
				SecurityRequirement securityRequirement = (SecurityRequirement)theEObject;
				T result = caseSecurityRequirement(securityRequirement);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.SCHEMA_REFERENCE: {
				SchemaReference schemaReference = (SchemaReference)theEObject;
				T result = caseSchemaReference(schemaReference);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.PATH_ITEM_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, PathItem> pathItemEntry = (Map.Entry<String, PathItem>)theEObject;
				T result = casePathItemEntry(pathItemEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.OPERATION_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Operation> operationEntry = (Map.Entry<String, Operation>)theEObject;
				T result = caseOperationEntry(operationEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.RESPONSE_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Response> responseEntry = (Map.Entry<String, Response>)theEObject;
				T result = caseResponseEntry(responseEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.PARAMETER_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Parameter> parameterEntry = (Map.Entry<String, Parameter>)theEObject;
				T result = caseParameterEntry(parameterEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.EXAMPLE_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Example> exampleEntry = (Map.Entry<String, Example>)theEObject;
				T result = caseExampleEntry(exampleEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.REQUEST_BODY_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, RequestBody> requestBodyEntry = (Map.Entry<String, RequestBody>)theEObject;
				T result = caseRequestBodyEntry(requestBodyEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.HEADER_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Header> headerEntry = (Map.Entry<String, Header>)theEObject;
				T result = caseHeaderEntry(headerEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.SECURITY_SCHEME_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, SecurityScheme> securitySchemeEntry = (Map.Entry<String, SecurityScheme>)theEObject;
				T result = caseSecuritySchemeEntry(securitySchemeEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.LINK_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Link> linkEntry = (Map.Entry<String, Link>)theEObject;
				T result = caseLinkEntry(linkEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.CALLBACK_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Callback> callbackEntry = (Map.Entry<String, Callback>)theEObject;
				T result = caseCallbackEntry(callbackEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.MEDIA_TYPE_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, MediaType> mediaTypeEntry = (Map.Entry<String, MediaType>)theEObject;
				T result = caseMediaTypeEntry(mediaTypeEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.ENCODING_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Encoding> encodingEntry = (Map.Entry<String, Encoding>)theEObject;
				T result = caseEncodingEntry(encodingEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.SERVER_VARIABLE_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, ServerVariable> serverVariableEntry = (Map.Entry<String, ServerVariable>)theEObject;
				T result = caseServerVariableEntry(serverVariableEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.STRING_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, String> stringEntry = (Map.Entry<String, String>)theEObject;
				T result = caseStringEntry(stringEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.ANY_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Object> anyEntry = (Map.Entry<String, Object>)theEObject;
				T result = caseAnyEntry(anyEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.EXTENSION_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, Object> extensionEntry = (Map.Entry<String, Object>)theEObject;
				T result = caseExtensionEntry(extensionEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case OasPackage.SECURITY_REQUIREMENT_ENTRY: {
				@SuppressWarnings("unchecked") Map.Entry<String, EList<String>> securityRequirementEntry = (Map.Entry<String, EList<String>>)theEObject;
				T result = caseSecurityRequirementEntry(securityRequirementEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Open API</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Open API</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOpenAPI(OpenAPI object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Info</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Info</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseInfo(Info object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Contact</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Contact</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseContact(Contact object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>License</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>License</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLicense(License object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Server</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Server</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseServer(Server object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Server Variable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Server Variable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseServerVariable(ServerVariable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Components</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Components</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseComponents(Components object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Path Item</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Path Item</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePathItem(PathItem object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Operation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Operation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOperation(Operation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>External Documentation</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>External Documentation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExternalDocumentation(ExternalDocumentation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameter(Parameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Request Body</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Request Body</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRequestBody(RequestBody object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Media Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Media Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMediaType(MediaType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Encoding</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Encoding</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEncoding(Encoding object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Response</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Response</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResponse(Response object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Callback</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Callback</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCallback(Callback object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Example</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Example</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExample(Example object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Link</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Link</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLink(Link object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Header</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Header</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseHeader(Header object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Tag</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Tag</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTag(Tag object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Security Scheme</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Security Scheme</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSecurityScheme(SecurityScheme object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>OAuth Flows</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>OAuth Flows</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOAuthFlows(OAuthFlows object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>OAuth Flow</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>OAuth Flow</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOAuthFlow(OAuthFlow object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Security Requirement</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Security Requirement</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSecurityRequirement(SecurityRequirement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Schema Reference</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Schema Reference</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSchemaReference(SchemaReference object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Path Item Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Path Item Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePathItemEntry(Map.Entry<String, PathItem> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Operation Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Operation Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOperationEntry(Map.Entry<String, Operation> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Response Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Response Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResponseEntry(Map.Entry<String, Response> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterEntry(Map.Entry<String, Parameter> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Example Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Example Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExampleEntry(Map.Entry<String, Example> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Request Body Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Request Body Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRequestBodyEntry(Map.Entry<String, RequestBody> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Header Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Header Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseHeaderEntry(Map.Entry<String, Header> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Security Scheme Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Security Scheme Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSecuritySchemeEntry(Map.Entry<String, SecurityScheme> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Link Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Link Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLinkEntry(Map.Entry<String, Link> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Callback Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Callback Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCallbackEntry(Map.Entry<String, Callback> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Media Type Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Media Type Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMediaTypeEntry(Map.Entry<String, MediaType> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Encoding Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Encoding Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEncodingEntry(Map.Entry<String, Encoding> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Server Variable Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Server Variable Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseServerVariableEntry(Map.Entry<String, ServerVariable> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringEntry(Map.Entry<String, String> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Any Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Any Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAnyEntry(Map.Entry<String, Object> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Extension Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Extension Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExtensionEntry(Map.Entry<String, Object> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Security Requirement Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Security Requirement Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSecurityRequirementEntry(Map.Entry<String, EList<String>> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //OasSwitch
