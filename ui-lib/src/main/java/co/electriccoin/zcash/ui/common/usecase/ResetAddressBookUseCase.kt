package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class ResetAddressBookUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke() = addressBookRepository.resetAddressBook()
}