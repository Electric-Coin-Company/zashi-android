package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.AccountUuid
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.SecretKeyAccess
import com.google.crypto.tink.util.SecretBytes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface MetadataKeyStorageProvider {
    suspend fun get(uuid: AccountUuid): MetadataKey?

    suspend fun store(uuid: AccountUuid, key: MetadataKey)
}

class MetadataKeyStorageProviderImpl(
    encryptedPreferenceProvider: EncryptedPreferenceProvider
) : MetadataKeyStorageProvider {
    private val default = MetadataKeyPreferenceDefault(encryptedPreferenceProvider)

    override suspend fun get(uuid: AccountUuid): MetadataKey? = default.getValue(uuid)

    override suspend fun store(uuid: AccountUuid, key: MetadataKey) = default.putValue(uuid, key)
}

private class MetadataKeyPreferenceDefault(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) {
    private val secretKeyAccess: SecretKeyAccess?
        get() = InsecureSecretKeyAccess.get()

    suspend fun getValue(uuid: AccountUuid): MetadataKey? =
        encryptedPreferenceProvider()
            .getStringSet(key = getKey(uuid))
            ?.decode(secretKeyAccess)

    suspend fun putValue(uuid: AccountUuid, newValue: MetadataKey?) {
        encryptedPreferenceProvider().putStringSet(
            key = getKey(uuid),
            value = newValue?.encode(secretKeyAccess)
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getKey(uuid: AccountUuid) = PreferenceKey("metadata_key_${uuid.value.toHexString()}")
}

@OptIn(ExperimentalEncodingApi::class)
private fun MetadataKey?.encode(secretKeyAccess: SecretKeyAccess?): Set<String>? =
    this
        ?.bytes
        ?.map {
            Base64.encode(it.toByteArray(secretKeyAccess))
        }?.toSet()

@OptIn(ExperimentalEncodingApi::class)
private fun Set<String>?.decode(secretKeyAccess: SecretKeyAccess?) =
    if (this != null) {
        MetadataKey(
            this
                .toList()
                .map {
                    SecretBytes.copyFrom(Base64.decode(it), secretKeyAccess)
                }
        )
    } else {
        null
    }
