package co.electriccoin.zcash.ui.screen.chooseserver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.usecase.AvailableServersProvider
import co.electriccoin.zcash.ui.common.usecase.ObserveFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ObservePersistableWalletUseCase
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointException
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateEndpointUseCase
import co.electriccoin.zcash.ui.design.component.AlertDialogState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class ChooseServerViewModel(
    observePersistableWallet: ObservePersistableWalletUseCase,
    observeFastestServers: ObserveFastestServersUseCase,
    getAvailableServers: AvailableServersProvider,
    private val persistEndpoint: PersistEndpointUseCase,
    private val validateEndpoint: ValidateEndpointUseCase,
) : ViewModel() {
    private val userCustomEndpointText = MutableStateFlow<String?>(null)

    private val userEndpointSelection = MutableStateFlow<Selection?>(null)

    private val isSaveButtonEnabled = MutableStateFlow(true)

    private val dialogState = MutableStateFlow<ServerDialogState?>(null)

    private val fastest =
        combine(
            observePersistableWallet().map { it?.endpoint },
            observeFastestServers(),
            userEndpointSelection,
        ) { selectedEndpoint, result, userEndpointSelection ->
            ServerListState.Fastest(
                title = stringRes("Fastest"),
                servers =
                    result.servers
                        ?.map { endpoint ->
                            createDefaultServerState(endpoint, userEndpointSelection, selectedEndpoint)
                        }
                        .orEmpty(),
                isLoading = false
            )
        }

    private val all =
        combine(
            observePersistableWallet().map { it?.endpoint },
            userCustomEndpointText,
            userEndpointSelection,
        ) { selectedEndpoint, userCustomEndpointText, userEndpointSelection ->
            if (selectedEndpoint == null) return@combine null

            val isSelectedEndpointCustom = !getAvailableServers().contains(selectedEndpoint)

            val customEndpointState =
                createCustomServerState(
                    userEndpointSelection = userEndpointSelection,
                    isSelectedEndpointCustom = isSelectedEndpointCustom,
                    userCustomEndpointText = userCustomEndpointText,
                    selectedEndpoint = selectedEndpoint
                )

            ServerListState.All(
                title = stringRes("All"),
                servers =
                    getAvailableServers()
                        .map<LightWalletEndpoint, ServerState> { endpoint ->
                            createDefaultServerState(endpoint, userEndpointSelection, selectedEndpoint)
                        }
                        .toMutableList()
                        .apply {
                            val index = 1.coerceIn(0, size.coerceAtLeast(0))
                            add(index, customEndpointState)
                        }
                        .toList()
            )
        }

    val state: StateFlow<ChooseServerState?> =
        combine(fastest, all, isSaveButtonEnabled, dialogState) { fastest, all,
                                                                  isButtonEnabled, dialogState ->
            if (all == null) { // not loaded yet
                return@combine null
            }

            ChooseServerState(
                fastest = fastest,
                all = all,
                saveButton =
                    ButtonState(
                        text = stringRes(R.string.choose_server_save),
                        isEnabled = isButtonEnabled,
                        showProgressBar = !isButtonEnabled,
                        onClick = ::onSaveButtonClicked
                    ),
                dialogState = dialogState,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    fun canGoBack(): Boolean = isSaveButtonEnabled.value

    private fun createCustomServerState(
        userEndpointSelection: Selection?,
        isSelectedEndpointCustom: Boolean,
        userCustomEndpointText: String?,
        selectedEndpoint: LightWalletEndpoint
    ) = ServerState.Custom(
        name = stringRes(R.string.choose_server_custom),
        isChecked =
            userEndpointSelection is Selection.Custom ||
                (userEndpointSelection == null && isSelectedEndpointCustom),
        newServerTextFieldState =
            TextFieldState(
                value =
                    userCustomEndpointText?.let { stringRes(it) } ?: if (isSelectedEndpointCustom) {
                        stringRes(
                            resource = R.string.choose_server_full_server_name,
                            selectedEndpoint.host,
                            selectedEndpoint.port
                        )
                    } else {
                        stringRes("")
                    },
                onValueChange = ::onCustomEndpointTextChanged,
            ),
        onClick = ::onCustomEndpointClicked,
    )

    private fun createDefaultServerState(
        endpoint: LightWalletEndpoint,
        userEndpointSelection: Selection?,
        selectedEndpoint: LightWalletEndpoint?
    ): ServerState.Default {
        val isEndpointChecked =
            (userEndpointSelection is Selection.Endpoint && userEndpointSelection.endpoint == endpoint) ||
                (userEndpointSelection == null && selectedEndpoint == endpoint)

        return ServerState.Default(
            name = stringRes(R.string.choose_server_full_server_name, endpoint.host, endpoint.port),
            isChecked = isEndpointChecked,
            onClick = { onEndpointClicked(endpoint) }
        )
    }

    private fun onCustomEndpointTextChanged(new: String) {
        this.userCustomEndpointText.update { new }
    }

    private fun onEndpointClicked(endpoint: LightWalletEndpoint) {
        userEndpointSelection.update { Selection.Endpoint(endpoint) }
    }

    private fun onCustomEndpointClicked() {
        userEndpointSelection.update { Selection.Custom }
    }

    private fun onSaveButtonClicked() =
        viewModelScope.launch {
            isSaveButtonEnabled.update { false }
            val selection = getUserEndpointSelectionOrShowError() ?: return@launch
            try {
                persistEndpoint(selection)
                showSuccessDialog()
            } catch (e: PersistEndpointException) {
                showValidationErrorDialog(e.message)
            }
            isSaveButtonEnabled.update { true }
        }

    private fun onConfirmDialogButtonClicked() {
        dialogState.update { null }
    }

    /**
     * @return an endpoint selected by user or null if user didn't select any new endpoint explicitly or if selected
     * custom endpoint is invalid
     */
    private fun getUserEndpointSelectionOrShowError(): LightWalletEndpoint? {
        return when (val selection = userEndpointSelection.value) {
            Selection.Custom -> {
                val endpoint = userCustomEndpointText.value
                val validated = validateEndpoint(endpoint.orEmpty())
                if (validated == null) {
                    showValidationErrorDialog(null)
                }
                validated
            }

            is Selection.Endpoint -> selection.endpoint
            null -> null
        }
    }

    private fun showValidationErrorDialog(reason: String?) {
        dialogState.update {
            ServerDialogState.Validation(
                AlertDialogState(
                    title = stringRes(R.string.choose_server_validation_dialog_error_title),
                    text = stringRes(R.string.choose_server_validation_dialog_error_text),
                    confirmButtonState =
                        ButtonState(
                            text = stringRes(R.string.choose_server_save_success_dialog_btn),
                            onClick = ::onConfirmDialogButtonClicked
                        ),
                ),
                reason = reason?.let { stringRes(it) }
            )
        }
    }

    private fun showSuccessDialog() {
        dialogState.update {
            ServerDialogState.SaveSuccess(
                AlertDialogState(
                    title = stringRes(R.string.choose_server_save_success_dialog_title),
                    text = stringRes(R.string.choose_server_save_success_dialog_text),
                    confirmButtonState =
                        ButtonState(
                            text = stringRes(R.string.choose_server_save_success_dialog_btn),
                            onClick = ::onConfirmDialogButtonClicked
                        ),
                )
            )
        }
    }
}

private sealed interface Selection {
    data object Custom : Selection

    data class Endpoint(val endpoint: LightWalletEndpoint) : Selection
}
