/*
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
 */
package org.eclipse.fennec.model.metadata.api;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Metadata Index</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Combined index interface for metadata lookups and maintenance. Extends both MetadataIndexReader (query operations) and MetadataIndexWriter (index maintenance). Implementations include MapBasedMetadataIndex (in-memory hash maps) and potentially LuceneMetadataIndex (future). Injected into MetadataWhiteboard via setMetadataIndex/unsetMetadataIndex for OSGi DS lifecycle management.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.model.metadata.api.ApiPackage#getMetadataIndex()
 * @model interface="true" abstract="true"
 * @generated
 */
@ProviderType
public interface MetadataIndex extends MetadataIndexReader, MetadataIndexWriter {
} // MetadataIndex
