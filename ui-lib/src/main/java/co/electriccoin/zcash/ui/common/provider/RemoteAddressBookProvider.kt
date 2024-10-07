package co.electriccoin.zcash.ui.common.provider

import co.electriccoin.zcash.ui.common.model.AddressBookContacts

interface RemoteAddressBookProvider {
    suspend fun getContacts(): AddressBookContacts?

    suspend fun saveContacts(contacts: AddressBookContacts): AddressBookContacts
}

class RemoteAddressBookProviderImpl : RemoteAddressBookProvider {
    override suspend fun getContacts(): AddressBookContacts? = null

    override suspend fun saveContacts(contacts: AddressBookContacts): AddressBookContacts = contacts
}
