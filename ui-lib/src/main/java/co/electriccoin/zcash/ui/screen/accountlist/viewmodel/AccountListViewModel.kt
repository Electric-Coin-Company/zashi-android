package co.electriccoin.zcash.ui.screen.accountlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
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
import co.electriccoin.zcash.ui.screen.connectkeystone.ConnectKeystone
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountListViewModel(
    observeWalletAccounts: ObserveWalletAccountsUseCase,
    private val selectWalletAccount: SelectWalletAccountUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {

    val hideBottomSheetRequest = MutableSharedFlow<Unit>()

    private val bottomSheetHiddenResponse = MutableSharedFlow<Unit>()

    val state =
        observeWalletAccounts().map { accounts ->
            val items = listOfNotNull(
                *accounts.orEmpty()
                    .map<WalletAccount, AccountListItem> { account ->
                        AccountListItem.Account(
                            ZashiAccountListItemState(
                                title = account.name,
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
                        title = stringRes(co.electriccoin.zcash.ui.R.string.account_list_keystone_promo_title),
                        subtitle = stringRes(co.electriccoin.zcash.ui.R.string.account_list_keystone_promo_subtitle),
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
                onBottomSheetHidden = ::onBottomSheetHidden,
                onBack = ::onBack
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private suspend fun hideBottomSheet() {
        hideBottomSheetRequest.emit(Unit)
        bottomSheetHiddenResponse.first()
    }

    private fun onAccountClicked(account: WalletAccount) = viewModelScope.launch {
        selectWalletAccount(account)
        hideBottomSheet()
        navigationRouter.back()
    }

    private fun onAddWalletButtonClicked() = viewModelScope.launch {
        hideBottomSheet()
        navigationRouter.forward(ConnectKeystone)
    }

    private fun onBottomSheetHidden() = viewModelScope.launch {
        bottomSheetHiddenResponse.emit(Unit)
    }

    private fun onBack() = viewModelScope.launch {
        hideBottomSheet()
        navigationRouter.back()
    }
}