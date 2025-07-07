package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.map

class GetABContactsUseCase(private val addressBookRepository: AddressBookRepository) {
    fun observe(zcashContactsOnly: Boolean) = addressBookRepository
        .contacts
        .map {
            it?.filter { contact -> if (zcashContactsOnly) contact.blockchain == null else true }
        }
}
