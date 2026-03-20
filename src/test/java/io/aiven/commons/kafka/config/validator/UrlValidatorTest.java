package io.aiven.commons.kafka.config.validator;
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
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UrlValidatorTest {

	@Test
	void testUrlValidatorWithDefaultHttps() {
		UrlValidator validator = UrlValidator.builder().schemes("ftp", "https").hosts("example.com").build();

		assertThatNoException().isThrownBy(() -> validator.ensureValid("null", null));
		assertThatNoException().isThrownBy(() -> validator.ensureValid("ftp", "ftp://example.com"));
		assertThatNoException().isThrownBy(() -> validator.ensureValid("https", "https://example.com"));
		assertThatThrownBy(() -> validator.ensureValid("wrong host", "https://example.net"), "wrong host")
				.isInstanceOf(ConfigException.class).hasMessageContaining("host must be: 'example.com'");
		assertThatThrownBy(() -> validator.ensureValid("wrong protocol", "http://example.com"), "wong protocol")
				.isInstanceOf(ConfigException.class).hasMessageContaining("scheme must be one of: 'ftp', 'https'");
		assertThatThrownBy(() -> validator.ensureValid("no protocol", "example.com"), "no Protocol")
				.isInstanceOf(ConfigException.class).hasMessageContaining("scheme must be one of: 'ftp', 'https'");
		assertThatThrownBy(() -> validator.ensureValid("empty", ""), "empty URL").isInstanceOf(ConfigException.class)
				.hasMessageContaining("must be non-empty");
	}

	@Test
	void testToString() {
		UrlValidator.Builder builder = UrlValidator.builder();

		String msg = builder.build().toString();
		assertThat(msg).as("No scheme, no host").isEqualTo("A valid URL.");

		msg = builder.schemes("ftp", "https").build().toString();
		assertThat(msg).as("Two schemes").isEqualTo("A valid URL. URL scheme must be one of: 'ftp', 'https'.");

		msg = builder.hosts("example.net").build().toString();
		assertThat(msg).as("Two schemes, one host")
				.isEqualTo("A valid URL. URL scheme must be one of: 'ftp', 'https'. URL host must be: 'example.net'.");

		msg = builder.hosts("example.com").build().toString();
		assertThat(msg).as("Two schemes, two hosts").isEqualTo(
				"A valid URL. URL scheme must be one of: 'ftp', 'https'. URL host must be one of: 'example.com', 'example.net'.");

		msg = UrlValidator.builder().hosts("example.net", "example.com").build().toString();
		assertThat(msg).as("Two hosts")
				.isEqualTo("A valid URL. URL host must be one of: 'example.com', 'example.net'.");

		msg = UrlValidator.builder().hosts("example.com").build().toString();
		assertThat(msg).as("No scheme, One hosts").isEqualTo("A valid URL. URL host must be: 'example.com'.");

	}
}
