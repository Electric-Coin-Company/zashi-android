package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.type.WalletBirthday
import org.json.JSONObject

// WalletBirthday needs a companion
// https://github.com/zcash/zcash-android-wallet-sdk/issues/310
object WalletBirthdayCompanion {
    internal const val VERSION_1 = 1
    internal const val KEY_VERSION = "version"
    internal const val KEY_HEIGHT = "height"
    internal const val KEY_HASH = "hash"
    internal const val KEY_EPOCH_SECONDS = "epoch_seconds"
    internal const val KEY_TREE = "tree"

    fun from(jsonString: String) = from(JSONObject(jsonString))

    fun from(jsonObject: JSONObject): WalletBirthday {
        when (val version = jsonObject.getInt(KEY_VERSION)) {
            VERSION_1 -> {
                val height = jsonObject.getInt(KEY_HEIGHT)
                val hash = jsonObject.getString(KEY_HASH)
                val epochSeconds = jsonObject.getLong(KEY_EPOCH_SECONDS)
                val tree = jsonObject.getString(KEY_TREE)

                return WalletBirthday(height, hash, epochSeconds, tree)
            }
            else -> {
                throw IllegalArgumentException("Unsupported version $version")
            }
        }
    }
}

fun WalletBirthday.toJson() = JSONObject().apply {
    put(WalletBirthdayCompanion.KEY_VERSION, WalletBirthdayCompanion.VERSION_1)
    put(WalletBirthdayCompanion.KEY_HEIGHT, height)
    put(WalletBirthdayCompanion.KEY_HASH, hash)
    put(WalletBirthdayCompanion.KEY_EPOCH_SECONDS, time)
    put(WalletBirthdayCompanion.KEY_TREE, tree)
}
