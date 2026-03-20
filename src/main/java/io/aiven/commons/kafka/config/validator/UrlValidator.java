package io.aiven.commons.kafka.config.validator;
/*
         Copyright 2020-2025 Aiven Oy and project contributors

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
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

/**
 * Validates that the string is a valid URL.
 */
public class UrlValidator implements ConfigDef.Validator {
	/** the list of valid schemes. Empty list allows all schemes */
	private final List<String> schemes;
	/** the list of valid hosts. Empty list allows all hosts */
	private final List<String> hosts;

	/**
	 * Creates a new builder.
	 *
	 * @return a new builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	private UrlValidator(Builder builder) {
		this.schemes = builder.schemes.isEmpty() ? Collections.emptyList() : new ArrayList<>(builder.schemes);
		this.hosts = builder.hosts.isEmpty() ? Collections.emptyList() : new ArrayList<>(builder.hosts);
	}

	private String formatOutput(String name, List<String> validValues) {
		if (validValues.size() > 1) {
			return String.format("URL %s must be one of: '%s'.", name, String.join("', '", validValues));
		} else {
			return String.format("URL %s must be: '%s'.", name, validValues.get(0));
		}
	}

	@Override
	public void ensureValid(final String name, final Object value) {
		if (Objects.nonNull(value)) {
			var valueStr = value.toString();

			if (valueStr.isBlank()) {
				throw new ConfigException(name, value, "must be non-empty");
			}
			try {
				URI uri = new URI(valueStr);
				if (!schemes.isEmpty() && !schemes.contains(uri.getScheme())) {
					throw new ConfigException(name, value, formatOutput("scheme", schemes));
				}
				if (!hosts.isEmpty() && !hosts.contains(uri.getHost())) {
					throw new ConfigException(name, value, formatOutput("host", hosts));
				}
				// now check that the URL is correct.
				uri.toURL();
			} catch (ConfigException e) {
				throw e;
			} catch (final Exception e) {
				throw new ConfigException(name, value, "should be valid URL");
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("A valid URL.");
		if (!schemes.isEmpty()) {
			sb.append(" ").append(formatOutput("scheme", schemes));
		}
		if (!hosts.isEmpty()) {
			sb.append(" ").append(formatOutput("host", hosts));
		}
		return sb.toString();
	}

	/**
	 * Builder for UrlValidator.
	 */
	public static class Builder {
		/** The list of valid schemes */
		TreeSet<String> schemes = new TreeSet<>();
		/** The list of valid hosts */
		TreeSet<String> hosts = new TreeSet<>();

		private Builder() {
		}

		/**
		 * Builds a UrlValidator.
		 * 
		 * @return a new UrlValidator.
		 */
		public UrlValidator build() {
			return new UrlValidator(this);
		}
		/**
		 * Adds one or more schemes to the list of valid schemes.
		 * 
		 * @param scheme
		 *            the scheme(s) to add.
		 * @return this
		 */
		public Builder schemes(String... scheme) {
			this.schemes.addAll(List.of(scheme));
			return this;
		}

		/**
		 * Adds one or more hosts to the list of valid hosts.
		 * 
		 * @param host
		 *            the host(s) to add.
		 * @return this
		 */
		public Builder hosts(String... host) {
			this.hosts.addAll(List.of(host));
			return this;
		}
	}
}
