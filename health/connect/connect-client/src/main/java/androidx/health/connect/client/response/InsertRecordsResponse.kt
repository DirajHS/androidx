/*
 * Copyright (C) 2022 The Android Open Source Project
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
package androidx.health.connect.client.response

import androidx.annotation.RestrictTo

/**
 * Response to record insertion.
 *
 * @see [androidx.health.connect.client.HealthConnectClient.insertRecords]
 */
public class InsertRecordsResponse
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
    /*
     * Contains
     * [androidx.health.connect.client.metadata.Metadata.recordId] of inserted [Record] in same order as
     * passed to [androidx.health.connect.client.HealthDataClient.insertRecords].
     */
    val recordIdsList: List<String>
)
