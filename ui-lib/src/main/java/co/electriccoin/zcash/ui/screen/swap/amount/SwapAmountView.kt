package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarTags
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiVerticallDivider
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber

@Composable
fun SwapAmountView(
    state: SwapAmountState,
) {
    BlankBgScaffold(
        topBar = {
            TopAppBar(state)
        }
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .scaffoldPadding(it)
        ) {
            SwapTextField(state = state.recipientGets)
            Spacer(8.dp)
            SlippageButton(
                modifier = Modifier.padding(start = 20.dp),
                state = state.slippage
            )
            Spacer(8.dp)
            SwapText(state = state.youPay)
            Spacer(24.dp)
            Spacer(1f)
            ZashiButton(
                modifier = Modifier.fillMaxWidth(),
                state = state.primaryButton
            )
        }
    }
}

@Composable
private fun SlippageButton(state: ButtonState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ZashiVerticallDivider(
            modifier = Modifier.height(32.dp),
            thickness = 1.dp,
            color = ZashiColors.Utility.Gray.utilityGray100
        )
        Spacer(20.dp)
        Text(
            text = "Slippage",
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Text.textSecondary
        )
        Spacer(1f)
        ZashiButton(
            state,
            contentPadding = PaddingValues(start = 12.dp, end = 10.dp)
        )
    }
}

@Composable
private fun TopAppBar(state: SwapAmountState) {
    ZashiSmallTopAppBar(
        title = "SWAPnPAY",
        navigationAction = {
            ZashiTopAppBarBackNavigation(
                onBack = state.onBack,
                modifier = Modifier.testTag(ZashiTopAppBarTags.BACK)
            )
        },
        colors =
            ZcashTheme.colors.topAppBarColors orDark
                ZcashTheme.colors.topAppBarColors.copyColors(
                    containerColor = Color.Transparent
                ),
    )
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        SwapAmountView(
            state =
                SwapAmountState(
                    recipientGets =
                        SwapTextFieldState(
                            token = SwapTokenState(stringRes("USDT")),
                            title = stringRes("Recipient gets"),
                            symbol = stringRes("$"),
                            primaryText = TextFieldState(value = stringRes("")) {},
                            primaryPlaceholder = stringResByDynamicCurrencyNumber(0, "$"),
                            secondaryText = stringResByDynamicCurrencyNumber(100, "USDT"),
                            exchangeRate = stringResByDynamicCurrencyNumber(100, "$"),
                            onSwapChange = {},
                        ),
                    slippage =
                        ButtonState(
                            stringRes("1%"),
                            icon = R.drawable.ic_swap_slippage
                        ),
                    youPay =
                        SwapTextState(
                            token =
                                SwapTokenState(
                                    stringRes("ZEC")
                                ),
                            title = stringRes("You pay"),
                            text = stringResByDynamicCurrencyNumber(101, "$"),
                            secondaryText = stringResByDynamicCurrencyNumber(2.47123, "ZEC")
                        ),
                    primaryButton =
                        ButtonState(
                            stringRes("Get a quote")
                        ),
                    onBack = {}
                )
        )
    }
