
package io.aiven.commons.kafka.config.docs;
/*
         Copyright 2026 Aiven Oy and project contributors

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
import io.aiven.commons.kafka.config.DeprecatedInfo;
import io.aiven.commons.kafka.config.ExtendedConfigKey;
import org.apache.kafka.common.config.ConfigDef;

/**
 * Defines the variables that are available for the Velocity template to access
 * a {@link ConfigDef.ConfigKey} object.
 *
 * @see <a href="https://velocity.apache.org/">Apache Velocity</a>
 */
public class ExtendedConfigKeyBean extends ConfigKeyBean {
	/** The key */
	private final boolean extendedFlag;

	/**
	 * Constructor.
	 *
	 * @param key
	 *            the Key to wrap.
	 */
	public ExtendedConfigKeyBean(final ConfigDef.ConfigKey key) {
		super(key);
		this.extendedFlag = (key instanceof ExtendedConfigKey);
	}

	/**
	 * Gets ths extendedFlag value.
	 *
	 * @return the extendedFlag value
	 */
	public final boolean isExtendedFlag() {
		return extendedFlag;
	}

	/**
	 * Gets the {@link ExtendedConfigKey} if the extendedFlag is set, otherwise
	 * returns null.
	 *
	 * @return EndendedConfigKey if the extendedFlag is set, otherwise returns null.
	 */
	private ExtendedConfigKey asExtended() {
		return extendedFlag ? (ExtendedConfigKey) key : null;
	}

	/**
	 * Gets the since value.
	 *
	 * @return the since value or {@code null} if not an extended key.
	 */
	public final String since() {
		return extendedFlag ? asExtended().since.toString() : null;
	}

	/**
	 * Gets the deprecated flag.
	 *
	 * @return the deprecated flag.
	 */
	@SuppressWarnings("unused")
	public final boolean isDeprecated() {
		return extendedFlag && asExtended().isDeprecated();
	}

	/**
	 * Gets the DeprecatedInfo or {@code null} if not an extended key.
	 *
	 * @return the DeprecatedInfo or {@code null} if not an extended key.
	 */
	public final DeprecatedInfo deprecated() {
		return extendedFlag ? asExtended().deprecated : null;
	}
}
