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
# ConfigFragment Usage

Config fragments encapsulate logical fragments of configuration that may be used across multiple Connectors or across the source/sink of a connector pair.  They provide the definition of configuration options and implementation of data accessors for those options.

## Best practices

  * All implementing classes should be final.
  * Property names should be defined as `static final String` variables.  These variables should be package private so that test cases can utilize them if necessary.
  * `ConfigFragments` implementations should include a `public static class Setter extends AbstractFragmentSetter<Setter>` that sets the values managed by the fragment.  This class should be instantiated via a `public static Setter setter(Map<String, String> props)` method. See [The setter](#the-setter) section below.
  * Access to the defined properties should be through getters.
  * Access to objects composed of one or more property should be through a getter.
  * Fragments should calculate default values when the default values are dependant upon values of other parameters.
  * Fragments should override the `public void validate(final Map<String, ConfigValue> configMap)` method if there are properties that are mutually exclusive or that have co-restrictions.

## Defining options.

Traditionally `ConfigDef` objects are defined inline using the `ConfigDef.config()` method.  The fragment defines the properties necessary to produce the data the fragment manages.  The properties defined by the fragment are added to the `ConfigDef` via the `public static ConfigDef update(final ConfigDef configDef)` method.

A simple example of this method is:

```java
    static final String PARAM1 = "foo.param1";
    static final String PARAM2 = "foo.param2";
    
    public static ConfigDef update(final ConfigDef configDef) {
        return configDef.define(ExtendedConfigKey.builder(PARAM1).type(ConfigDef.Type.INT)
                .defaultValue(1).validator(atLeast(1)).importance(ConfigDef.Importance.HIGH)
                .documentation("The first foo parameter").build())
            .define(ExtendedConfigKey.builder(PARAM2)
                .documentation("The task ID that this connector is working with.").build());
    }
```

This defines "foo.param1" and "foo.param2". 

## The setter

The Setter is used to make testing documentation easier to read and to restrict the pollution of the code with the defined property names from the fragment.

The setter should extend the `AbstractFragmentSetter` which handles populating the map of property to string value while accepting standard data types. 

The setter for the above example would look something like:

```java
    public static Setter setter(Map<String, String> props) {
        return new Setter(props);
    }

    public static class Setter extends AbstractFragmentSetter<Setter> {
        private Setter(Map<String, String> props) {
            super(props);
        }
        
        public Setter param1(int value) {
            return setValue(PARAM1, value);
        }

        public Setter param2(String value) {
            return setValue(PARAM2, value);
        }
    }
```

## The getters

The `ConfigFragment` constructor requires a `FragmentDataAccess` argument.  All access to the data for the parameters is performed via `ConfigFragment` methods that access the `FragmentDataAccess` argument.

The getters for the example above would look something like:

```java
public class FooFragment extends ConfigFragment {
    public FooFragment(FragmentDataAccess dataAccess) {
        super(dataAccess);
    }
    
    public int getParam1() {
        return getInt(PARAM1);
    }
    
    public String getParam2() {
        return getString(PARAM2);
    }
}
```

## Composed values

In some cases one or more properties are used to make a single object.  If, in our example, PARAM2 was the name of an enum value we might provide a method like:

```java
    public enum Thing {ONE, TWO}

    public Thing getThing() {
        return Think.valueOf(getString(PARAM2).toUpperCase(ROOT));
    }
```

## Calculating default values

In some cases default values for properties may be dependant upon other properties.  For example in expanding upon the above example.  Suppose that the default value for PARAM1 is 5 when PARAM2 is Thing.ONE and 10 when PARAM2 is Thing.TWO.  To do this we implement a `public static postProcess(CommonConfig.ChangeTrackingMap configMap)` method like this:

````java
    public static postProcess(CommonConfig.ChangeTrackingMap configMap) {
        if (configMap.get(PARAM1).value() == null) {
            configMap.override(PARAM1, configMap.get(PARAM2).value().toString().equalsIgnoreCase("ONE") ? 5 : 10);
        }
    }
````

This will set the default values if PARAM1 is not already set. 

## Validation

The `ConfigFragment` supports validation by participating in the `CommonConfigDef.multiValidate()` method through the `public void validate(final Map<String, ConfigValue> configMap)` method.

The values in the `configMap` argument are of the type defined in the ConfigDef for the property.  See [Notes on data types](./GeneralArchitecture.html#Notes-on-data-types) ins the General Architecture documentation.

Building on our previous example, if we assume that `Thing.ONE` limits PARAM1 to values less than 10 and `Thing.TWO` limits to values less than 20 we could implement something like the following:

```java
    public void validate(final Map<String, ConfigValue> configMap) {
        if (configMap.get(PARAM2).value().equalsIgnoreCase("ONE") && configMap.get(PARAM1).value >= 10) {
            registerIssue(configMap, PARAM1, schemaEnable.value(),
                    String.format("%s must be less than 10 if %s is %s.",
                            PARAM1, PARAM2, configMap.get(PARAM2).value()));    
        } else if (configMap.get(PARAM1).value() >= 20) {
            registerIssue(configMap, PARAM1, schemaEnable.value(),
                    String.format("%s must be less than 20 if %s is %s.",
                            PARAM1, PARAM2, configMap.get(PARAM2).value()));
        }
    }
```

The above code displays detailed error messages like: "Invalid value 11 for configuration foo.param1: foo.param1 must be less than 10 if foo.param2 is ONE."
