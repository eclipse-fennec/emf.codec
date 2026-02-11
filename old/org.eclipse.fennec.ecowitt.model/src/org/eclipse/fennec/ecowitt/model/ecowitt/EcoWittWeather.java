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

import java.util.Date;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Weather</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Represents a single data payload from an Ecowitt Weather Station Gateway.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getPasskey <em>Passkey</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getStationType <em>Station Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDateUTC <em>Date UTC</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDnsIP <em>Dns IP</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDnsErrorCount <em>Dns Error Count</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getCdnFlag <em>Cdn Flag</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getTemperaturIndoor <em>Temperatur Indoor</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHuminityIndoor <em>Huminity Indoor</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getBarometerRel <em>Barometer Rel</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getBarometerAbs <em>Barometer Abs</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getTemperaturOutdoor <em>Temperatur Outdoor</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHumidityOutdoor <em>Humidity Outdoor</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getVpd <em>Vpd</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindDirection <em>Wind Direction</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindDirAvg10min <em>Wind Dir Avg10min</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindSpeedMph <em>Wind Speed Mph</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getGustDailyMax <em>Gust Daily Max</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getGustWindMPH <em>Gust Wind MPH</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getSolarRadiation <em>Solar Radiation</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getUvIndex <em>Uv Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainRate <em>Rain Rate</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainEvent <em>Rain Event</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainDaily <em>Rain Daily</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainHourly <em>Rain Hourly</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainLast24H <em>Rain Last24 H</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainWeekly <em>Rain Weekly</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainMonthly <em>Rain Monthly</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainYearly <em>Rain Yearly</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWh65BatteryStatus <em>Wh65 Battery Status</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getFrequency <em>Frequency</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getModel <em>Model</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getUpdateInterval <em>Update Interval</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRuntime <em>Runtime</em>}</li>
 *   <li>{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHeap <em>Heap</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather()
 * @model
 * @generated
 */
@ProviderType
public interface EcoWittWeather extends EObject {
	/**
	 * Returns the value of the '<em><b>Passkey</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The security key to authenticate the data feed.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Passkey</em>' attribute.
	 * @see #setPasskey(String)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_Passkey()
	 * @model extendedMetaData="name='PASSKEY'"
	 * @generated
	 */
	String getPasskey();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getPasskey <em>Passkey</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Passkey</em>' attribute.
	 * @see #getPasskey()
	 * @generated
	 */
	void setPasskey(String value);

	/**
	 * Returns the value of the '<em><b>Station Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The firmware/hardware version of the sending gateway.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Station Type</em>' attribute.
	 * @see #setStationType(String)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_StationType()
	 * @model extendedMetaData="name='stationtype'"
	 * @generated
	 */
	String getStationType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getStationType <em>Station Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Station Type</em>' attribute.
	 * @see #getStationType()
	 * @generated
	 */
	void setStationType(String value);

	/**
	 * Returns the value of the '<em><b>Date UTC</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The timestamp of the data reading in Coordinated Universal Time.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Date UTC</em>' attribute.
	 * @see #setDateUTC(Date)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_DateUTC()
	 * @model extendedMetaData="name='dateutc'"
	 * @generated
	 */
	Date getDateUTC();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDateUTC <em>Date UTC</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Date UTC</em>' attribute.
	 * @see #getDateUTC()
	 * @generated
	 */
	void setDateUTC(Date value);

	/**
	 * Returns the value of the '<em><b>Dns IP</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dns IP</em>' attribute.
	 * @see #setDnsIP(String)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_DnsIP()
	 * @model extendedMetaData="name='dns_ip'"
	 * @generated
	 */
	String getDnsIP();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDnsIP <em>Dns IP</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dns IP</em>' attribute.
	 * @see #getDnsIP()
	 * @generated
	 */
	void setDnsIP(String value);

	/**
	 * Returns the value of the '<em><b>Dns Error Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dns Error Count</em>' attribute.
	 * @see #setDnsErrorCount(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_DnsErrorCount()
	 * @model extendedMetaData="name='dns_err_cnt'"
	 * @generated
	 */
	int getDnsErrorCount();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getDnsErrorCount <em>Dns Error Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dns Error Count</em>' attribute.
	 * @see #getDnsErrorCount()
	 * @generated
	 */
	void setDnsErrorCount(int value);

	/**
	 * Returns the value of the '<em><b>Cdn Flag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cdn Flag</em>' attribute.
	 * @see #setCdnFlag(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_CdnFlag()
	 * @model extendedMetaData="name='cdnflg'"
	 * @generated
	 */
	int getCdnFlag();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getCdnFlag <em>Cdn Flag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cdn Flag</em>' attribute.
	 * @see #getCdnFlag()
	 * @generated
	 */
	void setCdnFlag(int value);

	/**
	 * Returns the value of the '<em><b>Temperatur Indoor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The indoor temperature reading in degrees Fahrenheit.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Temperatur Indoor</em>' attribute.
	 * @see #setTemperaturIndoor(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_TemperaturIndoor()
	 * @model extendedMetaData="name='tempinf'"
	 * @generated
	 */
	double getTemperaturIndoor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getTemperaturIndoor <em>Temperatur Indoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Temperatur Indoor</em>' attribute.
	 * @see #getTemperaturIndoor()
	 * @generated
	 */
	void setTemperaturIndoor(double value);

	/**
	 * Returns the value of the '<em><b>Huminity Indoor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The indoor humidity reading in percent.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Huminity Indoor</em>' attribute.
	 * @see #setHuminityIndoor(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_HuminityIndoor()
	 * @model extendedMetaData="name='humidityin'"
	 * @generated
	 */
	int getHuminityIndoor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHuminityIndoor <em>Huminity Indoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Huminity Indoor</em>' attribute.
	 * @see #getHuminityIndoor()
	 * @generated
	 */
	void setHuminityIndoor(int value);

	/**
	 * Returns the value of the '<em><b>Barometer Rel</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The relative (sea-level adjusted) barometric pressure in inches of Mercury (inHg).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Barometer Rel</em>' attribute.
	 * @see #setBarometerRel(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_BarometerRel()
	 * @model extendedMetaData="name='baromrelin'"
	 * @generated
	 */
	double getBarometerRel();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getBarometerRel <em>Barometer Rel</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Barometer Rel</em>' attribute.
	 * @see #getBarometerRel()
	 * @generated
	 */
	void setBarometerRel(double value);

	/**
	 * Returns the value of the '<em><b>Barometer Abs</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The absolute (station) barometric pressure in inches of Mercury (inHg).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Barometer Abs</em>' attribute.
	 * @see #setBarometerAbs(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_BarometerAbs()
	 * @model extendedMetaData="name='baromabsin'"
	 * @generated
	 */
	double getBarometerAbs();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getBarometerAbs <em>Barometer Abs</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Barometer Abs</em>' attribute.
	 * @see #getBarometerAbs()
	 * @generated
	 */
	void setBarometerAbs(double value);

	/**
	 * Returns the value of the '<em><b>Temperatur Outdoor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The outdoor temperature reading in degrees Fahrenheit.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Temperatur Outdoor</em>' attribute.
	 * @see #setTemperaturOutdoor(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_TemperaturOutdoor()
	 * @model extendedMetaData="name='tempf'"
	 * @generated
	 */
	double getTemperaturOutdoor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getTemperaturOutdoor <em>Temperatur Outdoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Temperatur Outdoor</em>' attribute.
	 * @see #getTemperaturOutdoor()
	 * @generated
	 */
	void setTemperaturOutdoor(double value);

	/**
	 * Returns the value of the '<em><b>Humidity Outdoor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The outdoor humidity reading in percent.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Humidity Outdoor</em>' attribute.
	 * @see #setHumidityOutdoor(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_HumidityOutdoor()
	 * @model extendedMetaData="name='humidity'"
	 * @generated
	 */
	int getHumidityOutdoor();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHumidityOutdoor <em>Humidity Outdoor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Humidity Outdoor</em>' attribute.
	 * @see #getHumidityOutdoor()
	 * @generated
	 */
	void setHumidityOutdoor(int value);

	/**
	 * Returns the value of the '<em><b>Vpd</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vpd</em>' attribute.
	 * @see #setVpd(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_Vpd()
	 * @model
	 * @generated
	 */
	double getVpd();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getVpd <em>Vpd</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Vpd</em>' attribute.
	 * @see #getVpd()
	 * @generated
	 */
	void setVpd(double value);

	/**
	 * Returns the value of the '<em><b>Wind Direction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The current wind direction in degrees.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Wind Direction</em>' attribute.
	 * @see #setWindDirection(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_WindDirection()
	 * @model extendedMetaData="name='winddir'"
	 * @generated
	 */
	int getWindDirection();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindDirection <em>Wind Direction</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wind Direction</em>' attribute.
	 * @see #getWindDirection()
	 * @generated
	 */
	void setWindDirection(int value);

	/**
	 * Returns the value of the '<em><b>Wind Dir Avg10min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wind Dir Avg10min</em>' attribute.
	 * @see #setWindDirAvg10min(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_WindDirAvg10min()
	 * @model extendedMetaData="name='winddir_avg10m'"
	 * @generated
	 */
	int getWindDirAvg10min();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindDirAvg10min <em>Wind Dir Avg10min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wind Dir Avg10min</em>' attribute.
	 * @see #getWindDirAvg10min()
	 * @generated
	 */
	void setWindDirAvg10min(int value);

	/**
	 * Returns the value of the '<em><b>Wind Speed Mph</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The current wind speed in miles per hour (mph).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Wind Speed Mph</em>' attribute.
	 * @see #setWindSpeedMph(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_WindSpeedMph()
	 * @model extendedMetaData="name='windspeedmph'"
	 * @generated
	 */
	double getWindSpeedMph();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWindSpeedMph <em>Wind Speed Mph</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wind Speed Mph</em>' attribute.
	 * @see #getWindSpeedMph()
	 * @generated
	 */
	void setWindSpeedMph(double value);

	/**
	 * Returns the value of the '<em><b>Gust Daily Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The maximum wind gust recorded since midnight in miles per hour (mph).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Gust Daily Max</em>' attribute.
	 * @see #setGustDailyMax(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_GustDailyMax()
	 * @model extendedMetaData="name='maxdailygust'"
	 * @generated
	 */
	double getGustDailyMax();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getGustDailyMax <em>Gust Daily Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gust Daily Max</em>' attribute.
	 * @see #getGustDailyMax()
	 * @generated
	 */
	void setGustDailyMax(double value);

	/**
	 * Returns the value of the '<em><b>Gust Wind MPH</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The wind gust in miles per hour (mph).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Gust Wind MPH</em>' attribute.
	 * @see #setGustWindMPH(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_GustWindMPH()
	 * @model extendedMetaData="name='windgustmph'"
	 * @generated
	 */
	double getGustWindMPH();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getGustWindMPH <em>Gust Wind MPH</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gust Wind MPH</em>' attribute.
	 * @see #getGustWindMPH()
	 * @generated
	 */
	void setGustWindMPH(double value);

	/**
	 * Returns the value of the '<em><b>Solar Radiation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The intensity of solar radiation in watts per square meter (W/m^2).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Solar Radiation</em>' attribute.
	 * @see #setSolarRadiation(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_SolarRadiation()
	 * @model extendedMetaData="name='solarradiation'"
	 * @generated
	 */
	double getSolarRadiation();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getSolarRadiation <em>Solar Radiation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Solar Radiation</em>' attribute.
	 * @see #getSolarRadiation()
	 * @generated
	 */
	void setSolarRadiation(double value);

	/**
	 * Returns the value of the '<em><b>Uv Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The current UltraViolet index.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Uv Index</em>' attribute.
	 * @see #setUvIndex(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_UvIndex()
	 * @model extendedMetaData="name='uv'"
	 * @generated
	 */
	int getUvIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getUvIndex <em>Uv Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uv Index</em>' attribute.
	 * @see #getUvIndex()
	 * @generated
	 */
	void setUvIndex(int value);

	/**
	 * Returns the value of the '<em><b>Rain Rate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rain Rate</em>' attribute.
	 * @see #setRainRate(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_RainRate()
	 * @model extendedMetaData="name='rainratein'"
	 * @generated
	 */
	double getRainRate();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainRate <em>Rain Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rain Rate</em>' attribute.
	 * @see #getRainRate()
	 * @generated
	 */
	void setRainRate(double value);

	/**
	 * Returns the value of the '<em><b>Rain Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rain Event</em>' attribute.
	 * @see #setRainEvent(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_RainEvent()
	 * @model extendedMetaData="name='eventrainin'"
	 * @generated
	 */
	double getRainEvent();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainEvent <em>Rain Event</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rain Event</em>' attribute.
	 * @see #getRainEvent()
	 * @generated
	 */
	void setRainEvent(double value);

	/**
	 * Returns the value of the '<em><b>Rain Daily</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The rainfall total since midnight in inches.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Rain Daily</em>' attribute.
	 * @see #setRainDaily(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_RainDaily()
	 * @model extendedMetaData="name='dailyrainin'"
	 * @generated
	 */
	double getRainDaily();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainDaily <em>Rain Daily</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rain Daily</em>' attribute.
	 * @see #getRainDaily()
	 * @generated
	 */
	void setRainDaily(double value);

	/**
	 * Returns the value of the '<em><b>Rain Hourly</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rain Hourly</em>' attribute.
	 * @see #setRainHourly(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_RainHourly()
	 * @model extendedMetaData="name='hourlyrainin'"
	 * @generated
	 */
	double getRainHourly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainHourly <em>Rain Hourly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rain Hourly</em>' attribute.
	 * @see #getRainHourly()
	 * @generated
	 */
	void setRainHourly(double value);

	/**
	 * Returns the value of the '<em><b>Rain Last24 H</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rain Last24 H</em>' attribute.
	 * @see #setRainLast24H(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_RainLast24H()
	 * @model extendedMetaData="name='last24hrainin'"
	 * @generated
	 */
	double getRainLast24H();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainLast24H <em>Rain Last24 H</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rain Last24 H</em>' attribute.
	 * @see #getRainLast24H()
	 * @generated
	 */
	void setRainLast24H(double value);

	/**
	 * Returns the value of the '<em><b>Rain Weekly</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rain Weekly</em>' attribute.
	 * @see #setRainWeekly(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_RainWeekly()
	 * @model extendedMetaData="name='weeklyrainin'"
	 * @generated
	 */
	double getRainWeekly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainWeekly <em>Rain Weekly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rain Weekly</em>' attribute.
	 * @see #getRainWeekly()
	 * @generated
	 */
	void setRainWeekly(double value);

	/**
	 * Returns the value of the '<em><b>Rain Monthly</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rain Monthly</em>' attribute.
	 * @see #setRainMonthly(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_RainMonthly()
	 * @model extendedMetaData="name='monthlyrainin'"
	 * @generated
	 */
	double getRainMonthly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainMonthly <em>Rain Monthly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rain Monthly</em>' attribute.
	 * @see #getRainMonthly()
	 * @generated
	 */
	void setRainMonthly(double value);

	/**
	 * Returns the value of the '<em><b>Rain Yearly</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The rainfall total for the current year in inches.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Rain Yearly</em>' attribute.
	 * @see #setRainYearly(double)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_RainYearly()
	 * @model extendedMetaData="name='yearlyrainin'"
	 * @generated
	 */
	double getRainYearly();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRainYearly <em>Rain Yearly</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rain Yearly</em>' attribute.
	 * @see #getRainYearly()
	 * @generated
	 */
	void setRainYearly(double value);

	/**
	 * Returns the value of the '<em><b>Wh65 Battery Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Battery status for the primary outdoor sensor array (0=Normal, 1=Low).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Wh65 Battery Status</em>' attribute.
	 * @see #setWh65BatteryStatus(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_Wh65BatteryStatus()
	 * @model extendedMetaData="name='wh65batt'"
	 * @generated
	 */
	int getWh65BatteryStatus();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getWh65BatteryStatus <em>Wh65 Battery Status</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wh65 Battery Status</em>' attribute.
	 * @see #getWh65BatteryStatus()
	 * @generated
	 */
	void setWh65BatteryStatus(int value);

	/**
	 * Returns the value of the '<em><b>Frequency</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The transmission frequency in MHz
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Frequency</em>' attribute.
	 * @see #setFrequency(String)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_Frequency()
	 * @model extendedMetaData="name='freq'"
	 * @generated
	 */
	String getFrequency();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getFrequency <em>Frequency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Frequency</em>' attribute.
	 * @see #getFrequency()
	 * @generated
	 */
	void setFrequency(String value);

	/**
	 * Returns the value of the '<em><b>Model</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The weather station model identifier
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Model</em>' attribute.
	 * @see #setModel(String)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_Model()
	 * @model
	 * @generated
	 */
	String getModel();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getModel <em>Model</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Model</em>' attribute.
	 * @see #getModel()
	 * @generated
	 */
	void setModel(String value);

	/**
	 * Returns the value of the '<em><b>Update Interval</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The reporting interval at which data is sent in seconds.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Update Interval</em>' attribute.
	 * @see #setUpdateInterval(int)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_UpdateInterval()
	 * @model extendedMetaData="name='interval'"
	 * @generated
	 */
	int getUpdateInterval();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getUpdateInterval <em>Update Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Update Interval</em>' attribute.
	 * @see #getUpdateInterval()
	 * @generated
	 */
	void setUpdateInterval(int value);

	/**
	 * Returns the value of the '<em><b>Runtime</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The runtime version.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Runtime</em>' attribute.
	 * @see #setRuntime(String)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_Runtime()
	 * @model
	 * @generated
	 */
	String getRuntime();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getRuntime <em>Runtime</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Runtime</em>' attribute.
	 * @see #getRuntime()
	 * @generated
	 */
	void setRuntime(String value);

	/**
	 * Returns the value of the '<em><b>Heap</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The runtime version.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Heap</em>' attribute.
	 * @see #setHeap(long)
	 * @see org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage#getEcoWittWeather_Heap()
	 * @model
	 * @generated
	 */
	long getHeap();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather#getHeap <em>Heap</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Heap</em>' attribute.
	 * @see #getHeap()
	 * @generated
	 */
	void setHeap(long value);

} // EcoWittWeather
