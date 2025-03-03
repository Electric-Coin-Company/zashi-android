package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.MetadataRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetMetadataUseCase(
    private val metadataRepository: MetadataRepository,
) {
    suspend operator fun invoke() = observe().first()

    fun observe() = metadataRepository.metadata.filterNotNull()
}
