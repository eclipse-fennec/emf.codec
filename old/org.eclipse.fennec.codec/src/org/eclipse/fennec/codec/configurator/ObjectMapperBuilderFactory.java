/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.codec.configurator;

import tools.jackson.databind.json.JsonMapper.Builder;

/**
 * 
 * @author ilenia
 * @since Oct 30, 2024
 */
public interface ObjectMapperBuilderFactory {
	
	Builder createObjectMapperBuilder();

}
