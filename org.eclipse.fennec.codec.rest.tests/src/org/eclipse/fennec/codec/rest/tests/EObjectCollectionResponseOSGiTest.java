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
package org.eclipse.fennec.codec.rest.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.jakartars.runtime.JakartarsServiceRuntime;
import org.osgi.service.jakartars.runtime.JakartarsServiceRuntimeConstants;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * A resource method returning a {@code List} of EObjects is answered by the codec's collection
 * writer through the whiteboard (issue #334).
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("Collection of EObjects as response (issue #334)")
public class EObjectCollectionResponseOSGiTest {

	private static final String XMI = "application/xmi";

	/** The resource under test; EPackages because Ecore is always available. */
	@Path("issue334")
	public static class PackagesResource {

		@GET
		@Path("two")
		@Produces(XMI)
		public List<EPackage> two() {
			return List.of(ePackage("first"), ePackage("second"));
		}

		@GET
		@Path("none")
		@Produces(XMI)
		public List<EObject> none() {
			return List.of();
		}

		private static EPackage ePackage(String name) {
			EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
			ePackage.setName(name);
			ePackage.setNsURI("http://example.org/issue334/" + name);
			return ePackage;
		}
	}

	private ServiceRegistration<Object> registration;
	private Configuration whiteboardConfiguration;
	private String endpoint;

	/**
	 * Starts the Jakarta RS whiteboard - it requires a configuration - and registers the resource.
	 */
	@BeforeEach
	void startWhiteboard(@InjectBundleContext BundleContext ctx,
			@InjectService(timeout = 5000) ConfigurationAdmin configurationAdmin,
			@InjectService(cardinality = 0) ServiceAware<JakartarsServiceRuntime> runtime) throws Exception {
		whiteboardConfiguration = configurationAdmin
				.createFactoryConfiguration("JakartarsServletWhiteboardRuntimeComponent", "?");
		Dictionary<String, Object> config = new Hashtable<>();
		config.put("jersey.context.path", "rest");
		whiteboardConfiguration.update(config);

		assertNotNull(runtime.waitForService(15_000), "the Jakarta RS whiteboard must start");
		Object endpoints = runtime.getServiceReference()
				.getProperty(JakartarsServiceRuntimeConstants.JAKARTA_RS_SERVICE_ENDPOINT);
		String first = endpoints instanceof String[] array ? array[0] : String.valueOf(endpoints);
		endpoint = first.endsWith("/") ? first : first + "/";

		Dictionary<String, Object> props = new Hashtable<>();
		props.put(JakartarsWhiteboardConstants.JAKARTA_RS_RESOURCE, Boolean.TRUE);
		props.put(JakartarsWhiteboardConstants.JAKARTA_RS_NAME, "issue334");
		registration = ctx.registerService(Object.class, new PackagesResource(), props);
	}

	@AfterEach
	void stopWhiteboard() throws Exception {
		if (registration != null) {
			registration.unregister();
		}
		if (whiteboardConfiguration != null) {
			whiteboardConfiguration.delete();
		}
	}

	@Test
	@DisplayName("a List<EPackage> is written by the codec as one document")
	void listIsWritten() throws Exception {
		HttpResponse<String> response = get(endpoint + "issue334/two");

		assertEquals(200, response.statusCode(), response.body());
		assertTrue(response.body().contains("name=\"first\"") && response.body().contains("name=\"second\""),
				response.body());
	}

	@Test
	@DisplayName("an empty list is answered by the codec, not refused")
	void emptyListIsWritten() throws Exception {
		HttpResponse<String> response = get(endpoint + "issue334/none");

		assertEquals(200, response.statusCode(), response.body());
	}

	/** GETs the URI, retrying while the whiteboard has not yet picked up the resource (404). */
	private static HttpResponse<String> get(String uri) throws Exception {
		HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(uri)).header("Accept", XMI).GET().build();
		long deadline = System.currentTimeMillis() + 10_000;
		HttpResponse<String> response;
		do {
			try {
				response = client.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException e) {
				// the HTTP runtime is still starting
				response = null;
			}
			if (response != null && response.statusCode() != 404) {
				return response;
			}
			Thread.sleep(100);
		} while (System.currentTimeMillis() < deadline);
		assertNotNull(response, "no answer from " + uri);
		return response;
	}
}
