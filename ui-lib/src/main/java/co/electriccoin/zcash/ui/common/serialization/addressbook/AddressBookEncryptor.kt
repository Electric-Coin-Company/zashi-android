package co.electriccoin.zcash.ui.common.serialization.addressbook

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.serialization.ADDRESS_BOOK_ENCRYPTION_V1
import co.electriccoin.zcash.ui.common.serialization.ADDRESS_BOOK_SALT_SIZE
import co.electriccoin.zcash.ui.common.serialization.BaseEncryptor
import co.electriccoin.zcash.ui.common.serialization.Encryptor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

interface AddressBookEncryptor : Encryptor<AddressBookKey, AddressBook>

class AddressBookEncryptorImpl(
    private val addressBookSerializer: AddressBookSerializer,
) : AddressBookEncryptor, BaseEncryptor<AddressBookKey, AddressBook>() {
    override val version: Int = ADDRESS_BOOK_ENCRYPTION_V1
    override val saltSize: Int = ADDRESS_BOOK_SALT_SIZE

    override fun serialize(
        outputStream: ByteArrayOutputStream,
        data: AddressBook
    ) {
        addressBookSerializer.serializeAddressBook(outputStream, data)
    }

    override fun deserialize(inputStream: ByteArrayInputStream): AddressBook {
        return addressBookSerializer.deserializeAddressBook(inputStream)
    }
}
