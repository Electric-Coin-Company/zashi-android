package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class DeleteContactUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(contact: AddressBookContact) {
        addressBookRepository.deleteContact(contact)
    }
}
