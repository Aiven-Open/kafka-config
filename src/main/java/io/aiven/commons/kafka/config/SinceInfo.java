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
package io.aiven.commons.kafka.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Map;
import java.util.Objects;

/**
 * Contains the information about when this option was added. Since options can
 * be added at various stages and by different libraries and because the "since"
 * field is to assist users in determining at what version an option became
 * available in the final connector the SinceInfo tracks the module and version
 * where the option was added but provides a mechanism to override the display
 * in the final configuration.
 */
public final class SinceInfo {

	/**
	 * Record of the data to display.
	 * 
	 * @param groupId
	 *            the groupId. May be {@code null}.
	 * @param artifactId
	 *            the artifactId. May be {@code null}.
	 * @param version
	 *            the version. Must be specified.
	 */
	public record Data(String groupId, String artifactId, String version) implements Comparable<Data> {
		/**
		 * Construct the Data object from a builder.
		 * 
		 * @param builder
		 *            the builder to construct from.
		 */
		public Data(Builder builder) {
			this(builder.groupId, builder.artifactId, builder.version);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (StringUtils.isNotEmpty(groupId)) {
				sb.append(groupId).append(":");
			}
			if (StringUtils.isNotEmpty(artifactId)) {
				sb.append(artifactId).append(":");
			}
			sb.append(version);
			return sb.toString();
		}

		@Override
		public int compareTo(Data other) {
			int result = StringUtils.defaultIfEmpty(groupId, "")
					.compareTo(StringUtils.defaultIfEmpty(other.groupId, ""));
			if (result == 0) {
				result = StringUtils.defaultIfEmpty(artifactId, "")
						.compareTo(StringUtils.defaultIfEmpty(other.artifactId, ""));
			}
			if (result == 0) {
				result = StringUtils.defaultIfEmpty(version, "")
						.compareTo(StringUtils.defaultIfEmpty(other.version, ""));
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Data)) {
				return false;
			}
			return compareTo((Data) obj) == 0;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 31 * hash + (groupId == null ? 0 : groupId.hashCode());
			hash = 31 * hash + (artifactId == null ? 0 : artifactId.hashCode());
			hash = 31 * hash + (version == null ? 0 : version.hashCode());
			return hash;
		}

	}

	/** The baseData for display if there are no overrides */
	private final Data baseData;
	/** The data to display as an override */
	private Data override;
	/** The artifact version */
	private final ArtifactVersion artifactVersion;

	/**
	 * Constructs the SinceInfo from the builder. The builder must define
	 * {@code groupId}, {@code artifactId}, and {@code version}.
	 * 
	 * @param builder
	 *            the builder to use.
	 */
	private SinceInfo(final Builder builder) {
		Objects.requireNonNull(builder.groupId, "groupId nay not be null");
		Objects.requireNonNull(builder.artifactId, "artifactId nay not be null");
		if (StringUtils.isEmpty(builder.version)) {
			throw new IllegalArgumentException("version nay not be null or empty");
		}
		baseData = new Data(builder);
		artifactVersion = new DefaultArtifactVersion(builder.version);
	}

	/**
	 * Sets the override for this SinceInfo. The builder must define
	 * {@code version}. The {@code groupId} and {@code artifactId} are optional and
	 * will be displayed if present.
	 * 
	 * @param builder
	 *            the builder to define the override.
	 */
	public void setOverride(Builder builder) {
		if (StringUtils.isEmpty(builder.version)) {
			throw new IllegalArgumentException("version nay not be null or empty");
		}
		override = new Data(builder);
	}

	/**
	 * Sets the override from the Builder associated with the matching OverrideRange
	 * in the overrideMap. If multiple ranges match the last one in the map will be
	 * applied.
	 * 
	 * @param overrideMap
	 *            the map of OverrideRanges and builders to apply.
	 */
	public void setOverride(Map<OverrideRange, Data> overrideMap) {
		overrideMap.entrySet().stream().filter(e -> e.getKey().matches(this))
				.forEach(e -> this.override = e.getValue());
	}

	/**
	 * Sets the override to be just the version of this component.
	 * 
	 * @return this
	 */
	public SinceInfo setVersionOnly() {
		override = new Data(null, null, baseData.version);
		return this;
	}
	/**
	 * Gets the parsed ArtifactVersion for this sinceInfo.
	 * 
	 * @return the parsed ArtifactVersion for this sinceInfo.
	 */
	public ArtifactVersion artifactVersion() {
		return artifactVersion;
	}

	@Override
	public String toString() {
		return override == null ? baseData.toString() : override.toString();
	}

	/**
	 * The builder for SinceInfo and SinceInfo overrides.
	 * 
	 * @return the Builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * A builder for SinceInfo and the SinceInfo override.
	 */
	public static class Builder {
		/** the group ID */
		private String groupId;
		/** The artifact ID */
		private String artifactId;
		/** The version */
		private String version;

		private Builder() {
		}

		/**
		 * Sets the groupId.
		 * 
		 * @param groupId
		 *            the group ID.
		 * @return this
		 */
		public Builder groupId(final String groupId) {
			this.groupId = groupId.trim();
			return this;
		}

		/**
		 * Sets the artifactId.
		 * 
		 * @param artifactId
		 *            the artifact ID
		 * @return this
		 */
		public Builder artifactId(final String artifactId) {
			this.artifactId = artifactId.trim();
			return this;
		}

		/**
		 * Sets the version.
		 * 
		 * @param version
		 *            the version.
		 * @return this
		 */
		public Builder version(final String version) {
			this.version = version.trim();
			return this;
		}

		/**
		 * Builds a SinceInfo without an override.
		 * 
		 * @return A new SinceInfo
		 */
		public SinceInfo build() {
			return new SinceInfo(this);
		}
	}

	/**
	 * Matches a range of SinceInfo records.
	 */
	public static final class OverrideRange implements Comparable<OverrideRange> {
		/** The internal data */
		private final SinceInfo.Data data;
		/** The version range for the data */
		private final VersionRange versionRange;

		/**
		 * Creates an OverrideRange from a SinceInfo.Builder. The version specified may
		 * be a range as defined in the Maven method
		 * {@link VersionRange#createFromVersionSpec(String)}.
		 * 
		 * @param builder
		 *            the Builder to extract the SinceInfo from.
		 * @throws InvalidVersionSpecificationException
		 *             on an invalid specification.
		 * @see <a href=
		 *      "https://maven.apache.org/ref/3.9.7/apidocs/org/apache/maven/artifact/versioning/VersionRange.html#createFromVersionSpec(java.lang.String)">
		 *      VersionRange.createFromVersionSpec</a>
		 */
		public OverrideRange(SinceInfo.Builder builder) throws InvalidVersionSpecificationException {
			Objects.requireNonNull(builder.groupId, "groupId nay not be null");
			Objects.requireNonNull(builder.artifactId, "artifactId nay not be null");
			if (StringUtils.isEmpty(builder.version)) {
				throw new IllegalArgumentException("version nay not be null or empty");
			}
			data = new Data(builder);
			versionRange = VersionRange.createFromVersionSpec(builder.version);
		}

		@Override
		public String toString() {
			return data.toString();
		}

		/**
		 * Checks if this OverrideRange matches a SinceInfo.
		 * 
		 * @param sinceValue
		 *            the SinceInfo to check.
		 * @return {@code true} if sinceValue is within the OverrideRange.
		 */
		public boolean matches(SinceInfo sinceValue) {
			return data.groupId.equals(sinceValue.baseData.groupId)
					&& data.artifactId.equals(sinceValue.baseData.artifactId)
					&& versionRange.containsVersion(sinceValue.artifactVersion());
		}

		@Override
		public int compareTo(OverrideRange other) {
			return data.compareTo(other.data);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof OverrideRange)) {
				return false;
			}
			return this.compareTo((OverrideRange) obj) == 0;
		}

		@Override
		public int hashCode() {
			return data.hashCode();
		}
	}
}
