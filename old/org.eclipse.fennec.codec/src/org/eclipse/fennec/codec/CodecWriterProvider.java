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

import tools.jackson.core.TreeCodec;

/**
 * Simple provider for the writer and ObjectCodec used for generating.
 * 
 * @author grune
 * @since Apr 10, 2024
 */
public class CodecWriterProvider<W> implements ObjectCodecProvider{
	
	private final W writer;
	private TreeCodec objectCodec;

	/**
	 * Creates a new instance.
	 */
	public CodecWriterProvider(W writer, TreeCodec objectCodec) {
		this.writer = writer;
		this.objectCodec = objectCodec;
	}

	public W getWriter() {
		return writer;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.ObjectCodecProvider#getObjectCodec()
	 */
	@Override
	public TreeCodec getObjectCodec() {
		return objectCodec;
	}
	
}
