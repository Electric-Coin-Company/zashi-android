package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.type.WalletBirthday
import org.json.JSONObject

internal val WalletBirthday.Companion.VERSION_1
    get() = 1
internal val WalletBirthday.Companion.KEY_VERSION
    get() = "version"
internal val WalletBirthday.Companion.KEY_HEIGHT
    get() = "height"
internal val WalletBirthday.Companion.KEY_HASH
    get() = "hash"
internal val WalletBirthday.Companion.KEY_EPOCH_SECONDS
    get() = "epoch_seconds"
internal val WalletBirthday.Companion.KEY_TREE
    get() = "tree"

fun WalletBirthday.Companion.from(jsonString: String) = from(JSONObject(jsonString))

fun WalletBirthday.Companion.from(jsonObject: JSONObject): WalletBirthday {
    when (val version = jsonObject.getInt(WalletBirthday.KEY_VERSION)) {
        WalletBirthday.VERSION_1 -> {
            val height = jsonObject.getInt(WalletBirthday.KEY_HEIGHT)
            val hash = jsonObject.getString(WalletBirthday.KEY_HASH)
            val epochSeconds = jsonObject.getLong(WalletBirthday.KEY_EPOCH_SECONDS)
            val tree = jsonObject.getString(WalletBirthday.KEY_TREE)

            return WalletBirthday(height, hash, epochSeconds, tree)
        }
        else -> {
            throw IllegalArgumentException("Unsupported version $version")
        }
    }
}

fun WalletBirthday.toJson() = JSONObject().apply {
    put(WalletBirthday.KEY_VERSION, WalletBirthday.VERSION_1)
    put(WalletBirthday.KEY_HEIGHT, height)
    put(WalletBirthday.KEY_HASH, hash)
    put(WalletBirthday.KEY_EPOCH_SECONDS, time)
    put(WalletBirthday.KEY_TREE, tree)
}
