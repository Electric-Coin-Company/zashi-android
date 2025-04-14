package co.electriccoin.zcash.ui.screen.restore.estimation

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RestoreBDEstimationViewModel(
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state: StateFlow<RestoreBDEstimationState> = MutableStateFlow(createState()).asStateFlow()

    private fun createState() =
        RestoreBDEstimationState(
            dialogButton =
                IconButtonState(
                    icon = co.electriccoin.zcash.ui.design.R.drawable.ic_info,
                    onClick = ::onInfoButtonClick,
                ),
            onBack = ::onBack,
            text = stringRes("123456"),
            copy = ButtonState(stringRes(R.string.restore_bd_estimation_copy), icon = R.drawable.ic_copy) {},
            restore = ButtonState(stringRes(R.string.restore_bd_estimation_restore), onClick = ::onRestoreClick),
        )

    private fun onRestoreClick() {
        // do nothing
    }

    private fun onBack() {
        navigationRouter.back()
    }

    private fun onInfoButtonClick() {
        navigationRouter.forward(SeedInfo)
    }
}
