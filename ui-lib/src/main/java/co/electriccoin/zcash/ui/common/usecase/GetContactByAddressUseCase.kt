package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetContactByAddressUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(address: String) =
        addressBookRepository
            .addressBook
            .filterNotNull()
            .first()
            .contacts
            .find { contact -> contact.address == address }
}
