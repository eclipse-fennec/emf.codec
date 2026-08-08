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
package org.eclipse.fennec.codec.tabular;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.codec.config.ConfigurationResolver;
import org.eclipse.fennec.codec.format.FormatDelegate;
import org.eclipse.fennec.codec.tabular.model.tabular.TabularDocument;

/**
 * Generic {@link FormatDelegate} that drives the
 * {@link TabularDocumentBuilder} on {@link #close()} and hands the resulting
 * {@link TabularDocument} to a constructor-supplied
 * {@link TabularDocumentRenderer}.
 * <p>
 * All Jackson-style {@code writeXxx} methods are no-ops: tabular renderers
 * bypass the Jackson streaming pipeline and operate on the fully-built
 * document. The roots are captured up-front (typically from
 * {@code Resource.getContents()}); {@code close()} is the trigger point.
 * <p>
 * The {@code target} is never closed — stream ownership stays with the caller.
 *
 * @param <T> the format-specific output target type
 * @since 1.0
 */
public class TabularDocumentDelegate<T> implements FormatDelegate<T> {

    private T target;
    private final List<EObject> roots;
    private final Map<String, Object> options;
    private final ConfigurationResolver resolver;
    private final TabularDocumentRenderer<T> renderer;

    private boolean emitted;

    public TabularDocumentDelegate(T target, List<? extends EObject> roots,
            Map<String, Object> options, ConfigurationResolver resolver,
            TabularDocumentRenderer<T> renderer) {
        this.target = target;
        this.roots = roots == null ? List.of() : new ArrayList<>(roots);
        this.options = options != null ? options : Collections.emptyMap();
        this.resolver = resolver;
        this.renderer = renderer;
    }

    // ========================================================================
    // FormatDelegate API — Jackson writes are no-ops; emit happens on close().
    // ========================================================================

    @Override public void setTarget(T target) { this.target = target; }
    @Override public T getTarget() { return target; }

    @Override public void writeStartObject() throws IOException { /* no-op */ }
    @Override public void writeEndObject() throws IOException { /* no-op */ }
    @Override public void writeStartArray() throws IOException { /* no-op */ }
    @Override public void writeEndArray() throws IOException { /* no-op */ }
    @Override public void writeName(String name) throws IOException { /* no-op */ }
    @Override public void writeString(String value) throws IOException { /* no-op */ }
    @Override public void writeInt(int value) throws IOException { /* no-op */ }
    @Override public void writeLong(long value) throws IOException { /* no-op */ }
    @Override public void writeFloat(float value) throws IOException { /* no-op */ }
    @Override public void writeDouble(double value) throws IOException { /* no-op */ }
    @Override public void writeBigInteger(BigInteger value) throws IOException { /* no-op */ }
    @Override public void writeBigDecimal(BigDecimal value) throws IOException { /* no-op */ }
    @Override public void writeBoolean(boolean value) throws IOException { /* no-op */ }
    @Override public void writeNull() throws IOException { /* no-op */ }
    @Override public void writeBinary(byte[] data) throws IOException { /* no-op */ }

    @Override
    public void flush() throws IOException {
        // No-op; the renderer is responsible for any intermediate flushing.
    }

    @Override
    public void close() throws IOException {
        if (emitted) {
            return;
        }
        emitted = true;
        TabularDocument doc = TabularDocumentBuilder.build(roots, options, resolver);
        renderer.render(doc, target, options);
    }
}
