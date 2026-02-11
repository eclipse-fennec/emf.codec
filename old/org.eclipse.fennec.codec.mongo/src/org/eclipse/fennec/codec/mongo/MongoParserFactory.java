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
package org.eclipse.fennec.codec.mongo;

import org.bson.AbstractBsonReader;
import org.eclipse.fennec.codec.CodecParserFactory;
import org.eclipse.fennec.codec.CodecReaderProvider;

import tools.jackson.core.io.IOContext;

/**
 * 
 * @author grune
 * @since Apr 10, 2024
 */
public class MongoParserFactory implements CodecParserFactory<AbstractBsonReader, MongoCodecParser>{

	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecParserFactory#createParser(tools.jackson.core.io.IOContext, org.eclipse.fennec.codec.CodecReaderProvider)
	 */
	@Override
	public MongoCodecParser createParser(IOContext context, CodecReaderProvider<AbstractBsonReader> provider) {
		return new MongoCodecParser(context, provider);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecParserFactory#createParser(tools.jackson.core.io.IOContext, java.lang.Object)
	 */
	@Override
	public MongoCodecParser createParser(IOContext context, AbstractBsonReader input) {
		throw new UnsupportedOperationException("MongoParser cannot be created out of an InputStream");
	}

}
