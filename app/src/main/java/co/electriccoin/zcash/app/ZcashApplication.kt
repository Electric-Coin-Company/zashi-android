package co.electriccoin.zcash.app

import android.app.Application
import co.electriccoin.zcash.crash.android.GlobalCrashReporter
import co.electriccoin.zcash.spackle.StrictModeCompat
import co.electriccoin.zcash.spackle.Twig

@Suppress("unused")
class ZcashApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Twig.initialize(applicationContext)
        Twig.info { "Starting application…" }

        if (BuildConfig.DEBUG) {
            StrictModeCompat.enableStrictMode(BuildConfig.IS_STRICT_MODE_CRASH_ENABLED)

            // This is an internal API to the Zcash SDK to enable logging; it could change in the future
            cash.z.ecc.android.sdk.internal.Twig.enabled(true)
        } else {
            // In release builds, logs should be stripped by R8 rules
            Twig.assertLoggingStripped()
        }

        GlobalCrashReporter.register(this)
    }
}
