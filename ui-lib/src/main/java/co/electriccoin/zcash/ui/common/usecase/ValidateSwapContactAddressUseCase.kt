package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.model.SwapAssetBlockchain
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class ValidateSwapContactAddressUseCase(
    private val addressBookRepository: AddressBookRepository,
) {
    suspend operator fun invoke(
        address: String,
        blockchain: SwapAssetBlockchain?,
        exclude: AddressBookContact? = null
    ): ContactAddressValidationResult {
        return when {
            addressBookRepository.addressBook
                .filterNotNull()
                .first()
                .contacts
                .filter { it.chain != null }
                .filter {
                    if (exclude == null) true else it != exclude
                }.any {
                    it.address == address.trim()
                        && it.chain?.lowercase() == blockchain?.chainTicker?.lowercase()
                } -> ContactAddressValidationResult.NotUnique

            else -> ContactAddressValidationResult.Valid
        }
    }
}
