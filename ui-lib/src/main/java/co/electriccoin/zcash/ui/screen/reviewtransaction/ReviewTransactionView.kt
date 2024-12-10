package co.electriccoin.zcash.ui.screen.reviewtransaction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cash.z.ecc.android.sdk.model.FiatCurrencyConversion
import cash.z.ecc.sdk.extension.toZecStringFull
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.common.compose.BalanceWidgetBigLineOnly
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiChipButton
import co.electriccoin.zcash.ui.design.component.ZashiChipButtonState
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTextFieldDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeLabel
import kotlinx.datetime.Clock

@Composable
fun ReviewTransactionView(state: ReviewTransactionState) {
    BlankBgScaffold(
        topBar = {
            ZashiSmallTopAppBar(
                title = state.title.getValue()
            )
        }
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .scaffoldPadding(it)
            ) {
                state.items.forEach { item ->
                    when (item) {
                        is AmountState -> {
                            AmountWidget(item)
                        }

                        is ReceiverState -> {
                            Spacer(Modifier.height(24.dp))
                            ReceiverWidget(item)
                        }

                        is SenderState -> {
                            Spacer(Modifier.height(20.dp))
                            SenderWidget(item)
                            Spacer(Modifier.height(16.dp))
                        }

                        is ReceiverExpandedState -> {
                            Spacer(Modifier.height(8.dp))
                            ReceiverExpandedWidget(item)
                        }

                        is FinancialInfoState -> {
                            Spacer(Modifier.height(16.dp))
                            FinancialInfoWidget(item)
                        }

                        is MessageState -> {
                            Spacer(Modifier.height(16.dp))
                            MessageWidget(item)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
            BottomBar(state)
        }
    }
}

@Composable
fun ReceiverExpandedWidget(state: ReceiverExpandedState) {
    Column {
        Text(
            state.title.getValue(),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.name != null) {
            Text(
                state.name.getValue(),
                style = ZashiTypography.textSm,
                color = ZashiColors.Inputs.Filled.label,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            state.address.getValue(),
            style = ZashiTypography.textXs,
            color = ZashiColors.Text.textPrimary
        )

        Spacer(Modifier.height(16.dp))

        Row {
            ZashiChipButton(
                state = state.showButton,
                modifier = Modifier.padding(end = 8.dp)
            )
            Spacer(Modifier.width(12.dp))
            state.saveButton?.let {
                ZashiChipButton(
                    state = it
                )
            }
        }
    }
}

@Composable
fun SenderWidget(state: SenderState) {
    Column {
        Text(
            text = state.title.getValue(),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = state.icon),
                contentDescription = null
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = state.name.getValue(),
                style = ZashiTypography.textMd,
                color = ZashiColors.Text.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ReceiverWidget(state: ReceiverState) {
    Column {
        Text(
            state.title.getValue(),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.name != null) {
            Text(
                state.name.getValue(),
                style = ZashiTypography.textSm,
                color = ZashiColors.Inputs.Filled.label,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            state.address.getValue(),
            style = ZashiTypography.textXs,
            color = ZashiColors.Text.textPrimary
        )
    }
}

@Composable
fun MessageWidget(state: MessageState) {
    Column {
        Text(
            state.title.getValue(),
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Text.textTertiary
        )

        Spacer(modifier = Modifier.height(8.dp))

        ZashiTextField(
            state = TextFieldState(value = state.message, isEnabled = false) {},
            modifier =
                Modifier
                    .fillMaxWidth(),
            colors =
                ZashiTextFieldDefaults.defaultColors(
                    disabledTextColor = ZashiColors.Inputs.Filled.text,
                    disabledHintColor = ZashiColors.Inputs.Disabled.hint,
                    disabledBorderColor = Color.Unspecified,
                    disabledContainerColor = ZashiColors.Inputs.Disabled.bg,
                    disabledPlaceholderColor = ZashiColors.Inputs.Disabled.text,
                ),
            minLines = 4
        )
    }
}

@Composable
fun FinancialInfoWidget(state: FinancialInfoState) {
    Row {
        Text(
            modifier = Modifier.weight(1f),
            text = state.title.getValue(),
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Text.textTertiary
        )

        StyledBalance(
            balanceParts = state.amount.toZecStringFull().asZecAmountTriple(),
            isHideBalances = false,
            textStyle =
                StyledBalanceDefaults.textStyles(
                    mostSignificantPart = ZashiTypography.textSm.copy(fontWeight = FontWeight.SemiBold),
                    leastSignificantPart =
                        ZashiTypography.textXxs.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 8.sp,
                        )
                ),
        )
    }
}

@Composable
fun AmountWidget(state: AmountState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.title?.let {
            Text(
                text = state.title.getValue(),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textPrimary
            )
        }
        BalanceWidgetBigLineOnly(
            parts = state.amount.toZecStringFull().asZecAmountTriple(),
            isHideBalances = false
        )
        StyledExchangeLabel(
            zatoshi = state.amount,
            state = state.exchangeRate,
            isHideBalances = false,
            style = ZashiTypography.textMd.copy(fontWeight = FontWeight.SemiBold),
            textColor = ZashiColors.Text.textPrimary
        )
    }
}

@Composable
private fun BottomBar(state: ReviewTransactionState) {
    ZashiBottomBar {
        ZashiButton(
            state = state.primaryButton,
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
        )
        ZashiButton(
            state = state.negativeButton,
            colors = ZashiButtonDefaults.secondaryColors(),
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
        )
    }
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        ReviewTransactionView(
            state =
                ReviewTransactionState(
                    title = stringRes("Review"),
                    items =
                        listOf(
                            AmountState(
                                title = stringRes("Total Amount"),
                                amount = ZatoshiFixture.new(),
                                exchangeRate =
                                    ExchangeRateState.Data(
                                        currencyConversion =
                                            FiatCurrencyConversion(
                                                timestamp = Clock.System.now(),
                                                priceOfZec = 50.0
                                            ),
                                        isLoading = false,
                                        isStale = false,
                                        isRefreshEnabled = false,
                                        onRefresh = {}
                                    ),
                            ),
                            ReceiverState(
                                title = stringRes("Total Amount"),
                                name = stringRes("Receiver Name"),
                                address = stringRes("Receiver Address")
                            ),
                            SenderState(
                                title = stringRes("Sending from"),
                                icon = R.drawable.ic_item_keystone,
                                name = stringRes("Keystone wallet"),
                            ),
                            FinancialInfoState(
                                title = stringRes("Amount"),
                                amount = ZatoshiFixture.new()
                            ),
                            FinancialInfoState(
                                title = stringRes("Fee"),
                                amount = ZatoshiFixture.new()
                            ),
                            MessageState(
                                title = stringRes("Message"),
                                message = stringRes("Message")
                            )
                        ),
                    primaryButton =
                        ButtonState(
                            stringRes("Confirm with Keystone")
                        ),
                    negativeButton =
                        ButtonState(
                            stringRes("Cancel")
                        ),
                    onBack = {},
                )
        )
    }

@PreviewScreens
@Composable
private fun Zip321Preview() =
    ZcashTheme {
        ReviewTransactionView(
            state =
                ReviewTransactionState(
                    title = stringRes(co.electriccoin.zcash.ui.R.string.payment_request_title),
                    items =
                        listOf(
                            AmountState(
                                title = null,
                                amount = ZatoshiFixture.new(),
                                exchangeRate =
                                    ExchangeRateState.Data(
                                        currencyConversion =
                                            FiatCurrencyConversion(
                                                timestamp = Clock.System.now(),
                                                priceOfZec = 50.0
                                            ),
                                        isLoading = false,
                                        isStale = false,
                                        isRefreshEnabled = false,
                                        onRefresh = {}
                                    ),
                            ),
                            SenderState(
                                title = stringRes(co.electriccoin.zcash.ui.R.string.send_confirmation_address_from),
                                icon = R.drawable.ic_item_keystone,
                                name = stringRes("Keystone wallet"),
                            ),
                            ReceiverExpandedState(
                                title = stringRes(co.electriccoin.zcash.ui.R.string.payment_request_requested_by),
                                name = stringRes("Name"),
                                address = stringRes("Address"),
                                ZashiChipButtonState(
                                    icon = co.electriccoin.zcash.ui.R.drawable.ic_chevron_down,
                                    text =
                                        stringRes(
                                            co.electriccoin.zcash.ui.R.string.payment_request_btn_show_address,
                                        ),
                                    onClick = {}
                                ),
                                ZashiChipButtonState(
                                    icon = co.electriccoin.zcash.ui.R.drawable.ic_user_plus,
                                    text =
                                        stringRes(
                                            co.electriccoin.zcash.ui.R.string.payment_request_btn_save_contact,
                                        ),
                                    onClick = {}
                                )
                            ),
                            MessageState(
                                title = stringRes(co.electriccoin.zcash.ui.R.string.payment_request_memo),
                                message = stringRes("Message")
                            ),
                            FinancialInfoState(
                                title = stringRes(co.electriccoin.zcash.ui.R.string.payment_request_fee),
                                amount = ZatoshiFixture.new()
                            )
                        ),
                    primaryButton =
                        ButtonState(
                            stringRes(co.electriccoin.zcash.ui.R.string.review_keystone_transaction_positive)
                        ),
                    negativeButton =
                        ButtonState(
                            stringRes(co.electriccoin.zcash.ui.R.string.review_keystone_transaction_negative)
                        ),
                    onBack = {},
                )
        )
    }
