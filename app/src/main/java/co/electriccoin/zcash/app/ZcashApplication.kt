package co.electriccoin.zcash.app

import co.electriccoin.zcash.app.di.coreModule
import co.electriccoin.zcash.app.di.repositoryModule
import co.electriccoin.zcash.app.di.useCaseModule
import co.electriccoin.zcash.app.di.viewModelModule
import co.electriccoin.zcash.crash.android.GlobalCrashReporter
import co.electriccoin.zcash.preference.api.StandardPreferenceProvider
import co.electriccoin.zcash.spackle.StrictModeCompat
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@Suppress("unused")
class ZcashApplication : CoroutineApplication() {
    private val standardPreferenceProvider by inject<StandardPreferenceProvider>()

    override fun onCreate() {
        super.onCreate()

        configureLogging()

        configureStrictMode()

        startKoin {
            androidLogger()
            androidContext(this@ZcashApplication)
            modules(
                coreModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }

        // Since analytics will need disk IO internally, we want this to be registered after strict
        // mode is configured to ensure none of that IO happens on the main thread
        configureAnalytics()
    }

    private fun configureLogging() {
        Twig.initialize(applicationContext)
        Twig.info { "Starting application…" }

        if (!BuildConfig.DEBUG) {
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
        if (GlobalCrashReporter.register(this)) {
            applicationScope.launch {
                StandardPreferenceKeys.IS_ANALYTICS_ENABLED.observe(standardPreferenceProvider).collect {
                    if (it) {
                        GlobalCrashReporter.enable()
                    } else {
                        GlobalCrashReporter.disableAndDelete()
                    }
                }
            }
        }
    }
}
