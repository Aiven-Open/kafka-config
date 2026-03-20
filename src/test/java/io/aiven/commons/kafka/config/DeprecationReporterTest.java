package io.aiven.commons.kafka.config;
/*
         Copyright 2025 Aiven Oy and project contributors

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing,
        software distributed under the License is distributed on an
        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
        KIND, either express or implied.  See the License for the
        specific language governing permissions and limitations
        under the License.

        SPDX-License-Identifier: Apache-2
 */
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DeprecationReporterTest {

	private final ConfigDef configDef = new ConfigDef()
			.define(ExtendedConfigKey.builder("oldgreeting").defaultValue("Howdy")
					.deprecatedInfo(DeprecatedInfo.builder().description("because we say Hi")
							.since(SinceInfo.builder().groupId("g").artifactId("a").version("1960").build())
							.forRemoval(true))
					.documentation("deprecated with default value").build())
			.define(ExtendedConfigKey.builder("roads")
					.deprecatedInfo(DeprecatedInfo.builder().description("Where we're going we don't need roads")
							.since(SinceInfo.builder().groupId("g").artifactId("a").version("1985").build())
							.forRemoval(false))
					.documentation("deprecated no-default").build())
			.define(ExtendedConfigKey.builder("shangrila").documentation("not deprecated no-default").build())
			.define(ExtendedConfigKey.builder("greeting").defaultValue("Howdy").documentation("not deprecated default")
					.build());

	/**
	 * Creates the config for testing from the definition and the properties.
	 * 
	 * @param properties
	 *            the properties for the configuration.
	 * @return an AbstractConfig.
	 */
	private AbstractConfig makeConfig(Map<String, String> properties) {
		return new AbstractConfig(configDef, properties) {
		};
	}

	@Test
	void testNoValuesReport() {
		List<String> actual = DeprecationReporter.report(makeConfig(Collections.emptyMap()), configDef);
		assertThat(actual).isEmpty();
	}

	@Test
	void testChangedDefaultValueReport() {
		Map<String, String> properties = new HashMap<>();
		properties.put("oldgreeting", "Hi");
		List<String> expected = List
				.of("Option oldgreeting is deprecated for removal since g:a:1960: because we say Hi");
		List<String> actual = DeprecationReporter.report(makeConfig(properties), configDef);
		assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
	}

	@Test
	void testSetDeprecatedOptionReport() {
		Map<String, String> properties = new HashMap<>();
		properties.put("roads", "Thunder Road");
		List<String> expected = List
				.of("Option roads is deprecated since g:a:1985: Where we're going we don't need roads");
		List<String> actual = DeprecationReporter.report(makeConfig(properties), configDef);
		assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
	}

	@Test
	void testSetNotDeprecated() {
		Map<String, String> properties = new HashMap<>();
		properties.put("shangrila", "The place to be");
		properties.put("greeting", "Hola");
		List<String> actual = DeprecationReporter.report(makeConfig(properties), configDef);
		assertThat(actual).isEmpty();
	}

	@Test
	void testSetEverything() {
		Map<String, String> properties = new HashMap<>();
		properties.put("roads", "Thunder Road");
		properties.put("oldgreeting", "Hi");
		properties.put("shangrila", "The place to be");
		properties.put("greeting", "Hola");
		List<String> expected = Arrays.asList(
				"Option oldgreeting is deprecated for removal since g:a:1960: because we say Hi",
				"Option roads is deprecated since g:a:1985: Where we're going we don't need roads");
		List<String> actual = DeprecationReporter.report(makeConfig(properties), configDef);
		assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
	}

}
