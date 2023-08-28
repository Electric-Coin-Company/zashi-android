package co.electriccoin.zcash.ui.common

import android.content.Context
import de.adorsys.android.securestoragelibrary.SecurePreferences

class OldSecurePreference(private val appContext: Context) {

    fun getBoolean(key: String): Boolean {
        return getChunkedString(key)?.toBoolean() ?: false
    }

    fun getString(key: String): String {
        return getChunkedString(key) ?: ""
    }

    fun getLong(key: String): Long? {
        return getChunkedString(key)?.toLongOrNull()
    }

    /**
     * Returns a string value from storage by first fetching the key, directly. If that is missing,
     * it checks for a chunked version of the key. If that exists, it will be merged and returned.
     * If not, then null will be returned.
     *
     * @return the key if found and null otherwise.
     */
    private fun getChunkedString(key: String): String? {
        return SecurePreferences.getStringValue(appContext, key, null)
            ?: SecurePreferences.getStringListValue(appContext, key, listOf()).let { result ->
                if (result.size == 0) null else result.joinToString("")
            }
    }

    fun clear() {
        SecurePreferences.clearAllValues(appContext)
    }
}