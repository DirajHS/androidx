/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Usage with implicit "this" receiver.
 */
@SuppressWarnings({"deprecation", "unused", "WrongPackageStatement", "DefaultPackage"})
class MethodWithNoImportsOrPackage {
    @Deprecated
    @androidx.annotation.ReplaceWith(expression = "newMethod(obj)",
            imports = "androidx.annotation.Deprecated")
    void oldMethodSingleImport(Object obj) {}

    @Deprecated
    @androidx.annotation.ReplaceWith(expression = "newMethod(obj)",
            imports = {"androidx.annotation.Deprecated", "androidx.annotation.NonNull"})
    void oldMethodMultiImport(Object obj) {}

    void newMethod(Object obj) {}

    void usageSingleImport() {
        oldMethodSingleImport(null);
    }

    void usageMultiImport() {
        oldMethodMultiImport(null);
    }
}
