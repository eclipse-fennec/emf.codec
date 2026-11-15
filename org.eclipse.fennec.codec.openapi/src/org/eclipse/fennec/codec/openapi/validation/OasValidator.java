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
package org.eclipse.fennec.codec.openapi.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EValidatorRegistryImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.jsonschema.model.JsonSchemaPackage;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.Xml;
import org.eclipse.fennec.model.oas.Components;
import org.eclipse.fennec.model.oas.Encoding;
import org.eclipse.fennec.model.oas.Example;
import org.eclipse.fennec.model.oas.Header;
import org.eclipse.fennec.model.oas.MediaType;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.Parameter;
import org.eclipse.fennec.model.oas.ParameterLocation;
import org.eclipse.fennec.model.oas.ParameterStyle;
import org.eclipse.fennec.model.oas.PathItem;
import org.eclipse.fennec.model.oas.Response;
import org.eclipse.fennec.model.oas.ServerVariable;

/**
 * Checks an OpenAPI 3.0 - 3.2 description of the oas model for what the model's structure cannot
 * express (issue #305): key syntax, mutual exclusions, version-dependent rules. The structure itself
 * - required fields, types, enum values - is checked by {@link EObjectValidator}. Schema Objects are
 * not meta-validated; only the OpenAPI vocabulary in them ({@code xml}) is.
 * <p>
 * Run it with {@link #validate(OpenAPI)}: it uses a registry of its own, nothing is registered
 * globally.
 */
public class OasValidator extends EObjectValidator {

	/** The source of the diagnostics. */
	public static final String DIAGNOSTIC_SOURCE = "org.eclipse.fennec.codec.openapi";

	private static final Pattern OPENAPI = Pattern.compile("^3\\.[0-2]\\.[0-9]+(-.+)?$");
	private static final Pattern COMPONENT_KEY = Pattern.compile("^[a-zA-Z0-9._-]+$");
	private static final Pattern RESPONSE_CODE = Pattern.compile("^[1-5](?:[0-9]{2}|XX)$");
	/** An HTTP token (RFC 9110), the syntax of a header name. */
	private static final Pattern TOKEN = Pattern.compile("^[!#$%&'*+.^_`|~0-9A-Za-z-]+$");
	private static final Set<String> FIXED_METHODS = Set.of("GET", "PUT", "POST", "DELETE", "OPTIONS", "HEAD", "PATCH",
			"TRACE", "QUERY");

	private static final OasPackage OAS = OasPackage.eINSTANCE;

	/**
	 * Validates a description and everything in it, the derived Ecore aside.
	 *
	 * @param api the description
	 * @return the diagnostic, {@link Diagnostic#OK} when it is valid
	 */
	public static Diagnostic validate(OpenAPI api) {
		OasValidator validator = new OasValidator();
		EValidator.Registry registry = new EValidatorRegistryImpl();
		registry.put(OasPackage.eINSTANCE, validator);
		registry.put(JsonSchemaPackage.eINSTANCE, validator);
		Diagnostician diagnostician = new Diagnostician(registry) {
			@Override
			public boolean validate(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
				// the derived Ecore (Components.schemasPackage) is no part of the description
				return eObject instanceof EModelElement || super.validate(eClass, eObject, diagnostics, context);
			}
		};
		return diagnostician.validate(api);
	}

	@Override
	protected EPackage getEPackage() {
		return OAS;
	}

	@Override
	public boolean validate(EClass eClass, EObject object, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean valid = validate_EveryDefaultConstraint(object, diagnostics, context);
		List<String> problems = new ArrayList<>();
		check(object, problems);
		for (String problem : problems) {
			valid = false;
			if (diagnostics != null) {
				diagnostics.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						location(object) + ": " + problem, new Object[] { object }));
			}
		}
		return valid;
	}

	private void check(EObject object, List<String> problems) {
		extensions(object, problems);
		if (isReference(object)) {
			return; // a Reference Object: $ref, summary, description
		}
		String version = version(object);
		if (object instanceof OpenAPI api) {
			if (api.getOpenapi() != null && !OPENAPI.matcher(api.getOpenapi()).matches()) {
				problems.add("'openapi' is no OpenAPI 3.x version: " + api.getOpenapi());
			}
			boolean paths = api.eIsSet(OAS.getOpenAPI_Paths());
			if (version.startsWith("3.0") ? !paths
					: !paths && api.getComponents() == null && !api.eIsSet(OAS.getOpenAPI_Webhooks())) {
				problems.add(version.startsWith("3.0") ? "'paths' is required"
						: "one of 'paths', 'components' or 'webhooks' is required");
			}
			for (Map.Entry<String, PathItem> path : api.getPaths()) {
				if (!path.getKey().startsWith("/")) {
					problems.add("the path '" + path.getKey() + "' does not start with '/'");
				}
			}
		} else if (object instanceof Components components) {
			for (EStructuralFeature feature : components.eClass().getEAllStructuralFeatures()) {
				if (!feature.isTransient() && feature != OAS.getComponents_Extensions()
						&& components.eGet(feature) instanceof EMap<?, ?> map) {
					for (Object key : map.keySet()) {
						if (!COMPONENT_KEY.matcher(String.valueOf(key)).matches()) {
							problems.add("the component key '" + key + "' in '" + feature.getName()
									+ "' does not match ^[a-zA-Z0-9._-]+$");
						}
					}
				}
			}
			for (Map.Entry<String, Schema> schema : components.getSchemas()) {
				if (schema.getValue() == null) {
					problems.add("the schema '" + schema.getKey() + "' is null");
				}
			}
		} else if (object instanceof PathItem pathItem) {
			parameters(pathItem.getParameters(), problems);
			for (String method : pathItem.getAdditionalOperations().keySet()) {
				if (FIXED_METHODS.contains(method.toUpperCase(Locale.ROOT))) {
					problems.add("'additionalOperations' has '" + method + "', which is a fixed field");
				}
			}
		} else if (object instanceof Operation operation) {
			List<Parameter> all = new ArrayList<>(operation.getParameters());
			if (operation.eContainer() instanceof PathItem pathItem) {
				all.addAll(pathItem.getParameters());
			}
			parameters(all, problems);
			for (String code : operation.getResponses().keySet()) {
				if (!"default".equals(code) && !RESPONSE_CODE.matcher(code).matches()) {
					problems.add("the response code '" + code + "' is neither 'default' nor like 200 or 2XX");
				}
			}
		} else if (object instanceof Parameter parameter) {
			parameter(parameter, version, problems);
		} else if (object instanceof Header header) {
			exclusive(header, OAS.getHeader_Example(), OAS.getHeader_Examples(), problems);
			schemaOrContent(header, OAS.getHeader_Schema(), OAS.getHeader_Content(), problems);
			if (header.eIsSet(OAS.getHeader_AllowReserved())) {
				problems.add("'allowReserved' is not allowed for a header: its style does not percent-encode");
			}
			if (header.eContainer() instanceof Map.Entry<?, ?> entry && entry.getKey() instanceof String name
					&& !TOKEN.matcher(name).matches()) {
				problems.add("the header name '" + name + "' is not an HTTP token");
			}
		} else if (object instanceof MediaType mediaType) {
			exclusive(mediaType, OAS.getMediaType_Example(), OAS.getMediaType_Examples(), problems);
			exclusive(mediaType, OAS.getMediaType_Encoding(), OAS.getMediaType_PrefixEncoding(), problems);
			exclusive(mediaType, OAS.getMediaType_Encoding(), OAS.getMediaType_ItemEncoding(), problems);
			if (mediaType.eIsSet(OAS.getMediaType_Description()) && version.compareTo("3.3") < 0) {
				problems.add("'description' of a Media Type is not part of OpenAPI " + version + " (3.3)");
			}
		} else if (object instanceof Encoding encoding) {
			exclusive(encoding, OAS.getEncoding_Encoding(), OAS.getEncoding_PrefixEncoding(), problems);
			exclusive(encoding, OAS.getEncoding_Encoding(), OAS.getEncoding_ItemEncoding(), problems);
		} else if (object instanceof Example example) {
			exclusive(example, OAS.getExample_Value(), OAS.getExample_ExternalValue(), problems);
			exclusive(example, OAS.getExample_Value(), OAS.getExample_DataValue(), problems);
			exclusive(example, OAS.getExample_Value(), OAS.getExample_SerializedValue(), problems);
			exclusive(example, OAS.getExample_SerializedValue(), OAS.getExample_ExternalValue(), problems);
		} else if (object instanceof Response response) {
			if (version.compareTo("3.2") < 0 && response.getDescription() == null) {
				problems.add("'description' is required");
			}
		} else if (object instanceof ServerVariable variable) {
			if (variable.eIsSet(OAS.getServerVariable_Enum()) && variable.getEnum().isEmpty()) {
				problems.add("'enum' is empty");
			}
		} else if (object instanceof Xml xml) {
			JsonSchemaPackage js = JsonSchemaPackage.eINSTANCE;
			exclusive(xml, js.getXml_Attribute(), js.getXml_NodeType(), problems);
			exclusive(xml, js.getXml_Wrapped(), js.getXml_NodeType(), problems);
		} else if (object instanceof Schema schema && schema.getKeywords().containsKey("")) {
			problems.add("not a schema: " + schema.getKeywords().get(""));
		}
	}

	private void parameter(Parameter parameter, String version, List<String> problems) {
		if (parameter.getName() == null) {
			problems.add("'name' is required");
		}
		if (parameter.getIn() == null || parameter.getIn() == ParameterLocation.NULL) {
			problems.add("'in' is required");
		}
		exclusive(parameter, OAS.getParameter_Example(), OAS.getParameter_Examples(), problems);
		schemaOrContent(parameter, OAS.getParameter_Schema(), OAS.getParameter_Content(), problems);
		ParameterLocation in = parameter.getIn();
		String name = parameter.getName();
		if (name != null && in == ParameterLocation.HEADER && !TOKEN.matcher(name).matches()) {
			problems.add("the header name '" + name + "' is not an HTTP token");
		}
		if (name != null && in == ParameterLocation.PATH && (name.contains("{") || name.contains("}"))) {
			problems.add("the path parameter name '" + name + "' contains '{' or '}'");
		}
		if (parameter.eIsSet(OAS.getParameter_Content())) {
			for (EStructuralFeature feature : List.of(OAS.getParameter_Style(), OAS.getParameter_Explode(),
					OAS.getParameter_AllowReserved())) {
				if (parameter.eIsSet(feature)) {
					problems.add("'" + feature.getName() + "' is not allowed next to 'content'");
				}
			}
		}
		if (in == ParameterLocation.QUERYSTRING && parameter.eIsSet(OAS.getParameter_Schema())) {
			problems.add("a 'querystring' parameter has 'content', not 'schema'");
		}
		if (parameter.eIsSet(OAS.getParameter_AllowReserved())) {
			// 3.1: query only; 3.2: wherever the value is percent-encoded - query, path, a form cookie
			boolean percentEncoded = in == ParameterLocation.QUERY || (version.compareTo("3.2") >= 0
					&& (in == ParameterLocation.PATH
							|| (in == ParameterLocation.COOKIE && parameter.getStyle() != ParameterStyle.COOKIE)));
			if (!percentEncoded) {
				problems.add("'allowReserved' is only allowed where the value is percent-encoded");
			}
		}
	}

	/** At most one querystring parameter, and not together with query parameters. */
	private static void parameters(List<Parameter> parameters, List<String> problems) {
		long querystrings = parameters.stream().filter(p -> p.getIn() == ParameterLocation.QUERYSTRING).count();
		if (querystrings > 1) {
			problems.add("more than one 'querystring' parameter");
		}
		if (querystrings > 0 && parameters.stream().anyMatch(p -> p.getIn() == ParameterLocation.QUERY)) {
			problems.add("a 'querystring' parameter next to 'query' parameters");
		}
	}

	private static void schemaOrContent(EObject object, EStructuralFeature schema, EStructuralFeature content,
			List<String> problems) {
		boolean hasSchema = object.eIsSet(schema);
		boolean hasContent = object.eIsSet(content);
		if (hasSchema == hasContent) {
			problems.add("exactly one of 'schema' and 'content' is required");
		} else if (hasContent && ((EMap<?, ?>) object.eGet(content)).size() != 1) {
			problems.add("'content' has exactly one media type");
		}
	}

	private static void exclusive(EObject object, EStructuralFeature a, EStructuralFeature b, List<String> problems) {
		if (object.eIsSet(a) && object.eIsSet(b)) {
			problems.add("'" + a.getName() + "' and '" + b.getName() + "' exclude each other");
		}
	}

	/** Members the model does not know land in {@code extensions}: only {@code x-} ones are allowed. */
	private static void extensions(EObject object, List<String> problems) {
		EStructuralFeature extensions = object.eClass().getEStructuralFeature("extensions");
		if (extensions != null && object.eGet(extensions) instanceof EMap<?, ?> map) {
			for (Object key : map.keySet()) {
				if (!String.valueOf(key).startsWith("x-")) {
					problems.add("unknown field '" + key + "'");
				}
			}
		}
	}

	private static boolean isReference(EObject object) {
		EStructuralFeature ref = object.eClass().getEStructuralFeature("ref");
		return ref != null && !(object instanceof Schema) && object.eGet(ref) != null;
	}

	/** {@code major.minor} of the description an object is part of; {@code 3.2} when unknown. */
	private static String version(EObject object) {
		EObject root = EcoreUtil.getRootContainer(object);
		if (root instanceof OpenAPI api && api.getOpenapi() != null && api.getOpenapi().length() >= 3) {
			return api.getOpenapi().substring(0, 3);
		}
		return "3.2";
	}

	/** A JSON-pointer-like location: {@code /paths/~1pets/get/parameters/0}. */
	private static String location(EObject object) {
		StringBuilder path = new StringBuilder();
		for (EObject current = object; current.eContainer() != null; current = current.eContainer()) {
			EObject container = current.eContainer();
			String segment;
			if (current instanceof Map.Entry<?, ?> entry) {
				segment = current.eContainingFeature().getName() + "/" + escape(String.valueOf(entry.getKey()));
			} else if (container instanceof Map.Entry<?, ?>) {
				continue; // the value of a map entry: the entry names it
			} else if (current.eContainingFeature().isMany()) {
				segment = current.eContainingFeature().getName() + "/"
						+ ((List<?>) container.eGet(current.eContainingFeature())).indexOf(current);
			} else {
				segment = current.eContainingFeature().getName();
			}
			path.insert(0, "/" + segment);
		}
		return path.isEmpty() ? "/" : path.toString();
	}

	private static String escape(String key) {
		return key.replace("~", "~0").replace("/", "~1");
	}
}
