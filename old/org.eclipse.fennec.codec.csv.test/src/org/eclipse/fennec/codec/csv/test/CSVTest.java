/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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
package org.eclipse.fennec.codec.csv.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.codec.csv.resource.CSVResource;
import org.eclipse.fennec.codec.csv.resource.CSVResourceFactory;
import org.eclipse.fennec.codec.options.CodecModuleOptions;
import org.eclipse.fennec.codec.options.CodecResourceOptions;
import org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittPackage;
import org.eclipse.fennec.ecowitt.model.ecowitt.EcoWittWeather;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;

/**
 * See documentation here: 
 * 	https://github.com/osgi/osgi-test
 * 	https://github.com/osgi/osgi-test/wiki
 * Examples: https://github.com/osgi/osgi-test/tree/main/examples
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
//@ExtendWith(MockitoExtension.class)
public class CSVTest {

	//	@Mock
	//	TestInterface test;

	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {

	}

	@WithFactoryConfiguration(factoryPid = "DefaultObjectMapperConfigurator", location = "?", name = "csv", properties = {
			@Property(key = "type", value="csv"),
			@Property(key = "codecFactoryConfigurator.target", value="(type=csv)")
	})
	@WithFactoryConfiguration(factoryPid = "DefaultCodecModuleConfigurator", location = "?", name = "csv")
	@Test
	public void testCSVParser(
			@InjectService(cardinality = 0) ServiceAware<CSVResourceFactory> ecowittRFAware, @InjectService ResourceSet rs) {

		CSVResourceFactory csvRF = null;
		try {
			csvRF = ecowittRFAware.waitForService(5000l);
		} catch (InterruptedException e) {
			fail("Retrieven CSV ResourceFactory failed with timeout after 5 secs", e);
		}
		assertNotNull(csvRF);

		Map<String, Object> properties = new HashMap<>();
		properties.put(CodecResourceOptions.CODEC_ROOT_OBJECT, EcoWittPackage.eINSTANCE.getEcoWittWeather());
		properties.put(CodecModuleOptions.CODEC_MODULE_USE_NAMES_FROM_EXTENDED_METADATA, Boolean.TRUE);

		List<String> csvRowStringList = getCSVRowStringList(System.getProperty("data")+"test01.csv");
		List<EcoWittWeather> results = new LinkedList<>();
		int i = 0;
		for(String row : csvRowStringList) {
			Resource resource = csvRF.createResource(URI.createURI("test"+i+".csv"));
			assertInstanceOf(CSVResource.class, resource);
			try {
				resource.load(new ByteArrayInputStream(row.getBytes()), properties);
			} catch (IOException e) {
				fail("Failed loading Ecowitt DATA", e);
			}
			assertFalse(resource.getContents().isEmpty());
			EObject content = resource.getContents().get(0);
			assertInstanceOf(EcoWittWeather.class, content);
			EcoWittWeather weather = (EcoWittWeather) content;
			results.add(weather);
		}
		assertThat(results).hasSize(3);
		EcoWittWeather w1 = null, w2 = null, w3 = null;
		for(EcoWittWeather w : results) {
			if("aaa".equals(w.getPasskey())) {
				w1 = w;
			} else if("bbb".equals(w.getPasskey())) {
				w2 = w;
			} else if("ccc".equals(w.getPasskey())) {
				w3 = w;
			}
		}
		assertNotNull(w1);
		assertNotNull(w2);
		assertNotNull(w3);
		assertThat(w1.getStationType()).isEqualTo("A");
		assertThat(w2.getStationType()).isEqualTo("B");
		assertThat(w3.getStationType()).isEqualTo("C");
		assertThat(w1.getHuminityIndoor()).isEqualTo(10);
		assertThat(w2.getHuminityIndoor()).isEqualTo(20);
		assertThat(w3.getHuminityIndoor()).isEqualTo(30);

	}

	private List<String> getCSVRowStringList(String csvFilePath) {
		try (Reader reader = new FileReader(csvFilePath);
				CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT.builder().setHeader().get());) {
			List<String> csvRowStringList = new LinkedList<>();
			// The parser automatically uses the first row as headers.
			// You can get the headers if needed:
			Map<String, Integer> headers = csvParser.getHeaderMap();
			System.out.println("CSV Headers: " + headers.keySet());

			// Iterate over each record (row) in the CSV file
			for (CSVRecord csvRecord : csvParser) {
				StringBuilder sb = new StringBuilder();
				// Iterate through the headers to get the key-value pairs
				for (String header : headers.keySet()) {
					// Use the header to get the corresponding value from the record
					String value = csvRecord.get(header);
					sb.append(header+"="+value+"&");
				}
				csvRowStringList.add(sb.toString().substring(0, sb.toString().length()-1));
			}
			return csvRowStringList;
		} catch (IOException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}


}
