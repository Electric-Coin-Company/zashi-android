package co.electriccoin.zcash.ui.screen.selectkeystoneaccount.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.exception.InitializeException
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.DeriveKeystoneAccountUnifiedAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.ParseKeystoneUrToZashiAccountsUseCase
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.checkbox.ZashiExpandedCheckboxListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.model.SelectKeystoneAccountState
import com.keystone.module.ZcashAccount
import com.keystone.module.ZcashAccounts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectKeystoneAccountViewModel(
    args: SelectKeystoneAccount,
    parseKeystoneUrToZashiAccounts: ParseKeystoneUrToZashiAccountsUseCase,
    private val createKeystoneAccount: CreateKeystoneAccountUseCase,
    private val deriveKeystoneAccountUnifiedAddress: DeriveKeystoneAccountUnifiedAddressUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {
    private val accounts = parseKeystoneUrToZashiAccounts(args.ur)

    private val selectedAccount = MutableStateFlow<ZcashAccount?>(null)

    private val isCreatingAccount = MutableStateFlow(false)

    val state =
        combine(isCreatingAccount, selectedAccount) { isCreatingAccount, selection ->
            createState(selection, isCreatingAccount)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = null
        )

    private suspend fun createState(
        selection: ZcashAccount?,
        isCreatingAccount: Boolean
    ): SelectKeystoneAccountState {
        return SelectKeystoneAccountState(
            onBackClick = ::onBackClick,
            title = stringRes(co.electriccoin.zcash.ui.R.string.select_keystone_account_title),
            subtitle = stringRes(co.electriccoin.zcash.ui.R.string.select_keystone_account_subtitle),
            items =
                accounts.accounts
                    .take(1)
                    .map { account ->
                        createCheckboxState(account, selection)
                    },
            positiveButtonState =
                ButtonState(
                    text = stringRes(co.electriccoin.zcash.ui.R.string.select_keystone_account_positive),
                    onClick = {
                        if (selection != null) {
                            onUnlockClick(accounts, selection)
                        }
                    },
                    isEnabled = selection != null,
                    isLoading = isCreatingAccount
                ),
            negativeButtonState =
                ButtonState(
                    text = stringRes(co.electriccoin.zcash.ui.R.string.select_keystone_account_negative),
                    onClick = ::onForgetDeviceClick,
                ),
        )
    }

    private suspend fun createCheckboxState(
        account: ZcashAccount,
        selection: ZcashAccount?
    ) = ZashiExpandedCheckboxListItemState(
        title =
            account.name?.let { stringRes(it) }
                ?: stringRes(co.electriccoin.zcash.ui.R.string.select_keystone_account_default),
        subtitle = stringRes(deriveKeystoneAccountUnifiedAddress(account)),
        icon = R.drawable.ic_item_keystone,
        isSelected = selection == account,
        onClick = { onSelectAccountClick(account) },
        info = null
    )

    private fun onSelectAccountClick(account: ZcashAccount) {
        selectedAccount.update {
            if (it == account) {
                null
            } else {
                account
            }
        }
    }

    private fun onBackClick() {
        if (!isCreatingAccount.value) {
            navigationRouter.backToRoot()
        }
    }

    private fun onUnlockClick(
        accounts: ZcashAccounts,
        account: ZcashAccount
    ) = viewModelScope.launch {
        if (isCreatingAccount.value) return@launch

        try {
            isCreatingAccount.update { true }
            createKeystoneAccount(accounts, account)
        } catch (e: InitializeException.ImportAccountException) {
            Twig.error(e) { "Error importing account" }
        } finally {
            isCreatingAccount.update { false }
        }
    }

    private fun onForgetDeviceClick() {
        if (!isCreatingAccount.value) {
            navigationRouter.backToRoot()
        }
    }
}
