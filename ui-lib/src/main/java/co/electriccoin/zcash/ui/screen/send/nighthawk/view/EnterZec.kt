package co.electriccoin.zcash.ui.screen.send.nighthawk.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodyMedium
import co.electriccoin.zcash.ui.design.component.DottedBorderTextButton
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.TertiaryButton
import co.electriccoin.zcash.ui.design.component.TitleLarge
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.EnterZecUIState
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.NumberPadValueTypes
import co.electriccoin.zcash.ui.screen.send.nighthawk.model.numberPadKeys
import co.electriccoin.zcash.ui.screen.wallet.view.BalanceAmountRow

@Preview
@Composable
fun EnterZecPreview() {
    ZcashTheme(darkTheme = false) {
        Surface {
            val enterZecUIState = EnterZecUIState(
                enteredAmount = "11",
                amountUnit = "ZEC",
                spendableBalance = "10.88",
                convertedAmount = null, // if there is no Fiat conversion enabled
                isEnoughBalance = true
            )
            EnterZec(
                enterZecUIState = enterZecUIState,
                onBack = {},
                onScanPaymentCode = {},
                onContinue = {},
                onTopUpWallet = {},
                onNotEnoughZCash = {},
                onKeyPressed = {}
            )
        }
    }
}

@Composable
fun EnterZec(
    enterZecUIState: EnterZecUIState,
    onBack: () -> Unit,
    onScanPaymentCode: () -> Unit,
    onContinue: () -> Unit,
    onTopUpWallet: () -> Unit,
    onNotEnoughZCash: () -> Unit,
    onKeyPressed: (NumberPadValueTypes) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(dimensionResource(id = R.dimen.screen_standard_margin))
    ) {
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack, modifier = Modifier.size(dimensionResource(id = R.dimen.back_icon_size))) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.receive_back_content_description))
                }
                Image(painter = painterResource(id = R.drawable.ic_nighthawk_logo), contentDescription = "logo", contentScale = ContentScale.Inside, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
                TitleLarge(text = stringResource(id = R.string.ns_nighthawk), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.pageMargin)))
                BodyMedium(text = stringResource(id = R.string.ns_choose_send), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally), color = ZcashTheme.colors.surfaceEnd)
                BodyMedium(text = stringResource(id = R.string.ns_spendable_balance, enterZecUIState.spendableBalance, enterZecUIState.amountUnit), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally), color = ZcashTheme.colors.surfaceEnd)
                Spacer(modifier = Modifier.height(40.dp))
                BalanceAmountRow(balance = enterZecUIState.enteredAmount, balanceUnit = enterZecUIState.amountUnit, onFlipClicked = {}, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(12.dp))
                enterZecUIState.convertedAmount?.let {
                    BodyMedium(text = stringResource(id = R.string.ns_around, it), textAlign = TextAlign.Center, modifier = Modifier.align(Alignment.CenterHorizontally), color = ZcashTheme.colors.surfaceEnd)
                }
                Spacer(modifier = Modifier.weight(1f))

                if (enterZecUIState.isScanPaymentCodeOptionAvailable) {
                    DottedBorderTextButton(
                        onClick = onScanPaymentCode,
                        text = stringResource(id = R.string.ns_scan_payment_code),
                        modifier = Modifier.align(Alignment.CenterHorizontally).height(36.dp)
                    )
                }

                if (enterZecUIState.isEnoughBalance && enterZecUIState.isScanPaymentCodeOptionAvailable.not()) {
                    PrimaryButton(
                        onClick = onContinue,
                        text = stringResource(id = R.string.ns_continue).uppercase(),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .sizeIn(minWidth = dimensionResource(id = R.dimen.button_min_width), minHeight = dimensionResource(id = R.dimen.button_height))
                    )
                }

                if (enterZecUIState.isEnoughBalance.not()) {
                    PrimaryButton(
                        onClick = onTopUpWallet,
                        text = stringResource(id = R.string.ns_top_up_wallet).uppercase(),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .sizeIn(minWidth = 220.dp, minHeight = dimensionResource(id = R.dimen.button_height))
                    )

                    TertiaryButton(
                        onClick = onNotEnoughZCash,
                        text = stringResource (id = R.string.ns_not_enough_zcash).uppercase(),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .sizeIn(minWidth = 220.dp, minHeight = dimensionResource(id = R.dimen.button_height))
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.text_margin)))
            }
        }
        Box(
            modifier = Modifier.weight(0.4f)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3)
            ) {
                items(numberPadKeys) {
                    TextButton(onClick = {
                        if ((it is NumberPadValueTypes.BackSpace).not() && enterZecUIState.isEnoughBalance.not()) {
                            Toast.makeText(context, context.getString(R.string.insufficient_msg), Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        onKeyPressed(it)
                    }) {
                        Body(text = it.keyValue, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
