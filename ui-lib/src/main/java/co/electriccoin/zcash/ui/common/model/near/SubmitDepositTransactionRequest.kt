package co.electriccoin.zcash.ui.common.model.near

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitDepositTransactionRequest(
    @SerialName("txHash")
    val txHash: String,

    @SerialName("depositAddress")
    val depositAddress: String
)