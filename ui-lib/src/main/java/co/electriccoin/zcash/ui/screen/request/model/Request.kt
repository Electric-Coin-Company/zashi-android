package co.electriccoin.zcash.ui.screen.request.model

import cash.z.ecc.android.sdk.model.Zatoshi

data class Request(
    val amount: Zatoshi,
    val memo: String,
)