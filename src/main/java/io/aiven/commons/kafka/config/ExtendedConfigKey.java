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

package io.aiven.commons.kafka.config;

import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * An extended {@link ConfigDef.ConfigKey} that added deprecation information,
 * and since data.
 */
public class ExtendedConfigKey extends ConfigDef.ConfigKey {

	/** The deprecation information. May be {@code null}. */
	public final DeprecatedInfo deprecated;

	/** The version in which this attribute was added. May be {@code null}. */
	public final SinceInfo since;

	/**
	 * Creates an extended config key from a ConfigKey. If the key is already an
	 * ExtendedConfigKey it is simply returned.
	 * 
	 * @param key
	 *            The key to construct the ExtendedConfigKey from.
	 * @return An Extended config key.
	 */
	public static ExtendedConfigKey create(final ConfigDef.ConfigKey key) {
		if (key instanceof ExtendedConfigKey) {
			return (ExtendedConfigKey) key;
		}
		return Builder.unbuild(key).build();
	}

	/**
	 * The constructor called by the builder.
	 *
	 * @param builder
	 *            the builder.
	 */
	private ExtendedConfigKey(final Builder<?> builder) {
		super(builder.name, builder.type, builder.defaultValue, builder.validator, builder.importance,
				builder.generateDocumentation(), builder.group, builder.orderInGroup, builder.width,
				builder.displayName, builder.getDependents(), builder.recommender, builder.internalConfig);
		this.deprecated = builder.deprecated;
		this.since = builder.since;
	}

	/**
	 * Gets the deprecation message.
	 *
	 * @return If the deprecation was set, returns
	 *         {@link DeprecatedInfo#getDescription()} otherwise return an empty
	 *         string.
	 */
	public final String getDeprecationMessage() {
		return isDeprecated() ? deprecated.toString() : "";
	}

	/**
	 * Gets the value of since data element.
	 *
	 * @return the value of since if it was set, an empty string otherwise.
	 */
	public final String getSince() {
		return since == null ? "" : since.toString();
	}

	/**
	 * Sets since override. The builder must define {@code version} at a minimum.
	 * Any undefined values will be blank.
	 * 
	 * @param builder
	 *            the SinceInfo.Builder to define the override.
	 */
	public final void overrideSince(final SinceInfo.Builder builder) {
		if (since != null) {
			since.setOverride(builder);
		}
		if (deprecated != null) {
			deprecated.overrideSince(builder);
		}
	}

	/**
	 * Sets the override from the Builder associated with the matching OverrideRange
	 * in the overrideMap. If multiple ranges match the last one in the map will be
	 * applied.
	 *
	 * @param overrideMap
	 *            the map of OverrideRanges and builders to apply.
	 */
	public void overrideSince(final Map<SinceInfo.OverrideRange, SinceInfo.Data> overrideMap) {
		if (since != null) {
			since.setOverride(overrideMap);
		}
		if (deprecated != null) {
			deprecated.overrideSince(overrideMap);
		}
	}

	/**
	 * Get the deprecated flag.
	 *
	 * @return {@code true} if this key is deprecated, {@code false} otherwise.
	 */
	public final boolean isDeprecated() {
		return deprecated != null;
	}

	/**
	 * Creates a builder for the ExtendedConfigKey.
	 *
	 * @param name
	 *            the name for the resulting key.
	 * @return the builder.
	 * @param <T>
	 *            the type of the returned builder.
	 */
	public static <T extends Builder<?>> Builder<T> builder(final String name) {
		return new Builder<>(name);
	}

	/**
	 * The builder for an ExtendedConfigKey.
	 *
	 * @param <T>
	 *            the type of the file ConfigKey instance.
	 */
	public static class Builder<T extends Builder<?>> extends ConfigKeyBuilder<T> {
		/** The deprecated info. */
		private DeprecatedInfo deprecated;

		/** The since value. */
		private SinceInfo since;

		/**
		 * The builder.
		 *
		 * @param name
		 *            the name of the final ConfigKey
		 */
		protected Builder(final String name) {
			super(name);
		}

		/**
		 * unbuilds a key. Creates an ExtendedConfigKey.Builder from the key.
		 * 
		 * @param key
		 *            the key to unbuild.
		 * @return An extended key builder
		 * @param <T>
		 *            the type of the builder.
		 */
		public static <T extends Builder<?>> Builder<T> unbuild(final ConfigDef.ConfigKey key) {
			Builder<T> result = new Builder<T>(key.name);
			result.type(key.type).defaultValue(key.defaultValue).validator(key.validator).importance(key.importance)
					.documentation(key.documentation).group(key.group).orderInGroup(key.orderInGroup).width(key.width)
					.displayName(key.displayName).recommender(key.recommender).internalConfig(key.internalConfig)
					.dependents(key.dependents);
			if (key instanceof ExtendedConfigKey) {
				ExtendedConfigKey extendedKey = (ExtendedConfigKey) key;
				result.deprecatedInfo(extendedKey.deprecated).since(extendedKey.since);
			}
			return result;
		}

		@Override
		public ExtendedConfigKey build() {
			return new ExtendedConfigKey(this);
		}

		@Override
		protected String generateDocumentation() {
			StringBuilder result = new StringBuilder();
			if (deprecated != null) {
				result.append(deprecated.formatted(displayName)).append(". ");
			}
			result.append(documentation);
			return result.toString();
		}

		/**
		 * Sets the deprecation info.
		 *
		 * @param deprecatedInfoBuilder
		 *            the builder for the DeprecatedInfo
		 * @return this
		 */
		public final T deprecatedInfo(final DeprecatedInfo.Builder deprecatedInfoBuilder) {
			return deprecatedInfo(deprecatedInfoBuilder.build());
		}

		/**
		 * Sets the deprecation info.
		 *
		 * @param deprecatedInfo
		 *            the Deprecated info to use.
		 * @return this
		 */
		public final T deprecatedInfo(final DeprecatedInfo deprecatedInfo) {
			this.deprecated = deprecatedInfo;
			return self();
		}

		/**
		 * Sets the since value.
		 *
		 * @param since
		 *            the since value.
		 * @return this.
		 */
		public final T since(final SinceInfo since) {
			this.since = since;
			return self();
		}
	}
}
