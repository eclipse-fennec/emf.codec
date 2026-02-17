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
package org.eclipse.fennec.codec.bson;

import org.eclipse.fennec.codec.format.CodecFormatProvider;
import org.eclipse.fennec.codec.tests.tck.AbstractIdStrategyTCK;
import org.junit.jupiter.api.DisplayName;

@DisplayName("BSON ID Strategy TCK")
class BsonIdStrategyTCKTest extends AbstractIdStrategyTCK {

    @Override
    protected CodecFormatProvider<?, ?> createFormatProvider() {
        return new BsonFormatProvider();
    }

    @Override
    protected String getFileExtension() {
        return "bson";
    }
}
