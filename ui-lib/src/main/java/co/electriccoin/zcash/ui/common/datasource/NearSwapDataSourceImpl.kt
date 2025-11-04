package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.NearSwapQuote
import co.electriccoin.zcash.ui.common.model.NearSwapQuoteStatus
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapQuote
import co.electriccoin.zcash.ui.common.model.SwapQuoteStatus
import co.electriccoin.zcash.ui.common.model.near.AppFee
import co.electriccoin.zcash.ui.common.model.near.QuoteRequest
import co.electriccoin.zcash.ui.common.model.near.RecipientType
import co.electriccoin.zcash.ui.common.model.near.RefundType
import co.electriccoin.zcash.ui.common.model.near.SubmitDepositTransactionRequest
import co.electriccoin.zcash.ui.common.model.near.SwapType
import co.electriccoin.zcash.ui.common.provider.NearApiProvider
import co.electriccoin.zcash.ui.common.provider.ResponseWithErrorException
import co.electriccoin.zcash.ui.common.provider.SwapAssetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class NearSwapDataSourceImpl(
    private val nearApiProvider: NearApiProvider,
    private val swapAssetProvider: SwapAssetProvider,
) : SwapDataSource {
    override suspend fun getSupportedTokens(): List<SwapAsset> =
        withContext(Dispatchers.Default) {
            nearApiProvider.getSupportedTokens().map {
                swapAssetProvider.get(
                    tokenTicker = it.symbol,
                    chainTicker = it.blockchain,
                    usdPrice = it.price,
                    assetId = it.assetId,
                    decimals = it.decimals
                )
            }
        }

    @Suppress("MagicNumber")
    override suspend fun requestQuote(
        swapMode: SwapMode,
        amount: BigDecimal,
        refundAddress: String,
        originAsset: SwapAsset,
        destinationAddress: String,
        destinationAsset: SwapAsset,
        slippage: BigDecimal,
        affiliateAddress: String
    ): SwapQuote {
        val decimals =
            when (swapMode) {
                SwapMode.EXACT_INPUT -> originAsset.decimals
                SwapMode.EXACT_OUTPUT -> destinationAsset.decimals
            }

        val shifted = amount.movePointRight(decimals)
        val integer = shifted.toBigInteger().toBigDecimal()
        val normalizedAmount = shifted.round(MathContext(integer.precision(), RoundingMode.DOWN))

        val request =
            QuoteRequest(
                dry = false,
                swapType =
                    when (swapMode) {
                        SwapMode.EXACT_INPUT -> SwapType.EXACT_INPUT
                        SwapMode.EXACT_OUTPUT -> SwapType.EXACT_OUTPUT
                    },
                slippageTolerance = slippage.multiply(BigDecimal(100), MathContext.DECIMAL128).toInt(),
                originAsset = originAsset.assetId,
                depositType = RefundType.ORIGIN_CHAIN,
                destinationAsset = destinationAsset.assetId,
                amount = normalizedAmount,
                refundTo = refundAddress,
                refundType = RefundType.ORIGIN_CHAIN,
                recipient = destinationAddress,
                recipientType = RecipientType.DESTINATION_CHAIN,
                deadline = Clock.System.now() + 2.hours,
                quoteWaitingTimeMs = QUOTE_WAITING_TIME,
                appFees =
                    listOf(
                        AppFee(
                            recipient = affiliateAddress,
                            fee = AFFILIATE_FEE_BPS
                        )
                    ),
                referral = "zashi"
            )

        return try {
            NearSwapQuote(
                response = nearApiProvider.requestQuote(request),
                originAsset = originAsset,
                destinationAsset = destinationAsset
            )
        } catch (e: ResponseWithErrorException) {
            when {
                e.error.message.startsWith("Amount is too low for bridge, try at least") -> {
                    val errorAmount =
                        e.error.message
                            .split(" ")
                            .lastOrNull()
                            ?.toBigDecimalOrNull() ?: throw e
                    val errorAsset =
                        when (swapMode) {
                            SwapMode.EXACT_INPUT -> originAsset
                            SwapMode.EXACT_OUTPUT -> destinationAsset
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

                else -> throw e
            }
        }
    }

    override suspend fun submitDepositTransaction(txHash: String, depositAddress: String) {
        nearApiProvider.submitDepositTransaction(
            SubmitDepositTransactionRequest(
                txHash = txHash,
                depositAddress = depositAddress
            )
        )
    }

    override suspend fun checkSwapStatus(depositAddress: String, supportedTokens: List<SwapAsset>): SwapQuoteStatus {
        val response = this.nearApiProvider.checkSwapStatus(depositAddress)
        val originAsset =
            supportedTokens.find { it.assetId == response.quoteResponse.quoteRequest.originAsset }
                ?: throw TokenNotFoundException(response.quoteResponse.quoteRequest.originAsset)
        val destinationAsset =
            supportedTokens.find { it.assetId == response.quoteResponse.quoteRequest.destinationAsset }
                ?: throw TokenNotFoundException(response.quoteResponse.quoteRequest.destinationAsset)
        return NearSwapQuoteStatus(
            response = response,
            origin = originAsset,
            destination = destinationAsset,
        )
    }
}

const val AFFILIATE_FEE_BPS = 50
private const val QUOTE_WAITING_TIME = 3000
