package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.map

class ObserveAddressBookContactsUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    operator fun invoke() = addressBookRepository.addressBook.map { it?.contacts }
}
