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
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.types.Password;

import java.util.Objects;

/**
 * Validator to ensure that a password is set to either @{code null} or a
 * non-empty string.
 */
public class NonEmptyPassword implements ConfigDef.Validator {

	/**
	 * The instance of the NonEmptyPassword validator.
	 */
	public static NonEmptyPassword INSTANCE = new NonEmptyPassword();

	private NonEmptyPassword() {
	}

	@Override
	public void ensureValid(final String name, final Object value) {
		if (Objects.isNull(value)) {
			return;
		}
		final var pwd = (Password) value;
		if (pwd.value() == null || pwd.value().isBlank()) {
			throw new ConfigException(name, pwd, "Password must be non-empty");
		}
	}

	@Override
	public String toString() {
		return "A password string.  May be null.  May not be an empty string.";
	}
}