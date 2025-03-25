package co.electriccoin.zcash.ui.screen.restore.date

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.estimation.RestoreBDEstimation
import co.electriccoin.zcash.ui.screen.restore.info.RestoreSeedInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RestoreBDDateViewModel(
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state: StateFlow<RestoreBDDateState> = MutableStateFlow(createState()).asStateFlow()

    private fun createState() =
        RestoreBDDateState(
            next = ButtonState(stringRes(R.string.restore_bd_height_btn), onClick = ::onEstimateClick),
            dialogButton = IconButtonState(icon = R.drawable.ic_info, onClick = ::onInfoButtonClick),
            onBack = ::onBack,
        )

    private fun onEstimateClick() {
        navigationRouter.forward(RestoreBDEstimation)
    }

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onInfoButtonClick() {
        navigationRouter.forward(RestoreSeedInfo)
    }
}
