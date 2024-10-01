package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class SaveContactUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(name: String, address: String) {
        addressBookRepository.saveContact(name = name, address = address)
    }
}
