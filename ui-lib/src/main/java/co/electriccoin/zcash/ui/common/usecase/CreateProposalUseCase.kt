package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.type.AddressType
import cash.z.ecc.sdk.extension.floor
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.datasource.InsufficientFundsException
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.screen.insufficientfunds.InsufficientFundsArgs
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransactionArgs
import co.electriccoin.zcash.ui.screen.texunsupported.TEXUnsupportedArgs

class CreateProposalUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val accountDataSource: AccountDataSource,
    private val synchronizerProvider: SynchronizerProvider,
    private val navigationRouter: NavigationRouter,
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(zecSend: ZecSend, floor: Boolean) {
        val normalized = if (floor) zecSend.copy(amount = zecSend.amount.floor()) else zecSend
        try {
            when (accountDataSource.getSelectedAccount()) {
                is KeystoneAccount -> {
                    val destination =
                        synchronizerProvider
                            .getSynchronizer()
                            .validateAddress(zecSend.destination.address)
                    if (destination is AddressType.Tex) {
                        throw TexUnsupportedOnKSException()
                    } else {
                        keystoneProposalRepository.createProposal(normalized)
                        keystoneProposalRepository.createPCZTFromProposal()
                    }
                }

                is ZashiAccount ->
                    zashiProposalRepository.createProposal(normalized)
            }
            navigationRouter.forward(ReviewTransactionArgs)
        } catch (_: TexUnsupportedOnKSException) {
            navigationRouter.forward(TEXUnsupportedArgs)
            keystoneProposalRepository.clear()
            zashiProposalRepository.clear()
        } catch (_: InsufficientFundsException) {
            keystoneProposalRepository.clear()
            zashiProposalRepository.clear()
            navigationRouter.forward(InsufficientFundsArgs)
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            zashiProposalRepository.clear()
            throw e
        }
    }
}

private class TexUnsupportedOnKSException : Exception("TEX addresses are unsupported on KS")
