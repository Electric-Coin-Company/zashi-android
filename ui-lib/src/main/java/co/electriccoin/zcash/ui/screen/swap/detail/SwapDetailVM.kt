package co.electriccoin.zcash.ui.screen.swap.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.sdk.ANDROID_STATE_FLOW_TIMEOUT
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.model.SwapStatus
import co.electriccoin.zcash.ui.common.model.SwapStatus.*
import co.electriccoin.zcash.ui.common.usecase.CopyToClipboardUseCase
import co.electriccoin.zcash.ui.common.usecase.GetORSwapQuoteUseCase
import co.electriccoin.zcash.ui.common.usecase.SwapData
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.loadingImageRes
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByAddress
import co.electriccoin.zcash.ui.design.util.stringResByCurrencyNumber
import co.electriccoin.zcash.ui.design.util.stringResByDateTime
import co.electriccoin.zcash.ui.design.util.stringResByNumber
import co.electriccoin.zcash.ui.screen.transactiondetail.CommonTransactionDetailMapper
import co.electriccoin.zcash.ui.screen.transactiondetail.TransactionDetailHeaderState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailInfoRowState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailSwapStatusRowState
import co.electriccoin.zcash.ui.screen.transactiondetail.infoitems.TransactionDetailSwapStatusRowState.Mode.SWAP_INTO_ZEC
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.ZoneId

class SwapDetailVM(
    getORSwapQuote: GetORSwapQuoteUseCase,
    private val args: SwapDetailArgs,
    private val navigationRouter: NavigationRouter,
    private val copyToClipboard: CopyToClipboardUseCase,
    private val mapper: CommonTransactionDetailMapper
) : ViewModel() {
    val state: StateFlow<SwapDetailState?> =
        getORSwapQuote
            .observe(args.depositAddress)
            .map { swapData ->
                SwapDetailState(
                    quoteHeader = mapper
                        .createTransactionDetailQuoteHeaderState(
                            swap = swapData.data?.data,
                            originAsset = swapData.data?.originAsset,
                            destinationAsset = swapData.data?.destinationAsset
                        ),
                    transactionHeader = createTransactionHeaderState(swapData),
                    status = TransactionDetailSwapStatusRowState(
                        title = stringRes(R.string.transaction_detail_info_transaction_status),
                        status = swapData.data?.data?.status,
                        mode = SWAP_INTO_ZEC
                    ),
                    depositTo = TransactionDetailInfoRowState(
                        title = stringRes("Deposit to"),
                        message = stringResByAddress(args.depositAddress, true),
                        trailingIcon = R.drawable.ic_transaction_detail_info_copy,
                        onClick = ::onCopyDepositAddressClick
                    ),
                    recipient = createRecipientState(swapData),
                    totalFees = createTotalFeesState(swapData),
                    maxSlippage = createSlippageState(swapData),
                    timestamp = TransactionDetailInfoRowState(
                        title = stringRes(R.string.transaction_detail_info_timestamp),
                        message = swapData.data?.data?.timestamp
                            ?.atZone(ZoneId.systemDefault())
                            ?.let {
                                stringResByDateTime(
                                    zonedDateTime = it,
                                    useFullFormat = true
                                )
                            },
                    ),
                    errorFooter = mapper.createTransactionDetailErrorFooter(swapData.data?.error),
                    primaryButton = createPrimaryButtonState(swapData, swapData.data?.error),
                    onBack = ::onBack
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(ANDROID_STATE_FLOW_TIMEOUT),
                initialValue = null
            )

    private fun createTotalFeesState(swapData: SwapData): TransactionDetailInfoRowState =
        TransactionDetailInfoRowState(
            title = stringRes(R.string.transaction_detail_info_total_fees),
            message = if (swapData.data?.data?.amountInFee != null && swapData.data.originAsset != null) {
                stringResByCurrencyNumber(
                    amount = swapData.data.data.amountInFee,
                    ticker = swapData.data.originAsset.tokenTicker.uppercase()
                )
            } else {
                null
            }
        )

    private fun createRecipientState(swapData: SwapData): TransactionDetailInfoRowState =
        TransactionDetailInfoRowState(
            title = stringRes(R.string.transaction_detail_info_recipient),
            message = swapData.data?.data?.recipient?.let { stringResByAddress(it, true) },
            trailingIcon = R.drawable.ic_transaction_detail_info_copy,
            onClick = if (swapData.data?.data?.recipient != null) {
                { onCopyRecipientAddressClick(swapData.data.data.recipient) }
            } else {
                null
            }
        )

    private fun createSlippageState(swapData: SwapData): TransactionDetailInfoRowState = TransactionDetailInfoRowState(
        title = if (swapData.data?.data?.isSlippageRealized == true) {
            stringRes(R.string.transaction_detail_info_realized_slippage)
        } else {
            stringRes(R.string.transaction_detail_info_max_slippage)
        },
        message = swapData.data?.data?.maxSlippage?.let {
            stringResByNumber(it, 0) + stringRes("%")
        },
    )

    private fun createPrimaryButtonState(
        swapData: SwapData,
        error: Exception?
    ): ButtonState? = if (swapData.data?.error != null && swapData.data.data == null) {
        mapper.createTransactionDetailErrorButtonState(
            error = error,
            swapHandle = swapData.handle
        )
    } else {
        null
    }

    private fun createTransactionHeaderState(swapData: SwapData): TransactionDetailHeaderState =
        TransactionDetailHeaderState(
            title = when (swapData.data?.data?.status) {
                EXPIRED -> stringRes("Swap Expired")

                INCOMPLETE_DEPOSIT,
                PENDING -> stringRes("Swap Pending")

                SUCCESS -> stringRes("Swap Completed")
                REFUNDED -> stringRes("Swap Refunded")
                FAILED -> stringRes("Swap Failed")
                PROCESSING -> stringRes("Swap Processing")
                null -> null
            },
            amount = swapData.data?.data?.amountOutFormatted?.let { stringResByNumber(it) },
            icons = listOf(
                swapData.data?.originAsset?.tokenIcon ?: loadingImageRes(),
                imageRes(R.drawable.ic_swap_detail),
                imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_token_zec),
            )
        )

    private fun onBack() = navigationRouter.back()

    private fun onCopyDepositAddressClick() = copyToClipboard("deposit Address", args.depositAddress)

    private fun onCopyRecipientAddressClick(recipient: String) =
        copyToClipboard("recipient Address", recipient)
}
