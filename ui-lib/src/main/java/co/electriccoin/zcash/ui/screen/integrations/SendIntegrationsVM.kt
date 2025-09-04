package co.electriccoin.zcash.ui.screen.integrations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.NavigateToNearPayUseCase
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.send.Send
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SendIntegrationsVM(
    private val navigationRouter: NavigationRouter,
    private val navigateToNearPay: NavigateToNearPayUseCase
) : ViewModel() {

    val state = MutableStateFlow(createState()).asStateFlow()

    private fun createState() = IntegrationsState(
        disabledInfo = null,
        onBack = ::onBack,
        showFooter = false,
        items =
            listOfNotNull(
                ListItemState(
                    bigIcon = imageRes(R.drawable.ic_integrations_send),
                    title = stringRes("Send ZEC"),
                    subtitle = stringRes("Use shielded ZEC to send private Zcash payments"),
                    onClick = ::onSendClick,
                ),
                ListItemState(
                    bigIcon = imageRes(R.drawable.ic_integrations_near),
                    title = stringRes("CrossPay with Near"),
                    subtitle = stringRes("Use shielded ZEC to send cross-chain payments."),
                    onClick = ::onNearPayClick,
                ),

                ).toImmutableList(),
    )

    private fun onNearPayClick() = viewModelScope.launch { navigateToNearPay() }

    private fun onSendClick() = navigationRouter.forward(Send())

    private fun onBack() = navigationRouter.back()

}
