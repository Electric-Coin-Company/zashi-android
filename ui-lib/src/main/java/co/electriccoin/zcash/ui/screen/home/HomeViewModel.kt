package co.electriccoin.zcash.ui.screen.home

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.integrations.DialogIntegrations
import co.electriccoin.zcash.ui.screen.receive.Receive
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.send.Send
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    val state: StateFlow<HomeState?> =
        MutableStateFlow(
            HomeState(
                receiveButton =
                    BigIconButtonState(
                        text = stringRes("Receive"),
                        icon = R.drawable.ic_home_receive,
                        onClick = ::onReceiveButtonClick,
                    ),
                sendButton =
                    BigIconButtonState(
                        text = stringRes("Send"),
                        icon = R.drawable.ic_home_send,
                        onClick = ::onSendButtonClick,
                    ),
                scanButton =
                    BigIconButtonState(
                        text = stringRes("Scan"),
                        icon = R.drawable.ic_home_scan,
                        onClick = ::onScanButtonClick,
                    ),
                moreButton =
                    BigIconButtonState(
                        text = stringRes("More"),
                        icon = R.drawable.ic_home_more,
                        onClick = ::onMoreButtonClick,
                    ),
            )
        ).asStateFlow()

    private fun onMoreButtonClick() {
        navigationRouter.forward(DialogIntegrations)
    }

    private fun onSendButtonClick() {
        navigationRouter.forward(Send())
    }

    private fun onReceiveButtonClick() {
        navigationRouter.forward(Receive)
    }

    private fun onScanButtonClick() {
        navigationRouter.forward(Scan(Scan.HOMEPAGE))
    }
}
