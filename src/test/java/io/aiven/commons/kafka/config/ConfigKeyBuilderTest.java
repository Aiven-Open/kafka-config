package io.aiven.commons.kafka.config;
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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigKeyBuilderTest {

	private void assertStandardValues(ConfigDef.ConfigKey key, String name) {
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testDefault() {
		String name = "default";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testDisplayName() {
		String name = "displayName";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).displayName("displayNameIsSet").build();
		assertThat(key.displayName).isEqualTo("displayNameIsSet");
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testDocumentation() {
		String name = "documentation";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).documentation("documentation").build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEqualTo("documentation");
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testGroup() {
		String name = "group";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).group("group").build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isEqualTo("group");
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testDependents() {
		String name = "dependents";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).dependents("one", "two").build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).containsExactly("one", "two");
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();

		key = new ConfigKeyBuilder<>(name).dependent("one").dependent("two").build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).containsExactly("one", "two");
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();

		key = new ConfigKeyBuilder<>(name).dependents(Arrays.asList("one", "two")).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).containsExactly("one", "two");
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testImportance() {
		String name = "importance";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).importance(ConfigDef.Importance.HIGH).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.HIGH);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testInternalConfig() {
		String name = "internalConfig";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).internalConfig(true).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isTrue();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();

		key = new ConfigKeyBuilder<>(name).internalConfig(false).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testOrderInGroup() {
		String name = "orderInGroup";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).orderInGroup(5).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(5);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testRecommender() {
		String name = "recommender";
		ConfigDef.Recommender recommender = new ConfigDef.Recommender() {

			@Override
			public List<Object> validValues(String s, Map<String, Object> map) {
				return List.of();
			}

			@Override
			public boolean visible(String s, Map<String, Object> map) {
				return false;
			}
		};
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).recommender(recommender).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isEqualTo(recommender);
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testType() {
		String name = "type";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).type(ConfigDef.Type.BOOLEAN).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.BOOLEAN);
		assertThat(key.defaultValue).isNull();
		assertThat(key.validator).isNull();
	}

	@Test
	void testDefaultValue() {
		String name = "defaultValue";
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).defaultValue("SomeValue").build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isEqualTo("SomeValue");
		assertThat(key.validator).isNull();
	}

	@Test
	void testValidator() {
		String name = "validator";
		ConfigDef.Validator validator = ConfigDef.CaseInsensitiveValidString.in("the test");
		ConfigDef.ConfigKey key = new ConfigKeyBuilder<>(name).validator(validator)
				.defaultValue(ConfigDef.NO_DEFAULT_VALUE).build();
		assertThat(key.displayName).isEqualTo(name);
		assertThat(key.documentation).isEmpty();
		assertThat(key.group).isNull();
		assertThat(key.alternativeString).isNull();
		assertThat(key.dependents).isEmpty();
		assertThat(key.importance).isEqualTo(ConfigDef.Importance.MEDIUM);
		assertThat(key.internalConfig).isFalse();
		assertThat(key.name).isEqualTo(name);
		assertThat(key.orderInGroup).isEqualTo(-1);
		assertThat(key.recommender).isNull();
		assertThat(key.type).isEqualTo(ConfigDef.Type.STRING);
		assertThat(key.defaultValue).isEqualTo(ConfigDef.NO_DEFAULT_VALUE);
		assertThat(key.validator).isEqualTo(validator);
	}
}
