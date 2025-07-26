package co.electriccoin.zcash.ui.screen.swap.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.provider.LatestSwapAssetsProvider
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.usecase.FilterSwapAssetsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapAssetsUseCase
import co.electriccoin.zcash.ui.common.usecase.SelectSwapAssetUseCase
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

class SwapAssetPickerVM(
    args: SwapAssetPickerArgs,
    getSwapAssets: GetSwapAssetsUseCase,
    latestSwapAssetsProvider: LatestSwapAssetsProvider,
    private val selectSwapAsset: SelectSwapAssetUseCase,
    private val navigationRouter: NavigationRouter,
    private val filterSwapAssets: FilterSwapAssetsUseCase,
    private val swapRepository: SwapRepository,
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

    private val filteredSwapAssets =
        combine(
            getSwapAssets.observe(),
            latestSwapAssetsProvider.observe(),
            searchText
        ) { assets, latestAssets, text ->
            filterSwapAssets(
                assets = assets,
                latestAssets = latestAssets,
                text = text,
                onlyChainTicker = args.chainTicker
            )
        }.flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue =
                    filterSwapAssets(
                        assets = getSwapAssets.observe().value,
                        latestAssets = null,
                        text = searchText.value,
                        onlyChainTicker = args.chainTicker,
                    )
            )

    val state: StateFlow<SwapAssetPickerState> =
        combine(filteredSwapAssets, searchTextFieldState) { assets, search ->
            createState(assets, search)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = createState(filteredSwapAssets.value, searchTextFieldState.value)
        )

    private fun createState(assets: SwapAssetsData, search: TextFieldState): SwapAssetPickerState =
        SwapAssetPickerState(
            data =
                when {
                    assets.data != null ->
                        SwapAssetPickerDataState.Success(
                            assets.data.map {
                                ListItemState(
                                    bigIcon = it.tokenIcon,
                                    smallIcon = it.chainIcon,
                                    title = stringRes(it.tokenTicker),
                                    subtitle = it.chainName,
                                    onClick = { onSwapAssetClick(it) },
                                    contentType = "token",
                                    key = "${it.chainTicker}_${it.tokenTicker}_${it.assetId}"
                                )
                            }
                        )

                    assets.isLoading -> SwapAssetPickerDataState.Loading
                    else ->
                        SwapAssetPickerDataState.Error(
                            stringRes("Something went wrong"),
                            stringRes("We couldn’t load the assets. Please check your connection and try again."),
                            ButtonState(
                                text = stringRes("Try again"),
                                onClick = ::onRetry
                            )
                        )
                },
            onBack = ::onBack,
            search = search,
            title = stringRes("Select Token")
        )

    private fun createTextFieldState(it: String): TextFieldState =
        TextFieldState(
            value = stringRes(it),
            onValueChange = ::onSearchTextChange,
        )

    private fun onSearchTextChange(new: String) = searchText.update { new }

    private fun onSwapAssetClick(asset: SwapAsset) = selectSwapAsset.select(asset)

    private fun onBack() = navigationRouter.back()

    private fun onRetry() = swapRepository.requestRefreshAssets()
}
