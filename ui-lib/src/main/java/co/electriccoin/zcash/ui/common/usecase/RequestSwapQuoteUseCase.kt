package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.NearSwapQuote
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.model.near.SwapType
import co.electriccoin.zcash.ui.common.model.near.SwapType.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.near.SwapType.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteArgs
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class RequestSwapQuoteUseCase(
    private val swapRepository: SwapRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val accountDataSource: AccountDataSource,
    private val synchronizerProvider: SynchronizerProvider,
    private val navigationRouter: NavigationRouter,
    private val navigateToErrorUseCase: NavigateToErrorUseCase
) {
    suspend operator fun invoke(amount: BigDecimal, address: String, canNavigateToSwapQuote: () -> Boolean) {
        swapRepository.requestQuote(amount = amount, address = address)
        val result = swapRepository.quote.filter { it != null && it !is SwapQuoteData.Loading }.first()

        if (result is SwapQuoteData.Success) {
            val destinationAmount: Zatoshi
            val destinationAddress: WalletAddress
            val swapType: SwapType

            try {
                when (result.quote) {
                    is NearSwapQuote -> {
                        destinationAmount = Zatoshi(result.quote.response.quote.amountIn.toLong())
                        destinationAddress = getWalletAddress(result.quote.response.quote.depositAddress)
                        swapType = result.quote.response.quoteRequest.swapType
                    }
                }

                createProposal(destinationAddress, destinationAmount, swapType)
            } catch (e: Exception) {
                swapRepository.clearQuote()
                zashiProposalRepository.clear()
                keystoneProposalRepository.clear()
                navigateToErrorUseCase(ErrorArgs.General(e))
                return
            }
        }

        if (canNavigateToSwapQuote()) {
            navigationRouter.forward(SwapQuoteArgs)
        }
    }

    private suspend fun createProposal(
        destination: WalletAddress,
        amount: Zatoshi,
        swapType: SwapType,
    ) {
        try {
            val zecSend = ZecSend(
                destination = destination,
                amount = amount,
                memo = Memo(""),
                proposal = null
            )

            when (accountDataSource.getSelectedAccount()) {
                is KeystoneAccount -> {
                    when (swapType) {
                        EXACT_INPUT -> keystoneProposalRepository.createExactInputSwapProposal(zecSend)
                        EXACT_OUTPUT -> keystoneProposalRepository.createExactOutputSwapProposal(zecSend)
                    }

                    keystoneProposalRepository.createPCZTFromProposal()
                }
                is ZashiAccount -> when (swapType) {
                    EXACT_INPUT -> zashiProposalRepository.createExactInputSwapProposal(zecSend)
                    EXACT_OUTPUT -> zashiProposalRepository.createExactOutputSwapProposal(zecSend)
                }
            }
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            zashiProposalRepository.clear()
            throw e
        }
    }

    private suspend fun getWalletAddress(address: String): WalletAddress {
        return when (val result = synchronizerProvider.getSynchronizer().validateAddress(address)) {
            AddressType.Shielded -> WalletAddress.Sapling.new(address)
            AddressType.Tex -> WalletAddress.Tex.new(address)
            AddressType.Transparent -> WalletAddress.Transparent.new(address)
            AddressType.Unified -> WalletAddress.Unified.new(address)
            is AddressType.Invalid -> throw IllegalStateException(result.reason)
        }
    }
}
