package co.electriccoin.zcash.ui.screen.scankeystone.model

import androidx.annotation.IntRange
import co.electriccoin.zcash.ui.design.util.StringResource

data class ScanKeystoneState(
    val message: StringResource,
    @IntRange(from = 0, to = 100) val progress: Int?
)
