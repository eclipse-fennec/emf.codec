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
package org.eclipse.fennec.codec.util;

import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.codec.config.ClassConfig;
import org.eclipse.fennec.codec.context.CodecEntryContext;
import org.eclipse.fennec.codec.context.ContextHelper;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;

/**
 * Reports a value that was present in the document but could not be turned into a model
 * value (issue #131).
 * <p>
 * By default such a value is a diagnostic and the feature keeps its default — which a caller
 * cannot distinguish from the value being absent, because {@code 0} and {@code 0.0} are
 * values a model may legitimately hold, and the load reports success either way. With
 * {@code strictOnConversion} the load fails instead.
 * </p>
 * <p>
 * The decision belongs in one place: id, attribute and reference paths all drop values the
 * same way, and a caller who asks for strictness means all of them.
 * </p>
 *
 * @since 1.0
 */
public final class ConversionFailures {

    private static final Logger LOGGER = Logger.getLogger(ConversionFailures.class.getName());

    private ConversionFailures() {
        // utility
    }

    /**
     * Reports a failed conversion, escalating it when the owning class asks for strictness.
     *
     * @param entryContext the entry context carrying the effective configuration (may be null)
     * @param owner the class the value belongs to, used to resolve the strictness setting
     * @param ctxt the deserialization context receiving the diagnostic (may be null)
     * @param parser the parser, for the diagnostic's location (may be null)
     * @param source the reporting component, shown in the diagnostic
     * @param message what could not be converted
     * @throws IllegalStateException when {@code strictOnConversion} is set for the owner
     */
    public static void report(CodecEntryContext entryContext, EClass owner,
            DeserializationContext ctxt, JsonParser parser, String source, String message) {
        LOGGER.warning(message);
        if (isStrict(entryContext, owner)) {
            ContextHelper.addError(ctxt, message, parser, source);
            throw new IllegalStateException(message);
        }
        ContextHelper.addWarning(ctxt, message, parser, source);
    }

    /**
     * Tells whether failed conversions should fail the load for the given class.
     *
     * @param entryContext the entry context carrying the effective configuration (may be null)
     * @param owner the class to resolve the setting for (may be null)
     * @return true if a failed conversion has to be escalated
     */
    public static boolean isStrict(CodecEntryContext entryContext, EClass owner) {
        if (entryContext == null || entryContext.getEffectiveConfig() == null || owner == null) {
            return false;
        }
        ClassConfig classConfig = entryContext.getEffectiveConfig().resolveClassConfig(owner);
        return classConfig != null && classConfig.isStrictOnConversion();
    }
}
