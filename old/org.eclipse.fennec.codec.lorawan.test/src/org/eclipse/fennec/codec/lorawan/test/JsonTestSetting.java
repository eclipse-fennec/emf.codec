/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.codec.lorawan.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 
 * @author ilenia
 * @since Oct 18, 2024
 */
public abstract class JsonTestSetting {

	protected String fileName;

	public void beforeEach() throws Exception {
		fileName = UUID.randomUUID().toString().concat(".json");
	}

	public void afterEach() throws IOException {
		if(fileName != null) {
			Path path = Paths.get(fileName);
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}
	}
}
