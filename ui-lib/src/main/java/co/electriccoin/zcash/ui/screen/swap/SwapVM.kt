package co.electriccoin.zcash.ui.screen.swap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapMode.PAY
import co.electriccoin.zcash.ui.common.repository.SwapMode.SWAP
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.ContactWithSwapAsset
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapModeUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTotalSpendableBalanceUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZecSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.IsABContactHintVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToScanSwapAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSelectSwapRecipientUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapInfoUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapQuoteIfAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.RequestSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateSwapModeUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldInnerState
import co.electriccoin.zcash.ui.design.util.combine
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.swap.scan.ScanSwapAddressArgs
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

internal class SwapVM(
    getSwapMode: GetSwapModeUseCase,
    getSlippage: GetSlippageUseCase,
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    getTotalSpendableBalance: GetTotalSpendableBalanceUseCase,
    getZecSwapAsset: GetZecSwapAssetUseCase,
    private val updateSwapMode: UpdateSwapModeUseCase,
    private val navigateToSwapInfo: NavigateToSwapInfoUseCase,
    private val isABContactHintVisible: IsABContactHintVisibleUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val navigationRouter: NavigationRouter,
    private val requestSwapQuote: RequestSwapQuoteUseCase,
    private val navigateToSwapQuoteIfAvailable: NavigateToSwapQuoteIfAvailableUseCase,
    private val exactOutputVMMapper: ExactOutputVMMapper,
    private val exactInputVMMapper: ExactInputVMMapper,
    private val navigateToScanAddress: NavigateToScanSwapAddressUseCase,
    private val navigateToSelectSwapRecipient: NavigateToSelectSwapRecipientUseCase
) : ViewModel() {
    private val defaultCurrencyType: CurrencyType = CurrencyType.TOKEN

    private val currencyType: MutableStateFlow<CurrencyType> = MutableStateFlow(defaultCurrencyType)

    private val addressText: MutableStateFlow<String> = MutableStateFlow("")

    private val amountText: MutableStateFlow<NumberTextFieldInnerState> = MutableStateFlow(NumberTextFieldInnerState())

    private val isRequestingQuote = MutableStateFlow(false)

    private val isCancelStateVisible = MutableStateFlow(false)

    private var selectedContact: ContactWithSwapAsset? = null

    val cancelState = isCancelStateVisible
        .map { isVisible ->
            if (isVisible) {
                SwapCancelState(
                    icon = imageRes(R.drawable.ic_swap_quote_cancel),
                    title = stringRes("Are you sure?"),
                    subtitle = stringRes("If you leave this screen, all the information you entered will be lost. "),
                    negativeButton = ButtonState(
                        text = stringRes("Cancel swap"),
                        onClick = ::onCancelSwapClick
                    ),
                    positiveButton = ButtonState(
                        text = stringRes("Don’t cancel"),
                        onClick = ::onDismissCancelClick
                    ),
                    onBack = ::onBack
                )
            } else {
                null
            }
        }
        .stateIn(
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
            addressText.flatMapLatest { isABContactHintVisible.observe(it) },
            currencyType,
            getZecSwapAsset.observe(),
            getSwapMode.observe(),
            isRequestingQuote
        ) { spendable,
            address,
            amount,
            asset,
            slippage,
            isAddressBookHintVisible,
            currencyType,
            zecSwapAsset,
            mode,
            isRequestingQuote
            ->
            InternalStateImpl(
                swapAsset = asset,
                currencyType = currencyType,
                totalSpendableBalance = spendable,
                amountTextState = amount,
                addressText = address,
                slippage = slippage,
                isAddressBookHintVisible = isAddressBookHintVisible,
                zecSwapAsset = zecSwapAsset,
                swapMode = mode,
                isRequestingQuote = isRequestingQuote
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        getSwapMode
            .observe()
            .flatMapLatest { mode ->
                innerState.map { innerState ->
                    createState(mode, innerState)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    private fun createState(mode: SwapMode, innerState: InternalStateImpl): SwapState {
        val mapper =
            when (mode) {
                SWAP -> exactInputVMMapper
                PAY -> exactOutputVMMapper
            }

        return mapper.createState(
            internalState = innerState,
            onBack = ::onBack,
            onSwapInfoClick = ::onSwapInfoClick,
            onSwapAssetPickerClick = ::onSwapAssetPickerClick,
            onSwapCurrencyTypeClick = ::onSwapCurrencyTypeClick,
            onSlippageClick = ::onSlippageClick,
            onRequestSwapQuoteClick = ::onRequestSwapQuoteClick,
            onAddressChange = ::onAddressChange,
            onSwapModeChange = ::onSwapModeChange,
            onTextFieldChange = ::onTextFieldChange,
            onQrCodeScannerClick = ::onQrCodeScannerClick,
            onAddressBookClick = ::onAddressBookClick
        )
    }

    private fun onAddressBookClick() = viewModelScope.launch {
        val selected = navigateToSelectSwapRecipient()

        if (selected != null) {
            selectedContact = selected
            addressText.update { selected.contact.address }
        }
    }

    private fun onQrCodeScannerClick() = viewModelScope.launch {
        val result = navigateToScanAddress(ScanSwapAddressArgs.Mode.SWAP_SCAN_DESTINATION_ADDRESS)
        if (result != null) {
            selectedContact = null
            addressText.update { result.address }
            if (result.amount != null) {
                amountText.update { NumberTextFieldInnerState.fromAmount(result.amount) }
            }
        }
    }

    private fun onSlippageClick(fiatAmount: BigDecimal?) =
        navigationRouter.forward(SwapSlippageArgs(fiatAmount = fiatAmount?.toPlainString()))

    private fun onBack() = viewModelScope.launch {
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

    private fun onCancelSwapClick() = viewModelScope.launch {
        if (isCancelStateVisible.value) {
            hideCancelBottomSheet()
        }
        cancelSwap()
    }

    private fun onDismissCancelClick() = viewModelScope.launch {
        isCancelStateVisible.update { false }
        navigateToSwapQuoteIfAvailable { hideCancelBottomSheet() }
    }

    private suspend fun hideCancelBottomSheet() {
        isCancelStateVisible.update { false }
        delay(350)
    }

    private fun onSwapCurrencyTypeClick() = currencyType.update {
        when (it) {
            CurrencyType.TOKEN -> CurrencyType.FIAT
            CurrencyType.FIAT -> defaultCurrencyType
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
        selectedContact = null
        addressText.update { new }
    }

    private fun onSwapAssetPickerClick() = navigationRouter.forward(
        SwapAssetPickerArgs(
            chainTicker = selectedContact?.asset?.chainTicker
        )
    )
}

internal enum class CurrencyType { TOKEN, FIAT }

internal interface SwapVMMapper {
    fun createState(
        internalState: InternalState,
        onBack: () -> Unit,
        onSwapInfoClick: () -> Unit,
        onSwapAssetPickerClick: () -> Unit,
        onSwapCurrencyTypeClick: () -> Unit,
        onSlippageClick: (BigDecimal?) -> Unit,
        onRequestSwapQuoteClick: (BigDecimal, String) -> Unit,
        onAddressChange: (String) -> Unit,
        onSwapModeChange: (SwapMode) -> Unit,
        onTextFieldChange: (NumberTextFieldInnerState) -> Unit,
        onQrCodeScannerClick: () -> Unit,
        onAddressBookClick: () -> Unit
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
    val zecSwapAsset: SwapAsset?
    val swapMode: SwapMode
    val isRequestingQuote: Boolean
}

internal data class InternalStateImpl(
    override val swapAsset: SwapAsset?,
    override val currencyType: CurrencyType,
    override val totalSpendableBalance: Zatoshi?,
    override val amountTextState: NumberTextFieldInnerState,
    override val addressText: String,
    override val slippage: BigDecimal,
    override val isAddressBookHintVisible: Boolean,
    override val zecSwapAsset: SwapAsset?,
    override val swapMode: SwapMode,
    override val isRequestingQuote: Boolean,
) : InternalState

