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
package org.eclipse.fennec.codec.value;

import org.eclipse.emf.ecore.EAttribute;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for {@link CodecValueRegistry} tests.
 * <p>
 * Provides common setup and helper methods for testing the new value reader/writer API.
 *
 * @see <a href="docs/codec-v2-spec/14-custom-values.md">Spec 14: Custom Value Readers/Writers</a>
 */
abstract class CodecValueRegistryTestBase {

    protected CodecValueRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new CodecValueRegistry();
    }

    /**
     * Creates a simple string writer for testing with explicit name registration.
     */
    protected CodecValueWriter<String, EAttribute> createStringWriter() {
        return new CodecValueWriter<>() {
            @Override
            public String getName() {
                return "stringWriter";
            }

            @Override
            public void write(String value, EAttribute feature, CodecWriterContext ctx) {
                // Test implementation - actual writing would use ctx.getGenerator()
            }
        };
    }

    /**
     * Creates a simple string reader for testing with explicit name registration.
     */
    protected CodecValueReader<String, EAttribute> createStringReader() {
        return new CodecValueReader<>() {
            @Override
            public String getName() {
                return "stringReader";
            }

            @Override
            public String read(CodecReaderContext ctx, EAttribute feature) {
                // Test implementation - actual reading would use ctx.getParser()
                return null;
            }
        };
    }

    /**
     * Creates a simple integer writer for testing.
     */
    protected CodecValueWriter<Integer, EAttribute> createIntWriter() {
        return new CodecValueWriter<>() {
            @Override
            public String getName() {
                return "intWriter";
            }

            @Override
            public void write(Integer value, EAttribute feature, CodecWriterContext ctx) {
                // Test implementation
            }
        };
    }

    /**
     * Creates a simple integer reader for testing.
     */
    protected CodecValueReader<Integer, EAttribute> createIntReader() {
        return new CodecValueReader<>() {
            @Override
            public String getName() {
                return "intReader";
            }

            @Override
            public Integer read(CodecReaderContext ctx, EAttribute feature) {
                // Test implementation
                return null;
            }
        };
    }

    /**
     * Creates a writer with a custom name.
     */
    protected CodecValueWriter<String, EAttribute> createWriterWithName(String name) {
        return new CodecValueWriter<>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void write(String value, EAttribute feature, CodecWriterContext ctx) {
                // Test implementation
            }
        };
    }

    /**
     * Creates a reader with a custom name.
     */
    protected CodecValueReader<String, EAttribute> createReaderWithName(String name) {
        return new CodecValueReader<>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String read(CodecReaderContext ctx, EAttribute feature) {
                // Test implementation
                return null;
            }
        };
    }
}
