package co.electriccoin.zcash.ui.screen.swap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.model.SwapMode
import co.electriccoin.zcash.ui.common.model.WalletAccount
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import co.electriccoin.zcash.ui.common.repository.SwapAssetsData
import co.electriccoin.zcash.ui.common.repository.SwapRepository
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedWalletAccountUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapAssetsUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToScanGenericAddressUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSelectABSwapRecipientUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapInfoUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapQuoteIfAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.RequestSwapQuoteUseCase
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Suppress("TooManyFunctions")
internal class SwapVM(
    getSlippage: GetSlippageUseCase,
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    getSwapAssetsUseCase: GetSwapAssetsUseCase,
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val swapRepository: SwapRepository,
    private val navigateToSwapInfo: NavigateToSwapInfoUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val navigationRouter: NavigationRouter,
    private val requestSwapQuote: RequestSwapQuoteUseCase,
    private val navigateToSwapQuoteIfAvailable: NavigateToSwapQuoteIfAvailableUseCase,
    // private val exactOutputVMMapper: ExactOutputVMMapper,
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
                                text = stringRes("Cancel payment"),
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
            addressText,
            amountText,
            getSelectedSwapAsset.observe(),
            getSlippage.observe(),
            currencyType,
            getSwapAssetsUseCase.observe(),
            isRequestingQuote,
            selectedContact,
            getSelectedWalletAccount.observe()
        ) {
            address,
            amount,
            asset,
            slippage,
            currencyType,
            swapAssets,
            isRequestingQuote,
            selectedContact,
            account
            ->
            InternalStateImpl(
                swapAsset = asset,
                currencyType = currencyType,
                amountTextState = amount,
                addressText = address,
                slippage = slippage,
                swapAssets = swapAssets,
                isRequestingQuote = isRequestingQuote,
                selectedContact = selectedContact,
                account = account
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

    private fun createState(innerState: InternalStateImpl): SwapState =
        exactInputVMMapper.createState(
            internalState = innerState,
            onBack = ::onBack,
            onSwapInfoClick = ::onSwapInfoClick,
            onSwapAssetPickerClick = ::onSwapAssetPickerClick,
            onSwapCurrencyTypeClick = ::onSwapCurrencyTypeClick,
            onSlippageClick = ::onSlippageClick,
            onRequestSwapQuoteClick = ::onRequestSwapQuoteClick,
            onTryAgainClick = ::onTryAgainClick,
            onAddressChange = ::onAddressChange,
            onTextFieldChange = ::onTextFieldChange,
            onQrCodeScannerClick = ::onQrCodeScannerClick,
            onAddressBookClick = ::onAddressBookClick,
            onDeleteSelectedContactClick = ::onDeleteSelectedContactClick,
            onBalanceButtonClick = ::onBalanceButtonClick
        )

    private fun onBalanceButtonClick() {
        // navigationRouter.forward(SpendableBalanceArgs)
    }

    private fun onDeleteSelectedContactClick() = selectedContact.update { null }

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
        navigationRouter.forward(
            SwapSlippageArgs(
                fiatAmount = fiatAmount?.toPlainString(),
                mode = SwapMode.EXACT_INPUT
            )
        )

    private fun onBack() =
        viewModelScope.launch {
            if (isRequestingQuote.value) {
                isCancelStateVisible.update { true }
            } else if (isCancelStateVisible.value) {
                isCancelStateVisible.update { false }
                navigateToSwapQuoteIfAvailable(SwapMode.EXACT_INPUT) { hideCancelBottomSheet() }
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
            navigateToSwapQuoteIfAvailable(SwapMode.EXACT_INPUT) { hideCancelBottomSheet() }
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
                mode = SwapMode.EXACT_INPUT,
                canNavigateToSwapQuote = { !isCancelStateVisible.value }
            )
            isRequestingQuote.update { false }
        }

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

internal interface InternalState {
    val account: WalletAccount?
    val swapAsset: SwapAsset?
    val currencyType: CurrencyType
    val amountTextState: NumberTextFieldInnerState
    val addressText: String
    val slippage: BigDecimal
    val swapAssets: SwapAssetsData
    val isRequestingQuote: Boolean
    val selectedContact: EnhancedABContact?

    val totalSpendableBalance: Zatoshi
        get() = account?.spendableShieldedBalance ?: Zatoshi(0)
}

internal data class InternalStateImpl(
    override val account: WalletAccount?,
    override val swapAsset: SwapAsset?,
    override val currencyType: CurrencyType,
    override val amountTextState: NumberTextFieldInnerState,
    override val addressText: String,
    override val slippage: BigDecimal,
    override val swapAssets: SwapAssetsData,
    override val isRequestingQuote: Boolean,
    override val selectedContact: EnhancedABContact?,
) : InternalState
