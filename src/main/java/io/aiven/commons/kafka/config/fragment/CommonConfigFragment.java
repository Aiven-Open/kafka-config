/*
 * Copyright 2024 Aiven Oy
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

package io.aiven.commons.kafka.config.fragment;

import static org.apache.kafka.common.config.ConfigDef.Range.atLeast;

import io.aiven.commons.kafka.config.ConverterType;
import io.aiven.commons.kafka.config.ExtendedConfigKey;
import io.aiven.commons.kafka.config.SinceInfo;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.runtime.ConnectorConfig;

/** The common configuration fragment. */
public class CommonConfigFragment extends ConfigFragment {
  /** The task id configuration option */
  private static final String TASK_ID = "task.id";

  /**
   * Gets a setter for this fragment.
   *
   * @param data the data to modify.
   * @return The setter.
   */
  public static Setter setter(final Map<String, String> data) {
    return new Setter(data);
  }

  /**
   * Update the ConfigDef with the values from the fragment.
   *
   * @param configDef the configuraiton def to update.
   * @return the updated configuration def.
   */
  @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
  public static ConfigDef update(final ConfigDef configDef) {
    int orderInGroup = 0;
    final String commonGroup = "Common";
    SinceInfo tasksMaxSince =
        SinceInfo.builder()
            .groupId("org.apache.kafka")
            .artifactId("kafka")
            .version("0.9.0.0")
            .build();
    // We want the kafka version to display for the tasks.max property, these are
    // not Hardcoded IPs"
    SinceInfo.Builder overrideBuilder = SinceInfo.builder().version("Kafka 0.9.0.0");
    tasksMaxSince.setOverride(overrideBuilder);
    SinceInfo.Builder siBuilder =
        SinceInfo.builder().groupId("io.aiven.commons").artifactId("kafka-config").version("1.0.0");

    for (var configKey : ConnectorConfig.configDef().configKeys().values()) {
      if (configKey.hasDefault() && !configDef.configKeys().containsValue(configKey)) {
        configDef.define(ExtendedConfigKey.create(configKey));
      }
    }

    return configDef.define(
        ExtendedConfigKey.builder(TASK_ID)
            .type(ConfigDef.Type.INT)
            .defaultValue(1)
            .validator(atLeast(0))
            .importance(ConfigDef.Importance.HIGH)
            .group(commonGroup)
            .orderInGroup(++orderInGroup)
            .width(ConfigDef.Width.SHORT)
            .internalConfig(true)
            .documentation("The task ID that this connector is working with.")
            .since(siBuilder.version("1.0.0").build())
            .build());
  }

  /**
   * Create a fragment instance from an AbstractConfig.
   *
   * @param dataAccess the FragmentDataAccess to retrieve data from.
   */
  public CommonConfigFragment(final FragmentDataAccess dataAccess) {
    super(dataAccess);
  }

  /**
   * Get the task Id.
   *
   * @return the task Id.
   */
  public Integer getTaskId() {
    return getInt(TASK_ID);
  }

  /**
   * Get the maximum number of tasks.
   *
   * @return the maximum number of tasks.
   */
  public Integer getMaxTasks() {
    return getInt(ConnectorConfig.TASKS_MAX_CONFIG);
  }

  /**
   * the converter used for the key portion of the kafka event
   *
   * @return the converter used for the key portion of the kafka event
   */
  public ConverterType getKeyConverter() {
    return ConverterType.forClassName(getClass(ConnectorConfig.KEY_CONVERTER_CLASS_CONFIG));
  }

  /**
   * the converter used for the value portion of the kafka event
   *
   * @return the converter used for the value portion of the kafka event
   */
  public ConverterType getValueConverter() {
    return ConverterType.forClassName(getClass(ConnectorConfig.VALUE_CONVERTER_CLASS_CONFIG));
  }

  /** Setter to programmatically set values in the configuraiotn. */
  public static class Setter extends AbstractFragmentSetter<Setter> {
    /**
     * Creates the setter.
     *
     * @param data the map of data to update.
     */
    private Setter(final Map<String, String> data) {
      super(data);
    }

    /**
     * Sets the task ID value.
     *
     * @param taskId the task Id value.
     * @return this
     */
    public Setter taskId(final int taskId) {
      return setValue(TASK_ID, taskId);
    }

    /**
     * Sets the max tasks value.
     *
     * @param maxTasks the maximum number of tasks for this connector to run.
     * @return this
     */
    public Setter maxTasks(final int maxTasks) {
      return setValue(ConnectorConfig.TASKS_MAX_CONFIG, maxTasks);
    }

    /**
     * Set the key Converter
     *
     * @param keyConverter the converter to use to convert the key converter
     * @return this
     */
    public Setter keyConverter(final String keyConverter) {
      return setValue(ConnectorConfig.KEY_CONVERTER_CLASS_CONFIG, keyConverter);
    }

    /**
     * Set the value converter
     *
     * @param valueConverter the converter to use to convert value data
     * @return this
     */
    public Setter valueConverter(final String valueConverter) {
      return setValue(ConnectorConfig.VALUE_CONVERTER_CLASS_CONFIG, valueConverter);
    }
  }
}
