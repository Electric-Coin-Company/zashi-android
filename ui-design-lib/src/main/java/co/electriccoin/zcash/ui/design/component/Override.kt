package co.electriccoin.zcash.ui.design.component

import android.content.res.Configuration
import android.os.LocaleList
import android.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.StateFlow

/**
 * Wrap a Composable with a way to override the Android Configuration.  This is primarily useful
 * for automated tests.
 */
@Composable
fun Override(
    configurationOverrideFlow: StateFlow<ConfigurationOverride?>,
    content: @Composable () -> Unit,
) {
    val configurationOverride = configurationOverrideFlow.collectAsState().value
    val contentSlot = remember { movableContentOf { content() } }

    if (null == configurationOverride) {
        contentSlot()
    } else {
        val configuration = configurationOverride.newConfiguration(LocalConfiguration.current)

        val contextWrapper =
            run {
                val context = LocalContext.current
                object : ContextThemeWrapper(context, null) {
                    init {
                        applyOverrideConfiguration(configuration)
                    }
                }
            }

        CompositionLocalProvider(
            LocalConfiguration provides configuration,
            LocalContext provides contextWrapper
        ) {
            contentSlot()
        }
    }
}

data class ConfigurationOverride(
    val uiMode: UiMode?,
    val locale: LocaleList?
) {
    fun newConfiguration(fromConfiguration: Configuration) =
        Configuration(fromConfiguration).apply {
            this@ConfigurationOverride.uiMode?.let {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or it.flag()
            }

            this@ConfigurationOverride.locale?.let {
                setLocales(it)
            }
        }
}

enum class UiMode {
    Light,
    Dark
}

private fun UiMode.flag() =
    when (this) {
        UiMode.Light -> Configuration.UI_MODE_NIGHT_NO
        UiMode.Dark -> Configuration.UI_MODE_NIGHT_YES
    }
