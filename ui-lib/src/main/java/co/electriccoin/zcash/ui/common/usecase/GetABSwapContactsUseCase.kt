package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.map

class GetABSwapContactsUseCase(
    private val addressBookRepository: AddressBookRepository,
) {
    fun observe() = addressBookRepository.contacts
        .map { it?.filter { contact -> contact.blockchain != null } }
}
