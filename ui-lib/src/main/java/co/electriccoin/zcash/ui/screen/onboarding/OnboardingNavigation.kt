@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.onboarding

import android.content.Context
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel

/**
 * Persists existing wallet together with the backup complete flag to disk. Be aware of that, it
 * triggers navigation changes, as we observe the WalletViewModel.secretState.
 *
 * Write the backup complete flag first, then the seed phrase. That avoids the UI flickering to
 * the backup screen. Assume if a user is restoring from a backup, then the user has a valid backup.
 *
 * @param seedPhrase to be persisted as part of the wallet.
 * @param birthday optional user provided birthday to be persisted as part of the wallet.
 */
internal fun persistExistingWalletWithSeedPhrase(
    context: Context,
    walletViewModel: WalletViewModel,
    seedPhrase: SeedPhrase,
    birthday: BlockHeight
) {
    walletViewModel.persistExistingWalletWithSeedPhrase(
        network = ZcashNetwork.fromResources(context),
        seedPhrase = seedPhrase,
        birthday = birthday
    )
}
