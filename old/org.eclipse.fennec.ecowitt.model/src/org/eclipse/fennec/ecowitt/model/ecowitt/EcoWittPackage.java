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
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.ecowitt.model.ecowitt;


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;

import org.gecko.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel complianceLevel='17.0' oSGiCompatible='true' basePackage='org.eclipse.fennec.ecowitt.model' resource='XMI' copyrightText='Copyright (c) 2012 - 2025 Data In Motion and others.\nAll rights reserved. \n\nThis program and the accompanying materials are made\navailable under the terms of the Eclipse Public License 2.0\nwhich is available at https://www.eclipse.org/legal/epl-2.0/\n\nSPDX-License-Identifier: EPL-2.0\n\nContributors:\n     Mark Hoffmann - initial API and implementation' suppressGenModelAnnotations='false' contentTypeIdentifier='ecowitt#1.0'"
 * @generated
 */
@ProviderType
@EPackage(uri = EcoWittPackage.eNS_URI, genModel = "/model/ecowitt.genmodel", genModelSourceLocations = {"model/ecowitt.genmodel","org.eclipse.fennec.ecowitt.model/model/ecowitt.genmodel"}, ecore="/model/ecowitt.ecore", ecoreSourceLocations="/model/ecowitt.ecore")
public interface EcoWittPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ecowitt";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.ecowitt.com/weatherstation";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ecowitt";

	/**
	 * The package content type ID.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eCONTENT_TYPE = "ecowitt#1.0";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	EcoWittPackage eINSTANCE = org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl <em>Weather</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittPackageImpl#getEcoWittWeather()
	 * @generated
	 */
	int ECO_WITT_WEATHER = 0;

	/**
	 * The feature id for the '<em><b>Passkey</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__PASSKEY = 0;

	/**
	 * The feature id for the '<em><b>Station Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__STATION_TYPE = 1;

	/**
	 * The feature id for the '<em><b>Date UTC</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__DATE_UTC = 2;

	/**
	 * The feature id for the '<em><b>Dns IP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__DNS_IP = 3;

	/**
	 * The feature id for the '<em><b>Dns Error Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__DNS_ERROR_COUNT = 4;

	/**
	 * The feature id for the '<em><b>Cdn Flag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__CDN_FLAG = 5;

	/**
	 * The feature id for the '<em><b>Temperatur Indoor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__TEMPERATUR_INDOOR = 6;

	/**
	 * The feature id for the '<em><b>Huminity Indoor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__HUMINITY_INDOOR = 7;

	/**
	 * The feature id for the '<em><b>Barometer Rel</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__BAROMETER_REL = 8;

	/**
	 * The feature id for the '<em><b>Barometer Abs</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__BAROMETER_ABS = 9;

	/**
	 * The feature id for the '<em><b>Temperatur Outdoor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__TEMPERATUR_OUTDOOR = 10;

	/**
	 * The feature id for the '<em><b>Humidity Outdoor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__HUMIDITY_OUTDOOR = 11;

	/**
	 * The feature id for the '<em><b>Vpd</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__VPD = 12;

	/**
	 * The feature id for the '<em><b>Wind Direction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__WIND_DIRECTION = 13;

	/**
	 * The feature id for the '<em><b>Wind Dir Avg10min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__WIND_DIR_AVG10MIN = 14;

	/**
	 * The feature id for the '<em><b>Wind Speed Mph</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__WIND_SPEED_MPH = 15;

	/**
	 * The feature id for the '<em><b>Gust Daily Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__GUST_DAILY_MAX = 16;

	/**
	 * The feature id for the '<em><b>Gust Wind MPH</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__GUST_WIND_MPH = 17;

	/**
	 * The feature id for the '<em><b>Solar Radiation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__SOLAR_RADIATION = 18;

	/**
	 * The feature id for the '<em><b>Uv Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__UV_INDEX = 19;

	/**
	 * The feature id for the '<em><b>Rain Rate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RAIN_RATE = 20;

	/**
	 * The feature id for the '<em><b>Rain Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RAIN_EVENT = 21;

	/**
	 * The feature id for the '<em><b>Rain Daily</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RAIN_DAILY = 22;

	/**
	 * The feature id for the '<em><b>Rain Hourly</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RAIN_HOURLY = 23;

	/**
	 * The feature id for the '<em><b>Rain Last24 H</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RAIN_LAST24_H = 24;

	/**
	 * The feature id for the '<em><b>Rain Weekly</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RAIN_WEEKLY = 25;

	/**
	 * The feature id for the '<em><b>Rain Monthly</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RAIN_MONTHLY = 26;

	/**
	 * The feature id for the '<em><b>Rain Yearly</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RAIN_YEARLY = 27;

	/**
	 * The feature id for the '<em><b>Wh65 Battery Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__WH65_BATTERY_STATUS = 28;

	/**
	 * The feature id for the '<em><b>Frequency</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__FREQUENCY = 29;

	/**
	 * The feature id for the '<em><b>Model</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__MODEL = 30;

	/**
	 * The feature id for the '<em><b>Update Interval</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__UPDATE_INTERVAL = 31;

	/**
	 * The feature id for the '<em><b>Runtime</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__RUNTIME = 32;

	/**
	 * The feature id for the '<em><b>Heap</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER__HEAP = 33;

	/**
	 * The number of structural features of the '<em>Weather</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER_FEATURE_COUNT = 34;

	/**
	 * The number of operations of the '<em>Weather</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ECO_WITT_WEATHER_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather <em>Weather</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Weather</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather
	 * @generated
	 */
	EClass getEcoWittWeather();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getPasskey <em>Passkey</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Passkey</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getPasskey()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_Passkey();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getStationType <em>Station Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Station Type</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getStationType()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_StationType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDateUTC <em>Date UTC</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Date UTC</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDateUTC()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_DateUTC();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDnsIP <em>Dns IP</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dns IP</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDnsIP()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_DnsIP();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDnsErrorCount <em>Dns Error Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dns Error Count</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDnsErrorCount()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_DnsErrorCount();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getCdnFlag <em>Cdn Flag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cdn Flag</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getCdnFlag()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_CdnFlag();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getTemperaturIndoor <em>Temperatur Indoor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Temperatur Indoor</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getTemperaturIndoor()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_TemperaturIndoor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHuminityIndoor <em>Huminity Indoor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Huminity Indoor</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHuminityIndoor()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_HuminityIndoor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getBarometerRel <em>Barometer Rel</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Barometer Rel</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getBarometerRel()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_BarometerRel();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getBarometerAbs <em>Barometer Abs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Barometer Abs</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getBarometerAbs()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_BarometerAbs();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getTemperaturOutdoor <em>Temperatur Outdoor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Temperatur Outdoor</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getTemperaturOutdoor()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_TemperaturOutdoor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHumidityOutdoor <em>Humidity Outdoor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Humidity Outdoor</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHumidityOutdoor()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_HumidityOutdoor();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getVpd <em>Vpd</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Vpd</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getVpd()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_Vpd();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindDirection <em>Wind Direction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wind Direction</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindDirection()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_WindDirection();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindDirAvg10min <em>Wind Dir Avg10min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wind Dir Avg10min</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindDirAvg10min()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_WindDirAvg10min();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindSpeedMph <em>Wind Speed Mph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wind Speed Mph</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindSpeedMph()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_WindSpeedMph();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getGustDailyMax <em>Gust Daily Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gust Daily Max</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getGustDailyMax()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_GustDailyMax();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getGustWindMPH <em>Gust Wind MPH</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gust Wind MPH</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getGustWindMPH()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_GustWindMPH();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getSolarRadiation <em>Solar Radiation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Solar Radiation</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getSolarRadiation()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_SolarRadiation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getUvIndex <em>Uv Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uv Index</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getUvIndex()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_UvIndex();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainRate <em>Rain Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rain Rate</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainRate()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_RainRate();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainEvent <em>Rain Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rain Event</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainEvent()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_RainEvent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainDaily <em>Rain Daily</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rain Daily</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainDaily()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_RainDaily();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainHourly <em>Rain Hourly</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rain Hourly</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainHourly()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_RainHourly();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainLast24H <em>Rain Last24 H</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rain Last24 H</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainLast24H()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_RainLast24H();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainWeekly <em>Rain Weekly</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rain Weekly</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainWeekly()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_RainWeekly();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainMonthly <em>Rain Monthly</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rain Monthly</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainMonthly()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_RainMonthly();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainYearly <em>Rain Yearly</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rain Yearly</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainYearly()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_RainYearly();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWh65BatteryStatus <em>Wh65 Battery Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wh65 Battery Status</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWh65BatteryStatus()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_Wh65BatteryStatus();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getFrequency <em>Frequency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Frequency</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getFrequency()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_Frequency();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getModel <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Model</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getModel()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_Model();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getUpdateInterval <em>Update Interval</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Update Interval</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getUpdateInterval()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_UpdateInterval();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRuntime <em>Runtime</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Runtime</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRuntime()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_Runtime();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHeap <em>Heap</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Heap</em>'.
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHeap()
	 * @see #getEcoWittWeather()
	 * @generated
	 */
	EAttribute getEcoWittWeather_Heap();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	EcoWittFactory getEcoWittFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl <em>Weather</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl
		 * @see org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittPackageImpl#getEcoWittWeather()
		 * @generated
		 */
		EClass ECO_WITT_WEATHER = eINSTANCE.getEcoWittWeather();

		/**
		 * The meta object literal for the '<em><b>Passkey</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__PASSKEY = eINSTANCE.getEcoWittWeather_Passkey();

		/**
		 * The meta object literal for the '<em><b>Station Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__STATION_TYPE = eINSTANCE.getEcoWittWeather_StationType();

		/**
		 * The meta object literal for the '<em><b>Date UTC</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__DATE_UTC = eINSTANCE.getEcoWittWeather_DateUTC();

		/**
		 * The meta object literal for the '<em><b>Dns IP</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__DNS_IP = eINSTANCE.getEcoWittWeather_DnsIP();

		/**
		 * The meta object literal for the '<em><b>Dns Error Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__DNS_ERROR_COUNT = eINSTANCE.getEcoWittWeather_DnsErrorCount();

		/**
		 * The meta object literal for the '<em><b>Cdn Flag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__CDN_FLAG = eINSTANCE.getEcoWittWeather_CdnFlag();

		/**
		 * The meta object literal for the '<em><b>Temperatur Indoor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__TEMPERATUR_INDOOR = eINSTANCE.getEcoWittWeather_TemperaturIndoor();

		/**
		 * The meta object literal for the '<em><b>Huminity Indoor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__HUMINITY_INDOOR = eINSTANCE.getEcoWittWeather_HuminityIndoor();

		/**
		 * The meta object literal for the '<em><b>Barometer Rel</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__BAROMETER_REL = eINSTANCE.getEcoWittWeather_BarometerRel();

		/**
		 * The meta object literal for the '<em><b>Barometer Abs</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__BAROMETER_ABS = eINSTANCE.getEcoWittWeather_BarometerAbs();

		/**
		 * The meta object literal for the '<em><b>Temperatur Outdoor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__TEMPERATUR_OUTDOOR = eINSTANCE.getEcoWittWeather_TemperaturOutdoor();

		/**
		 * The meta object literal for the '<em><b>Humidity Outdoor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__HUMIDITY_OUTDOOR = eINSTANCE.getEcoWittWeather_HumidityOutdoor();

		/**
		 * The meta object literal for the '<em><b>Vpd</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__VPD = eINSTANCE.getEcoWittWeather_Vpd();

		/**
		 * The meta object literal for the '<em><b>Wind Direction</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__WIND_DIRECTION = eINSTANCE.getEcoWittWeather_WindDirection();

		/**
		 * The meta object literal for the '<em><b>Wind Dir Avg10min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__WIND_DIR_AVG10MIN = eINSTANCE.getEcoWittWeather_WindDirAvg10min();

		/**
		 * The meta object literal for the '<em><b>Wind Speed Mph</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__WIND_SPEED_MPH = eINSTANCE.getEcoWittWeather_WindSpeedMph();

		/**
		 * The meta object literal for the '<em><b>Gust Daily Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__GUST_DAILY_MAX = eINSTANCE.getEcoWittWeather_GustDailyMax();

		/**
		 * The meta object literal for the '<em><b>Gust Wind MPH</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__GUST_WIND_MPH = eINSTANCE.getEcoWittWeather_GustWindMPH();

		/**
		 * The meta object literal for the '<em><b>Solar Radiation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__SOLAR_RADIATION = eINSTANCE.getEcoWittWeather_SolarRadiation();

		/**
		 * The meta object literal for the '<em><b>Uv Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__UV_INDEX = eINSTANCE.getEcoWittWeather_UvIndex();

		/**
		 * The meta object literal for the '<em><b>Rain Rate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RAIN_RATE = eINSTANCE.getEcoWittWeather_RainRate();

		/**
		 * The meta object literal for the '<em><b>Rain Event</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RAIN_EVENT = eINSTANCE.getEcoWittWeather_RainEvent();

		/**
		 * The meta object literal for the '<em><b>Rain Daily</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RAIN_DAILY = eINSTANCE.getEcoWittWeather_RainDaily();

		/**
		 * The meta object literal for the '<em><b>Rain Hourly</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RAIN_HOURLY = eINSTANCE.getEcoWittWeather_RainHourly();

		/**
		 * The meta object literal for the '<em><b>Rain Last24 H</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RAIN_LAST24_H = eINSTANCE.getEcoWittWeather_RainLast24H();

		/**
		 * The meta object literal for the '<em><b>Rain Weekly</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RAIN_WEEKLY = eINSTANCE.getEcoWittWeather_RainWeekly();

		/**
		 * The meta object literal for the '<em><b>Rain Monthly</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RAIN_MONTHLY = eINSTANCE.getEcoWittWeather_RainMonthly();

		/**
		 * The meta object literal for the '<em><b>Rain Yearly</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RAIN_YEARLY = eINSTANCE.getEcoWittWeather_RainYearly();

		/**
		 * The meta object literal for the '<em><b>Wh65 Battery Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__WH65_BATTERY_STATUS = eINSTANCE.getEcoWittWeather_Wh65BatteryStatus();

		/**
		 * The meta object literal for the '<em><b>Frequency</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__FREQUENCY = eINSTANCE.getEcoWittWeather_Frequency();

		/**
		 * The meta object literal for the '<em><b>Model</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__MODEL = eINSTANCE.getEcoWittWeather_Model();

		/**
		 * The meta object literal for the '<em><b>Update Interval</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__UPDATE_INTERVAL = eINSTANCE.getEcoWittWeather_UpdateInterval();

		/**
		 * The meta object literal for the '<em><b>Runtime</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__RUNTIME = eINSTANCE.getEcoWittWeather_Runtime();

		/**
		 * The meta object literal for the '<em><b>Heap</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ECO_WITT_WEATHER__HEAP = eINSTANCE.getEcoWittWeather_Heap();

	}

} //EcoWittPackage
