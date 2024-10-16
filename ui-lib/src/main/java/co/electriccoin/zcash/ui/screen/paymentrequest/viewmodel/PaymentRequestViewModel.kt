package co.electriccoin.zcash.ui.screen.paymentrequest.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.provider.GetMonetarySeparatorProvider
import co.electriccoin.zcash.ui.common.provider.GetZcashCurrencyProvider
import co.electriccoin.zcash.ui.common.usecase.GetAddressesUseCase
import co.electriccoin.zcash.ui.common.usecase.ShareImageUseCase
import co.electriccoin.zcash.ui.common.usecase.Zip321BuildUriUseCase
import co.electriccoin.zcash.ui.common.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.qrcode.ext.fromReceiveAddressType
import co.electriccoin.zcash.ui.screen.receive.model.ReceiveAddressType
import co.electriccoin.zcash.ui.screen.request.model.AmountState
import co.electriccoin.zcash.ui.screen.request.model.MemoState
import co.electriccoin.zcash.ui.screen.request.model.QrCodeState
import co.electriccoin.zcash.ui.screen.request.model.Request
import co.electriccoin.zcash.ui.screen.request.model.RequestCurrency
import co.electriccoin.zcash.ui.screen.request.model.RequestStage
import co.electriccoin.zcash.ui.screen.request.model.RequestState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Suppress("TooManyFunctions")
class PaymentRequestViewModel(
    private val addressTypeOrdinal: Int,
    private val application: Application,
    getAddresses: GetAddressesUseCase,
    walletViewModel: WalletViewModel,
    getZcashCurrency: GetZcashCurrencyProvider,
    getMonetarySeparators: GetMonetarySeparatorProvider,
    shareImageBitmap: ShareImageUseCase,
    zip321BuildUriUseCase: Zip321BuildUriUseCase,
) : ViewModel() {
    companion object {
        private const val MAX_ZCASH_SUPPLY = 21_000_000
        private const val DEFAULT_MEMO = ""
        private const val DEFAULT_URI = ""
    }

    private val defaultAmount = application.getString(R.string.request_amount_empty)

    internal val request =
        MutableStateFlow(
            Request(
                amountState = AmountState.Default(defaultAmount, RequestCurrency.Zec),
                memoState = MemoState.Valid(DEFAULT_MEMO, 0, defaultAmount),
                qrCodeState = QrCodeState(DEFAULT_URI, defaultAmount, DEFAULT_MEMO, null),
            )
        )

    private val stage = MutableStateFlow(RequestStage.AMOUNT)

    internal val state =
        combine(
            getAddresses(),
            request,
            stage,
            walletViewModel.exchangeRateUsd,
        ) { addresses, request, currentStage, exchangeRateUsd ->
            val walletAddress = addresses.fromReceiveAddressType(ReceiveAddressType.fromOrdinal(addressTypeOrdinal))

            when (currentStage) {
                RequestStage.AMOUNT -> {
                    RequestState.Amount(
                        exchangeRateState = exchangeRateUsd,
                        monetarySeparators = getMonetarySeparators(),
                        onAmount = { onAmount(resolveExchangeRateValue(exchangeRateUsd), it) },
                        onBack = { onBack() },
                        onDone = {
                            when (walletAddress) {
                                is WalletAddress.Transparent -> {
                                    onAmountAndMemoDone(
                                        walletAddress.address,
                                        zip321BuildUriUseCase,
                                        resolveExchangeRateValue(exchangeRateUsd)
                                    )
                                }

                                is WalletAddress.Unified, is WalletAddress.Sapling -> {
                                    onAmountDone(resolveExchangeRateValue(exchangeRateUsd))
                                }

                                else -> error("Unexpected address type")
                            }
                        },
                        onSwitch = { onSwitch(resolveExchangeRateValue(exchangeRateUsd), it) },
                        request = request,
                        zcashCurrency = getZcashCurrency(),
                    )
                }

                RequestStage.MEMO -> {
                    RequestState.Memo(
                        walletAddress = walletAddress,
                        request = request,
                        onMemo = { onMemo(it) },
                        onDone = { onMemoDone(walletAddress.address, zip321BuildUriUseCase) },
                        onBack = ::onBack,
                        zcashCurrency = getZcashCurrency(),
                    )
                }

                RequestStage.QR_CODE -> {
                    RequestState.QrCode(
                        walletAddress = walletAddress,
                        request = request,
                        onQrCodeGenerate = { qrCodeForValue(request.qrCodeState.requestUri, it) },
                        onQrCodeShare = { onRequestQrCodeShare(it, shareImageBitmap) },
                        onBack = ::onBack,
                        onClose = ::onClose,
                        zcashCurrency = getZcashCurrency(),
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
            initialValue = RequestState.Loading
        )

    val backNavigationCommand = MutableSharedFlow<Unit>()
}