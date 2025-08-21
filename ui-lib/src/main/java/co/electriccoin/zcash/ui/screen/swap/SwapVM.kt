package co.electriccoin.zcash.ui.screen.swap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_INPUT
import co.electriccoin.zcash.ui.common.model.SwapMode.EXACT_OUTPUT
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapAssetsUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapModeUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTotalSpendableBalanceUseCase
import co.electriccoin.zcash.ui.common.usecase.IsABContactHintVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToScanGenericAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSelectABSwapRecipientUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapInfoUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapQuoteIfAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.RequestSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateSwapModeUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.InnerTextFieldState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldInnerState
import co.electriccoin.zcash.ui.design.component.TextSelection
import co.electriccoin.zcash.ui.design.util.combine
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicNumber
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPickerArgs
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Suppress("TooManyFunctions")
internal class SwapVM(
    getSwapMode: GetSwapModeUseCase,
    getSlippage: GetSlippageUseCase,
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    getTotalSpendableBalance: GetTotalSpendableBalanceUseCase,
    getSwapAssetsUseCase: GetSwapAssetsUseCase,
    private val swapRepository: SwapRepository,
    private val updateSwapMode: UpdateSwapModeUseCase,
    private val navigateToSwapInfo: NavigateToSwapInfoUseCase,
    private val isABContactHintVisible: IsABContactHintVisibleUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val navigationRouter: NavigationRouter,
    private val requestSwapQuote: RequestSwapQuoteUseCase,
    private val navigateToSwapQuoteIfAvailable: NavigateToSwapQuoteIfAvailableUseCase,
    private val exactOutputVMMapper: ExactOutputVMMapper,
    private val exactInputVMMapper: ExactInputVMMapper,
    private val navigateToScanAddress: NavigateToScanGenericAddressUseCase,
    private val navigateToSelectSwapRecipient: NavigateToSelectABSwapRecipientUseCase,
) : ViewModel() {
    private val currencyType: MutableStateFlow<CurrencyType> = MutableStateFlow(CurrencyType.TOKEN)

    private val addressText: MutableStateFlow<String> = MutableStateFlow("")

    private val amountText: MutableStateFlow<NumberTextFieldInnerState> = MutableStateFlow(NumberTextFieldInnerState())

    private val isRequestingQuote = MutableStateFlow(false)

    private val isCancelStateVisible = MutableStateFlow(false)

    private var selectedContact: MutableStateFlow<EnhancedABContact?> = MutableStateFlow(null)

    val cancelState =
        isCancelStateVisible
            .map { isVisible ->
                if (isVisible) {
                    SwapCancelState(
                        icon = imageRes(R.drawable.ic_swap_quote_cancel),
                        title = stringRes(R.string.swap_cancel_title),
                        subtitle = stringRes(R.string.swap_cancel_subtitle),
                        negativeButton =
                            ButtonState(
                                text = stringRes(R.string.swap_cancel_negative),
                                onClick = ::onCancelSwapClick
                            ),
                        positiveButton =
                            ButtonState(
                                text = stringRes(R.string.swap_cancel_positive),
                                onClick = ::onDismissCancelClick
                            ),
                        onBack = ::onBack
                    )
                } else {
                    null
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val innerState =
        combine(
            getTotalSpendableBalance.observe(),
            addressText,
            amountText,
            getSelectedSwapAsset.observe(),
            getSlippage.observe(),
            addressText.flatMapLatest { isABContactHintVisible.observe() },
            currencyType,
            getSwapAssetsUseCase.observe(),
            getSwapMode.observe(),
            isRequestingQuote,
            selectedContact
        ) {
            spendable,
            address,
            amount,
            asset,
            slippage,
            isAddressBookHintVisible,
            currencyType,
            swapAssets,
            mode,
            isRequestingQuote,
            selectedContact
            ->
            InternalStateImpl(
                swapAsset = asset,
                currencyType = currencyType,
                totalSpendableBalance = spendable,
                amountTextState = amount,
                addressText = address,
                slippage = slippage,
                isAddressBookHintVisible = isAddressBookHintVisible,
                swapAssets = swapAssets,
                swapMode = mode,
                isRequestingQuote = isRequestingQuote,
                selectedContact = selectedContact
            )
        }

    val state =
        innerState
            .map { innerState ->
                createState(innerState)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    private fun createState(innerState: InternalStateImpl): SwapState {
        val mapper =
            when (innerState.swapMode) {
                EXACT_INPUT -> exactInputVMMapper
                EXACT_OUTPUT -> exactOutputVMMapper
            }

        return mapper.createState(
            internalState = innerState,
            onBack = ::onBack,
            onSwapInfoClick = ::onSwapInfoClick,
            onSwapAssetPickerClick = ::onSwapAssetPickerClick,
            onSwapCurrencyTypeClick = ::onSwapCurrencyTypeClick,
            onSlippageClick = ::onSlippageClick,
            onRequestSwapQuoteClick = ::onRequestSwapQuoteClick,
            onTryAgainClick = ::onTryAgainClick,
            onAddressChange = ::onAddressChange,
            onSwapModeChange = ::onSwapModeChange,
            onTextFieldChange = ::onTextFieldChange,
            onQrCodeScannerClick = ::onQrCodeScannerClick,
            onAddressBookClick = ::onAddressBookClick,
            onDeleteSelectedContactClick = ::onDeleteSelectedContactClick
        )
    }

    private fun onDeleteSelectedContactClick() {
        selectedContact.update { null }
    }

    private fun onTryAgainClick() = swapRepository.requestRefreshAssets()

    private fun onAddressBookClick() =
        viewModelScope.launch {
            val selected = navigateToSelectSwapRecipient()

            if (selected != null) {
                selectedContact.update { selected }
                addressText.update { "" }
            }
        }

    private fun onQrCodeScannerClick() =
        viewModelScope.launch {
            val result = navigateToScanAddress()
            if (result != null) {
                navigationRouter.back()
                selectedContact.update { null }
                addressText.update { result.address }
                if (result.amount != null) {
                    amountText.update { NumberTextFieldInnerState.fromAmount(result.amount) }
                }
            }
        }

    private fun onSlippageClick(fiatAmount: BigDecimal?) =
        navigationRouter.forward(SwapSlippageArgs(fiatAmount = fiatAmount?.toPlainString()))

    private fun onBack() =
        viewModelScope.launch {
            if (isRequestingQuote.value) {
                isCancelStateVisible.update { true }
            } else if (isCancelStateVisible.value) {
                isCancelStateVisible.update { false }
                navigateToSwapQuoteIfAvailable { hideCancelBottomSheet() }
            } else {
                if (isCancelStateVisible.value) {
                    hideCancelBottomSheet()
                }
                cancelSwap()
            }
        }

    private fun onCancelSwapClick() =
        viewModelScope.launch {
            if (isCancelStateVisible.value) {
                hideCancelBottomSheet()
            }
            cancelSwap()
        }

    private fun onDismissCancelClick() =
        viewModelScope.launch {
            isCancelStateVisible.update { false }
            navigateToSwapQuoteIfAvailable { hideCancelBottomSheet() }
        }

    @Suppress("MagicNumber")
    private suspend fun hideCancelBottomSheet() {
        isCancelStateVisible.update { false }
        delay(350)
    }

    private fun onSwapCurrencyTypeClick(newTextFieldAmount: BigDecimal?) {
        amountText.update {
            NumberTextFieldInnerState(
                innerTextFieldState =
                    InnerTextFieldState(
                        value = newTextFieldAmount?.let { stringResByDynamicNumber(it) } ?: stringRes(""),
                        selection = TextSelection.End
                    ),
                amount = newTextFieldAmount,
                lastValidAmount = newTextFieldAmount
            )
        }
        currencyType.update {
            when (it) {
                CurrencyType.TOKEN -> CurrencyType.FIAT
                CurrencyType.FIAT -> CurrencyType.TOKEN
            }
        }
    }

    private fun onTextFieldChange(new: NumberTextFieldInnerState) = amountText.update { new }

    private fun onRequestSwapQuoteClick(amount: BigDecimal, address: String) =
        viewModelScope.launch {
            isRequestingQuote.update { true }
            requestSwapQuote(
                amount = amount,
                address = address,
                canNavigateToSwapQuote = { !isCancelStateVisible.value }
            )
            isRequestingQuote.update { false }
        }

    private fun onSwapModeChange(swapMode: SwapMode) = updateSwapMode(swapMode)

    private fun onSwapInfoClick() = navigateToSwapInfo()

    private fun onAddressChange(new: String) {
        selectedContact.update { null }
        addressText.update { new }
    }

    private fun onSwapAssetPickerClick() =
        navigationRouter.forward(
            SwapAssetPickerArgs(chainTicker = selectedContact.value?.blockchain?.chainTicker)
        )
}

internal enum class CurrencyType { TOKEN, FIAT }

internal interface SwapVMMapper {
    fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: (BigDecimal?) -> Unit,
        onSlippageClick: (BigDecimal?) -> Unit,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onTryAgainClick: () -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
        onQrCodeScannerClick: () -> Unit,
        onAddressBookClick: () -> Unit,
        onDeleteSelectedContactClick: () -> Unit
    ): SwapState
}

internal interface InternalState {
    val swapAsset: SwapAsset?
    val currencyType: CurrencyType
    val totalSpendableBalance: Zatoshi?
    val amountTextState: NumberTextFieldInnerState
    val addressText: String
    val slippage: BigDecimal
    val isAddressBookHintVisible: Boolean
    val swapAssets: SwapAssetsData
    val swapMode: SwapMode
    val isRequestingQuote: Boolean
    val selectedContact: EnhancedABContact?
}

internal data class InternalStateImpl(
    override val swapAsset: SwapAsset?,
    override val currencyType: CurrencyType,
    override val totalSpendableBalance: Zatoshi?,
    override val amountTextState: NumberTextFieldInnerState,
    override val addressText: String,
    override val slippage: BigDecimal,
    override val isAddressBookHintVisible: Boolean,
    override val swapAssets: SwapAssetsData,
    override val swapMode: SwapMode,
    override val isRequestingQuote: Boolean,
    override val selectedContact: EnhancedABContact?
) : InternalState
