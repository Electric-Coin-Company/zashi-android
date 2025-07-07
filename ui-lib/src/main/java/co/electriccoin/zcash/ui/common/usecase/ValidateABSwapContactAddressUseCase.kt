package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class ValidateABSwapContactAddressUseCase(
    private val addressBookRepository: AddressBookRepository,
) {
    suspend operator fun invoke(
        address: String,
        blockchain: SwapAssetBlockchain?,
        exclude: EnhancedABContact? = null
    ): ContactAddressValidationResult {
        return when {
            addressBookRepository.contacts
                .filterNotNull()
                .first()
                .filter { it.blockchain != null }
                .filter {
                    if (exclude == null) true else it != exclude
                }.any {
                    it.address == address.trim()
                        && it.blockchain?.chainTicker?.lowercase() == blockchain?.chainTicker?.lowercase()
                } -> ContactAddressValidationResult.NotUnique

            else -> ContactAddressValidationResult.Valid
        }
    }
}
