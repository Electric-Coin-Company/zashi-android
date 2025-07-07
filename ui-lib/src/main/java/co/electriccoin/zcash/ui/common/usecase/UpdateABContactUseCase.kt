package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact

class UpdateABContactUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(
        contact: EnhancedABContact,
        name: String,
        address: String,
        chain: String?,
    ) {
        addressBookRepository.updateContact(contact = contact, name = name, address = address, chain = chain)
    }
}
