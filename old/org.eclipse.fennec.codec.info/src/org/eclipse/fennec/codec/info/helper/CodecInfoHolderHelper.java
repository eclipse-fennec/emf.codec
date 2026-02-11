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
package org.eclipse.fennec.codec.info.helper;

import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoFactory;
import org.eclipse.fennec.codec.info.codecinfo.CodecInfoHolder;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueReader;
import org.eclipse.fennec.codec.info.codecinfo.CodecValueWriter;
import org.eclipse.fennec.codec.info.codecinfo.InfoType;
import org.eclipse.fennec.codec.info.value.readers.EClassReaderByName;
import org.eclipse.fennec.codec.info.value.readers.EClassReaderByQualifiedName;
import org.eclipse.fennec.codec.info.value.readers.URIReader;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


/**
 * 
 * @author ilenia
 * @since Jul 31, 2024
 */
@Component(immediate = true, name = "CodecInfoHolderHelper", service = CodecInfoHolderHelper.class)
public class CodecInfoHolderHelper {
	
	private ComponentServiceObjects<ResourceSet> rsFactory;
		
	private static final Logger LOGGER = Logger.getLogger(CodecInfoHolderHelper.class.getName());
	
	@Activate
	public CodecInfoHolderHelper(@Reference ComponentServiceObjects<ResourceSet> rsFactory) {
		this.rsFactory = rsFactory;
	}

	public CodecInfoHolder createCodecInfoHolderForType(InfoType codecType) {
		CodecInfoHolder codecInfoHolder = CodecInfoFactory.eINSTANCE.createCodecInfoHolder();
		codecInfoHolder.setInfoType(codecType);

		switch(codecType) {
		case ATTRIBUTE: case REFERENCE: case OBJECT: case OPERATION: case OTHER:
			break;
		case IDENTITY:
			codecInfoHolder.getReaders().add(CodecIOHelper.DEFAULT_ID_VALUE_READER);
			codecInfoHolder.getWriters().add(CodecIOHelper.DEFAULT_ID_VALUE_WRITER);
			break;
		case SUPER_TYPE:
			codecInfoHolder.getWriters().add(CodecIOHelper.ALL_SUPERTYPE_WRITER);	
			codecInfoHolder.getWriters().add(CodecIOHelper.SINGLE_SUPERTYPE_WRITER);	
			break;
		case TYPE: 
			codecInfoHolder.getReaders().add(new URIReader(rsFactory));
			codecInfoHolder.getReaders().add(new EClassReaderByName(rsFactory));
			codecInfoHolder.getReaders().add(new EClassReaderByQualifiedName(rsFactory));

			codecInfoHolder.getWriters().add(CodecIOHelper.URI_WRITER);		
			codecInfoHolder.getWriters().add(CodecIOHelper.WRITE_BY_CLASS_NAME);		
			codecInfoHolder.getWriters().add(CodecIOHelper.WRITE_BY_INSTANCE_CLASS_NAME);		
			break;
		default:
			break;

		}
		return codecInfoHolder;
	}
	
	public void addCodecReader(CodecInfoHolder infoHolder, CodecValueReader<?,?> reader) {
		boolean alreadyInThere = false;
		for(CodecValueReader<?,?> r : infoHolder.getReaders()) {
			if(r.getName().equals(reader.getName())) {
				alreadyInThere = true;
				break;
			}
		}
		if(alreadyInThere) {
			LOGGER.warning(String.format("A CodecValueReader with name %s already exists. Change name or yours won't be added.", reader.getName()));
			return;
		}
		infoHolder.getReaders().add(reader);
	}
	
	public void addCodecWriter(CodecInfoHolder infoHolder, CodecValueWriter<?,?> writer) {
		boolean alreadyInThere = false;
		for(CodecValueWriter<?,?> w : infoHolder.getWriters()) {
			if(w.getName().equals(writer.getName())) {
				alreadyInThere = true;
				break;
			}
		}
		if(alreadyInThere) {
			LOGGER.warning(String.format("A CodecValueWriter with name %s already exists. Change name or yours won't be added.", writer.getName()));
			return;
		}
		infoHolder.getWriters().add(writer);
	}

}
