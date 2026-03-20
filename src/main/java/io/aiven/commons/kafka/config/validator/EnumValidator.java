/*
         Copyright 2026 Aiven Oy and project contributors

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing,
        software distributed under the License is distributed on an
        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
        KIND, either express or implied.  See the License for the
        specific language governing permissions and limitations
        under the License.

        SPDX-License-Identifier: Apache-2
 */
package io.aiven.commons.kafka.config.validator;

import org.apache.kafka.common.config.ConfigDef;

import java.util.Arrays;

/**
 * Validates that a configuration value string is a case-insensitive match to an
 * Enum constant.
 *
 * Given an enum:
 * 
 * <pre>{@code
 * enum X {
 * 	ONE, TWO, THREE, one
 * };
 * }</pre>
 *
 * You can create a case-insensitive validator from the Enum class (recommended
 * for usability: enum constants do not usually differ only by case):
 * 
 * <pre>{@code
 * var validator = EnumValidator.caseInsensitive(X.class);
 * validator.ensureValid("config.name", "One"); // passes
 * validator.ensureValid("config.name", "ONE"); // passes
 * validator.ensureValid("config.name", "Three"); // passes
 * validator.ensureValid("config.name", "four"); // throws ConfigException
 * }</pre>
 *
 * or a case-sensitive validator, if necessary:
 *
 * <pre>{@code
 * var validator = EnumValidator.caseInsensitive(X.class);
 * validator.ensureValid("config.name", "one"); // passes
 * validator.ensureValid("config.name", "One"); // throws ConfigException
 * validator.ensureValid("config.name", "ONE"); // passes
 * validator.ensureValid("config.name", "Three"); // throws ConfigException
 * validator.ensureValid("config.name", "four"); // throws ConfigException
 * }</pre>
 */
public class EnumValidator {

	/**
	 * Private constructor. Do not instantiate.
	 */
	private EnumValidator() {
		// do not instantiate.
	}
	/**
	 * Creates a case insensitive EnumValidator.
	 * 
	 * @param enumClass
	 *            the class of the Enum that defines the valid values.
	 * @return a validator that allows any string value that is a case-insensitive
	 *         match for the enum constants in the given Enum class
	 */
	public static ConfigDef.Validator caseInsensitive(Class<? extends Enum<?>> enumClass) {
		return ConfigDef.CaseInsensitiveValidString
				.in(Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray(String[]::new));
	}

	/**
	 * Creates a case sensitive EnumValidator.
	 * 
	 * @param enumClass
	 *            the class of the Enum that defines the valid values.
	 * @return a validator that allows any string value that is a case-sensitive
	 *         match for the enum constants in the given Enum class
	 */
	public static ConfigDef.Validator caseSensitive(Class<? extends Enum<?>> enumClass) {
		return ConfigDef.ValidString
				.in(Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray(String[]::new));
	}
}
