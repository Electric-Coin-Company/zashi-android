package co.electriccoin.zcash.ui.screen.accountlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDesignType
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListItem
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListState
import co.electriccoin.zcash.ui.screen.accountlist.model.ZashiAccountListItemState
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystoneArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import co.electriccoin.zcash.ui.design.R


class AccountListViewModel : ViewModel() {
    private val isLoading = MutableStateFlow(true)

    val navigationCommand = MutableSharedFlow<String>()

    val state =
        isLoading.map { isLoading ->
            AccountListState(
                items =
                    listOf(
                        AccountListItem.Account(
                            ZashiAccountListItemState(
                                title = stringRes("Zashi"),
                                subtitle = stringRes("u1078r23uvtj8xj6dpdx..."),
                                icon = R.drawable.ic_item_zashi,
                                isSelected = true,
                                onClick = {}
                            )
                        ),
                        AccountListItem.Other(
                            ZashiListItemState(
                                title = stringRes("Keystone Hardware Wallet"),
                                subtitle = stringRes("Get a Keystone Hardware Wallet and secure your Zcash."),
                                icon = R.drawable.ic_item_keystone,
                                design = ZashiListItemDesignType.SECONDARY,
                                onClick = ::onAddWalletButtonClicked
                            )
                        )
                    ),
                isLoading = isLoading,
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
