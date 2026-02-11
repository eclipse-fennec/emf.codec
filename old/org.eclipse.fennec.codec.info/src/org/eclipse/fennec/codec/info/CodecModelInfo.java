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
package org.eclipse.fennec.codec.info;

import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.EClassCodecInfo;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.codecinfo.PackageCodecInfo;

/**
 * 
 * @author mark
 * @since 30.07.2024
 */
public interface CodecModelInfo extends EPackage.Registry {
	
	/**
	 * Retrieves the {@link PackageCodecInfo} for the {@link EPackage} with the given namespace URI, if available
	 * @param uri the namespace URI of the {@link EPackage}
	 * @return an {@link Optional} for the {@link PackageCodecInfo} for the {@link EPackage} with the given namespace URI, if available
	 */
	public Optional<PackageCodecInfo> getCodecInfoForPackage(String uri);
	
	
	/**
	 * @param ePackage
	 * @return
	 */
	Optional<PackageCodecInfo> getCodecInfoForPackage(EPackage ePackage);
	
	
	/**
	 * Retrieves the {@link EClassCodecInfo} for the given {@link EClass} 
	 * @param eClass the EClass to look for
	 * @return an {@link Optional} for the {@link EClassCodecInfo}
	 */
	public Optional<EClassCodecInfo> getCodecInfoForEClass(EClass eClass);
	
	/**
	 * Retrieves the {@link CodecInfoHolder}, if available, for a given {@link InfoType}
	 * @param infoType the {@link InfoType} for which we want to retrieve the {@link CodecInfoHolder}
	 * @return the {@link CodecInfoHolder} for the requested type
	 */
	public CodecInfoHolder getCodecInfoHolderByType(InfoType infoType);
	
	
	/**
	 * @param infoHolder
	 * @param writer
	 */
	public void addCodecValueWriterForType(InfoType infoType, CodecValueWriter<?,?> writer);
	
	/**
	 * @param infoHolder
	 * @param reader
	 */
	public void addCodecValueReaderForType(InfoType infoType, CodecValueReader<?,?> reader);

}
