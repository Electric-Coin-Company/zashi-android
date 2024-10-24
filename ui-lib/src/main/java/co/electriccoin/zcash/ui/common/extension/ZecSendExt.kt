package co.electriccoin.zcash.ui.common.extension

import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend

fun ZecSend.totalAmount() = amount + (proposal?.totalFeeRequired() ?: Zatoshi(0))
