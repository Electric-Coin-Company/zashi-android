package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.ZecSendExt
import cash.z.ecc.android.sdk.model.ZecString
import cash.z.ecc.android.sdk.model.ZecStringExt
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.ZcashTheme.dimens
import co.electriccoin.zcash.ui.screen.send.ext.ABBREVIATION_INDEX
import co.electriccoin.zcash.ui.screen.send.ext.abbreviated
import co.electriccoin.zcash.ui.screen.send.ext.valueOrEmptyChar
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import co.electriccoin.zcash.ui.screen.send.model.SendStage

@Composable
@Preview("Send")
private fun PreviewSend() {
    ZcashTheme(darkTheme = true) {
        GradientSurface {
            Send(
                mySpendableBalance = ZatoshiFixture.new(),
                sendArgumentsWrapper = null,
                sendStage = SendStage.Form,
                onSendStageChange = {},
                zecSend = null,
                onZecSendChange = {},
                onCreateAndSend = {},
                onQrScannerOpen = {},
                onBack = {},
                hasCameraFeature = true
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
fun Send(
    mySpendableBalance: Zatoshi,
    sendArgumentsWrapper: SendArgumentsWrapper?,
    sendStage: SendStage,
    onSendStageChange: (SendStage) -> Unit,
    zecSend: ZecSend?,
    onZecSendChange: (ZecSend) -> Unit,
    onBack: () -> Unit,
    onCreateAndSend: (ZecSend) -> Unit,
    onQrScannerOpen: () -> Unit,
    hasCameraFeature: Boolean
) {
    Scaffold(topBar = {
        SendTopAppBar(
            onBack = onBack,
            showBackNavigationButton = sendStage != SendStage.Sending
        )
    }) { paddingValues ->
        SendMainContent(
            myBalance = mySpendableBalance,
            sendArgumentsWrapper = sendArgumentsWrapper,
            onBack = onBack,
            sendStage = sendStage,
            onSendStageChange = onSendStageChange,
            zecSend = zecSend,
            onZecSendChange = onZecSendChange,
            onSendSubmit = onCreateAndSend,
            onQrScannerOpen = onQrScannerOpen,
            hasCameraFeature = hasCameraFeature,
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    top = paddingValues.calculateTopPadding() + dimens.spacingDefault,
                    bottom = paddingValues.calculateBottomPadding() + dimens.spacingDefault,
                    start = dimens.spacingDefault,
                    end = dimens.spacingDefault
                )
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SendTopAppBar(
    onBack: () -> Unit,
    showBackNavigationButton: Boolean = true
) {
    if (showBackNavigationButton) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.send_title)) },
            navigationIcon = {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.send_back_content_description)
                    )
                }
            }
        )
    } else {
        TopAppBar(title = { Text(text = stringResource(id = R.string.send_title)) })
    }
}

@Suppress("LongParameterList")
@Composable
private fun SendMainContent(
    myBalance: Zatoshi,
    sendArgumentsWrapper: SendArgumentsWrapper?,
    zecSend: ZecSend?,
    onZecSendChange: (ZecSend) -> Unit,
    onBack: () -> Unit,
    sendStage: SendStage,
    onSendStageChange: (SendStage) -> Unit,
    onSendSubmit: (ZecSend) -> Unit,
    onQrScannerOpen: () -> Unit,
    hasCameraFeature: Boolean,
    modifier: Modifier = Modifier
) {
    when {
        (sendStage == SendStage.Form || null == zecSend) -> {
            SendForm(
                myBalance = myBalance,
                sendArgumentsWrapper = sendArgumentsWrapper,
                previousZecSend = zecSend,
                onCreateZecSend = {
                    onSendStageChange(SendStage.Confirmation)
                    onZecSendChange(it)
                },
                onQrScannerOpen = onQrScannerOpen,
                hasCameraFeature = hasCameraFeature,
                modifier = modifier
            )
        }
        (sendStage == SendStage.Confirmation) -> {
            Confirmation(
                zecSend = zecSend,
                onConfirmation = {
                    onSendStageChange(SendStage.Sending)
                    onSendSubmit(zecSend)
                },
                modifier = modifier
            )
        }
        (sendStage == SendStage.Sending) -> {
            Sending(
                zecSend = zecSend,
                modifier = modifier
            )
        }
        (sendStage == SendStage.SendSuccessful) -> {
            SendSuccessful(
                zecSend = zecSend,
                modifier = modifier,
                onDone = onBack
            )
        }
        (sendStage == SendStage.SendFailure) -> {
            SendFailure(
                zecSend = zecSend,
                modifier = modifier,
                onDone = onBack
            )
        }
    }
}

// TODO [#217]: Need to handle changing of Locale after user input, but before submitting the button.
// TODO [#288]: TextField component can't do long-press backspace.
// TODO [#294]: DetektAll failed LongMethod
@Suppress("LongMethod", "LongParameterList")
@Composable
private fun SendForm(
    myBalance: Zatoshi,
    sendArgumentsWrapper: SendArgumentsWrapper?,
    previousZecSend: ZecSend?,
    onCreateZecSend: (ZecSend) -> Unit,
    onQrScannerOpen: () -> Unit,
    hasCameraFeature: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val monetarySeparators = MonetarySeparators.current()
    val allowedCharacters = ZecString.allowedCharacters(monetarySeparators)

    // TODO [#809]: Fix ZEC balance on Send screen
    // TODO [#809]: https://github.com/zcash/secant-android-wallet/issues/809
    var amountZecString by rememberSaveable {
        mutableStateOf(previousZecSend?.amount?.toZecString() ?: "")
    }
    var recipientAddressString by rememberSaveable {
        mutableStateOf(previousZecSend?.destination?.address ?: "")
    }
    var memoString by rememberSaveable { mutableStateOf(previousZecSend?.memo?.value ?: "") }

    var validation by rememberSaveable {
        mutableStateOf<Set<ZecSendExt.ZecSendValidation.Invalid.ValidationError>>(emptySet())
    }

    // TODO [#826]: SendArgumentsWrapper object properties validation
    // TODO [#826]: https://github.com/zcash/secant-android-wallet/issues/826
    if (sendArgumentsWrapper?.recipientAddress != null) {
        recipientAddressString = sendArgumentsWrapper.recipientAddress
    }
    if (sendArgumentsWrapper?.amount != null) {
        amountZecString = sendArgumentsWrapper.amount
    }
    if (sendArgumentsWrapper?.memo != null) {
        memoString = sendArgumentsWrapper.memo
    }

    Column(modifier) {
        Header(
            text = stringResource(id = R.string.send_balance, myBalance.toZecString()),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Body(
            text = stringResource(id = R.string.send_balance_subtitle),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimens.spacingLarge))

        FormTextField(
            value = recipientAddressString,
            onValueChange = { recipientAddressString = it },
            label = { Text(stringResource(id = R.string.send_to)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = if (hasCameraFeature) { {
                IconButton(
                    onClick = onQrScannerOpen,
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = stringResource(R.string.send_scan_content_description)
                        )
                    }
                )
            } } else { null }
        )

        Spacer(Modifier.size(dimens.spacingSmall))

        FormTextField(
            value = amountZecString,
            onValueChange = { newValue ->
                if (!ZecStringExt.filterContinuous(context, monetarySeparators, newValue)) {
                    return@FormTextField
                }
                amountZecString = newValue.filter { allowedCharacters.contains(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(id = R.string.send_amount)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(dimens.spacingSmall))

        // TODO [#810]: Disable Memo UI field in case of Transparent address
        // TODO [#810]: https://github.com/zcash/secant-android-wallet/issues/810
        FormTextField(
            value = memoString,
            onValueChange = {
                if (Memo.isWithinMaxLength(it)) {
                    memoString = it
                }
            },
            label = { Text(stringResource(id = R.string.send_memo)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        if (validation.isNotEmpty()) {
            /*
             * Note: this is not localized in that it uses the enum constant name and joins the string
             * without regard for RTL.  This will get resolved once we do proper validation for
             * the fields.
             */
            Text(
                text = validation.joinToString(", "),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        PrimaryButton(
            onClick = {
                val zecSendValidation = ZecSendExt.new(
                    context,
                    recipientAddressString,
                    amountZecString,
                    memoString,
                    monetarySeparators
                )

                when (zecSendValidation) {
                    is ZecSendExt.ZecSendValidation.Valid -> onCreateZecSend(zecSendValidation.zecSend)
                    is ZecSendExt.ZecSendValidation.Invalid -> validation = zecSendValidation.validationErrors
                }
            },
            text = stringResource(id = R.string.send_create),
            // Check for ABBREVIATION_INDEX goes away once proper address validation is in place.
            // For now, it just prevents a crash on the confirmation screen.
            enabled = amountZecString.isNotBlank() && recipientAddressString.length > ABBREVIATION_INDEX,
            outerPaddingValues = PaddingValues(top = dimens.spacingNone)
        )
    }
}

@Composable
private fun Confirmation(
    zecSend: ZecSend,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Body(
            stringResource(
                R.string.send_confirmation_amount_and_address_format,
                zecSend.amount.toZecString(),
                zecSend.destination.abbreviated()
            )
        )
        if (zecSend.memo.value.isNotEmpty()) {
            Body(
                stringResource(
                    R.string.send_confirmation_memo_format,
                    zecSend.memo.value
                )
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            modifier = Modifier.padding(top = dimens.spacingSmall),
            onClick = onConfirmation,
            text = stringResource(id = R.string.send_confirmation_button),
            outerPaddingValues = PaddingValues(top = dimens.spacingSmall)
        )
    }
}

@Composable
private fun Sending(
    zecSend: ZecSend,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Header(
            text = stringResource(
                R.string.send_in_progress_amount_format,
                zecSend.amount.toZecString()
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Body(
            text = zecSend.destination.abbreviated(),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (zecSend.memo.value.isNotEmpty()) {
            Body(
                stringResource(
                    R.string.send_in_progress_memo_format,
                    zecSend.memo.value
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        Body(
            modifier = Modifier
                .padding(vertical = dimens.spacingSmall)
                .fillMaxWidth(),
            text = stringResource(R.string.send_in_progress_wait),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SendSuccessful(
    zecSend: ZecSend,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Header(
            text = stringResource(R.string.send_successful_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.spacingDefault)
        )

        Body(
            stringResource(
                R.string.send_successful_amount_address_memo,
                zecSend.amount.toZecString(),
                zecSend.destination.abbreviated(),
                zecSend.memo.valueOrEmptyChar()
            )
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            modifier = Modifier.padding(top = dimens.spacingSmall),
            text = stringResource(R.string.send_successful_button),
            onClick = onDone,
            outerPaddingValues = PaddingValues(top = dimens.spacingSmall)
        )
    }
}

@Composable
private fun SendFailure(
    zecSend: ZecSend,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Header(
            text = stringResource(R.string.send_failure_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.spacingDefault)
        )

        Body(
            stringResource(
                R.string.send_failure_amount_address_memo,
                zecSend.amount.toZecString(),
                zecSend.destination.abbreviated(),
                zecSend.memo.valueOrEmptyChar()
            )
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            modifier = Modifier.padding(top = dimens.spacingSmall),
            text = stringResource(R.string.send_failure_button),
            onClick = onDone,
            outerPaddingValues = PaddingValues(top = dimens.spacingSmall)
        )
    }
}
