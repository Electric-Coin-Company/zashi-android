package co.electriccoin.zcash.ui.screen.restore.date

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.StringResource
import java.time.YearMonth

data class RestoreBDDateState(
    val title: StringResource,
    val subtitle: StringResource,
    val message: StringResource,
    val note: StringResource,
    val selection: YearMonth,
    val next: ButtonState,
    val dialogButton: IconButtonState,
    val onBack: () -> Unit,
    val onYearMonthChange: (YearMonth) -> Unit,
)
