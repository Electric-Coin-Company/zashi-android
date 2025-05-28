package co.electriccoin.zcash.ui.screen.swap.slippage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppBarTags
import co.electriccoin.zcash.ui.design.R
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.NumberTextFieldState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiCard
import co.electriccoin.zcash.ui.design.component.ZashiInScreenModalBottomSheet
import co.electriccoin.zcash.ui.design.component.ZashiInfoText
import co.electriccoin.zcash.ui.design.component.ZashiNumberTextField
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarCloseNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.SuffixVisualTransformation
import co.electriccoin.zcash.ui.design.util.asScaffoldPaddingValues
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.swap.slippage.SlippageInfoState.Mode.HIGH
import co.electriccoin.zcash.ui.screen.swap.slippage.SlippageInfoState.Mode.LOW
import co.electriccoin.zcash.ui.screen.swap.slippage.SlippageInfoState.Mode.MEDIUM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapPickerView(state: SlippageState?) {
    ZashiInScreenModalBottomSheet(
        state = state,
        dragHandle = null
    ) { innerState ->
        BlankBgScaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopAppBar(innerState) }
        ) { padding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(padding.asScaffoldPaddingValues())
            ) {
                Text(
                    text = "Slippage",
                    style = ZashiTypography.header6,
                    fontWeight = FontWeight.SemiBold,
                    color = ZashiColors.Text.textPrimary
                )
                Spacer(8.dp)
                Text(
                    text =
                        "This setting determines the maximum allowable difference between the expected price of a " +
                            "swap and the actual price you pay, which is outside of Zashi's control.",
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Text.textTertiary
                )
                Spacer(74.dp)
                SlippageSlider(state = innerState.slider)
                if (innerState.customSlippage != null) {
                    Spacer(24.dp)
                    Row {
                        Text(
                            text = "Enter custom slippage",
                            style = ZashiTypography.textSm,
                            color = ZashiColors.Inputs.Focused.label,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(2.dp)
                        Text(
                            text = "*",
                            style = ZashiTypography.textSm,
                            color = ZashiColors.Inputs.Focused.defaultRequired,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(6.dp)
                    ZashiNumberTextField(
                        modifier = Modifier.fillMaxWidth(),
                        state = innerState.customSlippage,
                        visualTransformation = SuffixVisualTransformation("%"),
                        placeholder = { Text("0%") }
                    )
                }
                Spacer(32.dp)
                SlippageInfoCard(innerState)
                Spacer(1f)
                ZashiInfoText(
                    text =
                        "Any unused portion of the slippage fee will be refunded if the swap executes with lower " +
                            "slippage than expected."
                )
                Spacer(24.dp)
                ZashiButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = innerState.primary
                )
            }
        }
    }
}

@Composable
private fun SlippageInfoCard(innerState: SlippageState) {
    ZashiCard(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = innerState.info.mode.containerColor
            ),
        contentPadding = PaddingValues(20.dp)
    ) {
        Row {
            Image(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                colorFilter = ColorFilter.tint(innerState.info.mode.titleColor)
            )
            Spacer(8.dp)
            Column {
                Spacer(2.dp)
                Text(
                    text = innerState.info.title.getValue(),
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.SemiBold,
                    color = innerState.info.mode.titleColor
                )
                Spacer(4.dp)
                Text(
                    text = innerState.info.description.getValue(),
                    style = ZashiTypography.textXs,
                    color = innerState.info.mode.descriptionColor
                )
            }
        }
    }
}

@Composable
private fun TopAppBar(innerState: SlippageState) {
    ZashiSmallTopAppBar(
        navigationAction = {
            ZashiTopAppBarCloseNavigation(
                onBack = innerState.onBack,
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

private val SlippageInfoState.Mode.containerColor: Color
    @Composable get() =
        when (this) {
            LOW -> ZashiColors.Utility.Gray.utilityGray50
            MEDIUM -> ZashiColors.Utility.WarningYellow.utilityOrange50
            HIGH -> ZashiColors.Utility.ErrorRed.utilityError50
        }

private val SlippageInfoState.Mode.titleColor: Color
    @Composable get() =
        when (this) {
            LOW -> ZashiColors.Utility.Gray.utilityGray600
            MEDIUM -> ZashiColors.Utility.WarningYellow.utilityOrange600
            HIGH -> ZashiColors.Utility.ErrorRed.utilityError600
        }

private val SlippageInfoState.Mode.descriptionColor: Color
    @Composable get() =
        when (this) {
            LOW -> ZashiColors.Utility.Gray.utilityGray800
            MEDIUM -> ZashiColors.Utility.WarningYellow.utilityOrange800
            HIGH -> ZashiColors.Utility.ErrorRed.utilityError800
        }

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        var selected: SlippageSliderState.Selection by remember {
            mutableStateOf(SlippageSliderState.Selection.Custom)
        }

        var custom by remember { mutableStateOf(NumberTextFieldState {}) }

        val isCustomVisible by remember { derivedStateOf { selected is SlippageSliderState.Selection.Custom } }

        SwapPickerView(
            state =
                SlippageState(
                    onBack = {},
                    customSlippage = custom.copy(onValueChange = { custom = it }).takeIf { isCustomVisible },
                    slider =
                        SlippageSliderState(
                            selected = selected,
                            percentRange = 0..300 step 10,
                            labelRange = 0..300 step 100,
                            onValueChange = { selected = it }
                        ),
                    info =
                        SlippageInfoState(
                            title = stringRes("Title"),
                            description = stringRes("Description"),
                            mode = SlippageInfoState.Mode.HIGH,
                        ),
                    primary =
                        ButtonState(
                            text = stringRes("Confirm")
                        )
                )
        )
    }
