package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetAddressBookSwapContactsUseCase(
    addressBookRepository: AddressBookRepository,
    private val swapRepository: SwapRepository,
) {
    private val contactsWithChain = addressBookRepository.addressBook
        .map {
            it
                ?.contacts
                ?.filter { contact -> contact.chain != null }
        }

    fun observe() = combine(
        contactsWithChain,
        swapRepository.assets
    ) { contacts, assets ->
        if (contacts == null || assets.data == null) return@combine null

        contacts
            .mapNotNull { contact ->
                val asset = assets.data.firstOrNull { it.chainTicker.lowercase() == contact.chain }
                    ?: return@mapNotNull null

                ContactWithSwapAsset(
                    contact = contact,
                    asset = asset
                )
            }
    }
}

data class ContactWithSwapAsset(
    val contact: AddressBookContact,
    val asset: SwapAsset,
)
