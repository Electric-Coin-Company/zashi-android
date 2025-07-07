package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.repository.AddressBookRepository

class SaveABContactUseCase(
    private val addressBookRepository: AddressBookRepository,
    private val navigationRouter: NavigationRouter,
) {
    suspend operator fun invoke(
        name: String,
        address: String,
        chain: String?,
    ) {
        addressBookRepository.saveContact(name = name, address = address, chain = chain)
        navigationRouter.back()
    }
}
