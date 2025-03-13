package co.electriccoin.zcash.global

import android.content.Context
import cash.z.ecc.android.sdk.WalletCoordinator
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.preference.PersistableWalletPreferenceDefault
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

fun WalletCoordinator.Companion.newInstance(
    context: Context,
    encryptedPreferenceProvider: EncryptedPreferenceProvider,
    persistableWalletPreference: PersistableWalletPreferenceDefault
): WalletCoordinator =
    WalletCoordinator(
        context = context,
        persistableWallet =
            flow {
                emitAll(persistableWalletPreference.observe(encryptedPreferenceProvider()))
            },
        accountName = context.getString(R.string.zashi_wallet_name),
        keySource = ZASHI_KEYSOURCE
    )

private const val ZASHI_KEYSOURCE = "zashi"
