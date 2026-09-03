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
package org.eclipse.fennec.codec.geojson.annotation;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.osgi.annotation.bundle.Requirement;

/**
 * Meta-annotation that generates an OSGi requirement for the Fennec GeoJSON codec
 * resource factory ({@code org.eclipse.fennec.codec.geojson} bundle).
 * <p>
 * Place this annotation on a package (in {@code package-info.java}) or type to
 * declare that the bundle needs the Fennec GeoJSON codec at runtime. The OSGi
 * resolver will then ensure the bundle providing the
 * {@code emf.configurator;emf.configuratorName=FennecCodecGeoJson} capability is
 * present - a missing codec becomes a resolve-time error instead of a per-request
 * failure once a consumer serves {@code application/geo+json} (issue #201).
 * </p>
 *
 * <p>Example - on a package:</p>
 * <pre>
 * &#64;RequireCodecGeoJson
 * package com.example.myapp;
 * </pre>
 *
 * @since 1.0
 */
@Documented
@Retention(CLASS)
@Target({ TYPE, PACKAGE })
@Requirement(
		namespace = EMFNamespaces.EMF_CONFIGURATOR_NAMESPACE,
		name = "RESOURCE_FACTORY",
		filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=FennecCodecGeoJson)"
)
public @interface RequireCodecGeoJson {
}
