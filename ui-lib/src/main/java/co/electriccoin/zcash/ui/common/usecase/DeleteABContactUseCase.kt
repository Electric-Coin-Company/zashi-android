package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact

class DeleteABContactUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(contact: EnhancedABContact) {
        addressBookRepository.deleteContact(contact)
    }
}
