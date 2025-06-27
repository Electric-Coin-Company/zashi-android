package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.NearSwapAsset
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.near.QuoteRequest
import co.electriccoin.zcash.ui.common.model.near.QuoteResponseDto
import co.electriccoin.zcash.ui.common.model.near.RecipientType
import co.electriccoin.zcash.ui.common.model.near.RefundType
import co.electriccoin.zcash.ui.common.model.near.SubmitDepositTransactionRequest
import co.electriccoin.zcash.ui.common.model.near.SwapStatusResponseDto
import co.electriccoin.zcash.ui.common.model.near.SwapType
import co.electriccoin.zcash.ui.common.provider.ChainIconProvider
import co.electriccoin.zcash.ui.common.provider.ChainNameProvider
import co.electriccoin.zcash.ui.common.provider.NearApiProvider
import co.electriccoin.zcash.ui.common.provider.TokenIconProvider
import co.electriccoin.zcash.ui.common.provider.TokenNameProvider
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapMode.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.io.IOException
import java.math.BigDecimal
import java.math.MathContext
import kotlin.time.Duration.Companion.minutes

interface SwapDataSource {
    @Throws(IOException::class)
    suspend fun getSupportedTokens(): List<NearSwapAsset>

    @Throws(IOException::class)
    suspend fun requestQuote(
        swapMode: SwapMode,
        amount: BigDecimal,
        originAddress: String,
        originAsset: SwapAsset,
        destinationAddress: String,
        destinationAsset: SwapAsset,
        slippage: BigDecimal,
    ): QuoteResponseDto

    @Throws(IOException::class)
    suspend fun submitDepositTransaction(txHash: String, depositAddress: String): SwapStatusResponseDto
}

class SwapDataSourceImpl(
    private val chainIconProvider: ChainIconProvider,
    private val chainNameProvider: ChainNameProvider,
    private val tokenIconProvider: TokenIconProvider,
    private val tokenNameProvider: TokenNameProvider,
    private val nearApiProvider: NearApiProvider,
) : SwapDataSource {
    override suspend fun getSupportedTokens(): List<NearSwapAsset> =
        withContext(Dispatchers.Default) {
            nearApiProvider.getSupportedTokens().map {
                NearSwapAsset(
                    token = it,
                    tokenName = tokenNameProvider.getName(it.symbol),
                    tokenIcon = tokenIconProvider.getIcon(it.symbol),
                    chainName = chainNameProvider.getName(it.blockchain),
                    chainIcon = chainIconProvider.getIcon(it.blockchain)
                )
            }
        }

    override suspend fun requestQuote(
        swapMode: SwapMode,
        amount: BigDecimal,
        originAddress: String,
        originAsset: SwapAsset,
        destinationAddress: String,
        destinationAsset: SwapAsset,
        slippage: BigDecimal,
    ): QuoteResponseDto {
        require(originAsset is NearSwapAsset)
        require(destinationAsset is NearSwapAsset)

        val request = QuoteRequest(
            dry = false,
            swapType = when (swapMode) {
                SWAP -> SwapType.EXACT_INPUT
                PAY -> SwapType.EXACT_OUTPUT
            },
            slippageTolerance = slippage.multiply(BigDecimal(100), MathContext.DECIMAL128).toInt(),
            originAsset = originAsset.assetId,
            depositType = RefundType.ORIGIN_CHAIN,
            destinationAsset = destinationAsset.assetId,
            amount = when (swapMode) {
                SWAP -> amount.movePointRight(originAsset.token.decimals)
                PAY -> amount.movePointRight(destinationAsset.token.decimals)
            },
            refundTo = originAddress,
            refundType = RefundType.ORIGIN_CHAIN,
            recipient = destinationAddress,
            recipientType = RecipientType.DESTINATION_CHAIN,
            deadline = Clock.System.now() + 10.minutes,
            quoteWaitingTimeMs = QUOTE_WAITING_TIME
        )

        return nearApiProvider.requestQuote(request)
    }

    override suspend fun submitDepositTransaction(txHash: String, depositAddress: String): SwapStatusResponseDto {
        return nearApiProvider.submitDepositTransaction(
            SubmitDepositTransactionRequest(
                txHash = txHash,
                depositAddress = depositAddress
            )
        )
    }
}

private const val QUOTE_WAITING_TIME = 3000
