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
package org.eclipse.fennec.codec.metadata.model.codec;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reference Codec Aspect</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Codec aspect for EReference serialization configuration. Extends FeatureCodecAspect with reference-specific settings.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#getReferenceConfig <em>Reference Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#getTypeConfig <em>Type Config</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isInheritTypeFromTarget <em>Inherit Type From Target</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isExpand <em>Expand</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getReferenceCodecAspect()
 * @model
 * @generated
 */
@ProviderType
public interface ReferenceCodecAspect extends FeatureCodecAspect {
	/**
	 * Returns the value of the '<em><b>Reference Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Reference serialization configuration.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Reference Config</em>' containment reference.
	 * @see #setReferenceConfig(ReferenceSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getReferenceCodecAspect_ReferenceConfig()
	 * @model containment="true"
	 * @generated
	 */
	ReferenceSerializationConfig getReferenceConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#getReferenceConfig <em>Reference Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reference Config</em>' containment reference.
	 * @see #getReferenceConfig()
	 * @generated
	 */
	void setReferenceConfig(ReferenceSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Type Config</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Type configuration for objects accessed via this reference.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Type Config</em>' containment reference.
	 * @see #setTypeConfig(TypeSerializationConfig)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getReferenceCodecAspect_TypeConfig()
	 * @model containment="true"
	 * @generated
	 */
	TypeSerializationConfig getTypeConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#getTypeConfig <em>Type Config</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Config</em>' containment reference.
	 * @see #getTypeConfig()
	 * @generated
	 */
	void setTypeConfig(TypeSerializationConfig value);

	/**
	 * Returns the value of the '<em><b>Inherit Type From Target</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to inherit type config from target EClass when not explicitly set.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Inherit Type From Target</em>' attribute.
	 * @see #setInheritTypeFromTarget(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getReferenceCodecAspect_InheritTypeFromTarget()
	 * @model default="true"
	 * @generated
	 */
	boolean isInheritTypeFromTarget();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isInheritTypeFromTarget <em>Inherit Type From Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inherit Type From Target</em>' attribute.
	 * @see #isInheritTypeFromTarget()
	 * @generated
	 */
	void setInheritTypeFromTarget(boolean value);

	/**
	 * Returns the value of the '<em><b>Expand</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether to expand (inline) referenced objects. Unsettable so that consumers (e.g. the properties bridge, issues #106/#175) can distinguish an explicitly configured value that happens to equal this default from "not configured" via eIsSet.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expand</em>' attribute.
	 * @see #isSetExpand()
	 * @see #unsetExpand()
	 * @see #setExpand(boolean)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getReferenceCodecAspect_Expand()
	 * @model default="false" unsettable="true"
	 * @generated
	 */
	boolean isExpand();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isExpand <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expand</em>' attribute.
	 * @see #isSetExpand()
	 * @see #unsetExpand()
	 * @see #isExpand()
	 * @generated
	 */
	void setExpand(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isExpand <em>Expand</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExpand()
	 * @see #isExpand()
	 * @see #setExpand(boolean)
	 * @generated
	 */
	void unsetExpand();

	/**
	 * Returns whether the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.ReferenceCodecAspect#isExpand <em>Expand</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Expand</em>' attribute is set.
	 * @see #unsetExpand()
	 * @see #isExpand()
	 * @see #setExpand(boolean)
	 * @generated
	 */
	boolean isSetExpand();

} // ReferenceCodecAspect
