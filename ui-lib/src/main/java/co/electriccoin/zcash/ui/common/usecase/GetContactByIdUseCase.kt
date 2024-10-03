package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class GetContactByIdUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(id: String) = addressBookRepository.getContact(id)
}
