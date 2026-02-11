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

/**
 * Jackson module integration for EMF codec.
 * <p>
 * This package provides the Jackson module that integrates EMF serialization
 * with the Jackson ObjectMapper. It uses the new {@link org.eclipse.fennec.codec.config.ConfigurationResolver}
 * for configuration and delegates to serializers/deserializers in the
 * {@code org.eclipse.fennec.codec.ser} and {@code org.eclipse.fennec.codec.deser} packages.
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/09-jackson-module.md">Spec 9: Jackson Module Integration</a>
 */
package org.eclipse.fennec.codec.module;
