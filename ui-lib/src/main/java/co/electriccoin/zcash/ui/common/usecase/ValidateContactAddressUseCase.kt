package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class ValidateContactAddressUseCase(
    private val addressBookRepository: AddressBookRepository,
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(address: String, exclude: AddressBookContact? = null): Result {
        val result = walletRepository.getSynchronizer().validateAddress(address)
        return when {
            result.isNotValid -> Result.Invalid
            addressBookRepository.contacts.value
                .filter {
                    if (exclude == null) true else it != exclude
                }
                .any { it.address == address.trim() } -> Result.NotUnique

            else -> Result.Valid
        }
    }

    sealed interface Result {
        data object Valid : Result
        data object Invalid : Result
        data object NotUnique : Result
    }
}
