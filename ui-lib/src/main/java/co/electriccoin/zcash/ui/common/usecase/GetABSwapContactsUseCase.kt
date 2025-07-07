package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetABSwapContactsUseCase(
    addressBookRepository: AddressBookRepository,
    private val swapRepository: SwapRepository,
) {
    private val contactsWithChain = addressBookRepository.contacts
        .map { it?.filter { contact -> contact.blockchain != null } }

    fun observe() = combine(contactsWithChain, swapRepository.assets) { contacts, assets ->
        if (contacts == null || assets.data == null) return@combine null

        contacts
            .mapNotNull { contact ->
                val asset = assets.data
                    .firstOrNull {
                        it.chainTicker.lowercase() == contact.blockchain?.chainTicker?.lowercase()
                    } ?: return@mapNotNull null

                ContactWithSwapAsset(contact = contact, asset = asset)
            }
    }
}

data class ContactWithSwapAsset(val contact: EnhancedABContact, val asset: SwapAsset)
