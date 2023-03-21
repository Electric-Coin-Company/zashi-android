package cash.z.ecc.sdk.fixture

import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.PersistableWallet
import cash.z.ecc.android.sdk.model.SeedPhrase
import cash.z.ecc.android.sdk.model.ZcashNetwork

object PersistableWalletFixture {

    val NETWORK = ZcashNetwork.Mainnet

    @Suppress("MagicNumber")
    val BIRTHDAY = BlockHeight.new(ZcashNetwork.Mainnet, 626603L)

    val SEED_PHRASE = SeedPhraseFixture.new()

    fun new(
        network: ZcashNetwork = NETWORK,
        birthday: BlockHeight = BIRTHDAY,
        seedPhrase: SeedPhrase = SEED_PHRASE
    ) = PersistableWallet(network, birthday, seedPhrase)
}
