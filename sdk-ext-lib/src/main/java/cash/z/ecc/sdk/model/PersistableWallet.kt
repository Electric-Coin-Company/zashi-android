package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.type.WalletBirthday
import cash.z.ecc.android.sdk.type.ZcashNetwork
import org.json.JSONObject

/**
 * Represents everything needed to save and restore a wallet.
 */
data class PersistableWallet(
    val network: ZcashNetwork,
    val birthday: WalletBirthday,
    val seedPhrase: String
) {

    /**
     * @return Wallet serialized to JSON format, suitable for long-term encrypted storage.
     */
    // Note: We're using a hand-crafted serializer so that we're less likely to have accidental
    // breakage from reflection or annotation based methods, and so that we can carefully manage versioning.
    fun toJson() = JSONObject().apply {
        put(KEY_VERSION, VERSION_1)
        put(KEY_NETWORK_ID, network.id)
        put(KEY_BIRTHDAY, birthday.toJson())
        put(KEY_SEED_PHRASE, seedPhrase)
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
                    val birthday = WalletBirthdayCompanion.from(jsonObject.getJSONObject(KEY_BIRTHDAY))
                    val seedPhrase = jsonObject.getString(KEY_SEED_PHRASE)

                    return PersistableWallet(ZcashNetwork.from(networkId), birthday, seedPhrase)
                }
                else -> {
                    throw IllegalArgumentException("Unsupported version $version")
                }
            }
        }
    }
}
