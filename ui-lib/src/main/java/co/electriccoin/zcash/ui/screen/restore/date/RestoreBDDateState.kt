package co.electriccoin.zcash.ui.screen.restore.date

import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import java.time.YearMonth

data class RestoreBDDateState(
    val selection: YearMonth,
    val next: ButtonState,
    val dialogButton: IconButtonState,
    val onBack: () -> Unit,
    val onYearMonthChange: (YearMonth) -> Unit,
)
