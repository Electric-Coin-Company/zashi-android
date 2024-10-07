package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.datasource.AddressBookDataSource
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

interface AddressBookRepository {
    val contacts: Flow<List<AddressBookContact>?>

    suspend fun saveContact(
        name: String,
        address: String
    )

    suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    )

    suspend fun deleteContact(contact: AddressBookContact)

    suspend fun getContactById(id: String): AddressBookContact?

    suspend fun getContactByAddress(address: String): AddressBookContact?
}

class AddressBookRepositoryImpl(
    private val addressBookDataSource: AddressBookDataSource
) : AddressBookRepository {
    override val contacts = addressBookDataSource.contacts.map { it?.contacts }

    override suspend fun saveContact(
        name: String,
        address: String
    ) {
        addressBookDataSource.saveContact(name, address)
    }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ) {
        addressBookDataSource.updateContact(contact, name, address)
    }

    override suspend fun deleteContact(contact: AddressBookContact) {
        addressBookDataSource.deleteContact(contact)
    }

    override suspend fun getContactById(id: String): AddressBookContact? = getLoadedContacts().find { it.id == id }

    override suspend fun getContactByAddress(address: String): AddressBookContact? =
        getLoadedContacts()
            .find { it.address == address }

    private suspend fun getLoadedContacts() = contacts.filterNotNull().first()
}
