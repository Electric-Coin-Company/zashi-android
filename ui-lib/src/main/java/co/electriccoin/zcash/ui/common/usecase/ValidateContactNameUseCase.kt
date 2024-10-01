package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class ValidateContactNameUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    operator fun invoke(
        name: String,
        exclude: AddressBookContact? = null
    ) = when {
        name.length > CONTACT_NAME_MAX_LENGTH -> Result.TooLong
        addressBookRepository.contacts.value
            .filter {
                if (exclude == null) true else it != exclude
            }
            .any { it.name == name.trim() } -> Result.NotUnique

        else -> Result.Valid
    }

    sealed interface Result {
        data object Valid : Result

        data object TooLong : Result

        data object NotUnique : Result
    }
}

private const val CONTACT_NAME_MAX_LENGTH = 32
