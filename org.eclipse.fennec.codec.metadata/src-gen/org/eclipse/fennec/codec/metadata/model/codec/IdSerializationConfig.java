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

import org.eclipse.emf.common.util.EList;

import org.eclipse.fennec.model.metadata.BaseIdConfig;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Id Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Concrete configuration for ID serialization. Extends BaseIdConfig with codec-specific settings.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdFeatures <em>Id Features</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdValueWriterName <em>Id Value Writer Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdValueReaderName <em>Id Value Reader Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getStrategyScope <em>Strategy Scope</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getFormatScope <em>Format Scope</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getIdSerializationConfig()
 * @model
 * @generated
 */
@ProviderType
public interface IdSerializationConfig extends BaseIdConfig {
	/**
	 * Returns the value of the '<em><b>Id Features</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Feature names for combined ID (when strategy is COMBINED).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Features</em>' attribute list.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getIdSerializationConfig_IdFeatures()
	 * @model
	 * @generated
	 */
	EList<String> getIdFeatures();

	/**
	 * Returns the value of the '<em><b>Id Value Writer Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Custom value writer for ID serialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Value Writer Name</em>' attribute.
	 * @see #setIdValueWriterName(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getIdSerializationConfig_IdValueWriterName()
	 * @model
	 * @generated
	 */
	String getIdValueWriterName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdValueWriterName <em>Id Value Writer Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Value Writer Name</em>' attribute.
	 * @see #getIdValueWriterName()
	 * @generated
	 */
	void setIdValueWriterName(String value);

	/**
	 * Returns the value of the '<em><b>Id Value Reader Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Custom value reader for ID deserialization.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Id Value Reader Name</em>' attribute.
	 * @see #setIdValueReaderName(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getIdSerializationConfig_IdValueReaderName()
	 * @model
	 * @generated
	 */
	String getIdValueReaderName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getIdValueReaderName <em>Id Value Reader Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id Value Reader Name</em>' attribute.
	 * @see #getIdValueReaderName()
	 * @generated
	 */
	void setIdValueReaderName(String value);

	/**
	 * Returns the value of the '<em><b>Strategy Scope</b></em>' attribute.
	 * The default value is <code>"ALL"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.StrategyScope}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Where the ID strategy applies in the object graph.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strategy Scope</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @see #setStrategyScope(StrategyScope)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getIdSerializationConfig_StrategyScope()
	 * @model default="ALL"
	 * @generated
	 */
	StrategyScope getStrategyScope();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getStrategyScope <em>Strategy Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strategy Scope</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @see #getStrategyScope()
	 * @generated
	 */
	void setStrategyScope(StrategyScope value);

	/**
	 * Returns the value of the '<em><b>Format Scope</b></em>' attribute.
	 * The default value is <code>"ALL"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.StrategyScope}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Where the ID format applies in the object graph. Independent from strategyScope.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format Scope</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @see #setFormatScope(StrategyScope)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getIdSerializationConfig_FormatScope()
	 * @model default="ALL"
	 * @generated
	 */
	StrategyScope getFormatScope();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.IdSerializationConfig#getFormatScope <em>Format Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format Scope</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @see #getFormatScope()
	 * @generated
	 */
	void setFormatScope(StrategyScope value);

} // IdSerializationConfig
