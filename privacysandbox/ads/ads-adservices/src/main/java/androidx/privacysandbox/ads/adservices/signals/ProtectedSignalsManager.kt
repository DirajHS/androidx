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

package androidx.privacysandbox.ads.adservices.signals

import android.adservices.common.AdServicesPermissions
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.privacysandbox.ads.adservices.common.ExperimentalFeatures
import androidx.privacysandbox.ads.adservices.internal.AdServicesInfo

/** This class provides APIs for app and ad-SDKs to join / leave custom audiences. */
abstract class ProtectedSignalsManager internal constructor() {
    /**
     * The updateSignals API will retrieve a JSON from the URI that describes which signals to add
     * or remove. This API also allows registering the encoder endpoint. The endpoint is used to
     * download an encoding logic, which enables encoding the signals.
     *
     * <p>The top level keys for the JSON must correspond to one of 5 commands:
     *
     * <p>"put" - Adds a new signal, overwriting any existing signals with the same key. The value
     * for this is a JSON object where the keys are base 64 strings corresponding to the key to put
     * for and the values are base 64 string corresponding to the value to put.
     *
     * <p>"append" - Appends a new signal/signals to a time series of signals, removing the oldest
     * signals to make room for the new ones if the size of the series exceeds the given maximum.
     * The value for this is a JSON object where the keys are base 64 strings corresponding to the
     * key to append to and the values are objects with two fields: "values" and "maxSignals" .
     * "values" is a list of base 64 strings corresponding to signal values to append to the time
     * series. "maxSignals" is the maximum number of values that are allowed in this timeseries. If
     * the current number of signals associated with the key exceeds maxSignals the oldest signals
     * will be removed. Note that you can append to a key added by put. Not that appending more than
     * the maximum number of values will cause a failure.
     *
     * <p>"put_if_not_present" - Adds a new signal only if there are no existing signals with the
     * same key. The value for this is a JSON object where the keys are base 64 strings
     * corresponding to the key to put for and the values are base 64 string corresponding to the
     * value to put.
     *
     * <p>"remove" - Removes the signal for a key. The value of this is a list of base 64 strings
     * corresponding to the keys of signals that should be deleted.
     *
     * <p>"update_encoder" - Provides an action to update the endpoint, and a URI which can be used
     * to retrieve an encoding logic. The sub-key for providing an update action is "action" and the
     * values currently supported are:
     * <ol>
     * <li>"REGISTER" : Registers the encoder endpoint if provided for the first time or overwrites
     *   the existing one with the newly provided endpoint. Providing the "endpoint" is required for
     *   the "REGISTER" action.
     * </ol>
     *
     * <p>The sub-key for providing an encoder endpoint is "endpoint" and the value is the URI
     * string for the endpoint.
     *
     * <p>Key may only be operated on by one command per JSON. If two command attempt to operate on
     * the same key, this method will through an {@link IllegalArgumentException}
     *
     * <p>This call fails with an {@link SecurityException} if
     * <ol>
     * <li>the {@code ownerPackageName} is not calling app's package name and/or
     * <li>the buyer is not authorized to use the API.
     * </ol>
     *
     * <p>This call fails with an {@link IllegalArgumentException} if
     * <ol>
     * <li>The JSON retrieved from the server is not valid.
     * <li>The provided URI is invalid.
     * </ol>
     *
     * <p>This call fails with {@link LimitExceededException} if the calling package exceeds the
     * allowed rate limits and is throttled.
     *
     * <p>This call fails with an {@link IllegalStateException} if an internal service error is
     * encountered.
     */
    @ExperimentalFeatures.Ext12OptIn
    @RequiresPermission(AdServicesPermissions.ACCESS_ADSERVICES_PROTECTED_SIGNALS)
    abstract suspend fun updateSignals(request: UpdateSignalsRequest)

    companion object {
        private const val TAG = "ProtectedSignalsManager"

        /**
         * Creates [ProtectedSignalsManager].
         *
         * @return ProtectedSignalsManager object. If the device is running an incompatible build,
         *   the value returned is null.
         */
        @JvmStatic
        @SuppressLint("NewApi")
        @ExperimentalFeatures.Ext12OptIn
        fun obtain(context: Context): ProtectedSignalsManager? {
            return if (AdServicesInfo.adServicesVersion() >= 12) {
                Log.d(TAG, "Adservices version 12 detected, obtaining ProtectedSignalsManagerImpl.")
                ProtectedSignalsManagerImpl(
                    android.adservices.signals.ProtectedSignalsManager.get(context)
                )
            } else {
                Log.d(
                    TAG,
                    "Adservices less than 12, returning null for ProtectedSignalsManager.obtain."
                )
                null
            }
        }
    }
}
