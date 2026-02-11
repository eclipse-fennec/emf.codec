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

import org.eclipse.fennec.codec.jackson.module.CodecModule;

/**
 * 
 * @author ilenia
 * @since Aug 15, 2024
 */
public interface CodecModuleConfigurator {	
	
	public CodecModule.Builder getCodecModuleBuilder();
	
}
