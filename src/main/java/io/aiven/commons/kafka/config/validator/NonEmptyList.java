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

import java.util.List;
import java.util.Objects;

/**
 * Validator to ensure that a list is either @{code null} or non-empty.
 */
public class NonEmptyList implements ConfigDef.Validator {

	/**
	 * The instance of the NonEmptyPassword validator.
	 */
	public static NonEmptyList INSTANCE = new NonEmptyList();

	private NonEmptyList() {
	}

	@Override
	public void ensureValid(final String name, final Object value) {
		if (Objects.isNull(value)) {
			return;
		}
		if (((List<?>) value).isEmpty()) {
			throw new ConfigException(name, "A non-empty list");
		}
	}

	@Override
	public String toString() {
		return "A non-empty list";
	}
}