package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.model.AddressType.SAPLING
import cash.z.ecc.sdk.model.AddressType.TEX
import cash.z.ecc.sdk.model.AddressType.TRANSPARENT
import cash.z.ecc.sdk.model.AddressType.UNIFIED
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.common.usecase.Zip321ParseUriValidationUseCase.Zip321ParseUriValidation
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.reviewtransaction.ReviewTransaction
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.ScanFlow.ADDRESS_BOOK
import co.electriccoin.zcash.ui.screen.scan.ScanFlow.HOMEPAGE
import co.electriccoin.zcash.ui.screen.scan.ScanFlow.SEND
import co.electriccoin.zcash.ui.screen.send.Send

class OnZip321ScannedUseCase(
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val accountDataSource: AccountDataSource,
    private val navigationRouter: NavigationRouter,
    private val prefillSend: PrefillSendUseCase
) {
    suspend operator fun invoke(
        zip321: Zip321ParseUriValidation.Valid,
        scanArgs: Scan
    ) {
        when (scanArgs.flow) {
            ADDRESS_BOOK -> addressBookFlow(zip321)
            SEND ->
                if (scanArgs.isScanZip321Enabled) {
                    sendFlow(zip321)
                } else {
                    sendFlowWithDisabledZip321(zip321)
                }

            HOMEPAGE -> homepageFlow(zip321)
        }
    }

    private fun addressBookFlow(zip321: Zip321ParseUriValidation.Valid) {
        navigationRouter.replace(
            AddContactArgs(
                zip321.payment.payments[0]
                    .recipientAddress.value
            )
        )
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun homepageFlow(zip321: Zip321ParseUriValidation.Valid) {
        try {
            val proposal =
                when (accountDataSource.getSelectedAccount()) {
                    is KeystoneAccount -> {
                        val result = keystoneProposalRepository.createZip321Proposal(zip321.zip321Uri)
                        keystoneProposalRepository.createPCZTFromProposal()
                        result
                    }

                    is ZashiAccount -> {
                        zashiProposalRepository.createZip321Proposal(zip321.zip321Uri)
                    }
                }

            navigationRouter
                .replace(
                    Send(
                        recipientAddress = proposal.destination.address,
                        recipientAddressType =
                            when (proposal.destination) {
                                is WalletAddress.Sapling -> SAPLING
                                is WalletAddress.Tex -> TEX
                                is WalletAddress.Transparent -> TRANSPARENT
                                is WalletAddress.Unified -> UNIFIED
                            }
                    ),
                    ReviewTransaction
                )
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            throw e
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun sendFlow(zip321: Zip321ParseUriValidation.Valid) {
        try {
            val proposal =
                when (accountDataSource.getSelectedAccount()) {
                    is KeystoneAccount -> {
                        val result = keystoneProposalRepository.createZip321Proposal(zip321.zip321Uri)
                        keystoneProposalRepository.createPCZTFromProposal()
                        result
                    }

                    is ZashiAccount -> {
                        zashiProposalRepository.createZip321Proposal(zip321.zip321Uri)
                    }
                }

            prefillSend.request(
                PrefillSendData.All(
                    amount = proposal.amount,
                    address = proposal.destination.address,
                    fee = proposal.proposal.totalFeeRequired(),
                    memos =
                        proposal.memo.value
                            .takeIf { it.isNotEmpty() }
                            ?.let { listOf(it) }
                )
            )
            navigationRouter.forward(ReviewTransaction)
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            throw e
        }
    }

    private fun sendFlowWithDisabledZip321(zip321: Zip321ParseUriValidation.Valid) {
        prefillSend.request(
            PrefillSendData.FromAddressScan(
                address =
                    zip321.payment.payments[0]
                        .recipientAddress.value
            )
        )
        navigationRouter.back()
    }
}
