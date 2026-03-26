/*
 * Copyright 2025 Aiven Oy and project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * SPDX-License-Identifier: Apache-2
 */
/** Kafka configuration utilities module def */
module io.aiven.commons.kafka.config {
  exports io.aiven.commons.kafka.config;
  exports io.aiven.commons.kafka.config.fragment;
  exports io.aiven.commons.kafka.config.validator;

  requires transitive io.aiven.commons.util;

  uses org.slf4j.Logger;
  uses org.slf4j.LoggerFactory;

  requires kafka.clients;
  requires connect.runtime;
  requires org.apache.commons.lang3;
  requires maven.artifact;
  requires org.apache.commons.io;
  requires org.slf4j;
  requires velocity.tools.generic;
}
