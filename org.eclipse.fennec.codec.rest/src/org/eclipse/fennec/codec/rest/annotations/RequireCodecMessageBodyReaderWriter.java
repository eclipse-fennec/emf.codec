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
package org.eclipse.fennec.codec.rest.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Requirement;

/**
 * Meta-annotation that generates an OSGi requirement for the Fennec codec
 * Jakarta REST message body reader/writer.
 * <p>
 * Place this annotation on a package (in {@code package-info.java}) or type
 * to declare that the bundle needs the Fennec codec REST handlers at runtime.
 * The OSGi resolver will then ensure the {@code org.eclipse.fennec.codec.rest}
 * bundle (which provides the capability) is present in the runtime.
 * </p>
 *
 * <p>Example usage on a package:</p>
 * <pre>
 * &#64;RequireCodecMessageBodyReaderWriter
 * package com.example.myapp.rest;
 * </pre>
 *
 * <p>Example usage on a type:</p>
 * <pre>
 * &#64;RequireCodecMessageBodyReaderWriter
 * public class MyResource { ... }
 * </pre>
 *
 * @see <a href="https://docs.osgi.org/specification/osgi.cmpn/7.0.0/service.jakartars.html">OSGi Jakarta RS Whiteboard</a>
 */
@Documented
@Retention(CLASS)
@Target({ TYPE, PACKAGE })
@Requirement(
		namespace = "fennec.codec.rest",
		name = "messagebody"
)
public @interface RequireCodecMessageBodyReaderWriter {

}
