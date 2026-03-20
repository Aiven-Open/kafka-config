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

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SinceInfoTest {

	/**
	 * Verifies that the OverrideRange matches the expected values.
	 * 
	 * @throws InvalidVersionSpecificationException
	 *             on error.
	 */
	@Test
	void OverrideRangeTest() throws InvalidVersionSpecificationException {
		SinceInfo.Builder builder = SinceInfo.builder().artifactId("anArtifact").groupId("aGroup");
		SinceInfo v_5 = builder.version("0.5").build();
		SinceInfo v1 = builder.version("1.0").build();
		SinceInfo v1_1 = builder.version("1.1").build();
		SinceInfo v1_2 = builder.version("1.2").build();
		SinceInfo v1_5 = builder.version("1.5").build();
		SinceInfo v2 = builder.version("2.0").build();
		SinceInfo v3 = builder.version("3.0").build();

		// Version 1.0 as a recommended version
		SinceInfo.OverrideRange range = new SinceInfo.OverrideRange(builder.version("1.0"));
		assertThat(range.matches(v_5)).isTrue();
		assertThat(range.matches(v1)).isTrue();
		assertThat(range.matches(v1_1)).isTrue();
		assertThat(range.matches(v1_2)).isTrue();
		assertThat(range.matches(v1_5)).isTrue();
		assertThat(range.matches(v2)).isTrue();
		assertThat(range.matches(v3)).isTrue();

		// Version 1.0 explicitly only
		range = new SinceInfo.OverrideRange(builder.version("[1.0]"));
		assertThat(range.matches(v_5)).isFalse();
		assertThat(range.matches(v1)).isTrue();
		assertThat(range.matches(v1_1)).isFalse();
		assertThat(range.matches(v1_2)).isFalse();
		assertThat(range.matches(v1_5)).isFalse();
		assertThat(range.matches(v2)).isFalse();
		assertThat(range.matches(v3)).isFalse();

		// Versions 1.0 (included) to 2.0 (not included)
		range = new SinceInfo.OverrideRange(builder.version("[1.0,2.0)"));
		assertThat(range.matches(v_5)).isFalse();
		assertThat(range.matches(v1)).isTrue();
		assertThat(range.matches(v1_1)).isTrue();
		assertThat(range.matches(v1_2)).isTrue();
		assertThat(range.matches(v1_5)).isTrue();
		assertThat(range.matches(v2)).isFalse();
		assertThat(range.matches(v3)).isFalse();

		// Versions 1.0 to 2.0 (both included)
		range = new SinceInfo.OverrideRange(builder.version("[1.0,2.0]"));
		assertThat(range.matches(v_5)).isFalse();
		assertThat(range.matches(v1)).isTrue();
		assertThat(range.matches(v1_1)).isTrue();
		assertThat(range.matches(v1_2)).isTrue();
		assertThat(range.matches(v1_5)).isTrue();
		assertThat(range.matches(v2)).isTrue();
		assertThat(range.matches(v3)).isFalse();

		// Versions 1.5 and higher
		range = new SinceInfo.OverrideRange(builder.version("[1.5,)"));
		assertThat(range.matches(v_5)).isFalse();
		assertThat(range.matches(v1)).isFalse();
		assertThat(range.matches(v1_1)).isFalse();
		assertThat(range.matches(v1_2)).isFalse();
		assertThat(range.matches(v1_5)).isTrue();
		assertThat(range.matches(v2)).isTrue();
		assertThat(range.matches(v3)).isTrue();

		// Versions up to 1.0 (included) and 1.2 or higher
		range = new SinceInfo.OverrideRange(builder.version("(,1.0],[1.2,)"));
		assertThat(range.matches(v_5)).isTrue();
		assertThat(range.matches(v1)).isTrue();
		assertThat(range.matches(v1_1)).isFalse();
		assertThat(range.matches(v1_2)).isTrue();
		assertThat(range.matches(v1_5)).isTrue();
		assertThat(range.matches(v2)).isTrue();
		assertThat(range.matches(v3)).isTrue();
	}

	/**
	 * Verifies that the override map only changes the toString value of the
	 * matching items.
	 * 
	 * @throws InvalidVersionSpecificationException
	 *             on error.
	 */
	@Test
	void OverrideMapTest() throws InvalidVersionSpecificationException {
		// make sure the map retains order of insertion.
		Map<SinceInfo.OverrideRange, SinceInfo.Data> overrideMap = new LinkedHashMap<>();

		SinceInfo.Builder builder = SinceInfo.builder().artifactId("anArtifact").groupId("aGroup");
		SinceInfo v_5 = builder.version("0.5").build();
		SinceInfo v1 = builder.version("1.0").build();
		SinceInfo v1_1 = builder.version("1.1").build();
		SinceInfo v1_2 = builder.version("1.2").build();
		SinceInfo v1_5 = builder.version("1.5").build();
		SinceInfo v2 = builder.version("2.0").build();
		SinceInfo v3 = builder.version("3.0").build();
		List<SinceInfo> testData = List.of(v_5, v1, v1_1, v1_2, v1_5, v2, v3);

		SinceInfo.Builder mappedValue = SinceInfo.builder();
		overrideMap.put(new SinceInfo.OverrideRange(builder.version("1.0")),
				new SinceInfo.Data(mappedValue.version("Version 1.0 as a recommended version")));
		for (SinceInfo testDatum : testData) {
			testDatum.setOverride(overrideMap);
		}
		assertThat(v_5.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1_1.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1_2.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1_5.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v2.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v3.toString()).isEqualTo("Version 1.0 as a recommended version");

		overrideMap.put(new SinceInfo.OverrideRange(builder.version("[1.0]")),
				new SinceInfo.Data(mappedValue.version("Version 1.0 explicitly only")));
		for (SinceInfo testDatum : testData) {
			testDatum.setOverride(overrideMap);
		}
		assertThat(v_5.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1.toString()).isEqualTo("Version 1.0 explicitly only");
		assertThat(v1_1.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1_2.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1_5.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v2.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v3.toString()).isEqualTo("Version 1.0 as a recommended version");

		overrideMap.put(new SinceInfo.OverrideRange(builder.version("[1.0,2.0)")),
				new SinceInfo.Data(mappedValue.version("Versions 1.0 (included) to 2.0 (not included)")));
		for (SinceInfo testDatum : testData) {
			testDatum.setOverride(overrideMap);
		}
		assertThat(v_5.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1.toString()).isEqualTo("Versions 1.0 (included) to 2.0 (not included)");
		assertThat(v1_1.toString()).isEqualTo("Versions 1.0 (included) to 2.0 (not included)");
		assertThat(v1_2.toString()).isEqualTo("Versions 1.0 (included) to 2.0 (not included)");
		assertThat(v1_5.toString()).isEqualTo("Versions 1.0 (included) to 2.0 (not included)");
		assertThat(v2.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v3.toString()).isEqualTo("Version 1.0 as a recommended version");

		overrideMap.put(new SinceInfo.OverrideRange(builder.version("[1.0,2.0]")),
				new SinceInfo.Data(mappedValue.version("Versions 1.0 to 2.0 (both included)")));
		for (SinceInfo testDatum : testData) {
			testDatum.setOverride(overrideMap);
		}
		assertThat(v_5.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v1_1.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v1_2.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v1_5.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v2.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v3.toString()).isEqualTo("Version 1.0 as a recommended version");

		overrideMap.put(new SinceInfo.OverrideRange(builder.version("[1.5,)")),
				new SinceInfo.Data(mappedValue.version("Versions 1.5 and higher")));
		for (SinceInfo testDatum : testData) {
			testDatum.setOverride(overrideMap);
		}
		assertThat(v_5.toString()).isEqualTo("Version 1.0 as a recommended version");
		assertThat(v1.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v1_1.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v1_2.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v1_5.toString()).isEqualTo("Versions 1.5 and higher");
		assertThat(v2.toString()).isEqualTo("Versions 1.5 and higher");
		assertThat(v3.toString()).isEqualTo("Versions 1.5 and higher");

		overrideMap.put(new SinceInfo.OverrideRange(builder.version("(,1.0],[1.2,)")),
				new SinceInfo.Data(mappedValue.version("Versions up to 1.0 (included) and 1.2 or higher")));
		for (SinceInfo testDatum : testData) {
			testDatum.setOverride(overrideMap);
		}
		assertThat(v_5.toString()).isEqualTo("Versions up to 1.0 (included) and 1.2 or higher");
		assertThat(v1.toString()).isEqualTo("Versions up to 1.0 (included) and 1.2 or higher");
		assertThat(v1_1.toString()).isEqualTo("Versions 1.0 to 2.0 (both included)");
		assertThat(v1_2.toString()).isEqualTo("Versions up to 1.0 (included) and 1.2 or higher");
		assertThat(v1_5.toString()).isEqualTo("Versions up to 1.0 (included) and 1.2 or higher");
		assertThat(v2.toString()).isEqualTo("Versions up to 1.0 (included) and 1.2 or higher");
		assertThat(v3.toString()).isEqualTo("Versions up to 1.0 (included) and 1.2 or higher");
	}
}