package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.NearSwapAsset
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapMode.PAY
import co.electriccoin.zcash.ui.common.model.SwapMode.SWAP
import co.electriccoin.zcash.ui.common.model.near.QuoteRequest
import co.electriccoin.zcash.ui.common.model.near.QuoteResponseDto
import co.electriccoin.zcash.ui.common.model.near.RecipientType
import co.electriccoin.zcash.ui.common.model.near.RefundType
import co.electriccoin.zcash.ui.common.model.near.SubmitDepositTransactionRequest
import co.electriccoin.zcash.ui.common.model.near.SwapStatusResponseDto
import co.electriccoin.zcash.ui.common.model.near.SwapType
import co.electriccoin.zcash.ui.common.provider.BlockchainProvider
import co.electriccoin.zcash.ui.common.provider.NearApiProvider
import co.electriccoin.zcash.ui.common.provider.ResponseWithErrorException
import co.electriccoin.zcash.ui.common.provider.TokenIconProvider
import co.electriccoin.zcash.ui.common.provider.TokenNameProvider
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.time.Duration.Companion.minutes

interface SwapDataSource {
    @Throws(ResponseException::class)
    suspend fun getSupportedTokens(): List<NearSwapAsset>

    @Throws(ResponseException::class, QuoteLowAmountException::class)
    suspend fun requestQuote(
        swapMode: SwapMode,
        amount: BigDecimal,
        originAddress: String,
        originAsset: SwapAsset,
        destinationAddress: String,
        destinationAsset: SwapAsset,
        slippage: BigDecimal,
    ): QuoteResponseDto

    @Throws(ResponseException::class)
    suspend fun submitDepositTransaction(txHash: String, depositAddress: String): SwapStatusResponseDto
}

class QuoteLowAmountException(
    val asset: SwapAsset,
    val amount: BigDecimal?,
    val amountFormatted: BigDecimal?
) : Exception()

class SwapDataSourceImpl(
    private val blockchainProvider: BlockchainProvider,
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
                    blockchain = blockchainProvider.getBlockchain(it.blockchain)
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
        val decimals = when (swapMode) {
            SWAP -> originAsset.decimals
            PAY -> destinationAsset.decimals
        }

        val shifted = amount.movePointRight(decimals)
        val integer = shifted.toBigInteger().toBigDecimal()
        val normalizedAmount = shifted.round(MathContext(integer.precision(), RoundingMode.HALF_EVEN))

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
            amount = normalizedAmount,
            refundTo = originAddress,
            refundType = RefundType.ORIGIN_CHAIN,
            recipient = destinationAddress,
            recipientType = RecipientType.DESTINATION_CHAIN,
            deadline = Clock.System.now() + 10.minutes,
            quoteWaitingTimeMs = QUOTE_WAITING_TIME
        )

        return try {
            nearApiProvider.requestQuote(request)
        } catch (e: ResponseWithErrorException) {
            when {
                e.error.message.startsWith("Amount is too low for bridge, try at least") -> {
                    val errorAmount = e.error.message.split(" ").lastOrNull()?.toBigDecimalOrNull() ?: throw e
                    val errorAsset = when (swapMode) {
                        SWAP -> originAsset
                        PAY -> destinationAsset
                    }
                    throw QuoteLowAmountException(
                        asset = errorAsset,
                        amount = errorAmount,
                        amountFormatted = errorAmount.movePointLeft(errorAsset.decimals)
                    )
                }

                e.error.message.startsWith("No quotes found") -> throw QuoteLowAmountException(
                    asset = originAsset,
                    amount = null,
                    amountFormatted = null
                )

                else -> {
                    throw e
                }
            }
        }
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
