@file:OptIn(ExperimentalSerializationApi::class)

package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.math.BigDecimal

@JsonIgnoreUnknownKeys
@Serializable
data class GetCMCQuoteResponse(
    @SerialName("data")
    val data: Map<String, ZcashCoinData>
)

@JsonIgnoreUnknownKeys
@Serializable
data class ZcashCoinData(
    @SerialName("quote")
    val quote: Map<String, QuoteDetail>
)

@JsonIgnoreUnknownKeys
@Serializable
data class QuoteDetail(
    @SerialName("price")
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
)
