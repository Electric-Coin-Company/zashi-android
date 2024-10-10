package co.electriccoin.zcash.ui.common.provider

import cash.z.ecc.android.sdk.model.MonetarySeparators
import java.util.Locale

class GetMonetarySeparatorProvider() {
    operator fun invoke() = MonetarySeparators.current(Locale.getDefault())
}
