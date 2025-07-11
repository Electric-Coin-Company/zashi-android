package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class ValidateContactAddressUseCase(
    private val addressBookRepository: AddressBookRepository,
    private val synchronizerProvider: SynchronizerProvider,
) {
    suspend operator fun invoke(
        address: String,
        exclude: AddressBookContact? = null
    ): Result {
        val result = synchronizerProvider.getSynchronizer().validateAddress(address)
        return when {
            result.isNotValid -> Result.Invalid
            addressBookRepository.addressBook
                .filterNotNull()
                .first()
                .contacts
                .filter {
                    if (exclude == null) true else it != exclude
                }.any { it.address == address.trim() } -> Result.NotUnique

            else -> Result.Valid
        }
    }

    sealed interface Result {
        data object Valid : Result

        data object Invalid : Result

        data object NotUnique : Result
    }
}
