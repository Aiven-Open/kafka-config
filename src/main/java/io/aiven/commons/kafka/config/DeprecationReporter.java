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

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Reports on deprecated configuration usage.
 */
public class DeprecationReporter {

	private DeprecationReporter() {
		// do not instantiate.
	}

	/**
	 * Generates a list of deprecation notices for deprecated items that have
	 * non-default values.
	 *
	 * @param config
	 *            The configuration class.
	 * @param configDef
	 *            the definition for the class.
	 * @return a list of deprecation notices for deprecated items that have
	 *         non-default values.
	 */
	public static List<String> report(AbstractConfig config, ConfigDef configDef) {
		/** The list of original values for the config */
		final Map<String, Object> originals = config.originals();

		// remove the defaults from the originals
		originals.values().removeAll(configDef.defaultValues().values());

		// generate the list.
		return configDef.configKeys().values().stream().filter(x -> x instanceof ExtendedConfigKey)
				.map(x -> (ExtendedConfigKey) x)// an extended key
				.filter(ex -> Objects.nonNull(originals.get(ex.name))) // has non-default value
				.filter(ExtendedConfigKey::isDeprecated) // is deprecated
				.map(ex -> String.format("Option %s", ex.deprecated.formatted(ex.name))).toList();
	}
}
