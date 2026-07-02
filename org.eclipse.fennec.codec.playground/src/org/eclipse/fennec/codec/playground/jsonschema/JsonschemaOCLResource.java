/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.codec.playground.jsonschema;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.fennec.codec.jsonschema.v2.annotation.RequireCodecJsonSchema;
import org.eclipse.fennec.codec.jsonschema.v2.constants.CodecJsonSchemaOptions;
import org.eclipse.fennec.codec.rest.annotations.RequireCodecMessageBodyReaderWriter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsName;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsResource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * Live test bench for JSON Schema &rarr; OCL-constrained {@link EPackage} conversion.
 * <p>
 * {@code POST /jsonschema-ocl/schema} converts a JSON Schema document into an
 * {@code EPackage} with OCL invariants attached (see {@code JsonSchemaOclConstraintGenerator}),
 * and registers it in {@link EPackage.Registry#INSTANCE} so a later, separate request can
 * validate an instance against it.
 * </p>
 * <p>
 * {@code POST /jsonschema-ocl/validate} deserializes an instance document and runs it through
 * {@link Diagnostician#validate(EObject)}, which transparently invokes the OCL delegate registered
 * for the generated invariants. No explicit type parameter is needed: the instance body is expected
 * to carry a {@code "_type": "<nsUri>#//<ClassName>"} field (the standard codec URI type strategy,
 * see {@code CodecOptions.CODEC_TYPE_STRATEGY}), which the codec resolves directly against
 * {@link EPackage.Registry#INSTANCE} via {@code TypeResolutionHelper.resolveFromUri} — the same
 * registry the {@code /schema} endpoint populates. The {@code /schema} response includes the exact
 * {@code _type} string to use for each class.
 * </p>
 *
 * @author ilenia
 * @since Jul 2, 2026
 */
@RequireCodecJsonSchema
@RequireCodecMessageBodyReaderWriter
@JakartarsResource()
@JakartarsName("JsonschemaOCLResource")
@Component(name = "JsonschemaOCLResource", service = JsonschemaOCLResource.class, scope = ServiceScope.PROTOTYPE)
@Path("/jsonschema-ocl")
public class JsonschemaOCLResource {

	/**
	 * Native delegate URI of the OCL engine running in this playground
	 * (org.eclipse.fennec.m2x.ocl.engine).
	 */
	private static final String M2X_DELEGATE_URI = "http://www.eclipse.org/fennec/m2x/ocl/1.0";

	@Reference
	private ResourceSet resourceSet;

	@POST
	@Path("/schema")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerSchema(String schemaJson) {
		Resource resource = null;
		try {
			resource = resourceSet.createResource(URI.createURI("schema.jsonschema"));
			Map<String, Object> options = Map.of(
					CodecJsonSchemaOptions.OPTION_GENERATE_OCL_CONSTRAINTS, Boolean.TRUE,
					CodecJsonSchemaOptions.OPTION_OCL_DELEGATE_URI, M2X_DELEGATE_URI);
			resource.load(new ByteArrayInputStream(schemaJson.getBytes(StandardCharsets.UTF_8)), options);

			if (resource.getContents().isEmpty() || !(resource.getContents().get(0) instanceof EPackage ePackage)) {
				return Response.status(Status.BAD_REQUEST)
						.entity("Schema did not convert to an EPackage").build();
			}
			if (ePackage.getNsURI() == null || ePackage.getNsURI().isBlank()) {
				return Response.status(Status.BAD_REQUEST)
						.entity("Schema must declare \"$id\" so the resulting EPackage can be registered").build();
			}

			EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);

			ObjectMapper mapper = JsonMapper.builder().build();
			ObjectNode responseNode = mapper.createObjectNode();
			responseNode.put("nsUri", ePackage.getNsURI());
			ObjectNode types = responseNode.putObject("types");
			for (EClassifier classifier : ePackage.getEClassifiers()) {
				if (classifier instanceof EClass) {
					types.put(classifier.getName(), ePackage.getNsURI() + "#//" + classifier.getName());
				}
			}
			return Response.ok(responseNode.toString()).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		} finally {
			if (resource != null) {
				resourceSet.getResources().remove(resource);
			}
		}
	}

	@POST
	@Path("/validate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validate(EObject instance) {
		Diagnostic diagnostic = Diagnostician.INSTANCE.validate(instance);
		ObjectMapper mapper = JsonMapper.builder().build();
		return Response.ok(toJson(mapper, diagnostic).toString()).build();
	}

	private ObjectNode toJson(ObjectMapper mapper, Diagnostic diagnostic) {
		ObjectNode node = mapper.createObjectNode();
		node.put("severity", severityName(diagnostic.getSeverity()));
		node.put("message", diagnostic.getMessage());
		node.put("source", diagnostic.getSource());
		ArrayNode children = node.putArray("children");
		for (Diagnostic child : diagnostic.getChildren()) {
			children.add(toJson(mapper, child));
		}
		return node;
	}

	private String severityName(int severity) {
		return switch (severity) {
			case Diagnostic.OK -> "OK";
			case Diagnostic.INFO -> "INFO";
			case Diagnostic.WARNING -> "WARNING";
			case Diagnostic.ERROR -> "ERROR";
			case Diagnostic.CANCEL -> "CANCEL";
			default -> "UNKNOWN";
		};
	}
}
