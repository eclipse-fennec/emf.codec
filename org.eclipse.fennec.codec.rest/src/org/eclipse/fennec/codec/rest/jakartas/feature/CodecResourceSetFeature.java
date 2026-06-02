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
package org.eclipse.fennec.codec.rest.jakartas.feature;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.rest.jakartas.internal.CodecResourceSetSupplier;
import org.eclipse.fennec.codec.rest.jakartas.spi.ResourceSetProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.jakartars.whiteboard.JakartarsWhiteboardConstants;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsApplicationSelect;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsExtension;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsName;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.Response;

/**
 * JAX-RS {@link Feature} that wires a request-scoped Jersey binding for
 * {@link ResourceSet}, resolving the instance via the highest-ranked OSGi
 * {@link ResourceSetProvider} service per incoming request.
 *
 * <p>Resources and providers obtain the {@link ResourceSet} via
 * {@code @Context ResourceSet} injection: Jersey calls
 * {@link CodecResourceSetSupplier#get()} which delegates to the bound
 * {@link ResourceSetProvider}. The
 * {@link org.eclipse.fennec.codec.rest.jakartas.feature.CodecResourceSetCleanupFilter}
 * releases the {@link ResourceSet} after the response is written.
 *
 * <h3>Self-assembling override</h3>
 * <p>The default {@link ResourceSetProvider} ships with the codec and
 * produces a fresh {@link ResourceSet} per request via the OSGi
 * {@code ResourceSetFactory}. Downstream bundles can swap it transparently
 * by publishing a higher-ranked {@link ResourceSetProvider} service &mdash;
 * the {@code DYNAMIC}/{@code GREEDY} reference below picks it up
 * immediately.
 *
 * <h3>OSGi service binding strategy</h3>
 * <p>This component is registered as a singleton JAX-RS extension via the
 * OSGi Jakarta RS Whiteboard. The whiteboard holds on to the singleton
 * feature instance for the lifetime of the JAX-RS application, so the
 * {@link ResourceSetProvider} reference uses:
 * <ul>
 *   <li>{@code policy = DYNAMIC} &mdash; allows rebinding without
 *       deactivation/reactivation, since the whiteboard holds on to the
 *       singleton instance.</li>
 *   <li>{@code policyOption = GREEDY} &mdash; ensures a higher-ranked
 *       service (e.g. an atlas-style scoped provider) replaces the current
 *       binding immediately.</li>
 * </ul>
 * <p>The reference is stored in an {@link AtomicReference} for thread-safe
 * access. The {@code unbind} callback uses
 * {@link AtomicReference#compareAndSet} to avoid nulling out a reference
 * that was already replaced by a higher-ranked service during a GREEDY
 * rebind.
 *
 * @author Data In Motion
 * @since 1.0
 */
@Component(service = Feature.class)
@JakartarsExtension
@JakartarsName(CodecResourceSetFeature.NAME)
@JakartarsApplicationSelect("(|(emf=true)("+ JakartarsWhiteboardConstants.JAKARTA_RS_NAME + "=.default))")
public class CodecResourceSetFeature implements Feature {

	/**
	 * Whiteboard name under which this feature is published
	 * ({@code osgi.jakartars.name}).
	 */
	public static final String NAME = "CodecResourceSetFeature";

	private final AtomicReference<ResourceSetProvider> providerRef = new AtomicReference<>();

	@Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	void bindResourceSetProvider(ResourceSetProvider provider) {
		providerRef.set(provider);
	}

	void unbindResourceSetProvider(ResourceSetProvider provider) {
		providerRef.compareAndSet(provider, null);
	}

	@Override
	public boolean configure(FeatureContext context) {
		context.register(new AbstractBinder() {
			@Override
			protected void configure() {
				// Bind this feature instance so the supplier can reach the
				// OSGi-side AtomicReference without fighting with HK2's
				// lifecycle.
				bind(CodecResourceSetFeature.this).to(CodecResourceSetFeature.class);

				bindFactory(CodecResourceSetSupplier.class)
						.to(ResourceSet.class)
						.proxy(true)
						.proxyForSameScope(false)
						.in(RequestScoped.class);
			}
		});
		return true;
	}

	/**
	 * Returns the currently bound {@link ResourceSetProvider}.
	 *
	 * @throws WebApplicationException 503 if no provider is currently
	 *         available (e.g. mid-rebind or before service activation).
	 */
	public ResourceSetProvider getResourceSetProvider() {
		ResourceSetProvider provider = providerRef.get();
		if (provider == null) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity("No ResourceSetProvider available")
							.build());
		}
		return provider;
	}
}
