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

package androidx.wear.protolayout.material3

import android.content.Context
import android.provider.Settings
import androidx.annotation.VisibleForTesting
import androidx.wear.protolayout.ColorBuilders.ColorProp
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.ModifiersBuilders.Corner
import androidx.wear.protolayout.material3.ColorTokens.ColorToken
import androidx.wear.protolayout.material3.Shape.ShapeToken

/**
 * Receiver scope which is used by all ProtoLayout Material3 components and layout to support
 * opinionated defaults and to provide the global information for styling Material3 components.
 *
 * The MaterialScope includes:
 * * theme, which is used to retrieve the color, typography or shape values.
 * * [DeviceParameters], which contains screen size, font scale, renderer schema version etc.
 * * Default usage of system theme, with the option to opt out.
 */
// TODO: b/352308384 - Add helper to read the exported Json or XML file from the Material Theme
//    Builder tool.
// TODO: b/350927030 - Customization setters of shape and typography, which are not fully
// customizable.
public open class MaterialScope
/**
 * @param context The Android Context for the Tile service
 * @param deviceConfiguration The device parameters for where the components will be rendered
 * @param allowDynamicTheme If dynamic colors theme should be used on components, meaning that
 *   colors will follow the system theme if enabled on the device. If not set, defaults to using the
 *   system theme
 * @param customColorScheme The customized colors to be used. If not set, default Material theme
 *   would be applied
 */
internal constructor(
    internal val context: Context,
    /** The device parameters for where the components will be rendered. */
    public val deviceConfiguration: DeviceParameters,
    internal val allowDynamicTheme: Boolean = true,
    customColorScheme: Map<Int, ColorProp> = emptyMap()
) {
    internal val theme: MaterialTheme = MaterialTheme(customColorScheme)
}

/**
 * Retrieves the [Corner] shape from the default Material theme with shape token name.
 *
 * @throws IllegalArgumentException if the token name is not recognized as one of the constants in
 *   [Shape]
 */
public fun MaterialScope.getCorner(@ShapeToken shapeToken: Int): Corner =
    theme.getCornerShape(shapeToken)

/**
 * Retrieves the [ColorProp] from the customized or default Material theme or dynamic system theme
 * with the given color token name.
 *
 * @throws IllegalArgumentException if the token name is not recognized as one of the constants in
 *   [ColorTokens]
 */
public fun MaterialScope.getColorProp(@ColorToken colorToken: Int): ColorProp =
    if (context.isDynamicThemeEnabled() && allowDynamicTheme) {
        DynamicMaterialTheme.getColorProp(context, colorToken) ?: theme.getColor(colorToken)
    } else {
        theme.getColor(colorToken)
    }

/**
 * Returns whether the dynamic colors theme (colors following the system theme) is enabled. If
 * enabled, and [MaterialScope] is opted in to using dynamic theme, colors will change whenever
 * system theme changes.
 */
public fun Context.isDynamicThemeEnabled(): Boolean {
    val overlaySetting: String? =
        Settings.Secure.getString(contentResolver, THEME_CUSTOMIZATION_OVERLAY_PACKAGES)
    return (!overlaySetting.isNullOrEmpty() && overlaySetting != "{}")
}

/** This maps to `android.provider.Settings.Secure.THEME_CUSTOMIZATION_OVERLAY_PACKAGES`. */
@VisibleForTesting
internal const val THEME_CUSTOMIZATION_OVERLAY_PACKAGES: String =
    "theme_customization_overlay_packages"
