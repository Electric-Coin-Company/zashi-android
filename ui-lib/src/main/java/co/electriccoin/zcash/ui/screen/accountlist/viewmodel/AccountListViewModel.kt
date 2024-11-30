package co.electriccoin.zcash.ui.screen.accountlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.common.model.KeystoneAccount
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.model.ZashiAccount
import co.electriccoin.zcash.ui.common.usecase.ObserveWalletAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.SelectWalletAccountUseCase
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemDesignType
import co.electriccoin.zcash.ui.design.component.listitem.ZashiListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListItem
import co.electriccoin.zcash.ui.screen.accountlist.model.AccountListState
import co.electriccoin.zcash.ui.screen.accountlist.model.ZashiAccountListItemState
import co.electriccoin.zcash.ui.screen.addressbook.viewmodel.ADDRESS_MAX_LENGTH
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystoneArgs
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountListViewModel(
    observeWalletAccounts: ObserveWalletAccountsUseCase,
    private val selectWalletAccount: SelectWalletAccountUseCase,
) : ViewModel() {
    val navigationCommand = MutableSharedFlow<String>()
    val backNavigationCommand = MutableSharedFlow<Unit>()

    val state =
        observeWalletAccounts().map { accounts ->
            val items = listOfNotNull(
                *accounts.orEmpty()
                    .map<WalletAccount, AccountListItem> { account ->
                        AccountListItem.Account(
                            ZashiAccountListItemState(
                                title = when (account) {
                                    is KeystoneAccount -> stringRes("Keystone")
                                    is ZashiAccount -> stringRes("Zashi")
                                },
                                subtitle =
                                stringRes("${account.unifiedAddress.address.take(ADDRESS_MAX_LENGTH)}..."),
                                icon = when (account) {
                                    is KeystoneAccount -> R.drawable.ic_item_keystone
                                    is ZashiAccount -> R.drawable.ic_item_zashi
                                },
                                isSelected = account.isSelected,
                                onClick = { onAccountClicked(account) }
                            )
                        )
                    }.toTypedArray(),
                AccountListItem.Other(
                    ZashiListItemState(
                        title = stringRes("Keystone Hardware Wallet"),
                        subtitle = stringRes("Get a Keystone Hardware Wallet and secure your Zcash."),
                        icon = R.drawable.ic_item_keystone,
                        design = ZashiListItemDesignType.SECONDARY,
                        onClick = ::onAddWalletButtonClicked
                    )
                ).takeIf {
                    accounts.orEmpty().none { it is KeystoneAccount }
                }
            )

            AccountListState(
                items = items,
                isLoading = accounts == null,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private fun onAccountClicked(account: WalletAccount) = viewModelScope.launch {
        selectWalletAccount(account)
        backNavigationCommand.emit(Unit)
    }

    private fun onAddWalletButtonClicked() {
        viewModelScope.launch {
            navigationCommand.emit(ConnectKeystoneArgs.PATH)
        }
    }
}
