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

import org.apache.kafka.common.config.ConfigDef;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A validator that is gated by a predicate. The predicate tests the value
 * object. If the predicate returns {@code true} then the associated validator
 * is executed. Othewise the validation passes.
 */
public class PredicateGatedValidator implements ConfigDef.Validator {
	private final Predicate<Object> predicate;
	private final ConfigDef.Validator validator;

	/**
	 * Constructor.
	 * 
	 * @param predicate
	 *            the predicate to gate access to the {@code validator}
	 * @param validator
	 *            the validator to execute if the predicate is {@code true}.
	 */
	public PredicateGatedValidator(final Predicate<Object> predicate, final ConfigDef.Validator validator) {
		this.validator = Objects.requireNonNull(validator);
		this.predicate = Objects.requireNonNull(predicate);
	}

	@Override
	public void ensureValid(final String name, final Object value) {
		if (predicate.test(value)) {
			validator.ensureValid(name, value);
		}
	}

	@Override
	public String toString() {
		return validator.toString();
	}
}
