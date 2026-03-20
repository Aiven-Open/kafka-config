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
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.ConfigValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The base configuration or all connectors.
 */
public class CommonConfig extends AbstractConfig {

	private final BackoffPolicyFragment backoffPolicyFragment;
	private final CommonConfigFragment commonConfigFragment;

	/**
	 * Checks the configuration definition for errors. If any errors are found an
	 * exception is thrown. Due to the point at which this is called there are no
	 * {@link ConfigDef.ConfigKey#validator} errors to worry about.
	 *
	 * @param definition
	 *            the ConfigDefinition to validate.
	 * @param props
	 *            The map of parameter name to values to verify with.
	 */
	private void doVerification(final CommonConfigDef definition, final Map<String, String> props) {
		Map<String, ConfigValue> configValueMap = definition.validateAll(props);

		// ensure that all the values are accounted for, not just those in the
		// properties
		this.values().forEach((k, v) -> {
			if (configValueMap.get(k) != null) {
				configValueMap.get(k).value(v);
			}
		});

		// process the detailed validation.
		definition.multiValidate(configValueMap);

		// if there are any reported errors produce a detailed configuration exception.
		final List<ConfigValue> errorConfigs = configValueMap.values().stream()
				.filter(configValue -> !configValue.errorMessages().isEmpty()).toList();
		if (!errorConfigs.isEmpty()) {
			final String msg = errorConfigs.stream().flatMap(configValue -> configValue.errorMessages().stream())
					.collect(Collectors.joining("\n"));
			throw new ConfigException("There are errors in the configuration:\n" + msg);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param definition
	 *            CommonConfigDef based definition to use.
	 * @param originals
	 *            the original property name to value map.
	 */
	public CommonConfig(final CommonConfigDef definition, final Map<String, String> originals) {
		super(definition, originals);
		doVerification(definition, originals);
		final FragmentDataAccess dataAccess = FragmentDataAccess.from(this);
		commonConfigFragment = new CommonConfigFragment(dataAccess);
		backoffPolicyFragment = new BackoffPolicyFragment(dataAccess);
	}

	/**
	 * Avoid Finalizer attack
	 */
	@Override
	@SuppressWarnings("PMD.EmptyFinalizer")
	protected final void finalize() {
		// Do nothing
	}

	@Override
	final protected Map<String, Object> postProcessParsedConfig(Map<String, Object> parsedValues) {
		ChangeTrackingMap result = new ChangeTrackingMap(parsedValues);
		fragmentPostProcess(result);
		return result.override;
	}

	/**
	 * Allows implementations to modify the values of the map from the fragments.
	 * Default implementation does nothing.
	 * 
	 * @param map
	 *            the map to make changes in.
	 */
	protected void fragmentPostProcess(ChangeTrackingMap map) {
		// does nothing.
	}

	/**
	 * Gets the Kafka retry backoff time in MS.
	 *
	 * @return The Kafka retry backoff time in MS.
	 */
	public Long getKafkaRetryBackoffMs() {
		return backoffPolicyFragment.getKafkaRetryBackoffMs();
	}

	/**
	 *
	 * Get the maximum number of tasks that should be run by this connector
	 * configuration Max Tasks is set within the Kafka Connect framework and so is
	 * retrieved slightly differently in ConnectorConfig.java
	 *
	 * @return The maximum number of tasks that should be run by this connector
	 *         configuration
	 */
	public int getMaxTasks() {
		return commonConfigFragment.getMaxTasks();
	}
	/**
	 * Get the task id for this configuration
	 *
	 * @return The task id for this configuration
	 */
	public int getTaskId() {
		return commonConfigFragment.getTaskId();
	}

	/**
	 * A map of values that allows overrides.
	 */
	public static class ChangeTrackingMap {
		private final Map<String, Object> baseMap;
		private final Map<String, Object> override;

		/**
		 * Constructor.
		 * 
		 * @param baseMap
		 *            the original map.
		 */
		public ChangeTrackingMap(Map<String, Object> baseMap) {
			this.baseMap = baseMap;
			this.override = new HashMap<>();
		}

		/**
		 * Sets the override for a key. Passing {@code null} removes any override, any
		 * other values sets the override.
		 * 
		 * @param key
		 *            the key to override.
		 * @param value
		 *            the value to set the key to.
		 */
		public void override(String key, Object value) {
			if (value == null) {
				override.remove(key);
			} else {
				override.put(key, value);
			}
		}

		/**
		 * Gets the current value of the key. This is the last override or the current
		 * value if no override is present.
		 * 
		 * @param key
		 *            the key to get the value for.
		 * @return the current value.
		 */
		public Object get(String key) {
			Object result = override.get(key);
			return result == null ? baseMap.get(key) : result;
		}

	}
}
