<!--
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
-->
# General Architecture

The `kafka-config` module has the goal of making it easier to generate accurate documentation, detailed exception and logging messages as well as simplify the detection of configuration issues.

The `kafka-config` module contains classes that make it easier to implement high level functionality across a number of Kafka component implementations.

Specifically, the `ExtendedConfigKey` extends the standard ConfigKey to allow us to specify the initial implementation version (`since`) as well as deprecation information.

The `ConfigFragment` framework allows common configuration options to be shared across dissimilar components.  For example connectivity configuration between a source and sink talking to the same backend.  Fragments also provide mechanisms for validation between multiple options within the fragment.  They also support complex default configurations.

The `CommonConfigDef` defines the configuration.

The `CommonConfig` ties all of this together along with the ability to generate detailed configuration error messages.

## ExtendedConfigKey

The `ExtendedConfigKey` provides a builder that will build a ConfigKey for the ConfigDef.
The builder has reasonable defaults for all items except the `name` of the key, and methods to set all the options available in the standard ConfigDef.  In addition, it adds the ability to set `since` using `SinceInfo` to describe when the option was added as well as how to display the value.  `ExtendedConfigKey` also supports adding `DeprecatedInfo` to display deprecation information in the documentation.

### SinceInfo

The `SinceInfo` class performs two functions.  First, it tracks the "groupId", "artifactId" and "version" of the package where the configuration option was introduced. For example, the `BackoffPolicyFragment` in this module defines the backoff milliseconds property as having a groupIf of "io.aiven.commons", an artifactID of "kafka-config" and a version of "1.0.0".  Second, it produces the display for the info.  By default, the backoff milliseconds will produce display value of "io.aiven.commons:kafka-config:1.0.0". However, this can, and should, be overridden in the final configuration to display the version of the item being configured.

See the [SinceInfo usage documentation](./SinceInfoUsage.html) for examples of how this is done.

### DeprecatedInfo

The `DeprecatedInfo` tracks that the item is deprecated, an optional description of why it was deprecated and potentially what option should be used in its place, an optional SinceInfo identifying when it was deprecated, and a flag that indicates that it is to be removed.

See the [DeprecatedInfo usage documentation](./DeprecatedInfoUsage.html) for examples of how to configure the DeprecatedInfo.

## ConfigFragment

ConfigFragments describe a grouping of options. For example all the options necessary to connect to a backend could be a fragment. In this package the `BackoffPolicyFragment` defines the options to configure the backoff policy when communicating with Kafka.  In addition to the options, fragments also define a `Setter`.  The setter provides methods to set String key-value pairs into a map. This is primarily used in testing classes to make it easier to read.  Another advantage is that most static final Strings in the fragment need only be exposed as package-private.

Every fragment should define a `public static int update(final ConfigDef configDef)` method that updates the configDef argument by defining `ConfigDef.ConfigKey` items, preferably by defining them with the `ExtendedConfigKey.Builder`.

If there are configuration options that are mutually exclusive or that impose other restrictions, the `ConfigFragment` should override `public void validate(final Map<String, ConfigValue> configMap)` to detect and report any conflicts between options.

See [ConfigFragment usage documentation](./ConfigFragmentUsage.html) for examples.

If there are non-standard defaults for options.  For example when setting one option changes the default on another option, the `ConfigFragment` should implement a method that accepts a `ChangeTrackingMap` and defines the changes from the default configuration.  This method will need to be called from the Configuration that adds the Fragment.

See `CommonConfig` and `ChangeTrackingMap` below.

Finally, `ConfigFragment` implementations should provide getters to retrieve the data. If the data format should be converted, for example from a `String` to an `enum`, the getter should perform that change.  This ensures that data conversions are consistently carried out across all the users of the fragment. 

## CommonConfigDef

`CommonConfigDef` defines the configuration options shared across all instances of `CommonConfig`.  It uses `ConfigFragment` instances to define shared options and provides a final implementation of `public List<ConfigValue> validate(final Map<String, String> props)` and defines `protected Map<String, ConfigValue> multiValidate(final Map<String, ConfigValue> valueMap)` to perform deep validation.

Implementing classes should implement `multiValidate` and pass the valueMap to all ConfigFragments instantiated within the class.

## CommonConfig

The `CommonConfig` class ties the above-mentioned classes together. Its constructor takes an instance of a class that extends `CommonConfigDef` and a `Map<String, String>` that are the properties and Constructs the CommonConfig.  During the construction the properties values are potentially modified by `ConfigFragment.validate()` calls and the results validated against the property definitions and the extra validation of the fragments.

The common config should provide access to the data in the fragments by implementing getter methods to retrieve values from the fragments.

`CommonConfig` instances with fragments that process the `ChangeTrackingMap` should override the `protected void fragmentPostProcess(ChangeTrackingMap map)` method and call those methods on the `ConfigFragment`s.

See [CommonConfig usage documentation](./CommonConfigUsage.html) for examples.

### ChangeTrackingMap

The `ChangeTrackingMap` is a map that allows retains the original values while allowing reversible overrides.  This allows fragments to change values of the parameters and back out that change if necessary.  The map implements the standard getter and an `override(key, value)` method.

# Notes on data types

The maps in `CommonConfig`, and it the Kafka `AbstractConfig` can be a bit confusing. They all have String for the key values,  This is the name of the key as defined by `ConfigDef` or `ExtendedConfigKey.Builder`.  The value however, is one of the following:

 * String - The string value from the property file.
 * Object - The object type specified in the ConfigDef.  This is one of the ConfigDef.Type values: 
    * BOOLEAN
    * STRING
    * INT
    * SHORT
    * LONG
    * DOUBLE
    * LIST
    * CLASS
    * PASSWORD
 * ConfigValue - An object that contains the definition of the key, the value provided for the key, a potentially empty collection of recommended values, a potentially empty collection of error messages, and a visibility flag. The `ConfigFragment.validate()` method should add messages to the ConfigValue items that have errors.  This can be easily done via the `ConfigFragment.registerIssue()` method.