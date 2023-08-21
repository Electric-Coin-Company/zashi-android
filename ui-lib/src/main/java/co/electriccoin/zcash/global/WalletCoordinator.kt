package co.electriccoin.zcash.global

import android.content.Context
import cash.z.ecc.android.sdk.WalletCoordinator
import cash.z.ecc.android.sdk.block.processor.CompactBlockProcessor
import co.electriccoin.zcash.spackle.LazyWithArgument
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceKeys
import co.electriccoin.zcash.ui.preference.EncryptedPreferenceSingleton
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

private val lazy = LazyWithArgument<Context, WalletCoordinator> {
    /**
     * A flow of the user's stored wallet.  Null indicates that no wallet has been stored.
     */
    val persistableWallet = flow {
        // EncryptedPreferenceSingleton.getInstance() is a suspending function, which is why we need
        // the flow builder to provide a coroutine context.
        val encryptedPreferenceProvider = EncryptedPreferenceSingleton.getInstance(it)

        emitAll(EncryptedPreferenceKeys.PERSISTABLE_WALLET.observe(encryptedPreferenceProvider))
    }

    WalletCoordinator(
        context = it,
        persistableWallet = persistableWallet,
        syncAlgorithm = CompactBlockProcessor.SyncAlgorithm.SPEND_BEFORE_SYNC
    )
}

fun WalletCoordinator.Companion.getInstance(context: Context) = lazy.getInstance(context)
