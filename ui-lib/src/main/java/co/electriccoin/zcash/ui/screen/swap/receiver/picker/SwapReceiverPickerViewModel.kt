package co.electriccoin.zcash.ui.screen.swap.receiver.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SwapTokenChain
import co.electriccoin.zcash.ui.common.repository.SwapTokenChains
import co.electriccoin.zcash.ui.common.usecase.FilterSwapTokenChainsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTokenChainsUseCase
import co.electriccoin.zcash.ui.common.usecase.SelectTokenChainUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.listitem.ListItemState
import co.electriccoin.zcash.ui.design.util.stringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SwapReceiverPickerViewModel(
    getTokenChains: GetTokenChainsUseCase,
    private val selectTokenChain: SelectTokenChainUseCase,
    private val navigationRouter: NavigationRouter,
    private val filterSwapTokenChains: FilterSwapTokenChainsUseCase
) : ViewModel() {
    private val searchText = MutableStateFlow("")

    private val searchTextFieldState =
        searchText
            .map {
                createTextFieldState(it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = createTextFieldState(searchText.value)
            )

    private val filteredTokenChains =
        combine(getTokenChains.observe(), searchText) { tokenChains, text ->
            filterSwapTokenChains(tokenChains, text)
        }.flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue =
                    filterSwapTokenChains(
                        tokenChains = getTokenChains.observe().value,
                        text = searchText.value
                    )
            )

    val state: StateFlow<SwapReceiverPickerState> =
        combine(filteredTokenChains, searchTextFieldState) { tokenChains, search ->
            createState(tokenChains, search)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = createState(filteredTokenChains.value, searchTextFieldState.value)
        )

    private fun createState(tokenChains: SwapTokenChains, search: TextFieldState): SwapReceiverPickerState =
        SwapReceiverPickerState(
            data =
                when {
                    tokenChains.data != null ->
                        SwapPickerDataState.Success(
                            tokenChains.data.map {
                                ListItemState(
                                    icon = it.tokenIcon,
                                    badge = it.chainIcon,
                                    title = stringRes(it.tokenTicker),
                                    subtitle = it.chainName,
                                    onClick = { onTokenChainClick(it) },
                                    contentType = "token",
                                    key = "${it.tokenTicker}_${it.chainTicker}"
                                )
                            }
                        )

                    tokenChains.isLoading -> SwapPickerDataState.Loading
                    else -> SwapPickerDataState.Error(ButtonState(stringRes("")))
                },
            onBack = ::onBack,
            search = search
        )

    private fun createTextFieldState(it: String): TextFieldState =
        TextFieldState(
            value = stringRes(it),
            onValueChange = ::onSearchTextChange,
        )

    private fun onSearchTextChange(new: String) = searchText.update { new }

    private fun onTokenChainClick(it: SwapTokenChain) = selectTokenChain.select(it)

    private fun onBack() = navigationRouter.back()
}
