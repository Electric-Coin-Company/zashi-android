package cash.z.ecc.sdk.fixture

import cash.z.ecc.android.sdk.type.WalletBirthday
import cash.z.ecc.android.sdk.type.ZcashNetwork
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.sdk.model.SeedPhrase

object PersistableWalletFixture {

    val NETWORK = ZcashNetwork.Testnet

    val BIRTHDAY = WalletBirthdayFixture.new()

    val SEED_PHRASE = SeedPhraseFixture.new()

    fun new(
        network: ZcashNetwork = NETWORK,
        birthday: WalletBirthday = BIRTHDAY,
        seedPhrase: SeedPhrase = SEED_PHRASE
    ) = PersistableWallet(network, birthday, seedPhrase)
}
