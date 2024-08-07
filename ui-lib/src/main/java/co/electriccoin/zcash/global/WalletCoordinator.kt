package co.electriccoin.zcash.global

import android.content.Context
import cash.z.ecc.android.sdk.WalletCoordinator
import co.electriccoin.zcash.preference.api.EncryptedPreferenceProvider
import co.electriccoin.zcash.ui.preference.PersistableWalletPreferenceDefault
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

fun WalletCoordinator.Companion.newInstance(
    context: Context,
    encryptedPreferenceProvider: EncryptedPreferenceProvider,
    persistableWalletPreference: PersistableWalletPreferenceDefault
): WalletCoordinator {
    return WalletCoordinator(
        context = context,
        persistableWallet =
            flow {
                emitAll(persistableWalletPreference.observe(encryptedPreferenceProvider))
            }
    )
}
