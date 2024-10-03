package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class ObserveAddressBookContactsUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    operator fun invoke() = addressBookRepository.contacts
}
