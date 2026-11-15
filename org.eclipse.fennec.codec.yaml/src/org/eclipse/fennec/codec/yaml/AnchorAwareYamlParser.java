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

import java.io.Reader;

import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.Anchor;
import org.snakeyaml.engine.v2.events.ScalarEvent;

import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.dataformat.yaml.YAMLParser;

/**
 * A {@link YAMLParser} that also tells the anchor of a scalar value (issue #297).
 * {@link YAMLParser#getObjectId()} names the anchor of an object or a sequence, but drops the one of
 * a scalar ({@code name: &n root}); the scalar event still carries it.
 */
class AnchorAwareYamlParser extends YAMLParser {

	AnchorAwareYamlParser(ObjectReadContext readCtxt, IOContext ioCtxt, BufferRecycler br,
			int streamReadFeatures, int formatFeatures, LoadSettings loadSettings, Reader reader) {
		super(readCtxt, ioCtxt, br, streamReadFeatures, formatFeatures, loadSettings, reader);
	}

	/**
	 * @return the anchor of the current token: of the object, sequence or scalar value it starts or
	 *         is, {@code null} for none or for any other token
	 */
	String currentAnchor() {
		JsonToken token = currentToken();
		if (token == null) {
			return null;
		}
		if (token.isScalarValue()) {
			return _lastEvent instanceof ScalarEvent scalar ? scalar.getAnchor().map(Anchor::getValue).orElse(null) : null;
		}
		return token.isStructStart() ? getObjectId() : null;
	}
}
