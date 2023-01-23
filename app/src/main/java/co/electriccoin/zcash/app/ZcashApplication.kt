package co.electriccoin.zcash.app

import co.electriccoin.zcash.crash.android.GlobalCrashReporter
import co.electriccoin.zcash.spackle.StrictModeCompat
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import kotlinx.coroutines.launch

@Suppress("unused")
class ZcashApplication : CoroutineApplication() {

    override fun onCreate() {
        super.onCreate()

        configureLogging()

        configureStrictMode()

        // Since analytics will need disk IO internally, we want this to be registered after strict
        // mode is configured to ensure none of that IO happens on the main thread
        configureAnalytics()
    }

    private fun configureLogging() {
        Twig.initialize(applicationContext)
        Twig.info { "Starting application…" }

        if (BuildConfig.DEBUG) {
            // This is an internal API to the Zcash SDK to enable logging; it could change in the future
            cash.z.ecc.android.sdk.internal.Twig.enabled(true)
        } else {
            // In release builds, logs should be stripped by R8 rules
            Twig.assertLoggingStripped()
        }
    }

    private fun configureStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictModeCompat.enableStrictMode(BuildConfig.IS_STRICT_MODE_CRASH_ENABLED)
        }
    }

    private fun configureAnalytics() {
        GlobalCrashReporter.register(this)

        applicationScope.launch {
            val prefs = StandardPreferenceSingleton.getInstance(applicationContext)
            StandardPreferenceKeys.IS_ANALYTICS_ENABLED.observe(prefs).collect {
                if (it) {
                    GlobalCrashReporter.enable()
                } else {
                    GlobalCrashReporter.disableAndDelete()
                }
            }
        }
    }
}
