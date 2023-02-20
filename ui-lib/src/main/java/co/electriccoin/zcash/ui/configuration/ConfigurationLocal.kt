package co.electriccoin.zcash.ui.configuration

import androidx.compose.runtime.compositionLocalOf
import co.electriccoin.zcash.configuration.model.map.Configuration
import co.electriccoin.zcash.configuration.model.map.StringConfiguration
import kotlinx.collections.immutable.persistentMapOf

val RemoteConfig = compositionLocalOf<Configuration> { StringConfiguration(persistentMapOf(), null) }
