/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec;

import tools.jackson.core.JsonParser;
import tools.jackson.core.io.IOContext;

/**
 * Factory for codec specific {@link JsonParser}.
 * 
 * @author grune
 * @since Apr 10, 2024
 */
public interface CodecParserFactory<R, P extends JsonParser> {
	P createParser(IOContext context, CodecReaderProvider<R> provider);
	
	P createParser(IOContext context, R input);
}
