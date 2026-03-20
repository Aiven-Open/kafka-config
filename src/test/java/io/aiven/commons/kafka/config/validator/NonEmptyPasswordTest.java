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
import org.apache.kafka.common.config.types.Password;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NonEmptyPasswordTest {

	@Test
	void testEnsureValid() {
		assertThatNoException().isThrownBy(() -> NonEmptyPassword.INSTANCE.ensureValid("null", null));
		assertThatThrownBy(() -> NonEmptyPassword.INSTANCE.ensureValid("null value", new Password(null)),
				"null password value").isInstanceOf(ConfigException.class)
				.hasMessageContaining("Password must be non-empty");
		assertThatThrownBy(() -> NonEmptyPassword.INSTANCE.ensureValid("empty value", new Password("")),
				"empty password value").isInstanceOf(ConfigException.class)
				.hasMessageContaining("Password must be non-empty");
		assertThatNoException()
				.isThrownBy(() -> NonEmptyPassword.INSTANCE.ensureValid("good", new Password("p@ssw0rd")));
	}
}
