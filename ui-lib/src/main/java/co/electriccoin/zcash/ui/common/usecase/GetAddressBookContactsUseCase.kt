package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.map

class GetAddressBookContactsUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    fun observe() = addressBookRepository.addressBook
        .map {
            it
                ?.contacts
                ?.filter { contact -> contact.chain == null }
        }
}
