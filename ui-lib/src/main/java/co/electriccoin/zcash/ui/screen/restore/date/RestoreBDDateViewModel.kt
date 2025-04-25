package co.electriccoin.zcash.ui.screen.restore.date

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.SdkSynchronizer
import cash.z.ecc.android.sdk.model.ZcashNetwork
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import cash.z.ecc.sdk.type.fromResources
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.estimation.RestoreBDEstimation
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

class RestoreBDDateViewModel(
    private val args: RestoreBDDate,
    private val navigationRouter: NavigationRouter,
    private val context: Context,
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
                initialValue = null
            )

    private fun createState(selection: YearMonth) =
        RestoreBDDateState(
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
                    context = context,
                    date = instant,
                    network = ZcashNetwork.fromResources(context)
                )
            navigationRouter.forward(RestoreBDEstimation(seed = args.seed, blockHeight = bday.value))
        }
    }

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onInfoButtonClick() {
        navigationRouter.forward(SeedInfo)
    }

    private fun onYearMonthChange(yearMonth: YearMonth) {
        selection.update { yearMonth }
    }
}
