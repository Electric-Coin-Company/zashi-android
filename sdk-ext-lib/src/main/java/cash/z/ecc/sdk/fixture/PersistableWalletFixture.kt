package cash.z.ecc.sdk.fixture

import cash.z.ecc.android.sdk.type.WalletBirthday
import cash.z.ecc.android.sdk.type.ZcashNetwork
import cash.z.ecc.sdk.model.PersistableWallet
import cash.z.ecc.sdk.model.SeedPhrase

object PersistableWalletFixture {

    val NETWORK = ZcashNetwork.Testnet

    val BIRTHDAY = WalletBirthdayFixture.new()

    @Suppress("MaxLineLength")
    val SEED_PHRASE = "still champion voice habit trend flight survey between bitter process artefact blind carbon truly provide dizzy crush flush breeze blouse charge solid fish spread"

    fun new(network: ZcashNetwork = NETWORK,
        birthday: WalletBirthday = BIRTHDAY,
        seedPhrase: String = SEED_PHRASE) = PersistableWallet(network, birthday, SeedPhrase(seedPhrase))
}
