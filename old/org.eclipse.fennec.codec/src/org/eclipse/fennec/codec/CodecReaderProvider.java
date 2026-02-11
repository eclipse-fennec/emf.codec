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
 * Simple provider for the reader and ObjectCodec used for parsing.
 * 
 * @author grune
 * @since Apr 10, 2024
 */
public class CodecReaderProvider<R> implements ObjectCodecProvider {

	private final R reader;
	private TreeCodec codec;

	/**
	 * Creates a new instance.
	 */
	public CodecReaderProvider(R reader) {
		this.reader = reader;
	}

	/**
	 * Creates a new instance.
	 */
	public CodecReaderProvider(R reader, TreeCodec codec) {
		this.reader = reader;
		setCodec(codec);
	}

	public R getReader() {
		return reader;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.codec.ObjectCodecProvider#getObjectCodec()
	 */
	@Override
	public TreeCodec getObjectCodec() {
		return codec;
	}

	/**
	 * Sets the codec.
	 * 
	 * @param codec the codec to set
	 */
	public void setCodec(TreeCodec codec) {
		this.codec = codec;
	}

}
