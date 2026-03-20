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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.kafka.common.config.ConfigDef;

/**
 * Builds Config Keys.
 *
 * @param <T>
 *            The Class of the ConfigKeyBuilder.
 */
public class ConfigKeyBuilder<T extends ConfigKeyBuilder<?>> {
	/** The name for the key. */
	protected final String name;

	/** The type of the key. Defaults to {@link ConfigDef.Type#STRING}. */
	protected ConfigDef.Type type = ConfigDef.Type.STRING;

	/**
	 * The default value for the config key. Defaults to {@code null}.
	 */
	protected Object defaultValue = null;

	/** The validator (if any) for this key. May be {@code null}. */
	protected ConfigDef.Validator validator;

	/**
	 * The importance of this key. Defaults to {@link ConfigDef.Importance#MEDIUM}
	 */
	protected ConfigDef.Importance importance = ConfigDef.Importance.MEDIUM;

	/** The documentation for this key. Defaults to an empty string. */
	protected String documentation = "";

	/** The group key is a part of. Defaults to {@code null} (no group). */
	protected String group;

	/** The order within the group. Defaults to -1 (no order specified). */
	protected int orderInGroup = -1;

	/**
	 * The width of the input field for this config. Defaults to
	 * {@link ConfigDef.Width#NONE}
	 */
	protected ConfigDef.Width width = ConfigDef.Width.NONE;

	/** The display name for the key. If not set, defaults to the {@link #name}. */
	protected String displayName;

	/**
	 * The set of names for {@link ConfigDef.ConfigKey}s that are dependent upon
	 * this key.
	 */
	protected Set<String> dependents;

	/** The recommender for this key. Defaults to {@code null} (no recommender). */
	protected ConfigDef.Recommender recommender;

	/**
	 * The internal config key for this builder. If set to {@code true} the
	 * configuration will be an internal config. Defaults to {@code false}
	 */
	protected boolean internalConfig;

	/**
	 * Creates an instance of a ConfigKeyBuilder.
	 *
	 * @param name
	 *            the name of the key being built.
	 */
	public ConfigKeyBuilder(String name) {
		this.name = name;
		this.displayName = name;
	}

	/**
	 * Builds a ConfigKey from the specified values.
	 *
	 * @return a ConfigKey from the specified values.
	 */
	public ConfigDef.ConfigKey build() {
		return new ConfigDef.ConfigKey(name, type, defaultValue, validator, importance, generateDocumentation(), group,
				orderInGroup, width, displayName, getDependents(), recommender, internalConfig);
	}

	/**
	 * Generates the documentation. This is an extension point for ConfigKey
	 * builders to modify the documentation. For example adding deprecation
	 * information, or interactions with other keys in the configuration.
	 * 
	 * @return the documentation for the component.
	 */
	protected String generateDocumentation() {
		return documentation;
	}

	/**
	 * Returns this cast to the actual builder type.
	 *
	 * @return this cast to the actual builder type.
	 */
	@SuppressWarnings("unchecked")
	protected T self() {
		return (T) this;
	}

	/**
	 * Sets the data type.
	 *
	 * @param type
	 *            the data type.
	 * @return this
	 * @see #type
	 */
	public final T type(final ConfigDef.Type type) {
		this.type = type;
		return self();
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue
	 *            the default value.
	 * @return this.
	 * @see #defaultValue
	 */
	public final T defaultValue(final Object defaultValue) {
		this.defaultValue = defaultValue;
		return self();
	}

	/**
	 * Sets the validator.
	 *
	 * @param validator
	 *            the validator.
	 * @return this
	 * @see #validator
	 */
	public final T validator(final ConfigDef.Validator validator) {
		this.validator = validator;
		return self();
	}

	/**
	 * Sets the importance.
	 *
	 * @param importance
	 *            the importance.
	 * @return this
	 * @see #importance
	 */
	public final T importance(final ConfigDef.Importance importance) {
		this.importance = importance;
		return self();
	}

	/**
	 * Sets the documentation.
	 *
	 * @param documentation
	 *            the documentation.
	 * @return this
	 * @see #documentation
	 */
	public final T documentation(final String documentation) {
		this.documentation = documentation;
		return self();
	}

	/**
	 * Sets the name of the group.
	 *
	 * @param group
	 *            the name of the group.
	 * @return this.
	 * @see #group
	 */
	public final T group(final String group) {
		this.group = group;
		return self();
	}

	/**
	 * Sets the order in the group.
	 *
	 * @param orderInGroup
	 *            the order in the group.
	 * @return this
	 */
	public final T orderInGroup(final int orderInGroup) {
		this.orderInGroup = orderInGroup;
		return self();
	}

	/**
	 * Sets the width.
	 *
	 * @param width
	 *            the width.
	 * @return this
	 * @see #width
	 */
	public final T width(final ConfigDef.Width width) {
		this.width = width;
		return self();
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName
	 *            the display name.
	 * @return this
	 */
	public final T displayName(final String displayName) {
		this.displayName = displayName;
		return self();
	}

	/**
	 * Add a collection of dependent names to the list of dependents.
	 *
	 * @param dependents
	 *            the dependents to add.
	 * @return this.
	 * @see #dependents
	 */
	public final T dependents(final String... dependents) {
		return dependents(Arrays.asList(dependents));
	}

	/**
	 * Add a collection of dependent names to the list of dependents.
	 *
	 * @param dependents
	 *            the dependents to add.
	 * @return this.
	 * @see #dependents
	 */
	public final T dependents(final Collection<String> dependents) {
		if (this.dependents == null) {
			this.dependents = new LinkedHashSet<>(dependents);
		} else {
			this.dependents.addAll(dependents);
		}
		return self();
	}

	/**
	 * Adds a dependent to the list of dependents.
	 *
	 * @param dependent
	 *            the dependent to add.
	 * @return this.
	 * @see #dependents
	 */
	public final T dependent(final String dependent) {
		if (this.dependents == null) {
			this.dependents = new LinkedHashSet<>();
		}
		this.dependents.add(dependent);
		return self();
	}

	/**
	 * Gets the list of dependents. Dependents are only listed once and are in
	 * alphabetical order.
	 *
	 * @return the list of dependents for an empty list.
	 */
	List<String> getDependents() {
		return dependents == null ? Collections.emptyList() : new LinkedList<>(dependents);
	}

	/**
	 * Sets the {@link #recommender}.
	 *
	 * @param recommender
	 *            der.
	 * @return this.
	 */
	public final T recommender(final ConfigDef.Recommender recommender) {
		this.recommender = recommender;
		return self();
	}

	/**
	 * Sets the {@link #internalConfig} flag.
	 *
	 * @param internalConfig
	 *            the value for the flag.
	 * @return this.
	 */
	public final T internalConfig(final boolean internalConfig) {
		this.internalConfig = internalConfig;
		return self();
	}
}
