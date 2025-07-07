package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class ValidateABSwapContactNameUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    suspend operator fun invoke(
        name: String,
        exclude: EnhancedABContact? = null
    ) = when {
        name.length > CONTACT_NAME_MAX_LENGTH -> ValidateContactNameResult.TooLong
        addressBookRepository.contacts
            .filterNotNull()
            .first()
            .filter { it.blockchain != null }
            .filter {
                if (exclude == null) true else it != exclude
            }.any { it.name == name.trim() } -> ValidateContactNameResult.NotUnique

        else -> ValidateContactNameResult.Valid
    }
}