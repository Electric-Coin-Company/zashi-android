package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface SelectedAccountUUIDProvider {
    val uuid: Flow<ByteArray?>

    suspend fun getUUID(): ByteArray?

    suspend fun setUUID(uuid: ByteArray)
}

class SelectedAccountUUIDProviderImpl(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) : SelectedAccountUUIDProvider {
    private val default = SelectedAccountUUIDPreferenceDefault()

    override val uuid: Flow<ByteArray?> = flow {
        emitAll(default.observe(encryptedPreferenceProvider()))
    }

    override suspend fun getUUID(): ByteArray? {
        return default.getValue(encryptedPreferenceProvider())
    }

    override suspend fun setUUID(uuid: ByteArray) {
        default.putValue(encryptedPreferenceProvider(), uuid)
    }
}

private class SelectedAccountUUIDPreferenceDefault : PreferenceDefault<ByteArray?> {

    override val key: PreferenceKey = PreferenceKey("selected_account_uui")

    override suspend fun getValue(preferenceProvider: PreferenceProvider): ByteArray? {
        return preferenceProvider.getString(key)?.decode()
    }

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: ByteArray?
    ) = preferenceProvider.putString(key, newValue?.encode())

    @OptIn(ExperimentalEncodingApi::class)
    private fun ByteArray.encode() = Base64.encode(this)

    @OptIn(ExperimentalEncodingApi::class)
    private fun String?.decode() =
        if (this != null) {
            Base64.decode(this)
        } else {
            null
        }
}
