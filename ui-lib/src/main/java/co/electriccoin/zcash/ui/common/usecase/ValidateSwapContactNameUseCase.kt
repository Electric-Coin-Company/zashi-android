package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class ValidateSwapContactNameUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(
        name: String,
        exclude: AddressBookContact? = null
    ) = when {
        name.length > CONTACT_NAME_MAX_LENGTH -> ValidateContactNameResult.TooLong
        addressBookRepository.addressBook
            .filterNotNull()
            .first()
            .contacts
            .filter { it.chain != null }
            .filter {
                if (exclude == null) true else it != exclude
            }.any { it.name == name.trim() } -> ValidateContactNameResult.NotUnique

        else -> ValidateContactNameResult.Valid
    }
}