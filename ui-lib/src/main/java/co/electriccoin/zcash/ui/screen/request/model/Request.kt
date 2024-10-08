package co.electriccoin.zcash.ui.screen.request.model

import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.Zatoshi

data class Request(
    val recipientAddress: WalletAddress,
    val amount: Zatoshi,
    val memo: String,
)