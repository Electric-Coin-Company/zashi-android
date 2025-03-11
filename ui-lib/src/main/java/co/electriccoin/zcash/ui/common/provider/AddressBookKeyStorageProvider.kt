package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.preference.EncryptedPreferenceProvider
import co.electriccoin.zcash.preference.api.PreferenceProvider
import co.electriccoin.zcash.preference.model.entry.PreferenceDefault
import co.electriccoin.zcash.preference.model.entry.PreferenceKey
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookKey
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.SecretKeyAccess
import com.google.crypto.tink.util.SecretBytes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface AddressBookKeyStorageProvider {
    suspend fun getAddressBookKey(): AddressBookKey?

    suspend fun storeAddressBookKey(addressBookKey: AddressBookKey)
}

class AddressBookKeyStorageProviderImpl(
    private val encryptedPreferenceProvider: EncryptedPreferenceProvider
) : AddressBookKeyStorageProvider {
    private val default = AddressBookKeyPreferenceDefault()

    override suspend fun getAddressBookKey(): AddressBookKey? = default.getValue(encryptedPreferenceProvider())

    override suspend fun storeAddressBookKey(addressBookKey: AddressBookKey) {
        default.putValue(encryptedPreferenceProvider(), addressBookKey)
    }
}

private class AddressBookKeyPreferenceDefault : PreferenceDefault<AddressBookKey?> {
    private val secretKeyAccess: SecretKeyAccess?
        get() = InsecureSecretKeyAccess.get()

    override val key: PreferenceKey = PreferenceKey("address_book_key")

    override suspend fun getValue(preferenceProvider: PreferenceProvider) = preferenceProvider.getString(key)?.decode()

    override suspend fun putValue(
        preferenceProvider: PreferenceProvider,
        newValue: AddressBookKey?
    ) = preferenceProvider.putString(key, newValue?.encode())

    @OptIn(ExperimentalEncodingApi::class)
    private fun AddressBookKey?.encode() =
        if (this != null) {
            Base64.encode(this.key.toByteArray(secretKeyAccess))
        } else {
            null
        }

    @OptIn(ExperimentalEncodingApi::class)
    private fun String?.decode() =
        if (this != null) {
            AddressBookKey(SecretBytes.copyFrom(Base64.decode(this), secretKeyAccess))
        } else {
            null
        }
}
