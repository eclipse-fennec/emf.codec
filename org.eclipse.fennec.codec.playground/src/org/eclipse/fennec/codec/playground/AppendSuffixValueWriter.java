/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.playground;

import java.io.IOException;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.value.AttributeValueWriter;
import org.eclipse.fennec.codec.value.CodecValueWriter;
import org.eclipse.fennec.codec.value.CodecWriterContext;
import org.osgi.service.component.annotations.Component;

@Component(service = CodecValueWriter.class)
public class AppendSuffixValueWriter implements AttributeValueWriter<String> {

	@Override
	public String getName() {
		return "appendSuffix";
	}

	@Override
	public boolean canHandle(EAttribute attribute) {
		return EcorePackage.Literals.ESTRING.equals(attribute.getEAttributeType());
	}

	@Override
	public void write(String value, EAttribute feature, CodecWriterContext ctx) throws IOException {
		ctx.getGenerator().writeString(value + "_WRITER");
	}
}
