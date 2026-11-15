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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.SerializableString;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.sym.PropertyNameMatcher;
import tools.jackson.core.util.JsonParserDelegate;
import tools.jackson.databind.util.TokenBuffer;

/**
 * Resolves YAML anchors and aliases (issue #297).
 * <p>
 * Jackson's YAML parser streams events and does not compose nodes: an alias {@code *a}
 * arrives as the string {@code "a"}, which silently corrupts the data. This parser records every
 * anchored node ({@code &a} on an object, a sequence or a scalar) while passing its tokens through,
 * and replays the recording where an alias names it. The replay runs inside the parsing context of
 * the alias, so property names and positions stay right; aliases inside an anchored node and nested
 * anchors are resolved as well.
 * </p>
 * <p>
 * Every replayed token counts against a budget ({@link YamlFormatProvider#OPTION_MAX_ALIAS_TOKENS}),
 * the guard against "billion laughs": nested aliases multiply, the input size does not show it.
 * An alias without an anchor before it is an error.
 * </p>
 * <p>
 * Merge keys ({@code <<: *a}, YAML 1.1) are not resolved; {@code <<} reads as an ordinary key.
 * </p>
 */
class YamlAliasResolvingParser extends JsonParserDelegate {

	private final AnchorAwareYamlParser source;
	private final long budget;
	private final Map<String, TokenBuffer> anchors = new HashMap<>();
	private final List<Recording> recordings = new ArrayList<>();
	private JsonParser replay;
	private long replayed;

	YamlAliasResolvingParser(AnchorAwareYamlParser source, long budget) {
		super(source);
		this.source = source;
		this.budget = budget;
	}

	@Override
	public JsonToken nextToken() {
		if (replay != null) {
			JsonToken token = replay.nextToken();
			if (token != null) {
				countReplayed();
				record(replay, token);
				return token;
			}
			endReplay();
		}
		JsonToken token = source.nextToken();
		if (token == null) {
			return null;
		}
		if (source.isCurrentAlias()) {
			return startReplay(source.getString());
		}
		String anchor = source.currentAnchor();
		if (anchor != null) {
			recordings.add(new Recording(anchor, TokenBuffer.forBuffering(source, ObjectReadContext.empty())));
		}
		record(source, token);
		return token;
	}

	@Override
	public JsonToken nextValue() {
		JsonToken token = nextToken();
		return token == JsonToken.PROPERTY_NAME ? nextToken() : token;
	}

	@Override
	public String nextName() {
		return nextToken() == JsonToken.PROPERTY_NAME ? currentName() : null;
	}

	@Override
	public boolean nextName(SerializableString str) {
		return nextToken() == JsonToken.PROPERTY_NAME && str.getValue().equals(currentName());
	}

	@Override
	public int nextNameMatch(PropertyNameMatcher matcher) {
		JsonToken token = nextToken();
		if (token == JsonToken.PROPERTY_NAME) {
			return matcher.matchName(currentName());
		}
		return token == JsonToken.END_OBJECT ? PropertyNameMatcher.MATCH_END_OBJECT : PropertyNameMatcher.MATCH_ODD_TOKEN;
	}

	/** Skips through {@link #nextToken()}, so that skipped anchors are recorded as well. */
	@Override
	public JsonParser skipChildren() {
		JsonToken current = currentToken();
		if (current == null || !current.isStructStart()) {
			return this;
		}
		int open = 1;
		while (open > 0) {
			JsonToken token = nextToken();
			if (token == null) {
				break;
			}
			if (token.isStructStart()) {
				open++;
			} else if (token.isStructEnd()) {
				open--;
			}
		}
		return this;
	}

	@Override
	public void close() {
		endReplay();
		super.close();
	}

	private JsonToken startReplay(String anchor) {
		TokenBuffer recorded = anchors.get(anchor);
		if (recorded == null) {
			throw new StreamReadException(source, "YAML alias '*" + anchor + "' refers to no anchor before it");
		}
		replay = recorded.asParser(ObjectReadContext.empty(), source);
		delegate = replay;
		return nextToken();
	}

	private void endReplay() {
		if (replay != null) {
			replay.close();
			replay = null;
			delegate = source;
		}
	}

	private void countReplayed() {
		if (++replayed > budget) {
			throw new StreamReadException(source, "YAML aliases expand to more than " + budget
					+ " tokens; raise " + YamlFormatProvider.OPTION_MAX_ALIAS_TOKENS + " if the document is trusted");
		}
	}

	private void record(JsonParser from, JsonToken token) {
		Iterator<Recording> it = recordings.iterator();
		while (it.hasNext()) {
			Recording recording = it.next();
			recording.buffer.copyCurrentEvent(from);
			if (token.isStructStart()) {
				recording.depth++;
			} else if (token.isStructEnd()) {
				recording.depth--;
			}
			if (recording.depth == 0) {
				anchors.put(recording.anchor, recording.buffer);
				it.remove();
			}
		}
	}

	private static final class Recording {
		private final String anchor;
		private final TokenBuffer buffer;
		private int depth;

		Recording(String anchor, TokenBuffer buffer) {
			this.anchor = anchor;
			this.buffer = buffer;
		}
	}
}
