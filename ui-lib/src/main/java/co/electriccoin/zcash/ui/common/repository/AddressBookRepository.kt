package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

interface AddressBookRepository {
    val contacts: StateFlow<List<AddressBookContact>>

    suspend fun saveContact(name: String, address: String)

    suspend fun updateContact(contact: AddressBookContact, name: String, address: String)

    suspend fun deleteContact(contact: AddressBookContact)

    suspend fun getContact(id: String): AddressBookContact?
}

class AddressBookRepositoryImpl : AddressBookRepository {
    override val contacts = MutableStateFlow(emptyList<AddressBookContact>())

    override suspend fun saveContact(name: String, address: String) {
        contacts.update { it + AddressBookContact(name = name.trim(), address = address.trim()) }
    }

    override suspend fun updateContact(contact: AddressBookContact, name: String, address: String) {
        contacts.update {
            it.toMutableList()
                .apply {
                    set(
                        it.indexOf(contact),
                        AddressBookContact(name = name.trim(), address = address.trim())
                    )
                }
                .toList()
        }
    }

    override suspend fun deleteContact(contact: AddressBookContact) {
        contacts.update {
            contacts.value.toMutableList()
                .apply {
                    remove(contact)
                }
                .toList()
        }
    }

    override suspend fun getContact(id: String): AddressBookContact? = contacts.value.find { it.id == id }
}
