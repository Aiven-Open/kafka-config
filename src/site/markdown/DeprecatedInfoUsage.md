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
# DeprecatedInfo Usage

`DeprecatedInfo` is used to denote an option that has been deprecated and may be removed in the future. To create an DeprecatedInfo instance use call `DeprecatedInfo.builder()`, add any optional values and call `build()`.   All the values available to the builder are optional.

## The options

### Description

The description provides the user with information about how to adjust to the deprecation.  For example if we have a configuration option named "bar" and it is deprecated, the description might say "use 'baz' instead", or "Due to security risks 'baz' is no longer recommended".

### Since

Describes when the configuration option was deprecated.  This option uses the `SinceInfo` class.  See [SinceInfo Usage](./SinceInfoUsage.html) for an overview of how the class functions.  The SinceInfo should specify groupId, artifactID and version where the option was deprecated.  The [SinceInfo override](/SinceInfoUsage.html#End_configuration_example) features also apply to deprecated since values.  

### For removal

A flag that indicates the option will be removed at some point in the future.

## Examples

### Simple deprecation

```java
    configDef.define(ExtendedConfigKey.builder("foo.property")
        .documentation("The foo connector property.")
        .deprecatedInfo(DeprecatedInfo.builder().build())
```

will display

```
foo.property:
    The foo connector property.

    Deprecated
```


### Deprecation with description

```java
    configDef.define(ExtendedConfigKey.builder("foo.property")
        .documentation("The foo connector property.")
        .deprecatedInfo(DeprecatedInfo.builder().description("use 'newfoo.property' instead").build())
```

will display

```
foo.property:
    The foo connector property.

    Deprecated: use 'newfoo.property' instead
```

### Deprecation with since and description

```java
    configDef.define(ExtendedConfigKey.builder("foo.property")
        .documentation("The foo connector property.")
        .deprecatedInfo(DeprecatedInfo.builder()
            .description("use 'newfoo.property' instead")
            .since(SinceInfo.builder().groupId("example.com").artifactId("foo-connector").version("1.3.4").build().setVersionOnly())
            .build())
```

will display

```
foo.property:
    The foo connector property.

    Deprecated since 1.3.4: use 'newfoo.property' instead
```

### Deprecation with forRemoval, since and description

```java
    configDef.define(ExtendedConfigKey.builder("foo.property")
        .documentation("The foo connector property.")
        .deprecatedInfo(DeprecatedInfo.builder()
            .description("use 'newfoo.property' instead")
            .since(SinceInfo.builder().groupId("example.com").artifactId("foo-connector").version("1.3.4").build().setVersionOnly())
            .forRemoval(true)
            .build())
```

will display

```
foo.property:
    The foo connector property.

    Deprecated for removal since 1.3.4: use 'newfoo.property' instead
```
