package co.electriccoin.zcash.ui.common.serialization.addressbook

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import kotlinx.datetime.Instant
import java.io.InputStream
import java.io.OutputStream

internal class AddressBookSerializer : BaseAddressBookSerializer() {
    fun serializeAddressBook(
        outputStream: OutputStream,
        addressBook: AddressBook
    ) {
        outputStream.write(addressBook.version.createByteArray())
        outputStream.write(addressBook.lastUpdated.toEpochMilliseconds().createByteArray())
        outputStream.write(addressBook.contacts.size.createByteArray())

        addressBook.contacts.forEach { contact ->
            outputStream.write(contact.lastUpdated.toEpochMilliseconds().createByteArray())
            outputStream.write(contact.address.createByteArray())
            outputStream.write(contact.name.createByteArray())
        }
    }

    fun deserializeAddressBook(inputStream: InputStream): AddressBook {
        return AddressBook(
            version = inputStream.readInt(),
            lastUpdated = inputStream.readLong().let { Instant.fromEpochMilliseconds(it) },
            contacts =
                inputStream.readInt().let { contactsSize ->
                    (0 until contactsSize).map { _ ->
                        AddressBookContact(
                            lastUpdated = inputStream.readLong().let { Instant.fromEpochMilliseconds(it) },
                            address = inputStream.readString(),
                            name = inputStream.readString(),
                        )
                    }
                }
        )
    }
}
