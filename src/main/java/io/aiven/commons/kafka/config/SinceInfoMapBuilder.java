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
package io.aiven.commons.kafka.config;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class to build and maintain a list of Override ranges to final version. Items
 * are retained in the order in which they were added.
 */
public class SinceInfoMapBuilder {
	/**
	 * The comment character for comment lines in input files. The comment must be
	 * the first character in the line.
	 */
	public static final String COMMENT_CHAR = "#";
	private final Map<SinceInfo.OverrideRange, SinceInfo.Data> overrideMap;

	/**
	 * Constructs a new SinceInfoMapBuilder.
	 */
	public SinceInfoMapBuilder() {
		overrideMap = new LinkedHashMap<>();

	}
	/**
	 * Builds the final map.
	 * 
	 * @return the override map.
	 */
	public Map<SinceInfo.OverrideRange, SinceInfo.Data> build() {
		return new LinkedHashMap<>(overrideMap);
	}

	/**
	 * Put a range and data into the map.
	 * 
	 * @param range
	 *            the OverrideRange.
	 * @param data
	 *            the final version data.
	 */
	public void put(SinceInfo.OverrideRange range, SinceInfo.Data data) {
		Objects.requireNonNull(range, "range may not be null");
		Objects.requireNonNull(data, "data may not be null");
		overrideMap.put(range, data);
	}

	/**
	 * put the range definition and the data into the map.
	 * 
	 * @param groupId
	 *            the group ID for the OverrideRange.
	 * @param artifactId
	 *            the artifact ID for the OverrideRange.
	 * @param version
	 *            the version for the OverrideRange.
	 * @param data
	 *            the final version data.
	 */
	public void put(String groupId, String artifactId, String version, SinceInfo.Data data) {
		try {
			put(new SinceInfo.OverrideRange(
					SinceInfo.builder().groupId(groupId).artifactId(artifactId).version(version)), data);
		} catch (InvalidVersionSpecificationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Put an override pattern and the data into the map.
	 * 
	 * @param pattern
	 *            a pattern in the form groupId:artifactId:versionRange
	 * @param data
	 *            the final version data.
	 */
	public void put(String pattern, SinceInfo.Data data) {
		Objects.requireNonNull(pattern, "pattern may not be null");
		Objects.requireNonNull(data, "data may not be null");
		String[] parts = pattern.split(":");
		if (parts.length != 3) {
			throw new IllegalArgumentException("pattern must have 3 parts separated by ':'");
		}
		put(parts[0], parts[1], parts[2], data);
	}

	/**
	 * Put an override range and final version into the map.
	 * 
	 * @param range
	 *            the override range.
	 * @param finalVersion
	 *            the final version.
	 */
	public void put(SinceInfo.OverrideRange range, String finalVersion) {
		Objects.requireNonNull(finalVersion, "finalVersion may not be null");
		overrideMap.put(range, new SinceInfo.Data(null, null, finalVersion));
	}

	/**
	 * Put the override range data and final version into the map.
	 *
	 * @param groupId
	 *            the group ID for the OverrideRange.
	 * @param artifactId
	 *            the artifact ID for the OverrideRange.
	 * @param version
	 *            the version for the OverrideRange.
	 * @param finalVersion
	 *            the final version data.
	 */
	public void put(String groupId, String artifactId, String version, String finalVersion) {
		try {
			put(new SinceInfo.OverrideRange(
					SinceInfo.builder().groupId(groupId).artifactId(artifactId).version(version)), finalVersion);
		} catch (InvalidVersionSpecificationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Put an override pattern and the final version into the map.
	 * 
	 * @param pattern
	 *            a pattern in the form groupId:artifactId:versionRange
	 * @param finalVersion
	 *            the final version data.
	 */
	public void put(String pattern, String finalVersion) {
		Objects.requireNonNull(pattern, "pattern may not be null");
		if (StringUtils.isEmpty(finalVersion)) {
			throw new IllegalArgumentException("finalVersion may not be null or empty");
		}
		String[] parts = pattern.split(":");
		if (parts.length != 3) {
			throw new IllegalArgumentException("pattern must have 3 parts separated by ':'");
		}
		put(parts[0], parts[1], parts[2], new SinceInfo.Data(null, null, finalVersion.trim()));
	}

	/**
	 * Serialize the map to a print stream.
	 * 
	 * @param printStream
	 *            the stream to serialize to.
	 */
	public void serialze(PrintStream printStream) {
		overrideMap.forEach((key, value) -> printStream.printf("%s:%s%n", key, value));
	}

	/**
	 * Read the map from an input stream. The stream must comprise lines of text in
	 * the form groupId:artifactId:versionRange:finalVersion Parser ignores empty
	 * lines and lines starting with '#'
	 * 
	 * @param inputStream
	 *            the input stream to read from.
	 * @throws IOException
	 *             on input error or format error.
	 */
	public void parse(InputStream inputStream) throws IOException {
		try (LineIterator it = IOUtils.lineIterator(inputStream, StandardCharsets.UTF_8)) {
			while (it.hasNext()) {
				String line = it.next().trim();
				if (!(line.isEmpty() || line.startsWith(COMMENT_CHAR))) {
					String[] parts = line.split(":");
					if (parts.length < 4 || parts.length > 6) {
						throw new IOException(
								String.format("Invalid structure: %s nust have 4 to 6 parts separated by ':'", line));
					}

					for (int i = 0; i < parts.length; i++) {
						parts[i] = parts[i].trim();
					}
					if (StringUtils.isEmpty(parts[parts.length - 1])) {
						throw new IOException("final argument may not be zero length");
					}
					try {
						SinceInfo.OverrideRange range = new SinceInfo.OverrideRange(
								SinceInfo.builder().groupId(parts[0]).artifactId(parts[1]).version(parts[2]));
						switch (parts.length) {
							case 4 :
								put(range, new SinceInfo.Data(null, null, parts[3]));
								break;
							case 5 :
								put(range, new SinceInfo.Data(null, parts[3], parts[4]));
								break;
							case 6 :
								put(range, new SinceInfo.Data(parts[3], parts[4], parts[5]));
								break;
						}
					} catch (InvalidVersionSpecificationException e) {
						throw new IOException(e);
					}
				}
			}
		}
	}

	/**
	 * Applies the mapping to s the final versions for the specified Since values of
	 * the ExtendedConfigKeys in the ConfigDef.
	 * 
	 * @param configDef
	 *            the configDef to adjust.
	 */
	public void applyTo(ConfigDef configDef) {
		for (String keyName : configDef.configKeys().keySet()) {
			ConfigDef.ConfigKey key = configDef.configKeys().get(keyName);
			if (key instanceof ExtendedConfigKey exKey) {
				exKey.overrideSince(this.overrideMap);
			}
		}
	}
}
