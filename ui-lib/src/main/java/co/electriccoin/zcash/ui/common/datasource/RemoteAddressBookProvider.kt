package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.AddressBook

interface RemoteAddressBookProvider {
    suspend fun fetchContacts(): AddressBook?

    suspend fun uploadContacts()
}

class RemoteAddressBookProviderImpl : RemoteAddressBookProvider {
    override suspend fun fetchContacts(): AddressBook? {
        // fetch
        // deserialize
        return null
    }

    override suspend fun uploadContacts() {
        // localAddressBookStorageProvider.openStorageInputStream() // read
        // upload file stream
    }
}
