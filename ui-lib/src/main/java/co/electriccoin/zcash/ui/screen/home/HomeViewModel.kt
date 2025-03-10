package co.electriccoin.zcash.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.IsRestoreSuccessDialogVisibleUseCase
import co.electriccoin.zcash.ui.design.component.BigIconButtonState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.integrations.DialogIntegrations
import co.electriccoin.zcash.ui.screen.receive.Receive
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.send.Send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val navigationRouter: NavigationRouter,
    private val isRestoreSuccessDialogVisible: IsRestoreSuccessDialogVisibleUseCase
) : ViewModel() {
    private val isRestoreDialogVisible: Flow<Boolean?> =
        isRestoreSuccessDialogVisible.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    val restoreDialogState: StateFlow<HomeRestoreDialogState?> =
        isRestoreDialogVisible
            .map { isVisible ->
                HomeRestoreDialogState(
                    onClick = ::onRestoreDialogSeenClick
                ).takeIf { isVisible == true }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

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

    fun onRestoreDialogSeenClick() =
        viewModelScope.launch {
            isRestoreSuccessDialogVisible.setSeen()
        }

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
