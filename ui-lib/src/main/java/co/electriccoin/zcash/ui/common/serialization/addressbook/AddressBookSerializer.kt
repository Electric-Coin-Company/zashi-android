package co.electriccoin.zcash.ui.common.serialization.addressbook

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.serialization.ADDRESS_BOOK_SERIALIZATION_V1
import co.electriccoin.zcash.ui.common.serialization.ADDRESS_BOOK_SERIALIZATION_V2
import co.electriccoin.zcash.ui.common.serialization.BaseSerializer
import kotlinx.datetime.Instant
import java.io.InputStream
import java.io.OutputStream

class AddressBookSerializer : BaseSerializer() {
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
            outputStream.write(contact.chain.createByteArray())
        }
    }

    fun deserializeAddressBook(inputStream: InputStream): AddressBook =
        when (inputStream.readInt()) {
            ADDRESS_BOOK_SERIALIZATION_V1 ->
                AddressBook(
                    version = ADDRESS_BOOK_SERIALIZATION_V1,
                    lastUpdated = inputStream.readLong().let { Instant.fromEpochMilliseconds(it) },
                    contacts =
                        inputStream.readInt().let { contactsSize ->
                            (0 until contactsSize).map { _ ->
                                AddressBookContact(
                                    lastUpdated = inputStream.readLong().let { Instant.fromEpochMilliseconds(it) },
                                    address = inputStream.readString(),
                                    name = inputStream.readString(),
                                    chain = null,
                                )
                            }
                        }
                )

            ADDRESS_BOOK_SERIALIZATION_V2 ->
                AddressBook(
                    version = ADDRESS_BOOK_SERIALIZATION_V2,
                    lastUpdated = inputStream.readLong().let { Instant.fromEpochMilliseconds(it) },
                    contacts =
                        inputStream.readInt().let { contactsSize ->
                            (0 until contactsSize).map { _ ->
                                AddressBookContact(
                                    lastUpdated = inputStream.readLong().let { Instant.fromEpochMilliseconds(it) },
                                    address = inputStream.readString(),
                                    name = inputStream.readString(),
                                    chain = inputStream.readString().takeIf { it.isNotEmpty() },
                                )
                            }
                        }
                )

            else -> throw UnsupportedOperationException("Unknown version of address book")
        }
}
