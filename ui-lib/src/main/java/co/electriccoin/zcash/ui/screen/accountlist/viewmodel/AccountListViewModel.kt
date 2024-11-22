package co.electriccoin.zcash.ui.screen.accountlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListState
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystoneArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountListViewModel : ViewModel() {
    private val isLoading = MutableStateFlow(true)

    val navigationCommand = MutableSharedFlow<String>()

    val state =
        isLoading.map { isLoading ->
            AccountListState(
                accounts =
                    listOf(
                        ZashiListItemState(
                            text = stringRes("title"),
                            subtitle = stringRes("subtitle"),
                            icon = R.drawable.ic_radio_button_checked
                        ),
                        ZashiListItemState(
                            text = stringRes("title"),
                            subtitle = stringRes("subtitle"),
                            icon = R.drawable.ic_radio_button_checked
                        )
                    ),
                isLoading = isLoading,
                addWalletButton =
                    ButtonState(
                        text = stringRes("Add hardware wallet"),
                        onClick = ::onAddWalletButtonClicked
                    )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun onAddWalletButtonClicked() {
        viewModelScope.launch {
            navigationCommand.emit(ConnectKeystoneArgs.PATH)
        }
    }
}
