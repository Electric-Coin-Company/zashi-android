package co.electriccoin.zcash.ui.design.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import co.electriccoin.zcash.ui.design.util.createConfiguration

/**
 * @param isDarkTheme true to force dark theme, false to force light theme, null to not override
 */
@Composable
fun ConfigurationOverride(
    isDarkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val newConfiguration = remember(configuration) {
        configuration.createConfiguration(isDarkTheme)
    }
    val newContext by remember(context) {
        derivedStateOf {
            context.createConfigurationContext(newConfiguration)
        }
    }

    CompositionLocalProvider(
        LocalConfiguration provides newConfiguration,
        LocalContext provides newContext
    ) {
        content()
    }
}

