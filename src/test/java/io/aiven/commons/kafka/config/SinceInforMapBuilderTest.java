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

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SinceInforMapBuilderTest {
	SinceInfoMapBuilder builder = new SinceInfoMapBuilder();

	@Test
	void roundTripTest() throws InvalidVersionSpecificationException, IOException {
		builder.put("a:b:[c]", new SinceInfo.Data("", "", "d"));
		SinceInfo.Builder siBuilder = SinceInfo.builder().groupId("e").artifactId("f").version("[g]");
		builder.put(new SinceInfo.OverrideRange(siBuilder), new SinceInfo.Data("", "", "h"));
		builder.put("i", "j", "[,k]", new SinceInfo.Data("", "l", "ll"));
		siBuilder = SinceInfo.builder().groupId("m").artifactId("n").version("[o]");
		builder.put(new SinceInfo.OverrideRange(siBuilder), "p");
		builder.put("q", "r", "s", "t");
		builder.put("u:v:[w]", "x");

		List<String> lines;
		byte[] bytes;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (PrintStream stream = new PrintStream(baos)) {
				builder.serialze(stream);
			}
			bytes = baos.toByteArray();
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
				lines = IOUtils.readLines(bais, StandardCharsets.UTF_8);
			}
		}
		assertThat(lines).contains("a:b:[c]:d");
		assertThat(lines).contains("e:f:[g]:h");
		assertThat(lines).contains("i:j:[,k]:l:ll");
		assertThat(lines).contains("m:n:[o]:p");
		assertThat(lines).contains("q:r:s:t");
		assertThat(lines).contains("u:v:[w]:x");

		SinceInfoMapBuilder reader = new SinceInfoMapBuilder();
		reader.parse(new ByteArrayInputStream(bytes));
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (PrintStream stream = new PrintStream(baos)) {
				reader.serialze(stream);
			}
			bytes = baos.toByteArray();
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
				List<String> readerLines = IOUtils.readLines(bais, StandardCharsets.UTF_8);
				assertThat(readerLines).containsExactlyElementsOf(lines);
			}
		}
	}

	@ParameterizedTest
	@MethodSource("dataParsingData")
	void extendedDataParsingTest(String pattern, SinceInfo.Data data) throws IOException {

		SinceInfoMapBuilder builder = new SinceInfoMapBuilder();
		try (ByteArrayInputStream bais = new ByteArrayInputStream(pattern.getBytes(StandardCharsets.UTF_8))) {
			builder.parse(bais);
		}

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (PrintStream printStream = new PrintStream(baos)) {
				builder.serialze(printStream);
			}
			byte[] bytes = baos.toByteArray();
			try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
				List<String> lines = IOUtils.readLines(bais, StandardCharsets.UTF_8);
				assertThat(lines).containsExactly(pattern);
				Map<SinceInfo.OverrideRange, SinceInfo.Data> map = builder.build();
				assertThat(map).hasSize(1);
				assertThat(map.values()).containsExactly(data);
			}
		}
	}

	static List<Arguments> dataParsingData() {
		List<Arguments> lst = new ArrayList<>();
		lst.add(Arguments.of("a:b:c:d:e:f", new SinceInfo.Data("d", "e", "f")));
		lst.add(Arguments.of("g:h:i:j:k", new SinceInfo.Data(null, "j", "k")));
		lst.add(Arguments.of("l:m:n:o", new SinceInfo.Data(null, null, "o")));
		return lst;
	}
}
