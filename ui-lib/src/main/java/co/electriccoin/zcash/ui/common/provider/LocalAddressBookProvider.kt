package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.AddressBookContacts
import kotlinx.datetime.Clock

interface LocalAddressBookProvider {
    suspend fun getContacts(): AddressBookContacts

    suspend fun saveContact(
        name: String,
        address: String
    ): AddressBookContacts

    suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ): AddressBookContacts

    suspend fun deleteContact(addressBookContact: AddressBookContact): AddressBookContacts

    suspend fun saveContacts(contacts: AddressBookContacts)
}

class LocalAddressBookProviderImpl : LocalAddressBookProvider {
    private var contacts: AddressBookContacts? = null

    override suspend fun getContacts(): AddressBookContacts {
        val contacts = this.contacts

        return if (contacts == null) {
            val new =
                AddressBookContacts(
                    lastUpdated = Clock.System.now(),
                    version = 1,
                    contacts = emptyList(),
                )
            this@LocalAddressBookProviderImpl.contacts = new
            new
        } else {
            contacts
        }
    }

    override suspend fun saveContact(
        name: String,
        address: String
    ): AddressBookContacts {
        val lastUpdated = Clock.System.now()
        contacts =
            AddressBookContacts(
                lastUpdated = lastUpdated,
                version = 1,
                contacts =
                    contacts?.contacts.orEmpty() +
                        AddressBookContact(
                            name = name,
                            address = address,
                            lastUpdated = lastUpdated,
                        ),
            )
        return contacts!!
    }

    override suspend fun updateContact(
        contact: AddressBookContact,
        name: String,
        address: String
    ): AddressBookContacts {
        val lastUpdated = Clock.System.now()
        contacts =
            AddressBookContacts(
                lastUpdated = lastUpdated,
                version = 1,
                contacts =
                    contacts?.contacts.orEmpty().toMutableList()
                        .apply {
                            set(
                                indexOf(contact),
                                AddressBookContact(
                                    name = name.trim(),
                                    address = address.trim(),
                                    lastUpdated = Clock.System.now()
                                )
                            )
                        }
                        .toList(),
            )
        return contacts!!
    }

    override suspend fun deleteContact(addressBookContact: AddressBookContact): AddressBookContacts {
        val lastUpdated = Clock.System.now()
        contacts =
            AddressBookContacts(
                lastUpdated = lastUpdated,
                version = 1,
                contacts =
                    contacts?.contacts.orEmpty().toMutableList()
                        .apply {
                            remove(addressBookContact)
                        }
                        .toList(),
            )
        return contacts!!
    }

    override suspend fun saveContacts(contacts: AddressBookContacts) {
        this@LocalAddressBookProviderImpl.contacts = contacts
    }
}
