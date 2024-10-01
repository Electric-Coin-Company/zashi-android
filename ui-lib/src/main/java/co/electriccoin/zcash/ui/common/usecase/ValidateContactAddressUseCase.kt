package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository

class ValidateContactAddressUseCase(
    private val addressBookRepository: AddressBookRepository,
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(address: String): Result {
        val result = walletRepository.getSynchronizer().validateAddress(address)
        return when {
            result.isNotValid -> Result.Invalid
            addressBookRepository.contacts.value.any { it.address.address == address } -> Result.NotUnique
            else -> Result.Valid
        }
    }

    sealed interface Result {
        data object Valid : Result
        data object Invalid : Result
        data object NotUnique : Result
    }
}
