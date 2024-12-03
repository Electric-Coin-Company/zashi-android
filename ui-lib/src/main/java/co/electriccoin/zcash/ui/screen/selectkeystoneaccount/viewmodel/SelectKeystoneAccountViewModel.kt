package co.electriccoin.zcash.ui.screen.selectkeystoneaccount.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.usecase.CreateKeystoneAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.DecodeUrToZashiAccountsUseCase
import co.electriccoin.zcash.ui.common.usecase.DeriveKeystoneAccountUnifiedAddressUseCase
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.listitem.checkbox.ZashiCheckboxListItemState
import co.electriccoin.zcash.ui.design.component.listitem.checkbox.ZashiExpandedCheckboxListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.SelectKeystoneAccount
import co.electriccoin.zcash.ui.screen.selectkeystoneaccount.model.SelectKeystoneAccountState
import com.keystone.module.ZcashAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectKeystoneAccountViewModel(
    args: SelectKeystoneAccount,
    decodeUrToZashiAccounts: DecodeUrToZashiAccountsUseCase,
    private val createKeystoneAccount: CreateKeystoneAccountUseCase,
    private val deriveKeystoneAccountUnifiedAddress: DeriveKeystoneAccountUnifiedAddressUseCase,
    private val navigationRouter: NavigationRouter,
) : ViewModel() {

    private val accounts = decodeUrToZashiAccounts(args.ur)

    private val selectedAccount = MutableStateFlow<ZcashAccount?>(null)

    val state = selectedAccount.map { selection ->
        createState(selection)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    private suspend fun createState(selection: ZcashAccount?): SelectKeystoneAccountState {
        val alternativeStyle = accounts?.accounts?.size == 1

        return SelectKeystoneAccountState(
            onBackClick = ::onBackClick,
            title = stringRes("Confirm Account to Access"), // TODO keystone string
            subtitle = stringRes("Select the wallet you'd like to connect to proceed. Once connected, you’ll be able " +
                "to wirelessly sign transactions with your hardware wallet."), // TODO keystone string
            items = accounts?.accounts
                ?.mapIndexed { index, account ->
                    if (alternativeStyle) {
                        createAlternativeCheckboxState(account, selection)
                    } else {
                        createCheckboxState(account, index, selection)
                    }
                }
                .orEmpty(),
            positiveButtonState = ButtonState(
                text = stringRes("Unlock"), // TODO keystone string
                onClick = { onUnlockClick(selection) },
                isEnabled = selection != null
            ),
            negativeButtonState = ButtonState(
                text = stringRes("Forget this device"), // TODO keystone string
                onClick = ::onForgetDeviceClick,
            ),
        )
    }

    private suspend fun createCheckboxState(
        account: ZcashAccount,
        index: Int,
        selection: ZcashAccount?
    ) = ZashiCheckboxListItemState(
        title = stringRes(account.name ?: "Keystone Wallet"), // TODO keystone string
        subtitle = stringRes(deriveKeystoneAccountUnifiedAddress(account)),
        icon = imageRes((index + 1).toString()),
        isSelected = selection == account,
        onClick = { selectedAccount.value = account }
    )

    private suspend fun createAlternativeCheckboxState(
        account: ZcashAccount,
        selection: ZcashAccount?
    ) = ZashiExpandedCheckboxListItemState(
        title = stringRes(account.name ?: "Keystone Wallet"), // TODO keystone string
        subtitle = stringRes(deriveKeystoneAccountUnifiedAddress(account)),
        icon = R.drawable.ic_item_keystone,
        isSelected = selection == account,
        onClick = { selectedAccount.value = account },
        info = null
    )

    private fun onBackClick() {
        navigationRouter.backToRoot()
    }

    private fun onUnlockClick(account: ZcashAccount?) = viewModelScope.launch {
        if (account == null) return@launch

        createKeystoneAccount(account)
        navigationRouter.backToRoot()
    }

    private fun onForgetDeviceClick() = navigationRouter.backToRoot()
}