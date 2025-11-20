package co.electriccoin.zcash.ui.screen.resync.estimation

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToEstimateBlockHeightUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.estimation.RestoreBDEstimationState
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResyncBDEstimationVM(
    private val args: ResyncBDEstimationArgs,
    private val navigationRouter: NavigationRouter,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val navigateToEstimateBlockHeight: NavigateToEstimateBlockHeightUseCase,
) : ViewModel() {
    val state: StateFlow<RestoreBDEstimationState> = MutableStateFlow(createState()).asStateFlow()

    private fun createState() =
        RestoreBDEstimationState(
            title = stringRes(R.string.resync_title),
            subtitle = stringRes(R.string.resync_bd_estimation_subtitle),
            message = stringRes(R.string.resync_bd_estimation_message),
            dialogButton =
                IconButtonState(
                    icon = R.drawable.ic_help,
                    onClick = ::onInfoButtonClick,
                ),
            onBack = ::onBack,
            text = stringRes(args.blockHeight.toString()),
            copy =
                ButtonState(
                    text = stringRes(R.string.restore_bd_estimation_copy),
                    icon = R.drawable.ic_copy,
                    onClick = ::onCopyClick
                ),
            restore =
                ButtonState(
                    text = stringRes(R.string.resync_bd_estimation_btn),
                    onClick = ::onSetHeightClick,
                    hapticFeedbackType = HapticFeedbackType.Confirm
                ),
        )

    private fun onCopyClick() {
        copyToClipboard(
            value = args.blockHeight.toString()
        )
    }

    private fun onSetHeightClick() {
        viewModelScope.launch {
            navigateToEstimateBlockHeight.onSelected(args.blockHeight, args)
        }
    }

    private fun onBack() = navigationRouter.back()

    private fun onInfoButtonClick() = navigationRouter.forward(SeedInfo)
}
