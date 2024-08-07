package co.electriccoin.zcash.app.di

import androidx.biometric.BiometricManager
import cash.z.ecc.android.sdk.WalletCoordinator
import co.electriccoin.zcash.configuration.AndroidConfigurationFactory
import co.electriccoin.zcash.global.newInstance
import co.electriccoin.zcash.preference.AndroidPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.preference.PersistableWalletPreferenceDefault
import co.electriccoin.zcash.ui.screen.update.AppUpdateChecker
import co.electriccoin.zcash.ui.screen.update.AppUpdateCheckerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule =
    module {
        single {
            WalletCoordinator.newInstance(
                context = get(),
                encryptedPreferenceProvider = get(),
                persistableWalletPreference = get(),
            )
        }

        single {
            PersistableWalletPreferenceDefault(PreferenceKey("persistable_wallet"))
        }

        single {
            AndroidPreferenceProvider.newEncrypted(context = get(), filename = "co.electriccoin.zcash.encrypted")
        }

        single {
            AndroidPreferenceProvider.newStandard(context = get(), filename = "co.electriccoin.zcash")
        }

        single {
            BiometricManager.from(
                // context =
                get()
            )
        }

        factoryOf(::AppUpdateCheckerImpl) bind AppUpdateChecker::class

        factory { AndroidConfigurationFactory.newInstance() }
    }
