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
# SinceInfo Usage

The SinceInfo tracks at which version event occurred.  The event is generally the introduction of a configuration option, or the deprecation of one.

The SinceInfo tracks the "groupId", "artifactId", and "version" when the event occurred and by default will display that information separated by colons (`:`).  However, in most circumstances we do not want to display that information but rather when that change was introduced in the final component.

## Example override from our code base.

As an example let's use `tasks.max` property defined in CommonConfigFragment.

Looking into the `CommonConfigFragment.update()` method we find

```java
    SinceInfo tasksMaxSince = SinceInfo.builder()
        .groupId("org.apache.kafka").artifactId("kafka")
        .version("0.9.0.0").build();
```

This code sets up the taskMaxSince info to have the groupId and artifactId of Kafka and the version when it was first delivered.

Below the code we see that the `taskMaxSince` variable is used to set the since property of the "tasks.max"

```java
configDef.define(ExtendedConfigKey.builder(ConnectorConfig.TASKS_MAX_CONFIG).type(ConfigDef.Type.INT)
    .defaultValue(1).validator(atLeast(1)).importance(ConfigDef.Importance.HIGH)
    .group(commonGroup).orderInGroup(++orderInGroup).width(ConfigDef.Width.SHORT)
    .documentation("Maximum number of tasks to use for this connector.").since(tasksMaxSince).build())
```

If we were to produce the documentation from the above we would see something like

```
tasks.max:
    Maximum number of tasks to use for this connector.
    
    available since: org.apache.kafka:kafka:0.9.0.0
```

Which is not what we want the user to see so we adjust the SinceInfo with:

```java
    SinceInfo.Builder overrideBuilder = SinceInfo.builder().version("Kafka 0.9.0.0");
    tasksMaxSince.setOverride(overrideBuilder);
```

This provides a default override that only has the version defined and se sets the display to be just the version in the override: `Kafka 0.9.0.0`.  So now the display looks like 

```
tasks.max:
    Maximum number of tasks to use for this connector.
    
    available since: Kafka 0.9.0.0
```

## A second overrwrite example 

When creating ExtendedConfigKeys the `since` field should contain the version at which the option was added.  For example:


```java

public static update(ConfigDef configDef) {
    SinceInfo siBuilder = SinceInfo.builder()
        .groupId("org.example").artifactId("foo-connector");

    configDef.define(ExtendedConfigKey.builder("foo.property")
        .documentation("The foo connector property.")
        .since(siBuilder.version("1.0.0").build())
        .build())
        ...
```

This will produce documentation that looks like:

```
foo.property:
    The foo connector property.
    
    available since: org.example:foo-connector:1.0.0
```

If the configuration is for a final configuration, like for a kafka connector implementation, and not for an intermediate configuration, like for a shared component, then we want the documentation to show the simple version at which the option was added, not the fully qualified version.  To do this we add a `setVersionOnly()` call to the SinceInfo after the `build()` call.  The define now looks like:

```java
    configDef.define(ExtendedConfigKey.builder("foo.property")
        .documentation("The foo connector property.")
        .since(siBuilder.version("1.0.0").build().setVersionOnly())
        .build())
        ...
```

and the documentation:

```
foo.property:
    The foo connector property.
    
    available since: 1.0.0
```

if we had two entries:


```java
    configDef.define(ExtendedConfigKey.builder("foo.property")
        .documentation("The foo connector property.")
        .since(siBuilder.version("1.0.0").build().setVersionOnly())
        .build())
    .define(ExtendedConfigKey.builder("bar.property")
        .documentation("The bar connector property.")
        .since(siBuilder.version("1.1.0").build().setVersionOnly())
        .build()))
```

the documentation would be:

```
foo.property:
    The foo connector property.
    
    available since: 1.0.0
    
bar.property
    The bar connector property.
    
    availabe since: 1.1.0    
```

`setVersionOnly()` creates an override that removes the groupId and artifactId from the display and only shows the version.  Since this is an override it is possible to retrieve the original values or to change the override to something else.

## How to override inherited properties.

When it comes time to generate the documentation we need to be able to replace version in inherited properties.  For example if our foo-connector uses a fragment that defines the connection to the foo-engine and that has a version property that was introduced in 1.5.3, then our documentation would have a line that indicated version "com.example:foo-engine:1.5.3" and we have no way to shorten that or modify it.  This is what the `SinceInfoMapBuilder` is for.

# SinceInfoMapBuilder

The `SinceInfoMapBuilder` creates a map of fully qualified versions to display versions.  There are a number of methods to create the map programmatically but these are generally for testing purposes.  The common solution is to create a file that preserves the mapping and then read that in when the documentation is generated.

As we saw above the format for the version is `groupId:artifactId:version`. The format for the SinceInfoMapBuilder input modifies and extends this pattern to `groupId:artifactId:versionPattern:[[dispGroup:]dispArtifact:]version` where the `[` and `]` denote optional values.

the `versionPattern` field defines the range of versions to match.  It amy be any pattern acceptable to the [Maven dependency version requirement specification](https://maven.apache.org/pom.html#Dependency_Version_Requirement_Specification).

To solve the problem noted above with the version string of "com.example:foo-engine:1.5.3" showing up in version 1.0.0 of our project. let's assume that we are actually including version 1.7 of the foo-engine which contains the 1.5.3 version property.

```
com.example:foo-engine:[,1.7]:1.0.0
```

An input file with the above will have all properties up to and including version 1.7 of the foo-engine listed as being available since 1.0.0 (our version).

If we later produce a 1.1.0 version that updates the foo-engine to version 2.0 and it includes additional properties we would modify the input file to read:

```
com.example:foo-engine:[,1.7]:1.0.0
com.example:foo-engine:(1.7,2.0]:1.1.0
```

Now all foo-engine properties up to and including 1.7 are listed as being available since 1.0.0 of our product, and all properties from 1.7 (excluding 1.7) up to and including 2.0 are listed as being available in our project since version 1.1.0.

## Usage requirements

The `SinceInfoMapBuilder` requires the org.apache.maven:maven-artifact jar.  We currently build with version 3.9.6 but earlier versions should also work.  The library is used to parse and match the version patterns.  If building with Maven the jar should be available.

The `SinceInfoMapBuilder` usage should be integrated into the documentation generation and not used in the main code.  It can also be used in testing.  If used in the main code the org.apache.maven:maven-artifact jar will need to be included in the packaging.



