/*
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
 * 
 */
package org.eclipse.fennec.codec.metadata.model.codec;

import org.eclipse.fennec.model.metadata.BaseTypeConfig;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type Serialization Config</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Concrete configuration for type information serialization. Extends BaseTypeConfig with codec-specific settings.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getMapId <em>Map Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getDiscriminatorPath <em>Discriminator Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getDiscriminatorValue <em>Discriminator Value</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getStrategyScope <em>Strategy Scope</em>}</li>
 *   <li>{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getFormatScope <em>Format Scope</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getTypeSerializationConfig()
 * @model
 * @generated
 */
@ProviderType
public interface TypeSerializationConfig extends BaseTypeConfig {
	/**
	 * Returns the value of the '<em><b>Map Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Identifier for type mapping registry lookup (Named Registry approach). References TypeDiscriminatorService mappings.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Map Id</em>' attribute.
	 * @see #setMapId(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getTypeSerializationConfig_MapId()
	 * @model
	 * @generated
	 */
	String getMapId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getMapId <em>Map Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Map Id</em>' attribute.
	 * @see #getMapId()
	 * @generated
	 */
	void setMapId(String value);

	/**
	 * Returns the value of the '<em><b>Discriminator Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Feature path to extract discriminator value from JSON (e.g., 'deviceInfo.profileName'). Used with mapId for Named Registry lookup via TypeDiscriminatorService.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Discriminator Path</em>' attribute.
	 * @see #setDiscriminatorPath(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getTypeSerializationConfig_DiscriminatorPath()
	 * @model
	 * @generated
	 */
	String getDiscriminatorPath();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getDiscriminatorPath <em>Discriminator Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discriminator Path</em>' attribute.
	 * @see #getDiscriminatorPath()
	 * @generated
	 */
	void setDiscriminatorPath(String value);

	/**
	 * Returns the value of the '<em><b>Discriminator Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Discriminator value that maps to this type (for discriminator-based resolution).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Discriminator Value</em>' attribute.
	 * @see #setDiscriminatorValue(String)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getTypeSerializationConfig_DiscriminatorValue()
	 * @model
	 * @generated
	 */
	String getDiscriminatorValue();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getDiscriminatorValue <em>Discriminator Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discriminator Value</em>' attribute.
	 * @see #getDiscriminatorValue()
	 * @generated
	 */
	void setDiscriminatorValue(String value);

	/**
	 * Returns the value of the '<em><b>Strategy Scope</b></em>' attribute.
	 * The default value is <code>"ALL"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.codec.metadata.model.codec.StrategyScope}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Where the type strategy applies in the object graph.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Strategy Scope</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @see #setStrategyScope(StrategyScope)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getTypeSerializationConfig_StrategyScope()
	 * @model default="ALL"
	 * @generated
	 */
	StrategyScope getStrategyScope();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getStrategyScope <em>Strategy Scope</em>}' attribute.
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
	 * Where the type format applies in the object graph. Independent from strategyScope.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Format Scope</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @see #setFormatScope(StrategyScope)
	 * @see org.eclipse.fennec.codec.metadata.model.codec.CodecPackage#getTypeSerializationConfig_FormatScope()
	 * @model default="ALL"
	 * @generated
	 */
	StrategyScope getFormatScope();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.codec.metadata.model.codec.TypeSerializationConfig#getFormatScope <em>Format Scope</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Format Scope</em>' attribute.
	 * @see org.eclipse.fennec.codec.metadata.model.codec.StrategyScope
	 * @see #getFormatScope()
	 * @generated
	 */
	void setFormatScope(StrategyScope value);

} // TypeSerializationConfig
