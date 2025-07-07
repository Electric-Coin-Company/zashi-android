package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class ValidateABContactAddressUseCase(
    private val addressBookRepository: AddressBookRepository,
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(
        address: String,
        exclude: EnhancedABContact? = null
    ): ContactAddressValidationResult {
        val result = walletRepository.getSynchronizer().validateAddress(address)
        return when {
            result.isNotValid -> ContactAddressValidationResult.Invalid
            addressBookRepository.contacts
                .filterNotNull()
                .first()
                .filter {
                    if (exclude == null) true else it != exclude
                }.any { it.address == address.trim() } -> ContactAddressValidationResult.NotUnique

            else -> ContactAddressValidationResult.Valid
        }
    }
}

sealed interface ContactAddressValidationResult {
    data object Valid : ContactAddressValidationResult

    data object Invalid : ContactAddressValidationResult

    data object NotUnique : ContactAddressValidationResult
}
