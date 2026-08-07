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
package org.eclipse.fennec.codec.rest.common.internal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link XMLURIHandler}.
 */
@DisplayName("XMLURIHandler")
class XMLURIHandlerTest {

	@Nested
	@DisplayName("deresolve")
	class Deresolve {

		@Test
		@DisplayName("no base at all does not fail (issue #83)")
		void deresolveWithoutBase() {
			XMLURIHandler handler = new XMLURIHandler();

			assertEquals(URI.createURI("other.xmi"),
					handler.deresolve(URI.createURI("other.xmi")));
		}

		@Test
		@DisplayName("a platform URI stays as it is")
		void deresolvePlatformUri() {
			XMLURIHandler handler = new XMLURIHandler(URI.createURI("temp/id"));
			URI uri = URI.createPlatformPluginURI("bundle/model/x.ecore", false);

			assertEquals(uri, handler.deresolve(uri));
		}
	}

	@Nested
	@DisplayName("resolve")
	class Resolve {

		@Test
		@DisplayName("returns single-segment relative .ecore URI untouched (issue #83)")
		void resolvesSingleSegmentEcoreUri() {
			XMLURIHandler handler = new XMLURIHandler();
			URI uri = URI.createURI("sensinact-mapping.ecore");
			URI resolved = assertDoesNotThrow(() -> handler.resolve(uri));
			assertEquals(uri, resolved);
		}

		@Test
		@DisplayName("returns two-segment relative .ecore URI untouched (issue #83)")
		void resolvesTwoSegmentEcoreUri() {
			XMLURIHandler handler = new XMLURIHandler();
			URI uri = URI.createURI("mapping/sensinact-mapping.ecore");
			URI resolved = assertDoesNotThrow(() -> handler.resolve(uri));
			assertEquals(uri, resolved);
		}

		@Test
		@DisplayName("maps three-segment relative .ecore URI to a platform plugin URI")
		void resolvesThreeSegmentEcoreUri() {
			XMLURIHandler handler = new XMLURIHandler();
			URI uri = URI.createURI("org.example.model/model/example.ecore");
			URI resolved = handler.resolve(uri);
			assertEquals(URI.createPlatformPluginURI("org.example.model/model/example.ecore", false), resolved);
		}

		@Test
		@DisplayName("returns URIs with an absolute path untouched")
		void resolvesAbsolutePathUri() {
			XMLURIHandler handler = new XMLURIHandler();
			URI uri = URI.createURI("/absolute/example.ecore");
			assertEquals(uri, handler.resolve(uri));
		}

		@Test
		@DisplayName("a relative base leaves the URI untouched instead of throwing (issue #83)")
		void resolvesAgainstRelativeBaseWithoutThrowing() {
			// The reader creates its resource with the relative URI "temp/id", and
			// URI.resolve against a relative base throws - the same crash as the .ecore
			// branch, one branch over. Nothing can be resolved here, so nothing is.
			XMLURIHandler handler = new XMLURIHandler(URI.createURI("temp/id"));
			URI uri = URI.createURI("other.xmi");

			assertEquals(uri, handler.resolve(uri));
		}

		@Test
		@DisplayName("no base at all leaves the URI untouched (issue #83)")
		void resolvesWithoutBaseWithoutThrowing() {
			XMLURIHandler handler = new XMLURIHandler();
			URI uri = URI.createURI("other.xmi");

			assertEquals(uri, handler.resolve(uri));
		}

		@Test
		@DisplayName("a fragment-only reference stays as it is")
		void resolvesFragmentOnlyReference() {
			XMLURIHandler handler = new XMLURIHandler(URI.createURI("temp/id"));
			URI uri = URI.createURI("#//Foo");

			assertEquals(uri, handler.resolve(uri));
		}

		@Test
		@DisplayName("resolves non-ecore relative URIs against the resource URI")
		void resolvesNonEcoreUriAgainstResourceUri() {
			URI resourceURI = URI.createURI("https://example.org/base/resource.xmi");
			XMLURIHandler handler = new XMLURIHandler(resourceURI);
			URI resolved = handler.resolve(URI.createURI("other.xmi"));
			assertEquals(URI.createURI("https://example.org/base/other.xmi"), resolved);
		}
	}
}
