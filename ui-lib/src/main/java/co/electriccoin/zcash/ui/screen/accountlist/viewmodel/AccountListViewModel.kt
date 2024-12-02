package co.electriccoin.zcash.ui.screen.accountlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiSettingsListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccountListViewModel : ViewModel() {
    private val isLoading = MutableStateFlow(true)

    val state =
        isLoading.map { isLoading ->
            AccountListState(
                accounts =
                    listOf(
                        ZashiSettingsListItemState(
                            text = stringRes("title"),
                            subtitle = stringRes("subtitle"),
                            icon = R.drawable.ic_radio_button_checked
                        ),
                        ZashiSettingsListItemState(
                            text = stringRes("title"),
                            subtitle = stringRes("subtitle"),
                            icon = R.drawable.ic_radio_button_checked
                        )
                    ),
                isLoading = isLoading,
                addWalletButton =
                    ButtonState(
                        text = stringRes("Add hardware wallet")
                    )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )
}
