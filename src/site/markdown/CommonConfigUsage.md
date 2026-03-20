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
# CommonConfig Usage

The `CommonConfig` class provides the basis for all Configurations.  It provides a concrete example of how to merge Fragments into a config.  The `CommonConfig` provides a simple mechanism to validate that parameters are correct and to calculate complex default values.

## Validating parameters are correct

The CommonConfig constructor requires a CommonConfigDef argument.  The constructor will call the `CommonConfigDef.multiValidate()` method.  This calls the `ConfigFragment.validate()` methods, providing the fragments the opportunity to detect conflicting arguments.  If there are any issues detected during the `multiValidate()` and exception will be thrown with detailed descriptions of each of the issues.

See [ConfigFragment Usage validation documentation](./ConfigFramgmentUsage.html#Validation) for details.

## Calculating complex default values

In some cases default values can only be determined by looking at other properties.  To support this operation `CommonConfig` implementations should implement the `fragmentPostProcess(ChangeTrackingMap map)` method to call the `postProcess(CommonConfig.ChangeTrackingMap configMap)` methods on embedded Fragments. This will allow the Fragments to properly set the default values before validation is checked.

