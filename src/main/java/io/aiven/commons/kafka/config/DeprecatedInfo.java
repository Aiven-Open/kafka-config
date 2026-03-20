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

import java.util.Map;

/** Contains the information about a deprecated ConfigKey */
public class DeprecatedInfo {
	/** The Builder for {@link DeprecatedInfo}. */
	public static class Builder {

		/** The description. */
		private String description;

		/**
		 * Whether this option is subject to removal in a future version.
		 *
		 * @see <a href=
		 *      "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Deprecated.html#forRemoval()">Deprecated.forRemoval</a>
		 */
		private boolean forRemoval;

		/**
		 * The version in which the option became deprecated.
		 *
		 * @see <a href=
		 *      "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Deprecated.html#forRemoval()">Deprecated.since</a>
		 */
		private SinceInfo since;

		/** Private constructor. */
		private Builder() {
		}

		/**
		 * Creates the DeprecatedInfo
		 * 
		 * @return a new DeprecatedInfo
		 */
		public DeprecatedInfo build() {
			return new DeprecatedInfo(description, since, forRemoval);
		}

		/**
		 * Sets the description.
		 *
		 * @param description
		 *            the description.
		 * @return {@code this} instance.
		 */
		public Builder description(final String description) {
			this.description = description;
			return this;
		}

		/**
		 * Sets whether this option is subject to removal in a future version.
		 *
		 * @param forRemoval
		 *            whether this is subject to removal in a future version.
		 * @return {@code this} instance.
		 * @see <a href=
		 *      "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Deprecated.html#forRemoval()">Deprecated.forRemoval</a>
		 */
		public Builder forRemoval(final boolean forRemoval) {
			this.forRemoval = forRemoval;
			return this;
		}

		/**
		 * Sets the version in which the option became deprecated.
		 *
		 * @param since
		 *            the version in which the option became deprecated.
		 * @return {@code this} instance.
		 */
		public Builder since(final SinceInfo since) {
			this.since = since;
			return this;
		}

		/**
		 * Sets the version in which the option became deprecated.
		 *
		 * @param sinceBuilder
		 *            the builder for the version in which the option became deprecated.
		 * @return {@code this} instance.
		 */
		public Builder since(final SinceInfo.Builder sinceBuilder) {
			this.since = sinceBuilder.build();
			return this;
		}
	}

	/**
	 * Creates a new builder.
	 *
	 * @return a new builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/** The description. */
	private final String description;

	/** Whether this option will be removed. */
	private final boolean forRemoval;

	/** The version label for removal. */
	private final SinceInfo since;

	/**
	 * Constructs a new instance.
	 *
	 * @param description
	 *            The description.
	 * @param since
	 *            The version label when deprecated.
	 * @param forRemoval
	 *            Whether this option will be removed.
	 */
	private DeprecatedInfo(final String description, final SinceInfo since, final boolean forRemoval) {
		this.description = toEmpty(description);
		this.since = since;
		this.forRemoval = forRemoval;
	}

	/**
	 * Gets the descriptions.
	 *
	 * @return the descriptions.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets version in which the option became deprecated.
	 *
	 * @return the version in which the option became deprecated.
	 */
	public SinceInfo getSince() {
		return since;
	}

	/**
	 * Sets the override for this SinceInfo. The builder must define
	 * {@code version}. The {@code groupId} and {@code artifactId} are optional and
	 * will be displayed if present.
	 *
	 * @param builder
	 *            the builder to define the override.
	 */
	public void overrideSince(SinceInfo.Builder builder) {
		if (since != null) {
			since.setOverride(builder);
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
	public void overrideSince(Map<SinceInfo.OverrideRange, SinceInfo.Data> overrideMap) {
		if (since != null) {
			since.setOverride(overrideMap);
		}
	}

	/**
	 * Tests whether this option is subject to removal in a future version.
	 *
	 * @return whether this option is subject to removal in a future version.
	 */
	public boolean isForRemoval() {
		return forRemoval;
	}

	private String toEmpty(final String since) {
		return since != null ? since : "";
	}

	@Override
	public String toString() {
		return formatted(null);
	}

	/**
	 * Gets a string that contains the deprecated information. The output will read:
	 * "{@code name} deprecated for removal since {@link #since}:
	 * {@link #description}". Where
	 *
	 * <ul>
	 * <li>{@code name} is the name argument. If {@code name} is empty, the start of
	 * the result will be "Deprecated"
	 * <li>"for removal" will not be present if {@link #forRemoval} is
	 * {@code false}.
	 * <li>"since {@link #since}" will contain the {@link #since} text, or be
	 * omitted if {@link #since} is not set.
	 * <li>": {@link #description}" will contain the {@link #description} text, or
	 * be omitted if {@link #description} is empty.
	 * </ul>
	 *
	 * @param name
	 *            The name to use for the formatted display.
	 * @return the formatted information.
	 */
	public String formatted(String name) {
		final StringBuilder builder = name == null || name.isEmpty()
				? new StringBuilder("Deprecated")
				: new StringBuilder(name).append(" is deprecated");
		if (forRemoval) {
			builder.append(" for removal");
		}
		if (since != null) {
			builder.append(" since ");
			builder.append(since);
		}
		if (!description.isEmpty()) {
			builder.append(": ");
			builder.append(description);
		}
		return builder.toString();
	}
}
