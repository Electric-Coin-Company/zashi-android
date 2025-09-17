package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.model.SwapQuote
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import co.electriccoin.zcash.ui.common.repository.KeystoneProposalRepository
import co.electriccoin.zcash.ui.common.repository.SwapQuoteData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.repository.ZashiProposalRepository
import co.electriccoin.zcash.ui.screen.swap.quote.SwapQuoteArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class RequestSwapQuoteUseCase(
    private val swapRepository: SwapRepository,
    private val zashiProposalRepository: ZashiProposalRepository,
    private val keystoneProposalRepository: KeystoneProposalRepository,
    private val accountDataSource: AccountDataSource,
    private val synchronizerProvider: SynchronizerProvider,
    private val navigationRouter: NavigationRouter,
    private val navigateToErrorUseCase: NavigateToErrorUseCase,
) {
    suspend fun requestExactInput(
        amount: BigDecimal,
        address: String,
        canNavigateToSwapQuote: () -> Boolean
    ) {
        requestQuote(
            requestQuote = { swapRepository.requestExactInputQuote(amount, address) },
            createProposal = true,
            canNavigateToSwapQuote = canNavigateToSwapQuote
        )
    }

    suspend fun requestExactOutput(
        amount: BigDecimal,
        address: String,
        canNavigateToSwapQuote: () -> Boolean
    ) {
        requestQuote(
            requestQuote = { swapRepository.requestExactOutputQuote(amount, address) },
            createProposal = true,
            canNavigateToSwapQuote = canNavigateToSwapQuote
        )
    }

    suspend fun requestExactInputIntoZec(
        amount: BigDecimal,
        refundAddress: String,
        canNavigateToSwapQuote: () -> Boolean
    ) {
        requestQuote(
            requestQuote = { swapRepository.requestExactInputIntoZec(amount, refundAddress) },
            createProposal = false,
            canNavigateToSwapQuote = canNavigateToSwapQuote
        )
    }

    private suspend fun requestQuote(
        requestQuote: suspend () -> Unit,
        createProposal: Boolean,
        canNavigateToSwapQuote: () -> Boolean
    ) = withContext(Dispatchers.Default) {
        requestQuote()

        val result = swapRepository.quote.filter { it !is SwapQuoteData.Loading }.first()

        if (result is SwapQuoteData.Success) {
            try {
                if (createProposal) {
                    createProposal(result.quote)
                }
            } catch (e: Exception) {
                swapRepository.clearQuote()
                zashiProposalRepository.clear()
                keystoneProposalRepository.clear()
                navigateToErrorUseCase(ErrorArgs.General(e))
                return@withContext
            }
        }

        if (canNavigateToSwapQuote()) {
            navigationRouter.forward(SwapQuoteArgs)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun createProposal(quote: SwapQuote) {
        try {
            val send =
                ZecSend(
                    destination = getWalletAddress(quote.depositAddress),
                    amount = quote.destinationAmountZatoshi,
                    memo = Memo(""),
                    proposal = null
                )

            when (accountDataSource.getSelectedAccount()) {
                is KeystoneAccount -> {
                    when (quote.type) {
                        EXACT_INPUT ->
                            keystoneProposalRepository.createExactInputSwapProposal(send, quote)

                        EXACT_OUTPUT ->
                            keystoneProposalRepository.createExactOutputSwapProposal(send, quote)
                    }
                    keystoneProposalRepository.createPCZTFromProposal()
                }

                is ZashiAccount ->
                    when (quote.type) {
                        EXACT_INPUT ->
                            zashiProposalRepository.createExactInputSwapProposal(send, quote)

                        EXACT_OUTPUT ->
                            zashiProposalRepository.createExactOutputSwapProposal(send, quote)
                    }
            }
        } catch (e: Exception) {
            keystoneProposalRepository.clear()
            zashiProposalRepository.clear()
            throw e
        }
    }

    private suspend fun getWalletAddress(address: String): WalletAddress =
        when (val result = synchronizerProvider.getSynchronizer().validateAddress(address)) {
            AddressType.Shielded -> WalletAddress.Sapling.new(address)
            AddressType.Tex -> WalletAddress.Tex.new(address)
            AddressType.Transparent -> WalletAddress.Transparent.new(address)
            AddressType.Unified -> WalletAddress.Unified.new(address)
            is AddressType.Invalid -> throw IllegalStateException(result.reason)
        }
}
