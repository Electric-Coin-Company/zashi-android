package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.MetadataRepository

class ResetInMemoryDataUseCase(
    private val addressBookRepository: AddressBookRepository,
    private val metadataRepository: MetadataRepository
) {
    suspend operator fun invoke() {
        addressBookRepository.resetAddressBook()
        metadataRepository.resetMetadata()
    }
}
