package co.electriccoin.zcash.ui.screen.restore.date

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.estimation.RestoreBDEstimationArgs
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinInstant
import java.time.YearMonth
import java.time.ZoneId

class RestoreBDDateVM(
    private val args: RestoreBDDateArgs,
    private val navigationRouter: NavigationRouter,
    private val application: Application,
) : ViewModel() {
    @Suppress("MagicNumber")
    private val selection = MutableStateFlow<YearMonth>(YearMonth.of(2018, 10))

    val state: StateFlow<RestoreBDDateState?> =
        selection
            .map {
                createState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = createState(selection.value)
            )

    private fun createState(selection: YearMonth) =
        RestoreBDDateState(
            title = stringRes(R.string.restore_title),
            subtitle = stringRes(R.string.restore_bd_date_subtitle),
            message = stringRes(R.string.restore_bd_date_message),
            note = stringRes(R.string.restore_bd_date_note),
            next = ButtonState(stringRes(R.string.restore_bd_height_btn), onClick = ::onEstimateClick),
            dialogButton =
                IconButtonState(
                    icon = R.drawable.ic_help,
                    onClick = ::onInfoButtonClick,
                ),
            onBack = ::onBack,
            onYearMonthChange = ::onYearMonthChange,
            selection = selection
        )

    private fun onEstimateClick() {
        viewModelScope.launch {
            val instant =
                selection.value
                    .atDay(1)
                    .atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toKotlinInstant()
            val bday =
                SdkSynchronizer.estimateBirthdayHeight(
                    context = application,
                    date = instant,
                    network = VersionInfo.NETWORK
                )
            navigationRouter.forward(RestoreBDEstimationArgs(seed = args.seed, blockHeight = bday.value))
        }
    }

    private fun onBack() = navigationRouter.back()

    private fun onInfoButtonClick() = navigationRouter.forward(SeedInfo)

    private fun onYearMonthChange(yearMonth: YearMonth) = selection.update { yearMonth }
}
