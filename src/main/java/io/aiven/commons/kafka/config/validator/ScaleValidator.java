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
import io.aiven.commons.collections.Scale;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.List;

/**
 * Validates input is within ranges for specific scale values.
 */
public class ScaleValidator implements ConfigDef.Validator {
	/**
	 * The minimum value in bytes
	 */
	private final Number minBytes;
	/**
	 * The scale of the minimum value
	 */
	private final Scale minScale;
	/**
	 * The maximum value in bytes.
	 */
	private final Number maxBytes;
	/**
	 * The scale of the maximum value.
	 */
	private final Scale maxScale;

	/**
	 * constructor accessed from static methods.
	 * 
	 * @param minBytes
	 *            the minimum number of bytes.
	 * @param maxBytes
	 *            the maximum number of bytes.
	 * @param possibleScales
	 *            the potential scales for display.
	 */
	ScaleValidator(final Number minBytes, final Number maxBytes, final List<Scale> possibleScales) {
		this.minBytes = minBytes;
		this.minScale = minBytes == null ? Scale.B : Scale.scaleOf(minBytes.longValue(), possibleScales);
		this.maxBytes = maxBytes;
		this.maxScale = maxBytes == null ? Scale.B : Scale.scaleOf(maxBytes.longValue(), possibleScales);
	}

	/**
	 * A scale range that checks only the lower bound
	 *
	 * @param min
	 *            The minimum acceptable number of bytes
	 * @param possibleScales
	 *            The list of potential scale values.
	 * @return A scale validator that requires at least the {@code min} number.
	 */
	public static ScaleValidator atLeast(final Number min, final List<Scale> possibleScales) {
		return new ScaleValidator(min, null, possibleScales);
	}

	/**
	 * A scale range that checks both the upper and lower bound.
	 * 
	 * @param min
	 *            the minimum acceptable number of bytes.
	 * @param max
	 *            the maximum acceptable number of bytes.
	 * @param possibleScales
	 *            The list of potential scale values.
	 * @return A scale validator that requires a value between the {@code min} and
	 *         {@code max} numbers inclusive.
	 */
	public static ScaleValidator between(final Number min, final Number max, final List<Scale> possibleScales) {
		return new ScaleValidator(min, max, possibleScales);
	}

	@Override
	public void ensureValid(final String name, final Object value) {
		if (value == null)
			throw new ConfigException(name, null, "Value must be non-null");
		final Number number = (Number) value;
		if (minBytes != null && number.longValue() < minBytes.longValue())
			throw new ConfigException(name, value,
					"Value must be at least " + minScale.displayValue(minBytes.longValue()));
		if (maxBytes != null && number.doubleValue() > maxBytes.doubleValue())
			throw new ConfigException(name, value,
					"Value must be no more than " + maxScale.displayValue(maxBytes.longValue()));
	}

	@Override
	public String toString() {
		if (minBytes == null)
			return "[...," + maxScale.format(maxBytes.longValue()) + "]";
		else if (maxBytes == null)
			return "[" + minScale.format(minBytes.longValue()) + ",...]";
		else
			return "[" + minScale.format(minBytes.longValue()) + ",...," + maxScale.format(maxBytes.longValue()) + "]";
	}
}
