package co.electriccoin.zcash.ui.screen.swap.receiver

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.appbar.ZashiMainTopAppBarState
import co.electriccoin.zcash.ui.common.appbar.ZashiTopAppbar
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.IconButtonState
import co.electriccoin.zcash.ui.design.component.PickerState
import co.electriccoin.zcash.ui.design.component.Spacer
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.ZashiIconButton
import co.electriccoin.zcash.ui.design.component.ZashiPicker
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.ZashiMainTopAppBarStateFixture
import co.electriccoin.zcash.ui.screen.balances.BalanceWidget
import co.electriccoin.zcash.ui.screen.balances.BalanceWidgetState

@Composable
fun SwapReceiverView(
    state: SwapReceiverState,
    balanceWidgetState: BalanceWidgetState,
    appBarState: ZashiMainTopAppBarState?,
) {
    BlankBgScaffold(
        topBar = {
            ZashiTopAppbar(
                title = stringRes("SWAPnPAY"),
                state = appBarState,
                onBack = state.onBack
            )
        }
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .scaffoldPadding(it)
        ) {
            Spacer(8.dp)
            BalanceWidget(
                modifier = Modifier.fillMaxWidth(),
                state = balanceWidgetState
            )
            Spacer(24.dp)
            Text(
                text = "Send to",
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Dropdowns.Default.label
            )
            Spacer(8.dp)
            AddressTextField(state)
            Spacer(20.dp)
            Text(
                text = "Select token",
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Dropdowns.Default.label
            )
            Spacer(8.dp)
            ZashiPicker(
                state = state.token
            )
            Spacer(20.dp)
            Text(
                text = "Select chain",
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium,
                color = ZashiColors.Dropdowns.Default.label
            )
            Spacer(8.dp)
            ZashiPicker(
                state = state.chain
            )
        }
    }
}

@Composable
private fun AddressTextField(
    state: SwapReceiverState
) {
    val focusManager = LocalFocusManager.current
    ZashiTextField(
        state = state.address,
        singleLine = true,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Enter address",
                style = ZashiTypography.textMd,
                color = ZashiColors.Inputs.Default.text
            )
        },
        suffix = {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                ZashiIconButton(
                    modifier = Modifier.size(36.dp),
                    state = state.addressBookButton
                )
                Spacer(modifier = Modifier.width(4.dp))
                ZashiIconButton(
                    modifier = Modifier.size(36.dp),
                    state = state.qrScannerButton
                )
            }
        },
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
        keyboardActions =
            KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                }
            ),
    )
}

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        SwapReceiverView(
            state =
                SwapReceiverState(
                    address = TextFieldState(stringRes("")) {},
                    token = PickerState(null, null, stringRes("Select...")) {},
                    chain = PickerState(null, null, stringRes("Select...")) {},
                    isAddressBookHintVisible = true,
                    addressBookButton = IconButtonState(R.drawable.send_address_book) {},
                    qrScannerButton = IconButtonState(R.drawable.qr_code_icon) {},
                    positiveButton = ButtonState(stringRes("Next")) {},
                    onBack = {},
                ),
            balanceWidgetState = BalanceStateFixture.new(),
            appBarState = ZashiMainTopAppBarStateFixture.new()
        )
    }
