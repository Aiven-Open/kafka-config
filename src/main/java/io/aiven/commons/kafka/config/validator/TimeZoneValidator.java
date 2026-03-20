/*
         Copyright 2020-2025 Aiven Oy and project contributors

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

package io.aiven.commons.kafka.config.validator;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.time.ZoneId;

/**
 * A validator that ensures that a timezone string is valid. Any value specified
 * in {@link ZoneId} is accepted.
 */
public class TimeZoneValidator implements ConfigDef.Validator {
	/**
	 * A TimeZone validator that does not accept null values.
	 */
	public static final TimeZoneValidator INSTANCE = new TimeZoneValidator();

	/**
	 * Private constructor.
	 */
	private TimeZoneValidator() {

	}

	@Override
	public void ensureValid(final String name, final Object value) {
		try {
			if (value == null) {
				return;
			}
			ZoneId.of(value.toString());
		} catch (final Exception e) { // NOPMD AvoidCatchingGenericException
			throw new ConfigException(name, value, e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "Any valid Time zone string.";
	}
}
