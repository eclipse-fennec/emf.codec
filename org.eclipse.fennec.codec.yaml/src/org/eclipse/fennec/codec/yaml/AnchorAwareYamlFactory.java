/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.codec.yaml;

import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;

import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.io.IOContext;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLFactoryBuilder;
import tools.jackson.dataformat.yaml.YAMLParser;

/**
 * A {@link YAMLFactory} whose parsers are {@link AnchorAwareYamlParser}s (issue #297), so that the
 * anchors of scalars can be resolved as well, reading through a {@link BlankLineReader} (issue #298).
 */
class AnchorAwareYamlFactory extends YAMLFactory {

	private static final long serialVersionUID = 1L;

	AnchorAwareYamlFactory() {
		super();
	}

	AnchorAwareYamlFactory(YAMLFactoryBuilder builder) {
		super(builder);
	}

	@Override
	protected YAMLParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt, InputStream in) {
		return create(readCtxt, ioCtxt, _createReader(in, null, ioCtxt));
	}

	@Override
	protected YAMLParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt, Reader r) {
		return create(readCtxt, ioCtxt, r);
	}

	@Override
	protected YAMLParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt, char[] data, int offset,
			int len, boolean recyclable) {
		return create(readCtxt, ioCtxt, new CharArrayReader(data, offset, len));
	}

	@Override
	protected YAMLParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt, byte[] data, int offset,
			int len) {
		return create(readCtxt, ioCtxt, _createReader(data, offset, len, null, ioCtxt));
	}

	private YAMLParser create(ObjectReadContext readCtxt, IOContext ioCtxt, Reader reader) {
		// blank lines emptied first: works around a snakeyaml-engine block scalar bug (issue #298)
		return new AnchorAwareYamlParser(readCtxt, ioCtxt, _getBufferRecycler(),
				readCtxt.getStreamReadFeatures(_streamReadFeatures),
				readCtxt.getFormatReadFeatures(_formatReadFeatures), _loadSettings, new BlankLineReader(reader));
	}
}
