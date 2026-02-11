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
package org.eclipse.fennec.codec.test.helper;

import org.eclipse.emf.common.util.URI;

/**
 * 
 * @author ilenia
 * @since Oct 22, 2024
 */
public abstract class CodecTestSetting {
	
	protected abstract URI getPersonURI();
	
	protected abstract URI getAddressURI();

}
