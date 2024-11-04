package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.AddressBook
import co.electriccoin.zcash.ui.common.serialization.addressbook.AddressBookV1Serializer
import java.io.File
import kotlin.LazyThreadSafetyMode.NONE

interface AddressBookProvider {
    fun writeAddressBookToFile(
        file: File,
        addressBook: AddressBook
    )

    fun readAddressBookFromFile(file: File): AddressBook
}

class AddressBookProviderImpl : AddressBookProvider {
    private val addressBookV1Serializer by lazy(NONE) { AddressBookV1Serializer() }

    override fun writeAddressBookToFile(
        file: File,
        addressBook: AddressBook
    ) {
        file.outputStream().buffered().use { stream ->
            addressBookV1Serializer.serializeAddressBook(stream, addressBook)
            stream.flush()
        }
    }

    override fun readAddressBookFromFile(file: File): AddressBook {
        return file.inputStream().use { stream ->
            addressBookV1Serializer.deserializeAddressBook(stream)
        }
    }
}

