package co.electriccoin.zcash.ui.common.datasource

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
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
    override suspend fun getZashiSpendingKey(): UnifiedSpendingKey =
        withContext(Dispatchers.IO) {
            val persistableWallet = persistableWalletProvider.requirePersistableWallet()

            val bip39Seed = Mnemonics.MnemonicCode(persistableWallet.seedPhrase.joinToString()).toSeed()
            DerivationTool.getInstance().deriveUnifiedSpendingKey(
                seed = bip39Seed,
                network = persistableWallet.network,
                accountIndex = accountDataSource.getZashiAccount().hdAccountIndex,
            )
        }
}
