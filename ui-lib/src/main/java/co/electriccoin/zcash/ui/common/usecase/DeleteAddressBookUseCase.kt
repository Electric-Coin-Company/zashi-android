package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAddressBookUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke() =
        withContext(Dispatchers.IO) {
            addressBookRepository.deleteAddressBook()
        }
}
