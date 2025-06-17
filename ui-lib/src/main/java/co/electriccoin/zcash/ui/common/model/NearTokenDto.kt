package co.electriccoin.zcash.ui.common.model

import co.electriccoin.zcash.ui.common.serialization.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class NearTokenDto(
    @SerialName("assetId")
    val assetId: String,
    @SerialName("decimals")
    val decimals: Long,
    @SerialName("blockchain")
    val blockchain: String,
    @SerialName("symbol")
    val symbol: String,
    @SerialName("price")
    @Serializable(BigDecimalSerializer::class)
    val price: BigDecimal? = null,
    @SerialName("priceUpdatedAt")
    val priceUpdatedAt: String,
    @SerialName("contractAddress")
    val contractAddress: String? = null,
)
