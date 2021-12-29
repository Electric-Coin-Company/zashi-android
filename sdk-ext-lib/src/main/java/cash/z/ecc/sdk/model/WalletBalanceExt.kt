package cash.z.ecc.sdk.model

import cash.z.ecc.android.sdk.type.WalletBalance

// These go away if we update WalletBalance to expose a Zatoshi field type instead of long
val WalletBalance.total get() = Zatoshi(totalZatoshi.coerceAtLeast(0))
val WalletBalance.available get() = Zatoshi(availableZatoshi.coerceAtLeast(0))
val WalletBalance.pending get() = Zatoshi(pendingZatoshi.coerceAtLeast(0))
