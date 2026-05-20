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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.ConfigValue;

/** The ConfigDef for the CommonConfig class. */
public class CommonConfigDef extends ConfigDef {
  /** Constructor . */
  public CommonConfigDef() {
    super();
    BackoffPolicyFragment.update(this);
    CommonConfigFragment.update(this);
  }

  /**
   * Gathers in depth, multi argument configuration issues. This method should be overridden when
   * the Fragments added to the config have validation rules that required inspection of multiple
   * properties.
   *
   * <p>Overriding methods should call the parent method to update the map and then add error
   * messages to the {@link ConfigValue} associated with property name that is in error.
   *
   * @param valueMap the map of configuration names to values.
   * @return the updated map.
   */
  protected Map<String, ConfigValue> multiValidate(final Map<String, ConfigValue> valueMap) {
    new BackoffPolicyFragment(FragmentDataAccess.from(valueMap)).validate(valueMap);
    return valueMap;
  }

  /**
   * Allows users to toggle certain config on and off in the documentation
   *
   * @param key The config key that you want to toggle hidden or unhidden in the documentation
   * @param state true hides the key from documentation false shows the config in the documentation
   */
  protected void hide(String key, boolean state) {
    ExtendedConfigKey newKey =
        ExtendedConfigKey.Builder.unbuild(configKeys().get(key)).internalConfig(state).build();
    configKeys().put(newKey.name, newKey);
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

  /**
   * Copies a key from the specified ConfigDef to this ConfigDef.
   *
   * @param configDef the ConfigDef to copy the key from.
   * @param keyName the name of the key to copy.
   */
  public void addKey(ConfigDef configDef, String keyName) {
    ConfigDef.ConfigKey key = configDef.configKeys().get(keyName);
    if (key == null) {
      throw new ConfigException(
          "Can not find key %s in %s", keyName, configDef.getClass().getName());
    }
    configKeys().put(key.name, key);
  }
}
