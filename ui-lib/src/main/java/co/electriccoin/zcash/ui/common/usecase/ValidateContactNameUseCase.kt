package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class ValidateContactNameUseCase(
    private val addressBookRepository: AddressBookRepository
) {

    operator fun invoke(name: String) = when {
        name.length > CONTACT_NAME_MAX_LENGTH -> Result.TooLong
        addressBookRepository.contacts.value.any { it.name == name } -> Result.NotUnique
        else -> Result.Valid
    }

    sealed interface Result {
        data object Valid : Result
        data object TooLong : Result
        data object NotUnique : Result
    }
}

private const val CONTACT_NAME_MAX_LENGTH = 32
