package co.electriccoin.zcash.configuration.internal.intent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.electriccoin.zcash.configuration.model.map.StringConfiguration
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.datetime.Clock

class IntentConfigurationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.defuse()?.let {
            val key = it.getStringExtra(ConfigurationIntent.EXTRA_STRING_KEY)
            val value = it.getStringExtra(ConfigurationIntent.EXTRA_STRING_VALUE)

            if (null != key) {
                val existingConfiguration = IntentConfigurationProvider.peekConfiguration().configurationMapping
                val newConfiguration = if (null == value) {
                    existingConfiguration.remove(key)
                } else {
                    existingConfiguration + (key to value)
                }

                IntentConfigurationProvider.setConfiguration(
                    StringConfiguration(newConfiguration.toPersistentMap(), Clock.System.now())
                )
            }
        }
    }
}

// https://issuetracker.google.com/issues/36927401
private fun Intent.defuse(): Intent? {
    return try {
        extras?.containsKey(null)
        this
    } catch (@Suppress("SwallowedException", "TooGenericExceptionCaught") e: Exception) {
        null
    }
}
