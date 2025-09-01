package co.electriccoin.zcash.ui.screen.pay

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
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapQuoteIfAvailableUseCase
import co.electriccoin.zcash.ui.common.usecase.RequestSwapQuoteUseCase
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldInnerState
import co.electriccoin.zcash.ui.design.util.combine
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.pay.info.PayInfoArgs
import co.electriccoin.zcash.ui.screen.swap.SwapCancelState
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPickerArgs
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Suppress("TooManyFunctions")
internal class PayVM(
    getSlippage: GetSlippageUseCase,
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    getSwapAssetsUseCase: GetSwapAssetsUseCase,
    getSelectedWalletAccount: GetSelectedWalletAccountUseCase,
    private val swapRepository: SwapRepository,
    private val cancelSwap: CancelSwapUseCase,
    private val navigationRouter: NavigationRouter,
    private val requestSwapQuote: RequestSwapQuoteUseCase,
    private val navigateToSwapQuoteIfAvailable: NavigateToSwapQuoteIfAvailableUseCase,
    private val exactOutputVMMapper: ExactOutputVMMapper,
    private val navigateToScanAddress: NavigateToScanGenericAddressUseCase,
    private val navigateToSelectSwapRecipient: NavigateToSelectABSwapRecipientUseCase,
) : ViewModel() {

    private val addressText: MutableStateFlow<String> = MutableStateFlow("")

    private val text = MutableStateFlow(NumberTextFieldInnerState() to NumberTextFieldInnerState())

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
            addressText,
            text,
            getSelectedSwapAsset.observe(),
            getSlippage.observe(),
            getSwapAssetsUseCase.observe(),
            isRequestingQuote,
            selectedContact,
            getSelectedWalletAccount.observe().filterNotNull()
        ) { address,
            text,
            asset,
            slippage,
            swapAssets,
            isRequestingQuote,
            selectedContact,
            account
            ->
            InternalStateImpl(
                asset = asset,
                amount = text.first,
                fiatAmount = text.second,
                address = address,
                slippage = slippage,
                swapAssets = swapAssets,
                isRequestingQuote = isRequestingQuote,
                selectedABContact = selectedContact,
                account = account
            )
        }

    val state =
        innerState
            .map { innerState ->
                exactOutputVMMapper.createState(
                    internalState = innerState,
                    onBack = ::onBack,
                    onSwapInfoClick = ::onInfoClick,
                    onSwapAssetPickerClick = ::onSwapAssetPickerClick,
                    onSlippageClick = ::onSlippageClick,
                    onRequestSwapQuoteClick = ::onRequestSwapQuoteClick,
                    onTryAgainClick = ::onTryAgainClick,
                    onAddressChange = ::onAddressChange,
                    onQrCodeScannerClick = ::onQrCodeScannerClick,
                    onAddressBookClick = ::onAddressBookClick,
                    onDeleteSelectedContactClick = ::onDeleteSelectedContactClick,
                    onTextFieldChange = ::onTextFieldChange,
                )

            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    init {
        swapRepository
            .selectedAsset
            .onEach { asset ->
                val newFiatAmountState = exactOutputVMMapper.createFiatAmountInnerState(
                    amountInnerState = text.value.first,
                    fiatInnerState = text.value.second,
                    asset = asset
                )
                text.update { it.copy(second = newFiatAmountState) }
            }
            .launchIn(viewModelScope)
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
                    text.update {
                        it.copy(
                            first = NumberTextFieldInnerState.fromAmount(result.amount)
                        )
                    }
                }
            }
        }

    private fun onSlippageClick(fiatAmount: BigDecimal?) =
        navigationRouter.forward(
            SwapSlippageArgs(
                fiatAmount = fiatAmount?.toPlainString(),
                mode = SwapMode.EXACT_OUTPUT
            )
        )

    private fun onBack() =
        viewModelScope.launch {
            if (isRequestingQuote.value) {
                isCancelStateVisible.update { true }
            } else if (isCancelStateVisible.value) {
                isCancelStateVisible.update { false }
                navigateToSwapQuoteIfAvailable(SwapMode.EXACT_OUTPUT) { hideCancelBottomSheet() }
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
            navigateToSwapQuoteIfAvailable(SwapMode.EXACT_OUTPUT) { hideCancelBottomSheet() }
        }

    @Suppress("MagicNumber")
    private suspend fun hideCancelBottomSheet() {
        isCancelStateVisible.update { false }
        delay(350)
    }

    private fun onTextFieldChange(amount: NumberTextFieldInnerState, fiat: NumberTextFieldInnerState) {
        text.update { amount to fiat }
    }

    private fun onRequestSwapQuoteClick(amount: BigDecimal, address: String) =
        viewModelScope.launch {
            isRequestingQuote.update { true }
            requestSwapQuote(
                amount = amount,
                address = address,
                mode = SwapMode.EXACT_OUTPUT,
                canNavigateToSwapQuote = { !isCancelStateVisible.value }
            )
            isRequestingQuote.update { false }
        }

    private fun onInfoClick() = navigationRouter.forward(PayInfoArgs)

    private fun onAddressChange(new: String) {
        selectedContact.update { null }
        addressText.update { new }
    }

    private fun onSwapAssetPickerClick() =
        navigationRouter
            .forward(SwapAssetPickerArgs(selectedContact.value?.blockchain?.chainTicker))
}

internal interface InternalState {
    val address: String
    val selectedABContact: EnhancedABContact?
    val asset: SwapAsset?
    val amount: NumberTextFieldInnerState
    val fiatAmount: NumberTextFieldInnerState
    val slippage: BigDecimal
    val isRequestingQuote: Boolean
    val account: WalletAccount
    val swapAssets: SwapAssetsData

    val totalSpendableBalance: Zatoshi
        get() = account.spendableShieldedBalance
}

internal data class InternalStateImpl(
    override val address: String,
    override val selectedABContact: EnhancedABContact?,
    override val asset: SwapAsset?,
    override val amount: NumberTextFieldInnerState,
    override val fiatAmount: NumberTextFieldInnerState,
    override val slippage: BigDecimal,
    override val isRequestingQuote: Boolean,
    override val account: WalletAccount,
    override val swapAssets: SwapAssetsData,
) : InternalState
