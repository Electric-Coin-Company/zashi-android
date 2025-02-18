package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.serialization.metada.MetadataKey
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.SecretKeyAccess
import com.google.crypto.tink.util.SecretBytes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface MetadataKeyStorageProvider {
    suspend fun get(account: WalletAccount): MetadataKey?

    suspend fun store(
        key: MetadataKey,
        account: WalletAccount
    )
}

class MetadataKeyStorageProviderImpl(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) : MetadataKeyStorageProvider {
    private val default = MetadataKeyPreferenceDefault()

    override suspend fun get(account: WalletAccount): MetadataKey? {
        return default.getValue(
            walletAccount = account,
            preferenceProvider = encryptedPreferenceProvider(),
        )
    }

    override suspend fun store(
        key: MetadataKey,
        account: WalletAccount
    ) {
        default.putValue(
            newValue = key,
            walletAccount = account,
            preferenceProvider = encryptedPreferenceProvider(),
        )
    }
}

private class MetadataKeyPreferenceDefault {
    private val secretKeyAccess: SecretKeyAccess?
        get() = InsecureSecretKeyAccess.get()

    suspend fun getValue(
        walletAccount: WalletAccount,
        preferenceProvider: PreferenceProvider,
    ): MetadataKey? {
        return preferenceProvider.getStringSet(
            key = getKey(walletAccount)
        )?.decode()
    }

    suspend fun putValue(
        newValue: MetadataKey?,
        walletAccount: WalletAccount,
        preferenceProvider: PreferenceProvider,
    ) {
        preferenceProvider.putStringSet(
            key = getKey(walletAccount),
            value = newValue?.encode()
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getKey(walletAccount: WalletAccount): PreferenceKey =
        PreferenceKey("metadata_key_${walletAccount.sdkAccount.accountUuid.value.toHexString()}")

    @OptIn(ExperimentalEncodingApi::class)
    private fun MetadataKey?.encode(): Set<String>? {
        return if (this != null) {
            setOfNotNull(
                Base64.encode(this.encryptionBytes.toByteArray(secretKeyAccess)),
                this.decryptionBytes?.let { Base64.encode(it.toByteArray(secretKeyAccess)) }
            )
        } else {
            null
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun Set<String>?.decode() =
        if (this != null) {
            MetadataKey(
                encryptionBytes = SecretBytes.copyFrom(Base64.decode(this.toList()[0]), secretKeyAccess),
                decryptionBytes =
                    this.toList().getOrNull(1)
                        ?.let { SecretBytes.copyFrom(Base64.decode(it), secretKeyAccess) },
            )
        } else {
            null
        }
}
