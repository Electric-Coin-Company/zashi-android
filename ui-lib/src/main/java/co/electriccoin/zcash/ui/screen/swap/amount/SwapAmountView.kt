package co.electriccoin.zcash.ui.screen.swap.amount

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarTags
import co.electriccoin.zcash.ui.design.component.AssetCardState
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiHorizontalDivider
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.listitem.SimpleListItemState
import co.electriccoin.zcash.ui.design.component.listitem.ZashiSimpleListItem
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.design.util.stringResByDynamicCurrencyNumber
import co.electriccoin.zcash.ui.screen.swap.amount.SwapWidgetState.Selection.*

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
            Spacer(14.dp)
            SlippageSeparator(state = state.swapWidgetState)
            Spacer(14.dp)
            SwapText(state = state.youPay)
            Spacer(32.dp)
            SlippageButton(
                state = state.slippage
            )

            state.infoItems.forEach { infoItem ->
                Spacer(16.dp)
                ZashiSimpleListItem(
                    state = infoItem
                )
            }

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
        Text(
            text = "Slippage tolerance",
            style = ZashiTypography.textSm,
            fontWeight = FontWeight.Medium,
            color = ZashiColors.Text.textTertiary
        )
        Spacer(1f)
        ZashiButton(
            state = state,
            contentPadding = PaddingValues(start = 10.dp, end = 12.dp),
            colors = ZashiButtonDefaults.tertiaryColors()
        )
    }
}

@Composable
private fun SlippageSeparator(
    state: SwapWidgetState,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        when (state.selection) {
            SWAP -> 0f
            PAY -> 360f
        }
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ZashiHorizontalDivider(
            modifier = Modifier.weight(1f),
            color = ZashiColors.Utility.Gray.utilityGray100
        )

        Image(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .rotate(rotation),
            painter = painterResource(co.electriccoin.zcash.ui.design.R.drawable.ic_arrow_narrow_down),
            contentDescription = null
        )

        ZashiHorizontalDivider(
            modifier = Modifier.weight(1f),
            color = ZashiColors.Utility.Gray.utilityGray100
        )
    }
}

@Composable
private fun TopAppBar(state: SwapAmountState) {
    ZashiSmallTopAppBar(
        content = {
            SwapWidget(state = state.swapWidgetState)
        },
        navigationAction = {
            ZashiTopAppBarBackNavigation(
                onBack = state.onBack,
                modifier = Modifier.testTag(ZashiTopAppBarTags.BACK)
            )
        },
        regularActions = {
            ZashiIconButton(state.swapInfoButton)
            Spacer(20.dp)
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
                            title = stringRes("From"),
                            error = null,
                            token = AssetCardState(stringRes("USDT"), null, null, {}),
                            textFieldPrefix = imageRes(co.electriccoin.zcash.ui.design.R.drawable.ic_zec_symbol),
                            textField = NumberTextFieldState {},
                            secondaryText = stringResByDynamicCurrencyNumber(100, "USDT"),
                            max = stringResByDynamicCurrencyNumber(100, "$"),
                            onSwapChange = {}
                        ),
                    slippage =
                        ButtonState(
                            stringRes("1%"),
                            trailingIcon = R.drawable.ic_swap_slippage
                        ),
                    youPay =
                        SwapTextState(
                            token =
                                AssetCardState(
                                    stringRes("ZEC"),
                                    null,
                                    null,
                                    {}
                                ),
                            title = stringRes("To"),
                            text = stringResByDynamicCurrencyNumber(101, "$"),
                            secondaryText = stringResByDynamicCurrencyNumber(2.47123, "ZEC"),
                            max = null
                        ),
                    primaryButton =
                        ButtonState(
                            stringRes("Get a quote")
                        ),
                    onBack = {},
                    swapWidgetState = SwapWidgetState(
                        selection = SWAP,
                        onClick = { }
                    ),
                    swapInfoButton = IconButtonState(R.drawable.ic_help) {},
                    infoItems = listOf(
                        SimpleListItemState(
                            title = stringRes("Rate"),
                            text = stringRes("1 ZEC = 51.74 USDC")
                        )
                    )
                )
        )
    }
