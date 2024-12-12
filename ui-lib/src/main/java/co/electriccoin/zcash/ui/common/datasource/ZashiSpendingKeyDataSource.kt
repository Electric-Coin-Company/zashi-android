package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.UnifiedSpendingKey
import cash.z.ecc.android.sdk.tool.DerivationTool
import co.electriccoin.zcash.ui.common.provider.PersistableWalletProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ZashiSpendingKeyDataSource {
    suspend fun getZashiSpendingKey(): UnifiedSpendingKey
}

class ZashiSpendingKeyDataSourceImpl(
    private val persistableWalletProvider: PersistableWalletProvider,
    private val accountDataSource: AccountDataSource,
) : ZashiSpendingKeyDataSource {
    override suspend fun getZashiSpendingKey(): UnifiedSpendingKey {
        return deriveSpendingKey(
            persistableWalletProvider.getPersistableWallet()
        )
    }

    private suspend fun deriveSpendingKey(persistableWallet: PersistableWallet): UnifiedSpendingKey {
        val bip39Seed =
            withContext(Dispatchers.IO) {
                Mnemonics.MnemonicCode(persistableWallet.seedPhrase.joinToString()).toSeed()
            }
        val spendingKey =
            DerivationTool.getInstance().deriveUnifiedSpendingKey(
                seed = bip39Seed,
                network = persistableWallet.network,
                accountIndex = accountDataSource.getZashiAccount().hdAccountIndex,
            )
        return spendingKey
    }
}
