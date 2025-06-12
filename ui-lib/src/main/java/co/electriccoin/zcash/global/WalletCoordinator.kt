package co.electriccoin.zcash.global

import android.content.Context
import cash.z.ecc.android.sdk.WalletCoordinator
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider

fun WalletCoordinator.Companion.newInstance(
    context: Context,
    persistableWalletProvider: PersistableWalletProvider
): WalletCoordinator =
    WalletCoordinator(
        context = context,
        persistableWallet = persistableWalletProvider.persistableWallet,
        accountName = context.getString(R.string.zashi_wallet_name),
        keySource = ZASHI_KEYSOURCE
    )

private const val ZASHI_KEYSOURCE = "zashi"
