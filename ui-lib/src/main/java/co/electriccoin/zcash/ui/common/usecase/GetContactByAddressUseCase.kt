package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class GetContactByAddressUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    operator fun invoke(address: String) = addressBookRepository.contacts.value.find { it.address == address }
}
