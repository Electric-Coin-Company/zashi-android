package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class ObserveContactByAddressUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    operator fun invoke(address: String) =
        addressBookRepository.contacts
            .filterNotNull()
            .map { contacts ->
                contacts.find { it.address == address }
            }
}
