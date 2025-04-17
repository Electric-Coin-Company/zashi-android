package co.electriccoin.zcash.app

import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import co.electriccoin.zcash.crash.android.GlobalCrashReporter
import co.electriccoin.zcash.crash.android.di.CrashReportersProvider
import co.electriccoin.zcash.crash.android.di.crashProviderModule
import co.electriccoin.zcash.di.addressBookModule
import co.electriccoin.zcash.di.coreModule
import co.electriccoin.zcash.di.dataSourceModule
import co.electriccoin.zcash.di.mapperModule
import co.electriccoin.zcash.di.metadataModule
import co.electriccoin.zcash.di.providerModule
import co.electriccoin.zcash.di.repositoryModule
import co.electriccoin.zcash.di.useCaseModule
import co.electriccoin.zcash.di.viewModelModule
import co.electriccoin.zcash.preference.StandardPreferenceProvider
import co.electriccoin.zcash.spackle.StrictModeCompat
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.provider.ApplicationStateProvider
import co.electriccoin.zcash.ui.common.repository.FlexaRepository
import co.electriccoin.zcash.ui.common.repository.HomeMessageCacheRepository
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ZcashApplication : CoroutineApplication() {
    private val standardPreferenceProvider by inject<StandardPreferenceProvider>()
    private val flexaRepository by inject<FlexaRepository>()
    private val applicationStateProvider: ApplicationStateProvider by inject()
    private val getAvailableCrashReporters: CrashReportersProvider by inject()
    private val homeMessageCacheRepository: HomeMessageCacheRepository by inject()

    override fun onCreate() {
        super.onCreate()

        configureLogging()

        configureStrictMode()

        startKoin {
            androidLogger()
            androidContext(this@ZcashApplication)
            modules(
                coreModule,
                providerModule,
                crashProviderModule,
                dataSourceModule,
                repositoryModule,
                addressBookModule,
                metadataModule,
                useCaseModule,
                mapperModule,
                viewModelModule
            )
        }

        // Observe the application process lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                applicationStateProvider.setApplicationState(event)
            }
        )

        // Since analytics will need disk IO internally, we want this to be registered after strict
        // mode is configured to ensure none of that IO happens on the main thread
        configureAnalytics()

        flexaRepository.init()
        homeMessageCacheRepository.init()
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
        if (GlobalCrashReporter.register(this, getAvailableCrashReporters())) {
            applicationScope.launch {
                StandardPreferenceKeys.IS_ANALYTICS_ENABLED.observe(standardPreferenceProvider()).collect {
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
