package cash.z.ecc.sdk.model

import android.app.Application
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toEntropy
import cash.z.ecc.android.sdk.tool.WalletBirthdayTool
import cash.z.ecc.android.sdk.type.WalletBirthday
import cash.z.ecc.android.sdk.type.ZcashNetwork
import cash.z.ecc.sdk.type.fromResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Represents everything needed to save and restore a wallet.
 */
data class PersistableWallet(
    val network: ZcashNetwork,
    val birthday: WalletBirthday?,
    val seedPhrase: SeedPhrase
) {

    /**
     * @return Wallet serialized to JSON format, suitable for long-term encrypted storage.
     */
    // Note: We're using a hand-crafted serializer so that we're less likely to have accidental
    // breakage from reflection or annotation based methods, and so that we can carefully manage versioning.
    fun toJson() = JSONObject().apply {
        put(KEY_VERSION, VERSION_1)
        put(KEY_NETWORK_ID, network.id)
        birthday?.let {
            put(KEY_BIRTHDAY, it.toJson())
        }
        put(KEY_SEED_PHRASE, seedPhrase.joinToString())
    }

    override fun toString(): String {
        // For security, intentionally override the toString method to reduce risk of accidentally logging secrets
        return "PersistableWallet"
    }

    companion object {
        private const val VERSION_1 = 1

        internal const val KEY_VERSION = "v"
        internal const val KEY_NETWORK_ID = "network_ID"
        internal const val KEY_BIRTHDAY = "birthday"
        internal const val KEY_SEED_PHRASE = "seed_phrase"

        fun from(jsonObject: JSONObject): PersistableWallet {
            when (val version = jsonObject.getInt(KEY_VERSION)) {
                VERSION_1 -> {
                    val networkId = jsonObject.getInt(KEY_NETWORK_ID)
                    val birthday = if (jsonObject.has(KEY_BIRTHDAY)) {
                        WalletBirthdayCompanion.from(jsonObject.getJSONObject(KEY_BIRTHDAY))
                    } else {
                        null
                    }
                    val seedPhrase = jsonObject.getString(KEY_SEED_PHRASE)

                    return PersistableWallet(ZcashNetwork.from(networkId), birthday, SeedPhrase.new(seedPhrase))
                }
                else -> {
                    throw IllegalArgumentException("Unsupported version $version")
                }
            }
        }

        /**
         * @return A new PersistableWallet with a random seed phrase.
         */
        suspend fun new(application: Application): PersistableWallet {
            val zcashNetwork = ZcashNetwork.fromResources(application)
            // Dispatchers can be removed once a new SDK is released implementing
            // https://github.com/zcash/zcash-android-wallet-sdk/issues/269
            val walletBirthday = withContext(Dispatchers.IO) {
                WalletBirthdayTool.loadNearest(application, zcashNetwork)
            }
            val seedPhrase = newSeedPhrase()

            return PersistableWallet(zcashNetwork, walletBirthday, seedPhrase)
        }
    }
}

// Using IO context because of https://github.com/zcash/kotlin-bip39/issues/13
private suspend fun newMnemonic() = withContext(Dispatchers.IO) {
    Mnemonics.MnemonicCode(cash.z.ecc.android.bip39.Mnemonics.WordCount.COUNT_24.toEntropy()).words
}

private suspend fun newSeedPhrase() = SeedPhrase(newMnemonic().map { it.concatToString() })
