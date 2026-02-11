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

import org.bson.BsonWriter;
import org.eclipse.fennec.codec.CodecDataOutput;
import org.eclipse.fennec.codec.CodecGeneratorFactory;
import org.eclipse.fennec.codec.CodecWriterProvider;

import tools.jackson.core.io.IOContext;

/**
 * 
 * @author grune
 * @since Apr 10, 2024
 */
public class MongoGeneratorFactory implements CodecGeneratorFactory<BsonWriter, MongoCodecGenerator>{

	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGeneratorFactory#createGenerator(org.eclipse.fennec.codec.CodecWriterProvider, tools.jackson.core.io.IOContext)
	 */
	@Override
	public MongoCodecGenerator createGenerator(CodecWriterProvider<BsonWriter> provider, IOContext ioCtxt) {
//		return new MongoCodecGenerator(provider.getWriter(), provider.getObjectCodec());		
		return new MongoCodecGenerator(provider.getWriter(), ioCtxt);
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.codec.CodecGeneratorFactory#createGenerator(org.eclipse.fennec.codec.CodecDataOutput, tools.jackson.core.io.IOContext)
	 */
	@Override
	public MongoCodecGenerator createGenerator(CodecDataOutput<BsonWriter> dataOutput, IOContext ioCtxt) {
		return new MongoCodecGenerator(dataOutput.getWriter(), ioCtxt);
	}

}
