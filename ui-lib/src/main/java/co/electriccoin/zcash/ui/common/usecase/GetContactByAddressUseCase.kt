package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class GetContactByAddressUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(address: String) = addressBookRepository.getContactByAddress(address)
}
