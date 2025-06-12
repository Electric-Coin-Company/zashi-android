package co.electriccoin.zcash.ui.screen.restore.estimation

import androidx.lifecycle.ViewModel
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.SeedPhrase
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.RestoreWalletUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.restore.info.SeedInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RestoreBDEstimationViewModel(
    private val args: RestoreBDEstimation,
    private val navigationRouter: NavigationRouter,
    private val restoreWallet: RestoreWalletUseCase,
    private val copyToClipboard: CopyToClipboardUseCase
) : ViewModel() {
    val state: StateFlow<RestoreBDEstimationState> = MutableStateFlow(createState()).asStateFlow()

    private fun createState() =
        RestoreBDEstimationState(
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
            restore = ButtonState(stringRes(R.string.restore_bd_estimation_restore), onClick = ::onRestoreClick),
        )

    private fun onCopyClick() {
        copyToClipboard(
            tag = "clipboard",
            value = args.blockHeight.toString()
        )
    }

    private fun onRestoreClick() {
        restoreWallet(
            seedPhrase = SeedPhrase.new(args.seed),
            birthday = BlockHeight.new(args.blockHeight)
        )
    }

    private fun onBack() = navigationRouter.back()

    private fun onInfoButtonClick() = navigationRouter.forward(SeedInfo)
}
