package co.electriccoin.zcash.ui.common.datasource

import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapQuote
import co.electriccoin.zcash.ui.common.model.SwapQuoteStatus
import io.ktor.client.plugins.ResponseException
import okio.IOException
import java.math.BigDecimal

interface SwapDataSource {
    @Throws(ResponseException::class)
    suspend fun getSupportedTokens(): List<SwapAsset>

    @Throws(ResponseException::class, QuoteLowAmountException::class, IOException::class)
    suspend fun requestQuote(
        swapMode: SwapMode,
        amount: BigDecimal,
        refundAddress: String,
        originAsset: SwapAsset,
        destinationAddress: String,
        destinationAsset: SwapAsset,
        slippage: BigDecimal,
        affiliateAddress: String
    ): SwapQuote

    @Throws(ResponseException::class, IOException::class)
    suspend fun submitDepositTransaction(txHash: String, depositAddress: String)

    @Throws(ResponseException::class, IOException::class, TokenNotFoundException::class)
    suspend fun checkSwapStatus(depositAddress: String, supportedTokens: List<SwapAsset>): SwapQuoteStatus
}

class QuoteLowAmountException(
    val asset: SwapAsset,
    val amount: BigDecimal?,
    val amountFormatted: BigDecimal?
) : Exception()

class TokenNotFoundException(tokenId: String): Exception("Token $tokenId not found")
