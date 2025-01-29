package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.SecretKeyAccess
import com.google.crypto.tink.util.SecretBytes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface MetadataKeyStorageProvider {
    suspend fun get(): MetadataKey?

    suspend fun store(key: MetadataKey)
}

class MetadataKeyStorageProviderImpl(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) : MetadataKeyStorageProvider {
    private val default = MetadataKeyPreferenceDefault()

    override suspend fun get(): MetadataKey? {
        return default.getValue(encryptedPreferenceProvider())
    }

    override suspend fun store(key: MetadataKey) {
        default.putValue(encryptedPreferenceProvider(), key)
    }
}

private class MetadataKeyPreferenceDefault : PreferenceDefault<MetadataKey?> {
    private val secretKeyAccess: SecretKeyAccess?
        get() = InsecureSecretKeyAccess.get()

    override val key: PreferenceKey = PreferenceKey("metadata_key")

    override suspend fun getValue(preferenceProvider: PreferenceProvider) = preferenceProvider.getString(key)?.decode()

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: MetadataKey?
    ) = preferenceProvider.putString(key, newValue?.encode())

    @OptIn(ExperimentalEncodingApi::class)
    private fun MetadataKey?.encode() =
        if (this != null) {
            Base64.encode(this.key.toByteArray(secretKeyAccess))
        } else {
            null
        }

    @OptIn(ExperimentalEncodingApi::class)
    private fun String?.decode() =
        if (this != null) {
            MetadataKey(SecretBytes.copyFrom(Base64.decode(this), secretKeyAccess))
        } else {
            null
        }
}
