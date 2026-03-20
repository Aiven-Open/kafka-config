/*
 * Copyright 2025 Aiven Oy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.aiven.commons.kafka.config.validator;

import io.aiven.commons.collections.TimeScale;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

/**
 * A validator for time scales.
 */
public class TimeScaleValidator implements ConfigDef.Validator {
	private final Number min;
	private final TimeScale minScale;
	private final Number max;
	private final TimeScale maxScale;

	/**
	 * Constructor.
	 * 
	 * @param minMilliseconds
	 *            the minimum number of milliseconds.
	 * @param maxMilliseconds
	 *            the maximum number of milliseconds.
	 */
	TimeScaleValidator(final Number minMilliseconds, final Number maxMilliseconds) {
		this.min = minMilliseconds;
		this.minScale = minMilliseconds == null ? TimeScale.MILLISECONDS : TimeScale.scaleOf(min.longValue());
		this.max = maxMilliseconds;
		this.maxScale = maxMilliseconds == null ? TimeScale.MILLISECONDS : TimeScale.scaleOf(max.longValue());
	}

	/**
	 * A numeric range that checks only the lower bound.
	 *
	 * @param min
	 *            The minimum acceptable value
	 * @return A times scale validator that requires a value of at least
	 *         {@code min}.
	 */
	public static TimeScaleValidator atLeast(final Number min) {
		return new TimeScaleValidator(min, null);
	}

	/**
	 * A numeric range that checks both the upper and lower bound.
	 *
	 * @param min
	 *            The minimum acceptable value
	 * @param max
	 *            The maximum acceptable value
	 * @return A scale validator that requires a value between the {@code min} and
	 *         {@code max} numbers inclusive.
	 */
	public static TimeScaleValidator between(final Number min, final Number max) {
		return new TimeScaleValidator(min, max);
	}

	@Override
	public void ensureValid(final String name, final Object value) {
		if (value == null) {
			throw new ConfigException(name, null, "Value must be non-null");
		}
		final Number number = (Number) value;
		if (min != null && number.longValue() < min.longValue()) {
			throw new ConfigException(name, value, "Value must be at least " + minScale.displayValue(min.longValue()));
		}
		if (max != null && number.doubleValue() > max.doubleValue()) {
			throw new ConfigException(name, value,
					"Value must be no more than " + maxScale.displayValue(max.longValue()));
		}
	}

	@Override
	public String toString() {
		if (min == null) {
			return "[...," + maxScale.format(max.longValue()) + "]";
		} else if (max == null) {
			return "[" + minScale.format(min.longValue()) + ",...]";
		} else {
			return "[" + minScale.format(min.longValue()) + ",...," + maxScale.format(max.longValue()) + "]";
		}
	}
}
