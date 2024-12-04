package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider

interface ZashiSpendingKeyDataSource {
    suspend fun getZashiSpendingKey(): UnifiedSpendingKey
}

class ZashiSpendingKeyDataSourceImpl(
    private val persistableWalletProvider: PersistableWalletProvider
): ZashiSpendingKeyDataSource {
    override suspend fun getZashiSpendingKey(): UnifiedSpendingKey {
        return deriveSpendingKey(
            persistableWalletProvider.getPersistableWallet()
        )!! // TODO keystone spending key
    }

    private suspend fun deriveSpendingKey(persistableWallet: PersistableWallet): UnifiedSpendingKey? {
        // // TODO keystone spending key
        // val bip39Seed =
        //     withContext(Dispatchers.IO) {
        //         Mnemonics.MnemonicCode(persistableWallet.seedPhrase.joinToString()).toSeed()
        //     }
        // val spendingKey = DerivationTool.getInstance().deriveUnifiedSpendingKey(
        //     seed = bip39Seed,
        //     network = persistableWallet.network,
        //     accountIndex = 0,
        // )
        // return spendingKey
        return null
    }
}