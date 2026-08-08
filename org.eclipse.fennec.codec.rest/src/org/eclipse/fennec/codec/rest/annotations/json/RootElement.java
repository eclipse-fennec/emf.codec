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
package org.eclipse.fennec.codec.rest.annotations.json;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.fennec.codec.constants.CodecOptions;

/**
 * Root element annotation for Jakarta REST endpoints.
 * <p>
 * Specifies the root type and schema hints used during deserialization
 * to resolve the correct EClass for the root JSON object.
 * </p>
 *
 * @author ilenia
 * @since 1.0
 */
@Documented
@Target({METHOD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RootElement {

	/**
	 * Type hint for the root object (EClass URI).
	 * <p>
	 * The URI of the EClass to use when deserializing the root JSON object.
	 * Example: {@code "http://example.org/model/1.0#//Person"}
	 * </p>
	 * @return the EClass URI string, or empty string if not specified
	 * @see CodecOptions#CODEC_ROOT_TYPE
	 */
	String rootType() default "";

	/**
	 * Schema context for NAME type strategy (EPackage namespace URI).
	 * <p>
	 * When using {@code TypeStrategy.NAME}, this provides the EPackage namespace
	 * URI to resolve type names against.
	 * Example: {@code "http://example.org/model/1.0"}
	 * </p>
	 * @return the EPackage namespace URI string, or empty string if not specified
	 * @see CodecOptions#CODEC_ROOT_SCHEMA
	 */
	String rootSchema() default "";
}
