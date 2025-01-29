package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class ObserveContactByAddressUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    operator fun invoke(address: String) = addressBookRepository.observeContactByAddress(address)
}
