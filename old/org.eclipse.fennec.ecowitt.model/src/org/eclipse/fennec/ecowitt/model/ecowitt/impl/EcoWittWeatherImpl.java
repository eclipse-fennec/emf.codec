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
package org.eclipse.fennec.ecowitt.model.ecowitt.impl;

import java.util.Date;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage;
import org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Weather</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getPasskey <em>Passkey</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getStationType <em>Station Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getDateUTC <em>Date UTC</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getDnsIP <em>Dns IP</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getDnsErrorCount <em>Dns Error Count</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getCdnFlag <em>Cdn Flag</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getTemperaturIndoor <em>Temperatur Indoor</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getHuminityIndoor <em>Huminity Indoor</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getBarometerRel <em>Barometer Rel</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getBarometerAbs <em>Barometer Abs</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getTemperaturOutdoor <em>Temperatur Outdoor</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getHumidityOutdoor <em>Humidity Outdoor</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getVpd <em>Vpd</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getWindDirection <em>Wind Direction</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getWindDirAvg10min <em>Wind Dir Avg10min</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getWindSpeedMph <em>Wind Speed Mph</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getGustDailyMax <em>Gust Daily Max</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getGustWindMPH <em>Gust Wind MPH</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getSolarRadiation <em>Solar Radiation</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getUvIndex <em>Uv Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRainRate <em>Rain Rate</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRainEvent <em>Rain Event</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRainDaily <em>Rain Daily</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRainHourly <em>Rain Hourly</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRainLast24H <em>Rain Last24 H</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRainWeekly <em>Rain Weekly</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRainMonthly <em>Rain Monthly</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRainYearly <em>Rain Yearly</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getWh65BatteryStatus <em>Wh65 Battery Status</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getFrequency <em>Frequency</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getModel <em>Model</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getUpdateInterval <em>Update Interval</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getRuntime <em>Runtime</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.impl.EcoWittWeatherImpl#getHeap <em>Heap</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EcoWittWeatherImpl extends MinimalEObjectImpl.Container implements EcoWittWeather {
	/**
	 * The default value of the '{@link #getPasskey() <em>Passkey</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPasskey()
	 * @generated
	 * @ordered
	 */
	protected static final String PASSKEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPasskey() <em>Passkey</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPasskey()
	 * @generated
	 * @ordered
	 */
	protected String passkey = PASSKEY_EDEFAULT;

	/**
	 * The default value of the '{@link #getStationType() <em>Station Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStationType()
	 * @generated
	 * @ordered
	 */
	protected static final String STATION_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getStationType() <em>Station Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStationType()
	 * @generated
	 * @ordered
	 */
	protected String stationType = STATION_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDateUTC() <em>Date UTC</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDateUTC()
	 * @generated
	 * @ordered
	 */
	protected static final Date DATE_UTC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDateUTC() <em>Date UTC</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDateUTC()
	 * @generated
	 * @ordered
	 */
	protected Date dateUTC = DATE_UTC_EDEFAULT;

	/**
	 * The default value of the '{@link #getDnsIP() <em>Dns IP</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDnsIP()
	 * @generated
	 * @ordered
	 */
	protected static final String DNS_IP_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDnsIP() <em>Dns IP</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDnsIP()
	 * @generated
	 * @ordered
	 */
	protected String dnsIP = DNS_IP_EDEFAULT;

	/**
	 * The default value of the '{@link #getDnsErrorCount() <em>Dns Error Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDnsErrorCount()
	 * @generated
	 * @ordered
	 */
	protected static final int DNS_ERROR_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getDnsErrorCount() <em>Dns Error Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDnsErrorCount()
	 * @generated
	 * @ordered
	 */
	protected int dnsErrorCount = DNS_ERROR_COUNT_EDEFAULT;

	/**
	 * The default value of the '{@link #getCdnFlag() <em>Cdn Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCdnFlag()
	 * @generated
	 * @ordered
	 */
	protected static final int CDN_FLAG_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getCdnFlag() <em>Cdn Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCdnFlag()
	 * @generated
	 * @ordered
	 */
	protected int cdnFlag = CDN_FLAG_EDEFAULT;

	/**
	 * The default value of the '{@link #getTemperaturIndoor() <em>Temperatur Indoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemperaturIndoor()
	 * @generated
	 * @ordered
	 */
	protected static final double TEMPERATUR_INDOOR_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getTemperaturIndoor() <em>Temperatur Indoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemperaturIndoor()
	 * @generated
	 * @ordered
	 */
	protected double temperaturIndoor = TEMPERATUR_INDOOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getHuminityIndoor() <em>Huminity Indoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHuminityIndoor()
	 * @generated
	 * @ordered
	 */
	protected static final int HUMINITY_INDOOR_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getHuminityIndoor() <em>Huminity Indoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHuminityIndoor()
	 * @generated
	 * @ordered
	 */
	protected int huminityIndoor = HUMINITY_INDOOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getBarometerRel() <em>Barometer Rel</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBarometerRel()
	 * @generated
	 * @ordered
	 */
	protected static final double BAROMETER_REL_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getBarometerRel() <em>Barometer Rel</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBarometerRel()
	 * @generated
	 * @ordered
	 */
	protected double barometerRel = BAROMETER_REL_EDEFAULT;

	/**
	 * The default value of the '{@link #getBarometerAbs() <em>Barometer Abs</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBarometerAbs()
	 * @generated
	 * @ordered
	 */
	protected static final double BAROMETER_ABS_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getBarometerAbs() <em>Barometer Abs</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBarometerAbs()
	 * @generated
	 * @ordered
	 */
	protected double barometerAbs = BAROMETER_ABS_EDEFAULT;

	/**
	 * The default value of the '{@link #getTemperaturOutdoor() <em>Temperatur Outdoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemperaturOutdoor()
	 * @generated
	 * @ordered
	 */
	protected static final double TEMPERATUR_OUTDOOR_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getTemperaturOutdoor() <em>Temperatur Outdoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemperaturOutdoor()
	 * @generated
	 * @ordered
	 */
	protected double temperaturOutdoor = TEMPERATUR_OUTDOOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getHumidityOutdoor() <em>Humidity Outdoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHumidityOutdoor()
	 * @generated
	 * @ordered
	 */
	protected static final int HUMIDITY_OUTDOOR_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getHumidityOutdoor() <em>Humidity Outdoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHumidityOutdoor()
	 * @generated
	 * @ordered
	 */
	protected int humidityOutdoor = HUMIDITY_OUTDOOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getVpd() <em>Vpd</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVpd()
	 * @generated
	 * @ordered
	 */
	protected static final double VPD_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getVpd() <em>Vpd</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVpd()
	 * @generated
	 * @ordered
	 */
	protected double vpd = VPD_EDEFAULT;

	/**
	 * The default value of the '{@link #getWindDirection() <em>Wind Direction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWindDirection()
	 * @generated
	 * @ordered
	 */
	protected static final int WIND_DIRECTION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getWindDirection() <em>Wind Direction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWindDirection()
	 * @generated
	 * @ordered
	 */
	protected int windDirection = WIND_DIRECTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getWindDirAvg10min() <em>Wind Dir Avg10min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWindDirAvg10min()
	 * @generated
	 * @ordered
	 */
	protected static final int WIND_DIR_AVG10MIN_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getWindDirAvg10min() <em>Wind Dir Avg10min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWindDirAvg10min()
	 * @generated
	 * @ordered
	 */
	protected int windDirAvg10min = WIND_DIR_AVG10MIN_EDEFAULT;

	/**
	 * The default value of the '{@link #getWindSpeedMph() <em>Wind Speed Mph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWindSpeedMph()
	 * @generated
	 * @ordered
	 */
	protected static final double WIND_SPEED_MPH_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getWindSpeedMph() <em>Wind Speed Mph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWindSpeedMph()
	 * @generated
	 * @ordered
	 */
	protected double windSpeedMph = WIND_SPEED_MPH_EDEFAULT;

	/**
	 * The default value of the '{@link #getGustDailyMax() <em>Gust Daily Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGustDailyMax()
	 * @generated
	 * @ordered
	 */
	protected static final double GUST_DAILY_MAX_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getGustDailyMax() <em>Gust Daily Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGustDailyMax()
	 * @generated
	 * @ordered
	 */
	protected double gustDailyMax = GUST_DAILY_MAX_EDEFAULT;

	/**
	 * The default value of the '{@link #getGustWindMPH() <em>Gust Wind MPH</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGustWindMPH()
	 * @generated
	 * @ordered
	 */
	protected static final double GUST_WIND_MPH_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getGustWindMPH() <em>Gust Wind MPH</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGustWindMPH()
	 * @generated
	 * @ordered
	 */
	protected double gustWindMPH = GUST_WIND_MPH_EDEFAULT;

	/**
	 * The default value of the '{@link #getSolarRadiation() <em>Solar Radiation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSolarRadiation()
	 * @generated
	 * @ordered
	 */
	protected static final double SOLAR_RADIATION_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getSolarRadiation() <em>Solar Radiation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSolarRadiation()
	 * @generated
	 * @ordered
	 */
	protected double solarRadiation = SOLAR_RADIATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getUvIndex() <em>Uv Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUvIndex()
	 * @generated
	 * @ordered
	 */
	protected static final int UV_INDEX_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getUvIndex() <em>Uv Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUvIndex()
	 * @generated
	 * @ordered
	 */
	protected int uvIndex = UV_INDEX_EDEFAULT;

	/**
	 * The default value of the '{@link #getRainRate() <em>Rain Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainRate()
	 * @generated
	 * @ordered
	 */
	protected static final double RAIN_RATE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRainRate() <em>Rain Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainRate()
	 * @generated
	 * @ordered
	 */
	protected double rainRate = RAIN_RATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getRainEvent() <em>Rain Event</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainEvent()
	 * @generated
	 * @ordered
	 */
	protected static final double RAIN_EVENT_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRainEvent() <em>Rain Event</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainEvent()
	 * @generated
	 * @ordered
	 */
	protected double rainEvent = RAIN_EVENT_EDEFAULT;

	/**
	 * The default value of the '{@link #getRainDaily() <em>Rain Daily</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainDaily()
	 * @generated
	 * @ordered
	 */
	protected static final double RAIN_DAILY_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRainDaily() <em>Rain Daily</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainDaily()
	 * @generated
	 * @ordered
	 */
	protected double rainDaily = RAIN_DAILY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRainHourly() <em>Rain Hourly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainHourly()
	 * @generated
	 * @ordered
	 */
	protected static final double RAIN_HOURLY_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRainHourly() <em>Rain Hourly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainHourly()
	 * @generated
	 * @ordered
	 */
	protected double rainHourly = RAIN_HOURLY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRainLast24H() <em>Rain Last24 H</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainLast24H()
	 * @generated
	 * @ordered
	 */
	protected static final double RAIN_LAST24_H_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRainLast24H() <em>Rain Last24 H</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainLast24H()
	 * @generated
	 * @ordered
	 */
	protected double rainLast24H = RAIN_LAST24_H_EDEFAULT;

	/**
	 * The default value of the '{@link #getRainWeekly() <em>Rain Weekly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainWeekly()
	 * @generated
	 * @ordered
	 */
	protected static final double RAIN_WEEKLY_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRainWeekly() <em>Rain Weekly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainWeekly()
	 * @generated
	 * @ordered
	 */
	protected double rainWeekly = RAIN_WEEKLY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRainMonthly() <em>Rain Monthly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainMonthly()
	 * @generated
	 * @ordered
	 */
	protected static final double RAIN_MONTHLY_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRainMonthly() <em>Rain Monthly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainMonthly()
	 * @generated
	 * @ordered
	 */
	protected double rainMonthly = RAIN_MONTHLY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRainYearly() <em>Rain Yearly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainYearly()
	 * @generated
	 * @ordered
	 */
	protected static final double RAIN_YEARLY_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRainYearly() <em>Rain Yearly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRainYearly()
	 * @generated
	 * @ordered
	 */
	protected double rainYearly = RAIN_YEARLY_EDEFAULT;

	/**
	 * The default value of the '{@link #getWh65BatteryStatus() <em>Wh65 Battery Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWh65BatteryStatus()
	 * @generated
	 * @ordered
	 */
	protected static final int WH65_BATTERY_STATUS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getWh65BatteryStatus() <em>Wh65 Battery Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWh65BatteryStatus()
	 * @generated
	 * @ordered
	 */
	protected int wh65BatteryStatus = WH65_BATTERY_STATUS_EDEFAULT;

	/**
	 * The default value of the '{@link #getFrequency() <em>Frequency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrequency()
	 * @generated
	 * @ordered
	 */
	protected static final String FREQUENCY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFrequency() <em>Frequency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFrequency()
	 * @generated
	 * @ordered
	 */
	protected String frequency = FREQUENCY_EDEFAULT;

	/**
	 * The default value of the '{@link #getModel() <em>Model</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModel()
	 * @generated
	 * @ordered
	 */
	protected static final String MODEL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getModel() <em>Model</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModel()
	 * @generated
	 * @ordered
	 */
	protected String model = MODEL_EDEFAULT;

	/**
	 * The default value of the '{@link #getUpdateInterval() <em>Update Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUpdateInterval()
	 * @generated
	 * @ordered
	 */
	protected static final int UPDATE_INTERVAL_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getUpdateInterval() <em>Update Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUpdateInterval()
	 * @generated
	 * @ordered
	 */
	protected int updateInterval = UPDATE_INTERVAL_EDEFAULT;

	/**
	 * The default value of the '{@link #getRuntime() <em>Runtime</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuntime()
	 * @generated
	 * @ordered
	 */
	protected static final String RUNTIME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRuntime() <em>Runtime</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuntime()
	 * @generated
	 * @ordered
	 */
	protected String runtime = RUNTIME_EDEFAULT;

	/**
	 * The default value of the '{@link #getHeap() <em>Heap</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHeap()
	 * @generated
	 * @ordered
	 */
	protected static final long HEAP_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getHeap() <em>Heap</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHeap()
	 * @generated
	 * @ordered
	 */
	protected long heap = HEAP_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EcoWittWeatherImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EcoWittPackage.Literals.ECO_WITT_WEATHER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getPasskey() {
		return passkey;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPasskey(String newPasskey) {
		String oldPasskey = passkey;
		passkey = newPasskey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__PASSKEY, oldPasskey, passkey));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getStationType() {
		return stationType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStationType(String newStationType) {
		String oldStationType = stationType;
		stationType = newStationType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__STATION_TYPE, oldStationType, stationType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getDateUTC() {
		return dateUTC;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDateUTC(Date newDateUTC) {
		Date oldDateUTC = dateUTC;
		dateUTC = newDateUTC;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__DATE_UTC, oldDateUTC, dateUTC));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDnsIP() {
		return dnsIP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDnsIP(String newDnsIP) {
		String oldDnsIP = dnsIP;
		dnsIP = newDnsIP;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__DNS_IP, oldDnsIP, dnsIP));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getDnsErrorCount() {
		return dnsErrorCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDnsErrorCount(int newDnsErrorCount) {
		int oldDnsErrorCount = dnsErrorCount;
		dnsErrorCount = newDnsErrorCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__DNS_ERROR_COUNT, oldDnsErrorCount, dnsErrorCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getCdnFlag() {
		return cdnFlag;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCdnFlag(int newCdnFlag) {
		int oldCdnFlag = cdnFlag;
		cdnFlag = newCdnFlag;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__CDN_FLAG, oldCdnFlag, cdnFlag));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getTemperaturIndoor() {
		return temperaturIndoor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTemperaturIndoor(double newTemperaturIndoor) {
		double oldTemperaturIndoor = temperaturIndoor;
		temperaturIndoor = newTemperaturIndoor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_INDOOR, oldTemperaturIndoor, temperaturIndoor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getHuminityIndoor() {
		return huminityIndoor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setHuminityIndoor(int newHuminityIndoor) {
		int oldHuminityIndoor = huminityIndoor;
		huminityIndoor = newHuminityIndoor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__HUMINITY_INDOOR, oldHuminityIndoor, huminityIndoor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getBarometerRel() {
		return barometerRel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBarometerRel(double newBarometerRel) {
		double oldBarometerRel = barometerRel;
		barometerRel = newBarometerRel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_REL, oldBarometerRel, barometerRel));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getBarometerAbs() {
		return barometerAbs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBarometerAbs(double newBarometerAbs) {
		double oldBarometerAbs = barometerAbs;
		barometerAbs = newBarometerAbs;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_ABS, oldBarometerAbs, barometerAbs));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getTemperaturOutdoor() {
		return temperaturOutdoor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTemperaturOutdoor(double newTemperaturOutdoor) {
		double oldTemperaturOutdoor = temperaturOutdoor;
		temperaturOutdoor = newTemperaturOutdoor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_OUTDOOR, oldTemperaturOutdoor, temperaturOutdoor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getHumidityOutdoor() {
		return humidityOutdoor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setHumidityOutdoor(int newHumidityOutdoor) {
		int oldHumidityOutdoor = humidityOutdoor;
		humidityOutdoor = newHumidityOutdoor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__HUMIDITY_OUTDOOR, oldHumidityOutdoor, humidityOutdoor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getVpd() {
		return vpd;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVpd(double newVpd) {
		double oldVpd = vpd;
		vpd = newVpd;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__VPD, oldVpd, vpd));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getWindDirection() {
		return windDirection;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWindDirection(int newWindDirection) {
		int oldWindDirection = windDirection;
		windDirection = newWindDirection;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__WIND_DIRECTION, oldWindDirection, windDirection));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getWindDirAvg10min() {
		return windDirAvg10min;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWindDirAvg10min(int newWindDirAvg10min) {
		int oldWindDirAvg10min = windDirAvg10min;
		windDirAvg10min = newWindDirAvg10min;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__WIND_DIR_AVG10MIN, oldWindDirAvg10min, windDirAvg10min));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getWindSpeedMph() {
		return windSpeedMph;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWindSpeedMph(double newWindSpeedMph) {
		double oldWindSpeedMph = windSpeedMph;
		windSpeedMph = newWindSpeedMph;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__WIND_SPEED_MPH, oldWindSpeedMph, windSpeedMph));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getGustDailyMax() {
		return gustDailyMax;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setGustDailyMax(double newGustDailyMax) {
		double oldGustDailyMax = gustDailyMax;
		gustDailyMax = newGustDailyMax;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__GUST_DAILY_MAX, oldGustDailyMax, gustDailyMax));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getGustWindMPH() {
		return gustWindMPH;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setGustWindMPH(double newGustWindMPH) {
		double oldGustWindMPH = gustWindMPH;
		gustWindMPH = newGustWindMPH;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__GUST_WIND_MPH, oldGustWindMPH, gustWindMPH));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getSolarRadiation() {
		return solarRadiation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSolarRadiation(double newSolarRadiation) {
		double oldSolarRadiation = solarRadiation;
		solarRadiation = newSolarRadiation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__SOLAR_RADIATION, oldSolarRadiation, solarRadiation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getUvIndex() {
		return uvIndex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUvIndex(int newUvIndex) {
		int oldUvIndex = uvIndex;
		uvIndex = newUvIndex;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__UV_INDEX, oldUvIndex, uvIndex));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRainRate() {
		return rainRate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRainRate(double newRainRate) {
		double oldRainRate = rainRate;
		rainRate = newRainRate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RAIN_RATE, oldRainRate, rainRate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRainEvent() {
		return rainEvent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRainEvent(double newRainEvent) {
		double oldRainEvent = rainEvent;
		rainEvent = newRainEvent;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RAIN_EVENT, oldRainEvent, rainEvent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRainDaily() {
		return rainDaily;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRainDaily(double newRainDaily) {
		double oldRainDaily = rainDaily;
		rainDaily = newRainDaily;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RAIN_DAILY, oldRainDaily, rainDaily));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRainHourly() {
		return rainHourly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRainHourly(double newRainHourly) {
		double oldRainHourly = rainHourly;
		rainHourly = newRainHourly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RAIN_HOURLY, oldRainHourly, rainHourly));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRainLast24H() {
		return rainLast24H;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRainLast24H(double newRainLast24H) {
		double oldRainLast24H = rainLast24H;
		rainLast24H = newRainLast24H;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RAIN_LAST24_H, oldRainLast24H, rainLast24H));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRainWeekly() {
		return rainWeekly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRainWeekly(double newRainWeekly) {
		double oldRainWeekly = rainWeekly;
		rainWeekly = newRainWeekly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RAIN_WEEKLY, oldRainWeekly, rainWeekly));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRainMonthly() {
		return rainMonthly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRainMonthly(double newRainMonthly) {
		double oldRainMonthly = rainMonthly;
		rainMonthly = newRainMonthly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RAIN_MONTHLY, oldRainMonthly, rainMonthly));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRainYearly() {
		return rainYearly;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRainYearly(double newRainYearly) {
		double oldRainYearly = rainYearly;
		rainYearly = newRainYearly;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RAIN_YEARLY, oldRainYearly, rainYearly));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getWh65BatteryStatus() {
		return wh65BatteryStatus;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWh65BatteryStatus(int newWh65BatteryStatus) {
		int oldWh65BatteryStatus = wh65BatteryStatus;
		wh65BatteryStatus = newWh65BatteryStatus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__WH65_BATTERY_STATUS, oldWh65BatteryStatus, wh65BatteryStatus));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getFrequency() {
		return frequency;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFrequency(String newFrequency) {
		String oldFrequency = frequency;
		frequency = newFrequency;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__FREQUENCY, oldFrequency, frequency));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getModel() {
		return model;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setModel(String newModel) {
		String oldModel = model;
		model = newModel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__MODEL, oldModel, model));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getUpdateInterval() {
		return updateInterval;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUpdateInterval(int newUpdateInterval) {
		int oldUpdateInterval = updateInterval;
		updateInterval = newUpdateInterval;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__UPDATE_INTERVAL, oldUpdateInterval, updateInterval));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRuntime() {
		return runtime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRuntime(String newRuntime) {
		String oldRuntime = runtime;
		runtime = newRuntime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__RUNTIME, oldRuntime, runtime));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getHeap() {
		return heap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setHeap(long newHeap) {
		long oldHeap = heap;
		heap = newHeap;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EcoWittPackage.ECO_WITT_WEATHER__HEAP, oldHeap, heap));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case EcoWittPackage.ECO_WITT_WEATHER__PASSKEY:
				return getPasskey();
			case EcoWittPackage.ECO_WITT_WEATHER__STATION_TYPE:
				return getStationType();
			case EcoWittPackage.ECO_WITT_WEATHER__DATE_UTC:
				return getDateUTC();
			case EcoWittPackage.ECO_WITT_WEATHER__DNS_IP:
				return getDnsIP();
			case EcoWittPackage.ECO_WITT_WEATHER__DNS_ERROR_COUNT:
				return getDnsErrorCount();
			case EcoWittPackage.ECO_WITT_WEATHER__CDN_FLAG:
				return getCdnFlag();
			case EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_INDOOR:
				return getTemperaturIndoor();
			case EcoWittPackage.ECO_WITT_WEATHER__HUMINITY_INDOOR:
				return getHuminityIndoor();
			case EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_REL:
				return getBarometerRel();
			case EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_ABS:
				return getBarometerAbs();
			case EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_OUTDOOR:
				return getTemperaturOutdoor();
			case EcoWittPackage.ECO_WITT_WEATHER__HUMIDITY_OUTDOOR:
				return getHumidityOutdoor();
			case EcoWittPackage.ECO_WITT_WEATHER__VPD:
				return getVpd();
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_DIRECTION:
				return getWindDirection();
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_DIR_AVG10MIN:
				return getWindDirAvg10min();
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_SPEED_MPH:
				return getWindSpeedMph();
			case EcoWittPackage.ECO_WITT_WEATHER__GUST_DAILY_MAX:
				return getGustDailyMax();
			case EcoWittPackage.ECO_WITT_WEATHER__GUST_WIND_MPH:
				return getGustWindMPH();
			case EcoWittPackage.ECO_WITT_WEATHER__SOLAR_RADIATION:
				return getSolarRadiation();
			case EcoWittPackage.ECO_WITT_WEATHER__UV_INDEX:
				return getUvIndex();
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_RATE:
				return getRainRate();
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_EVENT:
				return getRainEvent();
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_DAILY:
				return getRainDaily();
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_HOURLY:
				return getRainHourly();
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_LAST24_H:
				return getRainLast24H();
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_WEEKLY:
				return getRainWeekly();
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_MONTHLY:
				return getRainMonthly();
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_YEARLY:
				return getRainYearly();
			case EcoWittPackage.ECO_WITT_WEATHER__WH65_BATTERY_STATUS:
				return getWh65BatteryStatus();
			case EcoWittPackage.ECO_WITT_WEATHER__FREQUENCY:
				return getFrequency();
			case EcoWittPackage.ECO_WITT_WEATHER__MODEL:
				return getModel();
			case EcoWittPackage.ECO_WITT_WEATHER__UPDATE_INTERVAL:
				return getUpdateInterval();
			case EcoWittPackage.ECO_WITT_WEATHER__RUNTIME:
				return getRuntime();
			case EcoWittPackage.ECO_WITT_WEATHER__HEAP:
				return getHeap();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case EcoWittPackage.ECO_WITT_WEATHER__PASSKEY:
				setPasskey((String)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__STATION_TYPE:
				setStationType((String)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__DATE_UTC:
				setDateUTC((Date)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__DNS_IP:
				setDnsIP((String)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__DNS_ERROR_COUNT:
				setDnsErrorCount((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__CDN_FLAG:
				setCdnFlag((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_INDOOR:
				setTemperaturIndoor((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__HUMINITY_INDOOR:
				setHuminityIndoor((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_REL:
				setBarometerRel((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_ABS:
				setBarometerAbs((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_OUTDOOR:
				setTemperaturOutdoor((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__HUMIDITY_OUTDOOR:
				setHumidityOutdoor((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__VPD:
				setVpd((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_DIRECTION:
				setWindDirection((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_DIR_AVG10MIN:
				setWindDirAvg10min((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_SPEED_MPH:
				setWindSpeedMph((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__GUST_DAILY_MAX:
				setGustDailyMax((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__GUST_WIND_MPH:
				setGustWindMPH((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__SOLAR_RADIATION:
				setSolarRadiation((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__UV_INDEX:
				setUvIndex((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_RATE:
				setRainRate((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_EVENT:
				setRainEvent((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_DAILY:
				setRainDaily((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_HOURLY:
				setRainHourly((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_LAST24_H:
				setRainLast24H((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_WEEKLY:
				setRainWeekly((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_MONTHLY:
				setRainMonthly((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_YEARLY:
				setRainYearly((Double)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__WH65_BATTERY_STATUS:
				setWh65BatteryStatus((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__FREQUENCY:
				setFrequency((String)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__MODEL:
				setModel((String)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__UPDATE_INTERVAL:
				setUpdateInterval((Integer)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RUNTIME:
				setRuntime((String)newValue);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__HEAP:
				setHeap((Long)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case EcoWittPackage.ECO_WITT_WEATHER__PASSKEY:
				setPasskey(PASSKEY_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__STATION_TYPE:
				setStationType(STATION_TYPE_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__DATE_UTC:
				setDateUTC(DATE_UTC_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__DNS_IP:
				setDnsIP(DNS_IP_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__DNS_ERROR_COUNT:
				setDnsErrorCount(DNS_ERROR_COUNT_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__CDN_FLAG:
				setCdnFlag(CDN_FLAG_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_INDOOR:
				setTemperaturIndoor(TEMPERATUR_INDOOR_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__HUMINITY_INDOOR:
				setHuminityIndoor(HUMINITY_INDOOR_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_REL:
				setBarometerRel(BAROMETER_REL_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_ABS:
				setBarometerAbs(BAROMETER_ABS_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_OUTDOOR:
				setTemperaturOutdoor(TEMPERATUR_OUTDOOR_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__HUMIDITY_OUTDOOR:
				setHumidityOutdoor(HUMIDITY_OUTDOOR_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__VPD:
				setVpd(VPD_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_DIRECTION:
				setWindDirection(WIND_DIRECTION_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_DIR_AVG10MIN:
				setWindDirAvg10min(WIND_DIR_AVG10MIN_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_SPEED_MPH:
				setWindSpeedMph(WIND_SPEED_MPH_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__GUST_DAILY_MAX:
				setGustDailyMax(GUST_DAILY_MAX_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__GUST_WIND_MPH:
				setGustWindMPH(GUST_WIND_MPH_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__SOLAR_RADIATION:
				setSolarRadiation(SOLAR_RADIATION_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__UV_INDEX:
				setUvIndex(UV_INDEX_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_RATE:
				setRainRate(RAIN_RATE_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_EVENT:
				setRainEvent(RAIN_EVENT_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_DAILY:
				setRainDaily(RAIN_DAILY_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_HOURLY:
				setRainHourly(RAIN_HOURLY_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_LAST24_H:
				setRainLast24H(RAIN_LAST24_H_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_WEEKLY:
				setRainWeekly(RAIN_WEEKLY_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_MONTHLY:
				setRainMonthly(RAIN_MONTHLY_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_YEARLY:
				setRainYearly(RAIN_YEARLY_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__WH65_BATTERY_STATUS:
				setWh65BatteryStatus(WH65_BATTERY_STATUS_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__FREQUENCY:
				setFrequency(FREQUENCY_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__MODEL:
				setModel(MODEL_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__UPDATE_INTERVAL:
				setUpdateInterval(UPDATE_INTERVAL_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__RUNTIME:
				setRuntime(RUNTIME_EDEFAULT);
				return;
			case EcoWittPackage.ECO_WITT_WEATHER__HEAP:
				setHeap(HEAP_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case EcoWittPackage.ECO_WITT_WEATHER__PASSKEY:
				return PASSKEY_EDEFAULT == null ? passkey != null : !PASSKEY_EDEFAULT.equals(passkey);
			case EcoWittPackage.ECO_WITT_WEATHER__STATION_TYPE:
				return STATION_TYPE_EDEFAULT == null ? stationType != null : !STATION_TYPE_EDEFAULT.equals(stationType);
			case EcoWittPackage.ECO_WITT_WEATHER__DATE_UTC:
				return DATE_UTC_EDEFAULT == null ? dateUTC != null : !DATE_UTC_EDEFAULT.equals(dateUTC);
			case EcoWittPackage.ECO_WITT_WEATHER__DNS_IP:
				return DNS_IP_EDEFAULT == null ? dnsIP != null : !DNS_IP_EDEFAULT.equals(dnsIP);
			case EcoWittPackage.ECO_WITT_WEATHER__DNS_ERROR_COUNT:
				return dnsErrorCount != DNS_ERROR_COUNT_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__CDN_FLAG:
				return cdnFlag != CDN_FLAG_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_INDOOR:
				return temperaturIndoor != TEMPERATUR_INDOOR_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__HUMINITY_INDOOR:
				return huminityIndoor != HUMINITY_INDOOR_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_REL:
				return barometerRel != BAROMETER_REL_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__BAROMETER_ABS:
				return barometerAbs != BAROMETER_ABS_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__TEMPERATUR_OUTDOOR:
				return temperaturOutdoor != TEMPERATUR_OUTDOOR_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__HUMIDITY_OUTDOOR:
				return humidityOutdoor != HUMIDITY_OUTDOOR_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__VPD:
				return vpd != VPD_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_DIRECTION:
				return windDirection != WIND_DIRECTION_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_DIR_AVG10MIN:
				return windDirAvg10min != WIND_DIR_AVG10MIN_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__WIND_SPEED_MPH:
				return windSpeedMph != WIND_SPEED_MPH_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__GUST_DAILY_MAX:
				return gustDailyMax != GUST_DAILY_MAX_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__GUST_WIND_MPH:
				return gustWindMPH != GUST_WIND_MPH_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__SOLAR_RADIATION:
				return solarRadiation != SOLAR_RADIATION_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__UV_INDEX:
				return uvIndex != UV_INDEX_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_RATE:
				return rainRate != RAIN_RATE_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_EVENT:
				return rainEvent != RAIN_EVENT_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_DAILY:
				return rainDaily != RAIN_DAILY_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_HOURLY:
				return rainHourly != RAIN_HOURLY_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_LAST24_H:
				return rainLast24H != RAIN_LAST24_H_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_WEEKLY:
				return rainWeekly != RAIN_WEEKLY_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_MONTHLY:
				return rainMonthly != RAIN_MONTHLY_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RAIN_YEARLY:
				return rainYearly != RAIN_YEARLY_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__WH65_BATTERY_STATUS:
				return wh65BatteryStatus != WH65_BATTERY_STATUS_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__FREQUENCY:
				return FREQUENCY_EDEFAULT == null ? frequency != null : !FREQUENCY_EDEFAULT.equals(frequency);
			case EcoWittPackage.ECO_WITT_WEATHER__MODEL:
				return MODEL_EDEFAULT == null ? model != null : !MODEL_EDEFAULT.equals(model);
			case EcoWittPackage.ECO_WITT_WEATHER__UPDATE_INTERVAL:
				return updateInterval != UPDATE_INTERVAL_EDEFAULT;
			case EcoWittPackage.ECO_WITT_WEATHER__RUNTIME:
				return RUNTIME_EDEFAULT == null ? runtime != null : !RUNTIME_EDEFAULT.equals(runtime);
			case EcoWittPackage.ECO_WITT_WEATHER__HEAP:
				return heap != HEAP_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (passkey: ");
		result.append(passkey);
		result.append(", stationType: ");
		result.append(stationType);
		result.append(", dateUTC: ");
		result.append(dateUTC);
		result.append(", dnsIP: ");
		result.append(dnsIP);
		result.append(", dnsErrorCount: ");
		result.append(dnsErrorCount);
		result.append(", cdnFlag: ");
		result.append(cdnFlag);
		result.append(", temperaturIndoor: ");
		result.append(temperaturIndoor);
		result.append(", huminityIndoor: ");
		result.append(huminityIndoor);
		result.append(", barometerRel: ");
		result.append(barometerRel);
		result.append(", barometerAbs: ");
		result.append(barometerAbs);
		result.append(", temperaturOutdoor: ");
		result.append(temperaturOutdoor);
		result.append(", humidityOutdoor: ");
		result.append(humidityOutdoor);
		result.append(", vpd: ");
		result.append(vpd);
		result.append(", windDirection: ");
		result.append(windDirection);
		result.append(", windDirAvg10min: ");
		result.append(windDirAvg10min);
		result.append(", windSpeedMph: ");
		result.append(windSpeedMph);
		result.append(", gustDailyMax: ");
		result.append(gustDailyMax);
		result.append(", gustWindMPH: ");
		result.append(gustWindMPH);
		result.append(", solarRadiation: ");
		result.append(solarRadiation);
		result.append(", uvIndex: ");
		result.append(uvIndex);
		result.append(", rainRate: ");
		result.append(rainRate);
		result.append(", rainEvent: ");
		result.append(rainEvent);
		result.append(", rainDaily: ");
		result.append(rainDaily);
		result.append(", rainHourly: ");
		result.append(rainHourly);
		result.append(", rainLast24H: ");
		result.append(rainLast24H);
		result.append(", rainWeekly: ");
		result.append(rainWeekly);
		result.append(", rainMonthly: ");
		result.append(rainMonthly);
		result.append(", rainYearly: ");
		result.append(rainYearly);
		result.append(", wh65BatteryStatus: ");
		result.append(wh65BatteryStatus);
		result.append(", frequency: ");
		result.append(frequency);
		result.append(", model: ");
		result.append(model);
		result.append(", updateInterval: ");
		result.append(updateInterval);
		result.append(", runtime: ");
		result.append(runtime);
		result.append(", heap: ");
		result.append(heap);
		result.append(')');
		return result.toString();
	}

} //EcoWittWeatherImpl
