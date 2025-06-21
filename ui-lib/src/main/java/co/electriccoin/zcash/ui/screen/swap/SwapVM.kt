package co.electriccoin.zcash.ui.screen.swap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.convertZecToZatoshi
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.common.model.SwapAsset
import co.electriccoin.zcash.ui.common.repository.SwapMode
import co.electriccoin.zcash.ui.common.repository.SwapMode.PAY
import co.electriccoin.zcash.ui.common.repository.SwapMode.SWAP
import co.electriccoin.zcash.ui.common.usecase.CancelSwapUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSelectedSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSlippageUseCase
import co.electriccoin.zcash.ui.common.usecase.GetSwapModeUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTotalSpendableBalanceUseCase
import co.electriccoin.zcash.ui.common.usecase.GetTotalSpendableFiatBalanceUseCase
import co.electriccoin.zcash.ui.common.usecase.GetZecSwapAssetUseCase
import co.electriccoin.zcash.ui.common.usecase.IsABContactHintVisibleUseCase
import co.electriccoin.zcash.ui.common.usecase.NavigateToSwapInfoUseCase
import co.electriccoin.zcash.ui.common.usecase.UpdateSwapModeUseCase
import co.electriccoin.zcash.ui.design.component.NumberTextFieldInnerState
import co.electriccoin.zcash.ui.design.util.combine
import co.electriccoin.zcash.ui.screen.swap.picker.SwapAssetPicker
import co.electriccoin.zcash.ui.screen.swap.slippage.SwapSlippageArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.MathContext

internal class SwapVM(
    getSwapMode: GetSwapModeUseCase,
    getSlippage: GetSlippageUseCase,
    getSelectedSwapAsset: GetSelectedSwapAssetUseCase,
    getTotalSpendableFiatBalance: GetTotalSpendableFiatBalanceUseCase,
    getTotalSpendableBalance: GetTotalSpendableBalanceUseCase,
    getZecSwapAsset: GetZecSwapAssetUseCase,
    private val updateSwapMode: UpdateSwapModeUseCase,
    private val navigateToSwapInfo: NavigateToSwapInfoUseCase,
    private val isABContactHintVisible: IsABContactHintVisibleUseCase,
    private val cancelSwap: CancelSwapUseCase,
    private val navigationRouter: NavigationRouter,
    private val payMapper: PayMapper,
    private val swapMapper: SwapMapper
) : ViewModel() {
    private val defaultCurrencyType: CurrencyType
        get() = CurrencyType.TOKEN

    private val currencyType: MutableStateFlow<CurrencyType> = MutableStateFlow(defaultCurrencyType)

    private val addressText: MutableStateFlow<String> = MutableStateFlow("")

    private val amountText: MutableStateFlow<NumberTextFieldInnerState> = MutableStateFlow(NumberTextFieldInnerState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val innerState =
        combine(
            getTotalSpendableBalance.observe(),
            getTotalSpendableFiatBalance.observe(),
            addressText,
            amountText,
            getSelectedSwapAsset.observe(),
            getSlippage.observe(),
            addressText.flatMapLatest { isABContactHintVisible.observe(it) },
            currencyType,
            getZecSwapAsset.observe(),
            getSwapMode.observe()
        ) { spendable,
            spendableFiat,
            address,
            amount,
            asset,
            slippage,
            isAddressBookHintVisible,
            currencyType,
            zecSwapAsset,
            mode
            ->
            InternalState(
                swapAsset = asset,
                currencyType = currencyType,
                totalSpendableBalance = spendable,
                totalSpendableFiatBalance = spendableFiat,
                amountTextState = amount,
                addressText = address,
                slippage = slippage,
                isAddressBookHintVisible = isAddressBookHintVisible,
                zecSwapAsset = zecSwapAsset,
                swapMode = mode
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state =
        getSwapMode
            .observe()
            .flatMapLatest { mode ->
                innerState.map { innerState ->
                    val mapper =
                        when (mode) {
                            SWAP -> swapMapper
                            PAY -> payMapper
                        }

                    mapper.createState(
                        internalState = innerState,
                        onBack = ::onBack,
                        onSwapInfoClick = ::onSwapInfoClick,
                        onSwapAssetPickerClick = ::onSwapAssetPickerClick,
                        onSwapCurrencyTypeClick = ::onSwapCurrencyTypeClick,
                        onSlippageClick = ::onSlippageClick,
                        onPrimaryClick = ::onPrimaryClick,
                        onAddressChange = ::onAddressChange,
                        onSwapModeChange = ::onSwapModeChange,
                        onTextFieldChange = ::onTextFieldChange
                    )
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    private fun onSlippageClick(fiatAmount: BigDecimal?) = navigationRouter.forward(
        SwapSlippageArgs(fiatAmount = fiatAmount?.toPlainString())
    )

    private fun onBack() = cancelSwap()

    private fun onSwapCurrencyTypeClick() {
        currencyType.update {
            when (it) {
                CurrencyType.TOKEN -> CurrencyType.FIAT
                CurrencyType.FIAT -> defaultCurrencyType
            }
        }
    }

    private fun onTextFieldChange(new: NumberTextFieldInnerState) {
        amountText.update { new }
    }

    @Suppress("ForbiddenComment")
    private fun onPrimaryClick() {
        // TODO swap
    }

    private fun onSwapModeChange(swapMode: SwapMode) {
        // addressText.update { defaultText }
        // amountText.update { defaultTextFieldState }
        updateSwapMode(swapMode)
    }

    private fun onSwapInfoClick() = navigateToSwapInfo()

    private fun onAddressChange(new: String) = addressText.update { new }

    private fun onSwapAssetPickerClick() = navigationRouter.forward(SwapAssetPicker)
}

internal enum class CurrencyType { TOKEN, FIAT }

internal data class InternalState(
    val swapAsset: SwapAsset?,
    val currencyType: CurrencyType,
    val totalSpendableBalance: Zatoshi?,
    val totalSpendableFiatBalance: BigDecimal?,
    val amountTextState: NumberTextFieldInnerState,
    val addressText: String,
    val slippage: BigDecimal,
    val isAddressBookHintVisible: Boolean,
    val zecSwapAsset: SwapAsset?,
    val swapMode: SwapMode
) {
    fun getZatoshiAmount(): Zatoshi? {
        fun calculateAmountForSwap() = getAmountToken()?.convertZecToZatoshi()

        fun calculateAmountForPay(
            amountToken: BigDecimal?,
            swapAsset: SwapAsset?,
            zecSwapAsset: SwapAsset?
        ) = if (zecSwapAsset?.usdPrice == null || swapAsset?.usdPrice == null || amountToken == null) {
            null
        } else {
            amountToken
                .multiply(swapAsset.usdPrice, MathContext.DECIMAL32)
                .divide(zecSwapAsset.usdPrice, MathContext.DECIMAL32)
                .convertZecToZatoshi()
        }

        return when (swapMode) {
            SWAP -> calculateAmountForSwap()
            PAY -> calculateAmountForPay(getAmountToken(), swapAsset, zecSwapAsset)
        }
    }

    fun getTargetAssetAmount(): BigDecimal? {
        fun calculateAmountForSwap(
            amountToken: BigDecimal?,
            swapAsset: SwapAsset?,
            zecSwapAsset: SwapAsset?
        ) = if (zecSwapAsset?.usdPrice == null || swapAsset?.usdPrice == null || amountToken == null) {
            null
        } else {
            amountToken
                .multiply(zecSwapAsset.usdPrice, MathContext.DECIMAL32)
                .divide(swapAsset.usdPrice, MathContext.DECIMAL32)
        }

        fun calculateAmountForPay(
            amountToken: BigDecimal?,
            swapAsset: SwapAsset?,
            zecSwapAsset: SwapAsset?
        ) = if (zecSwapAsset?.usdPrice == null || swapAsset?.usdPrice == null || amountToken == null) {
            null
        } else {
            amountToken
                .multiply(swapAsset.usdPrice, MathContext.DECIMAL32)
                .divide(zecSwapAsset.usdPrice, MathContext.DECIMAL32)
        }

        return when (swapMode) {
            SWAP -> calculateAmountForSwap(getAmountToken(), swapAsset, zecSwapAsset)
            PAY -> calculateAmountForPay(getAmountToken(), swapAsset, zecSwapAsset)
        }
    }

    fun getAmountFiat(): BigDecimal? {
        fun calculateForPay() =
            when (currencyType) {
                CurrencyType.TOKEN -> {
                    val tokenAmount = amountTextState.amount
                    if (tokenAmount == null || swapAsset == null) null else tokenAmount.multiply(swapAsset.usdPrice)
                }

                CurrencyType.FIAT -> amountTextState.amount
            }

        fun calculateForSwap() =
            when (currencyType) {
                CurrencyType.TOKEN -> {
                    val tokenAmount = amountTextState.amount
                    if (tokenAmount == null || zecSwapAsset == null) null else tokenAmount.multiply(zecSwapAsset.usdPrice)
                }

                CurrencyType.FIAT -> amountTextState.amount
            }

        return when (swapMode) {
            SWAP -> calculateForSwap()
            PAY -> calculateForPay()
        }
    }

    fun getAmountToken(): BigDecimal? {
        fun calculateForPay(fiatAmount: BigDecimal?) =
            when (currencyType) {
                CurrencyType.TOKEN -> fiatAmount
                CurrencyType.FIAT ->
                    if (fiatAmount == null || swapAsset == null) {
                        null
                    } else {
                        fiatAmount.divide(swapAsset.usdPrice, MathContext.DECIMAL32)
                    }
            }

        fun calculateForSwap(fiatAmount: BigDecimal?) =
            when (currencyType) {
                CurrencyType.TOKEN -> fiatAmount
                CurrencyType.FIAT ->
                    if (fiatAmount == null || zecSwapAsset == null) {
                        null
                    } else {
                        fiatAmount.divide(zecSwapAsset.usdPrice, MathContext.DECIMAL32)
                    }
            }

        val fiatAmount = amountTextState.amount
        return when (swapMode) {
            SWAP -> calculateForSwap(fiatAmount)
            PAY -> calculateForPay(fiatAmount)
        }
    }

    fun getZecToAssetExchangeRate(): BigDecimal? {
        val zecUsdPrice = zecSwapAsset?.usdPrice
        val assetUsdPrice = swapAsset?.usdPrice
        if (zecUsdPrice == null || assetUsdPrice == null) return null

        return zecUsdPrice.divide(assetUsdPrice, MathContext.DECIMAL32)
    }
}
