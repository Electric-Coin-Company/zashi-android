package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class UpdateContactUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(
        contact: AddressBookContact,
        name: String,
        address: String
    ) {
        addressBookRepository.updateContact(contact = contact, name = name, address = address)
    }
}
