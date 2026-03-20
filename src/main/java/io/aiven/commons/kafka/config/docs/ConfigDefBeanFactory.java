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
package io.aiven.commons.kafka.config.docs;

import io.aiven.commons.kafka.config.SinceInfoMapBuilder;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

@DefaultKey("ConfigDefFactory")
@ValidScope({"application"})
public class ConfigDefBeanFactory {
	/**
	 * Constructs the ConfigDefBean for the named class that will return
	 * ExtendedConfigKeyBeans. If there is a property file in the path specified by
	 * the fully qualified className, that has the extension ".versionMap" it will
	 * be read and applied as a version map using the {@link SinceInfoMapBuilder}.
	 * For example if there is a class "foo.bar.FooBarConfigDef" and a file
	 * "foo/bar/FooBarConfigDef.versionMap" found in the properties that file will
	 * be read and applied to the configuration definition.
	 * 
	 * @param className
	 *            the class to construct the bean for.
	 * @return the ConfigDefBean for velocity usage.
	 */
	public ConfigDefBean<ExtendedConfigKeyBean> open(String className) {
		try {
			final Class<? extends ConfigDef> clazz = (Class<? extends ConfigDef>) Class.forName(className);
			final ConfigDef configDef = clazz.getConstructor().newInstance();
			final String versionMap = className.replace(".", "/") + ".versionMap";
			InputStream inputStream = clazz.getClassLoader().getResourceAsStream(versionMap);
			if (inputStream != null) {
				try {
					SinceInfoMapBuilder builder = new SinceInfoMapBuilder();
					builder.parse(inputStream);
					builder.applyTo(configDef);
				} catch (IOException e) {
					LoggerFactory.getLogger(ConfigDefBean.class).error("Unable to appy {}: {}", versionMap,
							e.getMessage(), e);
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {
						LoggerFactory.getLogger(ConfigDefBean.class).error("Error closing input stream", e);
					}
				}
			}
			return new ConfigDefBean<>(configDef, ExtendedConfigKeyBean::new);
		} catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException
				| NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
