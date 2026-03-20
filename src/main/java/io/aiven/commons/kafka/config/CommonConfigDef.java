/*
 * Copyright 2026 Aiven Oy
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

package io.aiven.commons.kafka.config;

import io.aiven.commons.kafka.config.fragment.BackoffPolicyFragment;
import io.aiven.commons.kafka.config.fragment.CommonConfigFragment;
import io.aiven.commons.kafka.config.fragment.FragmentDataAccess;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The ConfigDef for the CommonConfig class.
 */
public class CommonConfigDef extends ConfigDef {
	/**
	 * Constructor .
	 */
	public CommonConfigDef() {
		super();
		BackoffPolicyFragment.update(this);
		CommonConfigFragment.update(this);
	}

	/**
	 * Gathers in depth, multi argument configuration issues. This method should be
	 * overridden when the Fragments added to the config have validation rules that
	 * required inspection of multiple properties.
	 * <p>
	 * Overriding methods should call the parent method to update the map and then
	 * add error messages to the {@link ConfigValue} associated with property name
	 * that is in error.
	 * </p>
	 *
	 * @param valueMap
	 *            the map of configuration names to values.
	 * @return the updated map.
	 */
	protected Map<String, ConfigValue> multiValidate(final Map<String, ConfigValue> valueMap) {
		new BackoffPolicyFragment(FragmentDataAccess.from(valueMap)).validate(valueMap);
		return valueMap;
	}

	@SuppressWarnings("PMD.AvoidCatchingGenericException")
	@Override
	public final List<ConfigValue> validate(final Map<String, String> props) {
		final Map<String, ConfigValue> valueMap = validateAll(props);

		try {
			return new ArrayList<>(multiValidate(valueMap).values());
		} catch (RuntimeException e) {
			// any exceptions thrown in the above block are accounted for in the
			// super.validate(props) call.
			return new ArrayList<>(valueMap.values());
		}
	}
}
