/*
 * Copyright 2026 Aiven Oy
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

import java.util.Arrays;
import java.util.Objects;
import org.apache.kafka.common.config.ConfigException;

/** An enum which identifies a specific converter type */
public enum ConverterType {
  /** StringConverter */
  STRING("org.apache.kafka.connect.storage.StringConverter"),
  /** ByteConverter */
  BYTE("org.apache.kafka.connect.converters.ByteArrayConverter"),
  /** JsonConverter */
  JSON("org.apache.kafka.connect.json.JsonConverter"),
  /** AvroConverter */
  AVRO("io.confluent.connect.avro.AvroConverter");

  private final String name;

  ConverterType(final String name) {
    this.name = name;
  }

  /**
   * Gets an Enum instance of ConverterType
   *
   * @param name is the full name including package of the converter
   * @return An enum instance of ConverterType
   */
  public static ConverterType forName(final String name) {
    Objects.requireNonNull(name, "name cannot be null");
    for (final ConverterType converterType : values()) {
      if (converterType.name.equalsIgnoreCase(name)) {
        return converterType;
      }
    }
    throw new ConfigException(
        String.format(
            "Unknown serializer type: %s, allowed values %s ", name, Arrays.toString(values())));
  }
}
