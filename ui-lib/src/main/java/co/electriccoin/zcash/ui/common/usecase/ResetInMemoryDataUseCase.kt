package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.HomeMessageCacheRepository

class ResetInMemoryDataUseCase(
    private val addressBookRepository: AddressBookRepository,
    private val homeMessageCacheRepository: HomeMessageCacheRepository
) {
    suspend operator fun invoke() {
        addressBookRepository.resetAddressBook()
        homeMessageCacheRepository.reset()
    }
}
