/**
 * Jackson JSON-specific implementations for the codec.
 * <p>
 * This package contains JSON-specific classes that extend Jackson's JSON infrastructure:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.jackson.CodecJsonFactory} - Factory that creates codec-aware parsers</li>
 *   <li>{@link org.eclipse.fennec.codec.jackson.CodecJsonParser} - Parser with EMF context support</li>
 *   <li>{@link org.eclipse.fennec.codec.jackson.CodecJsonReadContext} - JSON read context with EMF state</li>
 * </ul>
 * <p>
 * For non-JSON formats (MongoDB, CSV, etc.), use the generic context classes in
 * {@link org.eclipse.fennec.codec.context} which extend {@link tools.jackson.core.TokenStreamContext}
 * directly rather than JSON-specific base classes.
 * </p>
 *
 * @see <a href="docs/codec-v2-serialization-spec.md#1811-emf-codec-context">Spec 18.11: EMF Codec Context</a>
 */
package org.eclipse.fennec.codec.jackson;
