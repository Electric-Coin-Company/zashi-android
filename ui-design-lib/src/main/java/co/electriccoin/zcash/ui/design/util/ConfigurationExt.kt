package co.electriccoin.zcash.ui.design.util

import android.content.res.Configuration

/**
 * @param isDarkTheme true to force dark theme, false to force light theme, null to not override
 */
fun Configuration.createConfiguration(
    isDarkTheme: Boolean?
): Configuration =
    Configuration(this).apply {
        when (isDarkTheme) {
            true -> {
                uiMode = Configuration.UI_MODE_NIGHT_YES
            }

            false -> {
                uiMode = Configuration.UI_MODE_NIGHT_NO
            }

            null -> {
                // do not override
            }
        }
    }
