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
package org.eclipse.fennec.codec.annotation;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.osgi.annotation.bundle.Requirement;

/**
 * Meta-annotation that generates an OSGi requirement for the Fennec JSON codec
 * resource factory ({@code org.eclipse.fennec.codec} bundle).
 * <p>
 * Place this annotation on a package (in {@code package-info.java}) or type to
 * declare that the bundle needs the Fennec JSON codec at runtime. The OSGi
 * resolver will then ensure the bundle providing the
 * {@code emf.configurator;emf.configuratorName=FennecCodecJson} capability is
 * present.
 * </p>
 *
 * <p>Example — on a package:</p>
 * <pre>
 * &#64;RequireCodecJson
 * package com.example.myapp;
 * </pre>
 *
 * <p>Example — on a type:</p>
 * <pre>
 * &#64;RequireCodecJson
 * public class MyJsonConsumer { ... }
 * </pre>
 */
@Documented
@Retention(CLASS)
@Target({ TYPE, PACKAGE })
@Requirement(
		namespace = EMFNamespaces.EMF_CONFIGURATOR_NAMESPACE,
		name = "RESOURCE_FACTORY",
		filter = "(" + EMFNamespaces.EMF_CONFIGURATOR_NAME + "=FennecCodecJson)"
)
public @interface RequireCodecJson {

}
