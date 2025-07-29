package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetABContactByIdUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(address: String, chain: String?): EnhancedABContact? =
        addressBookRepository
            .contacts
            .filterNotNull()
            .first()
            .find { contact ->
                contact.address == address && contact.blockchain?.chainTicker?.lowercase() == chain?.lowercase()
            }
}
