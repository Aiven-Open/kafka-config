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
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DeprecatedInfoTest {

	@Test
	void testEmpty() {
		DeprecatedInfo underTest = DeprecatedInfo.builder().build();
		assertThat(underTest.formatted("emptyTest")).isEqualTo("emptyTest is deprecated");
	}

	@Test
	void testForRemoval() {
		DeprecatedInfo underTest = DeprecatedInfo.builder().forRemoval(true).build();
		assertThat(underTest.formatted("emptyTest")).isEqualTo("emptyTest is deprecated for removal");
		underTest = DeprecatedInfo.builder().forRemoval(false).build();
		assertThat(underTest.formatted("emptyTest")).isEqualTo("emptyTest is deprecated");
	}

	@Test
	void testSince() {
		SinceInfo since = SinceInfo.builder().groupId("io.aiven.commons").artifactId("kafka-config").version("LATEST")
				.build();
		DeprecatedInfo underTest = DeprecatedInfo.builder().since(since).build();
		assertThat(underTest.formatted("emptyTest"))
				.isEqualTo("emptyTest is deprecated since io.aiven.commons:kafka-config:LATEST");
		since.setOverride(SinceInfo.builder().version("myVersion"));
		assertThat(underTest.formatted("emptyTest")).isEqualTo("emptyTest is deprecated since myVersion");
	}

	@Test
	void testSinceOverride() {
		SinceInfo since = SinceInfo.builder().groupId("io.aiven.commons").artifactId("kafka-config").version("LATEST")
				.build();
		DeprecatedInfo underTest = DeprecatedInfo.builder().since(since).build();
		assertThat(underTest.formatted("emptyTest"))
				.isEqualTo("emptyTest is deprecated since io.aiven.commons:kafka-config:LATEST");
		underTest.overrideSince(SinceInfo.builder().version("myVersion"));
		assertThat(underTest.formatted("emptyTest")).isEqualTo("emptyTest is deprecated since myVersion");
	}

	@Test
	void testSinceOverrideMap() throws InvalidVersionSpecificationException {
		SinceInfo.Builder builder = SinceInfo.builder().groupId("io.aiven.commons").artifactId("kafka-config")
				.version("1.0");
		SinceInfo since = builder.build();
		DeprecatedInfo underTest = DeprecatedInfo.builder().since(since).build();
		assertThat(underTest.formatted("emptyTest"))
				.isEqualTo("emptyTest is deprecated since io.aiven.commons:kafka-config:1.0");
		Map<SinceInfo.OverrideRange, SinceInfo.Data> overrideMap = Map.of(
				new SinceInfo.OverrideRange(builder.version("[,5.0]")),
				new SinceInfo.Data(SinceInfo.builder().version("myVersion")));
		underTest.overrideSince(overrideMap);
		assertThat(underTest.formatted("emptyTest")).isEqualTo("emptyTest is deprecated since myVersion");
	}

	@Test
	void testDescription() {
		DeprecatedInfo underTest = DeprecatedInfo.builder().description("why not").build();
		assertThat(underTest.formatted("emptyTest")).isEqualTo("emptyTest is deprecated: why not");
	}

	@Test
	void testForRemovalSince() {
		SinceInfo since = SinceInfo.builder().groupId("io.aiven.commons").artifactId("kafka-config").version("LATEST")
				.build();
		DeprecatedInfo underTest = DeprecatedInfo.builder().since(since).forRemoval(true).build();
		assertThat(underTest.formatted("emptyTest"))
				.isEqualTo("emptyTest is deprecated for removal since io.aiven.commons:kafka-config:LATEST");
	}

	@Test
	void testForRemovalDescription() {
		DeprecatedInfo underTest = DeprecatedInfo.builder().description("why not").forRemoval(true).build();
		assertThat(underTest.formatted("emptyTest")).isEqualTo("emptyTest is deprecated for removal: why not");
	}

	@Test
	void testSinceDescription() {
		SinceInfo since = SinceInfo.builder().groupId("io.aiven.commons").artifactId("kafka-config").version("LATEST")
				.build();
		DeprecatedInfo underTest = DeprecatedInfo.builder().description("why not").since(since).build();
		assertThat(underTest.formatted("emptyTest"))
				.isEqualTo("emptyTest is deprecated since io.aiven.commons:kafka-config:LATEST: why not");
	}

	@Test
	void testSinceDescriptionForRemoval() {
		SinceInfo since = SinceInfo.builder().groupId("io.aiven.commons").artifactId("kafka-config").version("LATEST")
				.build();
		DeprecatedInfo underTest = DeprecatedInfo.builder().description("why not").since(since).forRemoval(true)
				.build();
		assertThat(underTest.formatted("emptyTest"))
				.isEqualTo("emptyTest is deprecated for removal since io.aiven.commons:kafka-config:LATEST: why not");
	}
}