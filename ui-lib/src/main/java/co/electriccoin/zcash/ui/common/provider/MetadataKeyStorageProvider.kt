package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.Account
import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.SecretKeyAccess
import com.google.crypto.tink.util.SecretBytes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface MetadataKeyStorageProvider {
    suspend fun get(sdkAccount: Account): MetadataKey?

    suspend fun store(key: MetadataKey, sdkAccount: Account)
}

class MetadataKeyStorageProviderImpl(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) : MetadataKeyStorageProvider {
    private val default = MetadataKeyPreferenceDefault()

    override suspend fun get(sdkAccount: Account): MetadataKey? =
        default.getValue(
            sdkAccount = sdkAccount,
            preferenceProvider = encryptedPreferenceProvider(),
        )

    override suspend fun store(key: MetadataKey, sdkAccount: Account) {
        default.putValue(
            newValue = key,
            sdkAccount = sdkAccount,
            preferenceProvider = encryptedPreferenceProvider(),
        )
    }
}

private class MetadataKeyPreferenceDefault {
    private val secretKeyAccess: SecretKeyAccess?
        get() = InsecureSecretKeyAccess.get()

    suspend fun getValue(
        sdkAccount: Account,
        preferenceProvider: PreferenceProvider,
    ): MetadataKey? =
        preferenceProvider
            .getStringSet(
                key = getKey(sdkAccount)
            )?.decode()

    suspend fun putValue(
        newValue: MetadataKey?,
        sdkAccount: Account,
        preferenceProvider: PreferenceProvider,
    ) {
        preferenceProvider.putStringSet(
            key = getKey(sdkAccount),
            value = newValue?.encode()
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getKey(sdkAccount: Account): PreferenceKey =
        PreferenceKey("metadata_key_${sdkAccount.accountUuid.value.toHexString()}")

    @OptIn(ExperimentalEncodingApi::class)
    private fun MetadataKey?.encode(): Set<String>? =
        this
            ?.bytes
            ?.map {
                Base64.encode(it.toByteArray(secretKeyAccess))
            }?.toSet()

    @OptIn(ExperimentalEncodingApi::class)
    private fun Set<String>?.decode() =
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
}
