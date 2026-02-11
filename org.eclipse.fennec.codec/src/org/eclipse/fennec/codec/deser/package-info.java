/**
 * Deserialization entries for the codec-v2 implementation.
 * <p>
 * Each entry handles deserialization of a specific aspect of an EMF EObject:
 * <ul>
 *   <li>{@link TypeDeserializationEntry} - Type information</li>
 *   <li>{@link SuperTypeDeserializationEntry} - Supertype validation</li>
 *   <li>{@link IdDeserializationEntry} - ID property</li>
 *   <li>{@link AttributeDeserializationEntry} - EAttribute values</li>
 *   <li>{@link ReferenceDeserializationEntry} - EReference values</li>
 * </ul>
 * </p>
 *
 * @see <a href="docs/codec-v2-spec/">Codec V2 Specification</a>
 */
package org.eclipse.fennec.codec.deser;
