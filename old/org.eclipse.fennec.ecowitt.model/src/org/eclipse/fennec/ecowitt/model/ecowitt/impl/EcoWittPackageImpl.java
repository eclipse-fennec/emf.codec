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
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.ecowitt.model.ecowitt.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittFactory;
import org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage;
import org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EcoWittPackageImpl extends EPackageImpl implements EcoWittPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass ecoWittWeatherEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private EcoWittPackageImpl() {
		super(eNS_URI, EcoWittFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link EcoWittPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static EcoWittPackage init() {
		if (isInited) return (EcoWittPackage)EPackage.Registry.INSTANCE.getEPackage(EcoWittPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredEcoWittPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		EcoWittPackageImpl theEcoWittPackage = registeredEcoWittPackage instanceof EcoWittPackageImpl ? (EcoWittPackageImpl)registeredEcoWittPackage : new EcoWittPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theEcoWittPackage.createPackageContents();

		// Initialize created meta-data
		theEcoWittPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theEcoWittPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(EcoWittPackage.eNS_URI, theEcoWittPackage);
		return theEcoWittPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEcoWittWeather() {
		return ecoWittWeatherEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_Passkey() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_StationType() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_DateUTC() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_DnsIP() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_DnsErrorCount() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_CdnFlag() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_TemperaturIndoor() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_HuminityIndoor() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_BarometerRel() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_BarometerAbs() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_TemperaturOutdoor() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_HumidityOutdoor() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_Vpd() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_WindDirection() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_WindDirAvg10min() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_WindSpeedMph() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_GustDailyMax() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_GustWindMPH() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(17);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_SolarRadiation() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(18);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_UvIndex() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(19);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_RainRate() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(20);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_RainEvent() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(21);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_RainDaily() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(22);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_RainHourly() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(23);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_RainLast24H() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(24);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_RainWeekly() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(25);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_RainMonthly() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(26);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_RainYearly() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(27);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_Wh65BatteryStatus() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(28);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_Frequency() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(29);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_Model() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(30);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_UpdateInterval() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(31);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_Runtime() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(32);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getEcoWittWeather_Heap() {
		return (EAttribute)ecoWittWeatherEClass.getEStructuralFeatures().get(33);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EcoWittFactory getEcoWittFactory() {
		return (EcoWittFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		ecoWittWeatherEClass = createEClass(ECO_WITT_WEATHER);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__PASSKEY);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__STATION_TYPE);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__DATE_UTC);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__DNS_IP);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__DNS_ERROR_COUNT);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__CDN_FLAG);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__TEMPERATUR_INDOOR);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__HUMINITY_INDOOR);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__BAROMETER_REL);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__BAROMETER_ABS);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__TEMPERATUR_OUTDOOR);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__HUMIDITY_OUTDOOR);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__VPD);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__WIND_DIRECTION);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__WIND_DIR_AVG10MIN);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__WIND_SPEED_MPH);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__GUST_DAILY_MAX);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__GUST_WIND_MPH);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__SOLAR_RADIATION);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__UV_INDEX);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RAIN_RATE);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RAIN_EVENT);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RAIN_DAILY);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RAIN_HOURLY);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RAIN_LAST24_H);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RAIN_WEEKLY);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RAIN_MONTHLY);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RAIN_YEARLY);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__WH65_BATTERY_STATUS);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__FREQUENCY);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__MODEL);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__UPDATE_INTERVAL);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__RUNTIME);
		createEAttribute(ecoWittWeatherEClass, ECO_WITT_WEATHER__HEAP);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(ecoWittWeatherEClass, EcoWittWeather.class, "EcoWittWeather", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getEcoWittWeather_Passkey(), ecorePackage.getEString(), "passkey", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_StationType(), ecorePackage.getEString(), "stationType", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_DateUTC(), ecorePackage.getEDate(), "dateUTC", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_DnsIP(), ecorePackage.getEString(), "dnsIP", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_DnsErrorCount(), ecorePackage.getEInt(), "dnsErrorCount", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_CdnFlag(), ecorePackage.getEInt(), "cdnFlag", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_TemperaturIndoor(), ecorePackage.getEDouble(), "temperaturIndoor", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_HuminityIndoor(), ecorePackage.getEInt(), "huminityIndoor", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_BarometerRel(), ecorePackage.getEDouble(), "barometerRel", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_BarometerAbs(), ecorePackage.getEDouble(), "barometerAbs", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_TemperaturOutdoor(), ecorePackage.getEDouble(), "temperaturOutdoor", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_HumidityOutdoor(), ecorePackage.getEInt(), "humidityOutdoor", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_Vpd(), ecorePackage.getEDouble(), "vpd", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_WindDirection(), ecorePackage.getEInt(), "windDirection", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_WindDirAvg10min(), ecorePackage.getEInt(), "windDirAvg10min", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_WindSpeedMph(), ecorePackage.getEDouble(), "windSpeedMph", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_GustDailyMax(), ecorePackage.getEDouble(), "gustDailyMax", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_GustWindMPH(), ecorePackage.getEDouble(), "gustWindMPH", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_SolarRadiation(), ecorePackage.getEDouble(), "solarRadiation", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_UvIndex(), ecorePackage.getEInt(), "uvIndex", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_RainRate(), ecorePackage.getEDouble(), "rainRate", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_RainEvent(), ecorePackage.getEDouble(), "rainEvent", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_RainDaily(), ecorePackage.getEDouble(), "rainDaily", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_RainHourly(), ecorePackage.getEDouble(), "rainHourly", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_RainLast24H(), ecorePackage.getEDouble(), "rainLast24H", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_RainWeekly(), ecorePackage.getEDouble(), "rainWeekly", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_RainMonthly(), ecorePackage.getEDouble(), "rainMonthly", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_RainYearly(), ecorePackage.getEDouble(), "rainYearly", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_Wh65BatteryStatus(), ecorePackage.getEInt(), "wh65BatteryStatus", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_Frequency(), ecorePackage.getEString(), "frequency", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_Model(), ecorePackage.getEString(), "model", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_UpdateInterval(), ecorePackage.getEInt(), "updateInterval", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_Runtime(), ecorePackage.getEString(), "runtime", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getEcoWittWeather_Heap(), ecorePackage.getELong(), "heap", null, 0, 1, EcoWittWeather.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/emf/2002/GenModel
		createGenModelAnnotations();
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/emf/2002/GenModel</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createGenModelAnnotations() {
		String source = "http://www.eclipse.org/emf/2002/GenModel";
		addAnnotation
		  (this,
		   source,
		   new String[] {
			   "complianceLevel", "17.0",
			   "oSGiCompatible", "true",
			   "basePackage", "org.eclipse.fennec.ecowitt.model",
			   "resource", "XMI",
			   "copyrightText", "Copyright (c) 2012 - 2025 Data In Motion and others.\nAll rights reserved. \n\nThis program and the accompanying materials are made\navailable under the terms of the Eclipse Public License 2.0\nwhich is available at https://www.eclipse.org/legal/epl-2.0/\n\nSPDX-License-Identifier: EPL-2.0\n\nContributors:\n     Mark Hoffmann - initial API and implementation",
			   "suppressGenModelAnnotations", "false",
			   "contentTypeIdentifier", "ecowitt#1.0"
		   });
		addAnnotation
		  (ecoWittWeatherEClass,
		   source,
		   new String[] {
			   "documentation", "Represents a single data payload from an Ecowitt Weather Station Gateway."
		   });
		addAnnotation
		  (getEcoWittWeather_Passkey(),
		   source,
		   new String[] {
			   "documentation", "The security key to authenticate the data feed."
		   });
		addAnnotation
		  (getEcoWittWeather_StationType(),
		   source,
		   new String[] {
			   "documentation", "The firmware/hardware version of the sending gateway."
		   });
		addAnnotation
		  (getEcoWittWeather_DateUTC(),
		   source,
		   new String[] {
			   "documentation", "The timestamp of the data reading in Coordinated Universal Time."
		   });
		addAnnotation
		  (getEcoWittWeather_TemperaturIndoor(),
		   source,
		   new String[] {
			   "documentation", "The indoor temperature reading in degrees Fahrenheit."
		   });
		addAnnotation
		  (getEcoWittWeather_HuminityIndoor(),
		   source,
		   new String[] {
			   "documentation", "The indoor humidity reading in percent."
		   });
		addAnnotation
		  (getEcoWittWeather_BarometerRel(),
		   source,
		   new String[] {
			   "documentation", "The relative (sea-level adjusted) barometric pressure in inches of Mercury (inHg)."
		   });
		addAnnotation
		  (getEcoWittWeather_BarometerAbs(),
		   source,
		   new String[] {
			   "documentation", "The absolute (station) barometric pressure in inches of Mercury (inHg)."
		   });
		addAnnotation
		  (getEcoWittWeather_TemperaturOutdoor(),
		   source,
		   new String[] {
			   "documentation", "The outdoor temperature reading in degrees Fahrenheit."
		   });
		addAnnotation
		  (getEcoWittWeather_HumidityOutdoor(),
		   source,
		   new String[] {
			   "documentation", "The outdoor humidity reading in percent."
		   });
		addAnnotation
		  (getEcoWittWeather_WindDirection(),
		   source,
		   new String[] {
			   "documentation", "The current wind direction in degrees."
		   });
		addAnnotation
		  (getEcoWittWeather_WindSpeedMph(),
		   source,
		   new String[] {
			   "documentation", "The current wind speed in miles per hour (mph)."
		   });
		addAnnotation
		  (getEcoWittWeather_GustDailyMax(),
		   source,
		   new String[] {
			   "documentation", "The maximum wind gust recorded since midnight in miles per hour (mph)."
		   });
		addAnnotation
		  (getEcoWittWeather_GustWindMPH(),
		   source,
		   new String[] {
			   "documentation", "The wind gust in miles per hour (mph)."
		   });
		addAnnotation
		  (getEcoWittWeather_SolarRadiation(),
		   source,
		   new String[] {
			   "documentation", "The intensity of solar radiation in watts per square meter (W/m^2)."
		   });
		addAnnotation
		  (getEcoWittWeather_UvIndex(),
		   source,
		   new String[] {
			   "documentation", "The current UltraViolet index."
		   });
		addAnnotation
		  (getEcoWittWeather_RainDaily(),
		   source,
		   new String[] {
			   "documentation", "The rainfall total since midnight in inches."
		   });
		addAnnotation
		  (getEcoWittWeather_RainYearly(),
		   source,
		   new String[] {
			   "documentation", "The rainfall total for the current year in inches."
		   });
		addAnnotation
		  (getEcoWittWeather_Wh65BatteryStatus(),
		   source,
		   new String[] {
			   "documentation", "Battery status for the primary outdoor sensor array (0=Normal, 1=Low)."
		   });
		addAnnotation
		  (getEcoWittWeather_Frequency(),
		   source,
		   new String[] {
			   "documentation", "The transmission frequency in MHz"
		   });
		addAnnotation
		  (getEcoWittWeather_Model(),
		   source,
		   new String[] {
			   "documentation", "The weather station model identifier"
		   });
		addAnnotation
		  (getEcoWittWeather_UpdateInterval(),
		   source,
		   new String[] {
			   "documentation", "The reporting interval at which data is sent in seconds."
		   });
		addAnnotation
		  (getEcoWittWeather_Runtime(),
		   source,
		   new String[] {
			   "documentation", "The runtime version."
		   });
		addAnnotation
		  (getEcoWittWeather_Heap(),
		   source,
		   new String[] {
			   "documentation", "The runtime version."
		   });
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";
		addAnnotation
		  (getEcoWittWeather_Passkey(),
		   source,
		   new String[] {
			   "name", "PASSKEY"
		   });
		addAnnotation
		  (getEcoWittWeather_StationType(),
		   source,
		   new String[] {
			   "name", "stationtype"
		   });
		addAnnotation
		  (getEcoWittWeather_DateUTC(),
		   source,
		   new String[] {
			   "name", "dateutc"
		   });
		addAnnotation
		  (getEcoWittWeather_DnsIP(),
		   source,
		   new String[] {
			   "name", "dns_ip"
		   });
		addAnnotation
		  (getEcoWittWeather_DnsErrorCount(),
		   source,
		   new String[] {
			   "name", "dns_err_cnt"
		   });
		addAnnotation
		  (getEcoWittWeather_CdnFlag(),
		   source,
		   new String[] {
			   "name", "cdnflg"
		   });
		addAnnotation
		  (getEcoWittWeather_TemperaturIndoor(),
		   source,
		   new String[] {
			   "name", "tempinf"
		   });
		addAnnotation
		  (getEcoWittWeather_HuminityIndoor(),
		   source,
		   new String[] {
			   "name", "humidityin"
		   });
		addAnnotation
		  (getEcoWittWeather_BarometerRel(),
		   source,
		   new String[] {
			   "name", "baromrelin"
		   });
		addAnnotation
		  (getEcoWittWeather_BarometerAbs(),
		   source,
		   new String[] {
			   "name", "baromabsin"
		   });
		addAnnotation
		  (getEcoWittWeather_TemperaturOutdoor(),
		   source,
		   new String[] {
			   "name", "tempf"
		   });
		addAnnotation
		  (getEcoWittWeather_HumidityOutdoor(),
		   source,
		   new String[] {
			   "name", "humidity"
		   });
		addAnnotation
		  (getEcoWittWeather_WindDirection(),
		   source,
		   new String[] {
			   "name", "winddir"
		   });
		addAnnotation
		  (getEcoWittWeather_WindDirAvg10min(),
		   source,
		   new String[] {
			   "name", "winddir_avg10m"
		   });
		addAnnotation
		  (getEcoWittWeather_WindSpeedMph(),
		   source,
		   new String[] {
			   "name", "windspeedmph"
		   });
		addAnnotation
		  (getEcoWittWeather_GustDailyMax(),
		   source,
		   new String[] {
			   "name", "maxdailygust"
		   });
		addAnnotation
		  (getEcoWittWeather_GustWindMPH(),
		   source,
		   new String[] {
			   "name", "windgustmph"
		   });
		addAnnotation
		  (getEcoWittWeather_SolarRadiation(),
		   source,
		   new String[] {
			   "name", "solarradiation"
		   });
		addAnnotation
		  (getEcoWittWeather_UvIndex(),
		   source,
		   new String[] {
			   "name", "uv"
		   });
		addAnnotation
		  (getEcoWittWeather_RainRate(),
		   source,
		   new String[] {
			   "name", "rainratein"
		   });
		addAnnotation
		  (getEcoWittWeather_RainEvent(),
		   source,
		   new String[] {
			   "name", "eventrainin"
		   });
		addAnnotation
		  (getEcoWittWeather_RainDaily(),
		   source,
		   new String[] {
			   "name", "dailyrainin"
		   });
		addAnnotation
		  (getEcoWittWeather_RainHourly(),
		   source,
		   new String[] {
			   "name", "hourlyrainin"
		   });
		addAnnotation
		  (getEcoWittWeather_RainLast24H(),
		   source,
		   new String[] {
			   "name", "last24hrainin"
		   });
		addAnnotation
		  (getEcoWittWeather_RainWeekly(),
		   source,
		   new String[] {
			   "name", "weeklyrainin"
		   });
		addAnnotation
		  (getEcoWittWeather_RainMonthly(),
		   source,
		   new String[] {
			   "name", "monthlyrainin"
		   });
		addAnnotation
		  (getEcoWittWeather_RainYearly(),
		   source,
		   new String[] {
			   "name", "yearlyrainin"
		   });
		addAnnotation
		  (getEcoWittWeather_Wh65BatteryStatus(),
		   source,
		   new String[] {
			   "name", "wh65batt"
		   });
		addAnnotation
		  (getEcoWittWeather_Frequency(),
		   source,
		   new String[] {
			   "name", "freq"
		   });
		addAnnotation
		  (getEcoWittWeather_UpdateInterval(),
		   source,
		   new String[] {
			   "name", "interval"
		   });
	}

} //EcoWittPackageImpl
