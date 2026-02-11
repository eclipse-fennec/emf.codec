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
package org.eclipse.fennec.codec.csv.parser;

import java.io.InputStream;

import org.eclipse.fennec.codec.CodecParserFactory;
import org.eclipse.fennec.codec.CodecReaderProvider;

import tools.jackson.core.io.IOContext;

/**
 * 
 * @author ilenia
 * @since Sep 22, 2025
 */
public class CSVParserFactory implements CodecParserFactory<InputStream, CodecCSVParser>{

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecParserFactory#createParser(tools.jackson.core.io.IOContext, org.eclipse.fennec.codec.CodecReaderProvider)
	 */
	@Override
	public CodecCSVParser createParser(IOContext context, CodecReaderProvider<InputStream> provider) {
		return new CodecCSVParser(context, provider);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecParserFactory#createParser(tools.jackson.core.io.IOContext, java.lang.Object)
	 */
	@Override
	public CodecCSVParser createParser(IOContext context, InputStream input) {
		return new CodecCSVParser(context, input);
	}

}
