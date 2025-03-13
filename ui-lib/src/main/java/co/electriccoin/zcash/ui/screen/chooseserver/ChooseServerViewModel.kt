package co.electriccoin.zcash.ui.screen.chooseserver

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.lightwallet.client.model.LightWalletEndpoint
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetDefaultServersProvider
import co.electriccoin.zcash.ui.common.usecase.ObserveFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ObserveSelectedEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointException
import co.electriccoin.zcash.ui.common.usecase.PersistEndpointUseCase
import co.electriccoin.zcash.ui.common.usecase.RefreshFastestServersUseCase
import co.electriccoin.zcash.ui.common.usecase.ValidateEndpointUseCase
import co.electriccoin.zcash.ui.design.component.AlertDialogState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.RadioButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.util.getString
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions", "LongParameterList")
class ChooseServerViewModel(
    application: Application,
    observeFastestServers: ObserveFastestServersUseCase,
    observeSelectedEndpoint: ObserveSelectedEndpointUseCase,
    private val getAvailableServers: GetDefaultServersProvider,
    private val refreshFastestServersUseCase: RefreshFastestServersUseCase,
    private val persistEndpoint: PersistEndpointUseCase,
    private val validateEndpoint: ValidateEndpointUseCase,
) : AndroidViewModel(application) {
    private val userCustomEndpointText = MutableStateFlow<String?>(null)

    private val userEndpointSelection = MutableStateFlow<Selection?>(null)

    private val isSaveInProgress = MutableStateFlow(false)

    private val dialogState = MutableStateFlow<ServerDialogState?>(null)

    private val isCustomEndpointExpanded = MutableStateFlow(false)

    private val fastest =
        combine(
            observeSelectedEndpoint(),
            observeFastestServers(),
            userEndpointSelection,
        ) { selectedEndpoint, fastestServers, userEndpointSelection ->
            ServerListState.Fastest(
                title = stringRes(R.string.choose_server_fastest_servers),
                servers =
                    fastestServers.servers
                        ?.map { endpoint ->
                            createDefaultServerState(endpoint, userEndpointSelection, selectedEndpoint)
                        }.orEmpty(),
                isLoading = fastestServers.isLoading,
                retryButton =
                    ButtonState(
                        text = stringRes(R.string.choose_server_refresh),
                        onClick = ::onRefreshClicked
                    )
            )
        }

    private val other =
        combine(
            observeSelectedEndpoint(),
            observeFastestServers(),
            userCustomEndpointText,
            userEndpointSelection,
            isCustomEndpointExpanded
        ) { selectedEndpoint, fastest, userCustomEndpointText, userEndpointSelection, isCustomEndpointExpanded ->
            if (selectedEndpoint == null) return@combine null

            val isSelectedEndpointCustom = !getAvailableServers().contains(selectedEndpoint)

            val customEndpointState =
                createCustomServerState(
                    userEndpointSelection = userEndpointSelection,
                    isSelectedEndpointCustom = isSelectedEndpointCustom,
                    userCustomEndpointText = userCustomEndpointText,
                    selectedEndpoint = selectedEndpoint,
                    isCustomEndpointExpanded = isCustomEndpointExpanded
                )

            ServerListState.Other(
                title = stringRes(R.string.choose_server_other_servers),
                servers =
                    getAvailableServers()
                        .filter {
                            !fastest.servers.orEmpty().contains(it)
                        }.map<LightWalletEndpoint, ServerState> { endpoint ->
                            createDefaultServerState(endpoint, userEndpointSelection, selectedEndpoint)
                        }.toMutableList()
                        .apply {
                            val index = 1.coerceIn(0, size.coerceAtLeast(0))
                            add(index, customEndpointState)
                        }.toList()
            )
        }

    private val buttonState =
        combine(
            observeSelectedEndpoint(),
            userEndpointSelection,
            isSaveInProgress,
            userCustomEndpointText,
        ) { selectedEndpoint, userEndpointSelection, isSaveInProgress, userCustomEndpointText ->
            val userSelectedEndpoint =
                when (userEndpointSelection) {
                    Selection.Custom -> {
                        val isSelectedEndpointCustom = !getAvailableServers().contains(selectedEndpoint)
                        if (isSelectedEndpointCustom) selectedEndpoint else null
                    }
                    is Selection.Endpoint -> userEndpointSelection.endpoint
                    null -> null
                }

            val isCustomEndpointSelectedAndUpdated =
                when (userEndpointSelection) {
                    Selection.Custom -> {
                        val isSelectedEndpointCustom = !getAvailableServers().contains(selectedEndpoint)
                        when {
                            isSelectedEndpointCustom && userCustomEndpointText == null -> false
                            isSelectedEndpointCustom &&
                                selectedEndpoint?.generateUserString() !=
                                userCustomEndpointText -> true
                            else -> false
                        }
                    }
                    is Selection.Endpoint -> false
                    null -> false
                }

            ButtonState(
                text = stringRes(R.string.choose_server_save),
                isEnabled =
                    (userEndpointSelection != null && selectedEndpoint != userSelectedEndpoint) ||
                        isCustomEndpointSelectedAndUpdated,
                isLoading = isSaveInProgress,
                onClick = ::onSaveButtonClicked
            )
        }

    val state =
        combine(fastest, other, buttonState, dialogState) {
            fastest,
            all,
            buttonState,
            dialogState
            ->
            if (all == null) { // not loaded yet
                return@combine null
            }

            ChooseServerState(
                fastest = fastest,
                other = all,
                saveButton = buttonState,
                dialogState = dialogState,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT), null)

    fun canGoBack(): Boolean = isSaveInProgress.value.not()

    private fun createCustomServerState(
        userEndpointSelection: Selection?,
        isSelectedEndpointCustom: Boolean,
        userCustomEndpointText: String?,
        selectedEndpoint: LightWalletEndpoint,
        isCustomEndpointExpanded: Boolean,
    ) = ServerState.Custom(
        radioButtonState =
            RadioButtonState(
                text =
                    if (isSelectedEndpointCustom) {
                        stringRes(R.string.choose_server_full_server_name, selectedEndpoint.host, selectedEndpoint.port)
                    } else {
                        stringRes(R.string.choose_server_custom)
                    },
                isChecked =
                    userEndpointSelection is Selection.Custom ||
                        (userEndpointSelection == null && isSelectedEndpointCustom),
                onClick = ::onCustomEndpointClicked,
            ),
        newServerTextFieldState =
            TextFieldState(
                value =
                    userCustomEndpointText?.let { stringRes(it) } ?: if (isSelectedEndpointCustom) {
                        stringRes(
                            resource = R.string.choose_server_full_server_name_text_field,
                            selectedEndpoint.host,
                            selectedEndpoint.port
                        )
                    } else {
                        stringRes("")
                    },
                onValueChange = ::onCustomEndpointTextChanged,
            ),
        badge = if (isSelectedEndpointCustom) stringRes(R.string.choose_server_active) else null,
        isExpanded = isCustomEndpointExpanded
    )

    private fun createDefaultServerState(
        endpoint: LightWalletEndpoint,
        userEndpointSelection: Selection?,
        selectedEndpoint: LightWalletEndpoint?,
    ): ServerState.Default {
        val defaultEndpoint = getAvailableServers.defaultEndpoint()
        val isEndpointChecked =
            (userEndpointSelection is Selection.Endpoint && userEndpointSelection.endpoint == endpoint) ||
                (userEndpointSelection == null && selectedEndpoint == endpoint)

        return ServerState.Default(
            RadioButtonState(
                text = stringRes(R.string.choose_server_full_server_name, endpoint.host, endpoint.port),
                isChecked = isEndpointChecked,
                onClick = { onEndpointClicked(endpoint) },
                subtitle = if (endpoint == defaultEndpoint) stringRes(R.string.choose_server_save_default) else null,
            ),
            badge = if (endpoint == selectedEndpoint) stringRes(R.string.choose_server_active) else null,
        )
    }

    private fun onRefreshClicked() {
        refreshFastestServersUseCase()
    }

    private fun onCustomEndpointTextChanged(new: String) {
        this.userCustomEndpointText.update { new }
    }

    private fun onEndpointClicked(endpoint: LightWalletEndpoint) {
        isCustomEndpointExpanded.update { false }
        userEndpointSelection.update { Selection.Endpoint(endpoint) }
    }

    private fun onCustomEndpointClicked() {
        isCustomEndpointExpanded.update { true }
        userEndpointSelection.update { Selection.Custom }
    }

    private fun onSaveButtonClicked() =
        viewModelScope.launch {
            try {
                if (isSaveInProgress.value) return@launch
                isSaveInProgress.update { true }
                val selection = getUserEndpointSelectionOrShowError() ?: return@launch
                try {
                    persistEndpoint(selection)
                    isCustomEndpointExpanded.update { false }
                    userEndpointSelection.update { null }
                } catch (e: PersistEndpointException) {
                    showValidationErrorDialog(e.message)
                }
            } finally {
                isSaveInProgress.update { false }
            }
        }

    private fun onConfirmDialogButtonClicked() {
        dialogState.update { null }
    }

    /**
     * @return an endpoint selected by user or null if user didn't select any new endpoint explicitly or if selected
     * custom endpoint is invalid
     */
    private fun getUserEndpointSelectionOrShowError(): LightWalletEndpoint? =
        when (val selection = userEndpointSelection.value) {
            is Selection.Custom -> {
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

    private fun LightWalletEndpoint.generateUserString(): String =
        stringRes(resource = R.string.choose_server_full_server_name_text_field, host, port)
            .getString(getApplication())
}

private sealed interface Selection {
    data object Custom : Selection

    data class Endpoint(
        val endpoint: LightWalletEndpoint
    ) : Selection
}
