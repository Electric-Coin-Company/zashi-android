package co.electriccoin.zcash.ui.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val price: String,
    @SerialName("priceUpdatedAt")
    val priceUpdatedAt: String,
    @SerialName("contractAddress")
    val contractAddress: String? = null,
)
