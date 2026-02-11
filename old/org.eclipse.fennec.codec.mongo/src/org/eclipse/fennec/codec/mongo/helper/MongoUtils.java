/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
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
package org.eclipse.fennec.codec.mongo.helper;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

/**
 * TODO: Copy from org.gecko.emf.mongo 
 * 
 * Helper class with commonly used calls
 * @author bhunt
 */
public class MongoUtils {
	
	/**
	 * This function extracts the object ID from the given URI. The URI path must have exactly 3 segments and be of the form
	 * mongodb://host:[port]/database/collection/{id} where id is optional.
	 * 
	 * @param uri
	 * @return the object ID from the given URI or null if the id was not specified
	 * @throws IOException if the URI path is not exactly three segments
	 */
	public static Object getID(URI uri) throws IOException {
		// Require that the URI path has the form /database/collection/{id} making the id segment # 2.

		if (uri.segmentCount() != 3) {
			throw new IOException("The URI is not of the form 'mongo:/database/collection/{id}");
		}
		String id = uri.segment(2);

		// If the ID was specified in the URI, we first attempt to create a MongoDB ObjectId. If
		// that fails, we assume that the client has specified a non ObjectId and return the raw data.

		try {
			return id.isEmpty() ? null : new ObjectId(id);
		} catch (Throwable t) {
			return id;
		}
	}

	/**
	 * This function extracts the object ID from the given URI. The URI path must have exactly 3 segments and be of the form
	 * mongodb://host:[port]/database/collection/{id} where id is optional.
	 * 
	 * @param uri
	 * @return the object ID from the given URI or null if the id was not specified
	 * @throws IOException if the URI path is not exactly three segments
	 */
	public static String getIDAsString(URI uri) throws IOException {
		// Require that the URI path has the form /database/collection/{id} making the id segment # 2.
		
		if (uri.segmentCount() != 3) {
			throw new IOException("The URI is not of the form 'mongo:/database/collection/{id}");
		}
		String id = uri.segment(2);
		
		return id;
	}

	/**
	 * This function extracts the object ID from the given URI. The URI path must have exactly 3 segments and be of the form
	 * mongodb://host:[port]/database/collection/{id} where id is optional.
	 * 
	 * @param uri
	 * @return the object ID from the given URI or null if the id was not specified
	 */
	public static Object getIDWithValidURI(URI uri) {

		String id = uri.segment(2);
		
		// If the ID was specified in the URI, we first attempt to create a MongoDB ObjectId. If
		// that fails, we assume that the client has specified a non ObjectId and return the raw data.
		
		return id.isEmpty() ? new ObjectId() : (ObjectId.isValid((String) id) ? new ObjectId(id) : id);
	}

	/**
	 * This function determines whether or not the given EDataType can be represented natively by MongoDB.
	 * 
	 * @param dataType the EMF data type to check
	 * @return true if the data type can be represented natively by MongoDB; false otherwise
	 */
	public static boolean isNativeType(EDataType dataType) {
		if (dataType instanceof EEnum) {
			return true;
		}
		String instanceClassName = dataType.getInstanceClassName();
		//@formatter:off
		return
			instanceClassName == "java.lang.String"  ||
			instanceClassName == "int"               ||
			instanceClassName == "boolean"           ||
			instanceClassName == "float"             ||
			instanceClassName == "long"              ||
			instanceClassName == "double"            ||
			instanceClassName == "java.util.Date"    ||
			instanceClassName == "java.util.Calendar"||
			instanceClassName == "short"             ||
			instanceClassName == "char"             ||
			instanceClassName == "byte[]"            ||
			instanceClassName == "byte"              ||
			instanceClassName == "java.lang.Integer" ||
			instanceClassName == "java.lang.Character" ||
			instanceClassName == "java.lang.Boolean" ||
			instanceClassName == "java.lang.Long"    ||
			instanceClassName == "java.lang.Float"   ||
			instanceClassName == "java.lang.Double"  ||
			instanceClassName == "java.lang.Short"   ||
			instanceClassName == "java.lang.Byte";
		//@formatter:on
	}
	
	/**
	 * Returns the name of the given {@link EStructuralFeature}.
	 * If the useAnnotation parameter is set to <code>true</code>, the ExtendedMetadata annotations will
	 * be inspected for a name detail entry and the corresponding name of the entry returned.
	 * Otherwise the feature name will returned 
	 * @param feature the {@link EStructuralFeature} to look into
	 * @param useAnnotation set to <code>true</code>, if Extended MetaData annotation should used too
	 * @return the name of the feature or <code>null</code>
	 */
	public static String getNameByEStructuralFeature(EStructuralFeature feature, boolean useAnnotation) {
		if (feature == null) {
			return null;
		}
		String name = feature.getName();
		if (useAnnotation) {
			EAnnotation annotation = feature.getEAnnotation(ExtendedMetaData.ANNOTATION_URI);
			if (annotation != null) {
				String altName = annotation.getDetails().get("name");
				name = altName != null && !altName.isEmpty() ? altName : name;
			}
		}
		return name;
	}
	
	/**
	 * Returns the {@link EStructuralFeature} from the given {@link EClass}, using the given name.
	 * If the useAnnotation parameter is set to <code>true</code>, the ExtendedMetadata annotations will
	 * be inspected for a name detail entry and the corresponding feature will returned. 
	 * @param eclass the {@link EClass} to look into
	 * @param name the feature name
	 * @param useAnnotation set to <code>true</code>, if Extended MetaData annotation should used too
	 * @return the {@link EStructuralFeature} or <code>null</code>
	 */
	public static EStructuralFeature getEStructuralFeatureByName(EClass eclass, String name, boolean useAnnotation) {
		if (eclass == null || name == null) {
			return null;
		}
		EStructuralFeature currentFeature = eclass.getEStructuralFeature(name);
		if (currentFeature == null && useAnnotation) {
			currentFeature = eclass.getEAllStructuralFeatures().
				stream().
				map((f)->f.getEAnnotation(ExtendedMetaData.ANNOTATION_URI)).
				filter((a)-> a != null && name.equals(a.getDetails().get("name"))).
				findFirst().
				map((a)->(EStructuralFeature)a.getEModelElement()).
				orElse(null);
		}
		return currentFeature;
	}
	
	/**
	 * Returns the {@link EStructuralFeature} from the given {@link EClass}, using the given name.
	 * This does the same like {@link MongoUtils#getEStructuralFeatureByName(EClass, String, boolean)} using as third parameter <code>false</code>.
	 * @param eclass the {@link EClass} to look into
	 * @param name the feature name
	 * @return the {@link EStructuralFeature} or <code>null</code>
	 */
	public static EStructuralFeature getEStructuralFeatureByName(EClass eclass, String name) {
		return getEStructuralFeatureByName(eclass, name, false);
	}

}
