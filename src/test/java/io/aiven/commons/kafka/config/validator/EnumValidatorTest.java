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

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EnumValidatorTest {

	enum X {
		ONE, TWO, THREE, one, One, four
	}

	@ParameterizedTest
	@ValueSource(strings = {"ONE", "One", "one", "oNe", "two", "THREE", "four"})
	void testValidCaseInsensitive(String validString) {
		var validator = EnumValidator.caseInsensitive(X.class);
		assertThatNoException().isThrownBy(() -> validator.ensureValid("config.name", validString));
	}

	@ParameterizedTest
	@ValueSource(strings = {"five", "ONE "})
	void testInvalidCaseInsensitive(String invalidString) {
		var validator = EnumValidator.caseInsensitive(X.class);
		assertThatThrownBy(() -> validator.ensureValid("config.name", invalidString))
				.isInstanceOf(ConfigException.class).hasMessageStartingWith(
						"Invalid value %s for configuration config.name: String must be one of (case insensitive):",
						invalidString);
	}

	@ParameterizedTest
	@ValueSource(strings = {"ONE", "TWO", "THREE", "one", "One", "four"})
	void testValidCaseSensitive(String validString) {
		var validator = EnumValidator.caseSensitive(X.class);
		assertThatNoException().isThrownBy(() -> validator.ensureValid("config.name", validString));
	}

	@ParameterizedTest
	@ValueSource(strings = {"oNe", "two", "FOUR", "ONE "})
	void testInvalidCaseSensitive(String invalidString) {
		var validator = EnumValidator.caseSensitive(X.class);
		assertThatThrownBy(() -> validator.ensureValid("config.name", invalidString))
				.isInstanceOf(ConfigException.class).hasMessage(
						"Invalid value %s for configuration config.name: String must be one of: ONE, TWO, THREE, one, One, four",
						invalidString);
	}

}
