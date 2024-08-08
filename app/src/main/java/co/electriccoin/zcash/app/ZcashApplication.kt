package co.electriccoin.zcash.app

import co.electriccoin.zcash.crash.android.GlobalCrashReporter
import co.electriccoin.zcash.spackle.StrictModeCompat
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.preference.StandardPreferenceKeys
import co.electriccoin.zcash.ui.preference.StandardPreferenceSingleton
import com.flexa.core.Flexa
import com.flexa.core.shared.AppAccount
import com.flexa.core.shared.FlexaClientConfiguration
import com.flexa.core.theme.FlexaTheme
import kotlinx.coroutines.launch
import java.util.UUID

@Suppress("unused")
class ZcashApplication : CoroutineApplication() {
    override fun onCreate() {
        super.onCreate()

        configureLogging()

        configureStrictMode()

        // Since analytics will need disk IO internally, we want this to be registered after strict
        // mode is configured to ensure none of that IO happens on the main thread
        configureAnalytics()

        Flexa.init(
            FlexaClientConfiguration(
                context = applicationContext,
                publishableKey = "", // TODO
                theme = FlexaTheme(
                    useDynamicColorScheme = true,
                ),
                appAccounts = arrayListOf(
                    AppAccount(
                        accountId = UUID.randomUUID().toString(),
                        displayName = "My Wallet",
                        icon = "https://flexa.network/static/4bbb1733b3ef41240ca0f0675502c4f7/d8419/flexa-logo%403x.png",
                        availableAssets = emptyList(),
                        custodyModel = i.e.a
                    )
                ),
                webViewThemeConfig = "{\n" +
                    "    \"android\": {\n" +
                    "        \"light\": {\n" +
                    "            \"backgroundColor\": \"#100e29\",\n" +
                    "            \"sortTextColor\": \"#ed7f60\",\n" +
                    "            \"titleColor\": \"#ffffff\",\n" +
                    "            \"cardColor\": \"#2a254e\",\n" +
                    "            \"borderRadius\": \"15px\",\n" +
                    "            \"textColor\": \"#ffffff\"\n" +
                    "        },\n" +
                    "        \"dark\": {\n" +
                    "            \"backgroundColor\": \"#100e29\",\n" +
                    "            \"sortTextColor\": \"#ed7f60\",\n" +
                    "            \"titleColor\": \"#ffffff\",\n" +
                    "            \"cardColor\": \"#2a254e\",\n" +
                    "            \"borderRadius\": \"15px\",\n" +
                    "            \"textColor\": \"#ffffff\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}"
            )
        )
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
}
