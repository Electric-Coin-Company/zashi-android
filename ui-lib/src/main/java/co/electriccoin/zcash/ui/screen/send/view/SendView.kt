@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.ext.ZcashSdk
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.ZecSendExt
import cash.z.ecc.android.sdk.model.ZecString
import cash.z.ecc.android.sdk.model.ZecStringExt
import cash.z.ecc.android.sdk.model.fromZecString
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.Header
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.ZcashTheme.dimens
import co.electriccoin.zcash.ui.screen.send.SendTag
import co.electriccoin.zcash.ui.screen.send.ext.ABBREVIATION_INDEX
import co.electriccoin.zcash.ui.screen.send.ext.abbreviated
import co.electriccoin.zcash.ui.screen.send.ext.valueOrEmptyChar
import co.electriccoin.zcash.ui.screen.send.model.SendArgumentsWrapper
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import kotlinx.coroutines.runBlocking

@Composable
@Preview("SendForm")
private fun PreviewSendForm() {
    ZcashTheme(forceDarkMode = false) {
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
                onSettings = {},
                hasCameraFeature = true
            )
        }
    }
}

@Composable
@Preview("SendSuccessful")
private fun PreviewSendSuccessful() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            SendSuccessful(
                zecSend =
                    ZecSend(
                        destination = runBlocking { WalletAddressFixture.sapling() },
                        amount = ZatoshiFixture.new(),
                        memo = MemoFixture.new()
                    ),
                onDone = {}
            )
        }
    }
}

@Composable
@Preview("SendFailure")
private fun PreviewSendFailure() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            SendFailure(
                zecSend =
                    ZecSend(
                        destination = runBlocking { WalletAddressFixture.sapling() },
                        amount = ZatoshiFixture.new(),
                        memo = MemoFixture.new()
                    ),
                onDone = {}
            )
        }
    }
}

@Composable
@Preview("SendConfirmation")
private fun PreviewSendConfirmation() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            SendConfirmation(
                zecSend =
                    ZecSend(
                        destination = runBlocking { WalletAddressFixture.sapling() },
                        amount = ZatoshiFixture.new(),
                        memo = MemoFixture.new()
                    ),
                onConfirmation = {}
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
    onSettings: () -> Unit,
    onCreateAndSend: (ZecSend) -> Unit,
    onQrScannerOpen: () -> Unit,
    hasCameraFeature: Boolean
) {
    Scaffold(topBar = {
        SendTopAppBar(
            onBack = onBack,
            onSettings = onSettings,
            showBackNavigationButton = (sendStage != SendStage.Sending && sendStage != SendStage.Form)
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
            modifier =
                Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding() + dimens.spacingDefault,
                        bottom = paddingValues.calculateBottomPadding() + dimens.spacingHuge,
                        start = dimens.screenHorizontalSpacingRegular,
                        end = dimens.screenHorizontalSpacingRegular
                    )
        )
    }
}

@Composable
private fun SendTopAppBar(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    showBackNavigationButton: Boolean = true
) {
    SmallTopAppBar(
        titleText = stringResource(id = R.string.send_title),
        onBack = onBack,
        backText =
            if (showBackNavigationButton) {
                stringResource(id = R.string.send_back)
            } else {
                null
            },
        backContentDescriptionText = stringResource(id = R.string.send_back_content_description),
        hamburgerMenuActions = {
            IconButton(
                onClick = onSettings,
                modifier = Modifier.testTag(CommonTag.SETTINGS_TOP_BAR_BUTTON)
            ) {
                Icon(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.hamburger_menu_icon),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description)
                )
            }
        }
    )
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
            // TestSend(modifier)
        }
        (sendStage == SendStage.Confirmation) -> {
            SendConfirmation(
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
                onDone = onBack,
                modifier = modifier,
            )
        }
        (sendStage == SendStage.SendFailure) -> {
            SendFailure(
                zecSend = zecSend,
                onDone = onBack,
                modifier = modifier,
            )
        }
    }
}

// TODO [#217]: Need to handle changing of Locale after user input, but before submitting the button.
// TODO [#288]: TextField component can't do long-press backspace.
// TODO [#294]: DetektAll failed LongMethod
@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongMethod", "LongParameterList", "CyclomaticComplexMethod")
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
    val focusManager = LocalFocusManager.current

    // TODO [#809]: Fix ZEC balance on Send screen
    // TODO [#809]: https://github.com/Electric-Coin-Company/zashi-android/issues/809
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
    // TODO [#826]: https://github.com/Electric-Coin-Company/zashi-android/issues/826
    if (sendArgumentsWrapper?.recipientAddress != null) {
        recipientAddressString = sendArgumentsWrapper.recipientAddress
    }
    if (sendArgumentsWrapper?.amount != null) {
        amountZecString = sendArgumentsWrapper.amount
    }
    if (sendArgumentsWrapper?.memo != null) {
        memoString = sendArgumentsWrapper.memo
    }

    Column(
        modifier =
            Modifier
                .imePadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            onValueChange = {
                recipientAddressString = it
            },
            label = { Text(stringResource(id = R.string.send_to)) },
            modifier =
                Modifier
                    .fillMaxWidth(),
            trailingIcon =
                if (hasCameraFeature) {
                    {
                        IconButton(
                            onClick = onQrScannerOpen,
                            content = {
                                Icon(
                                    imageVector = Icons.Outlined.QrCodeScanner,
                                    contentDescription = stringResource(R.string.send_scan_content_description)
                                )
                            }
                        )
                    }
                } else {
                    null
                },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
        )

        Spacer(Modifier.size(dimens.spacingSmall))

        FormTextField(
            value = amountZecString,
            onValueChange = { newValue ->
                val validated =
                    runCatching {
                        ZecStringExt.filterContinuous(context, monetarySeparators, newValue)
                    }.onFailure {
                        Twig.error(it) { "Failed while filtering incoming characters in filterContinuous" }
                        return@FormTextField
                    }.getOrDefault(false)

                if (!validated) {
                    return@FormTextField
                }

                amountZecString = newValue.filter { allowedCharacters.contains(it) }
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
            label = { Text(stringResource(id = R.string.send_amount)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(dimens.spacingSmall))

        // TODO [#810]: Disable Memo UI field in case of Transparent address
        // TODO [#810]: https://github.com/Electric-Coin-Company/zashi-android/issues/810
        FormTextField(
            value = memoString,
            onValueChange = {
                if (Memo.isWithinMaxLength(it)) {
                    memoString = it
                }
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        focusManager.clearFocus(true)
                    }
                ),
            label = { Text(stringResource(id = R.string.send_memo)) },
            modifier = Modifier.fillMaxWidth()
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

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))

        // Create a send amount that is continuously checked for validity
        val sendValueCheck = (Zatoshi.fromZecString(context, amountZecString, monetarySeparators))?.value ?: 0L

        // Continuous amount check while user is typing into the amount field
        // Note: the check for ABBREVIATION_INDEX goes away once proper address validation is in place.
        // For now, it just prevents a crash on the confirmation screen.
        val sendButtonEnabled =
            amountZecString.isNotBlank() &&
                sendValueCheck > 0L &&
                myBalance.value >= (sendValueCheck + ZcashSdk.MINERS_FEE.value) &&
                recipientAddressString.length > ABBREVIATION_INDEX

        PrimaryButton(
            onClick = {
                val zecSendValidation =
                    ZecSendExt.new(
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
            enabled = sendButtonEnabled,
            modifier = Modifier.testTag(SendTag.SEND_FORM_BUTTON)
        )
    }
}

@Composable
private fun SendConfirmation(
    zecSend: ZecSend,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            modifier =
                Modifier
                    .padding(top = dimens.spacingSmall)
                    .testTag(SendTag.SEND_CONFIRMATION_BUTTON),
            onClick = onConfirmation,
            text = stringResource(id = R.string.send_confirmation_button),
            outerPaddingValues = PaddingValues(top = dimens.spacingSmall)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))
    }
}

@Composable
private fun Sending(
    zecSend: ZecSend,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Header(
            text =
                stringResource(
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
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Body(
            modifier =
                Modifier
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            text = stringResource(R.string.send_successful_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier =
                Modifier
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
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            modifier =
                Modifier
                    .padding(top = dimens.spacingSmall)
                    .testTag(SendTag.SEND_SUCCESS_BUTTON),
            text = stringResource(R.string.send_successful_button),
            onClick = onDone,
            outerPaddingValues = PaddingValues(top = dimens.spacingSmall)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))
    }
}

@Composable
private fun SendFailure(
    zecSend: ZecSend,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            text = stringResource(R.string.send_failure_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier =
                Modifier
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
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        PrimaryButton(
            modifier =
                Modifier
                    .padding(top = dimens.spacingSmall)
                    .testTag(SendTag.SEND_FAILED_BUTTON),
            text = stringResource(R.string.send_failure_button),
            onClick = onDone,
            outerPaddingValues = PaddingValues(top = dimens.spacingSmall)
        )

        Spacer(modifier = Modifier.height(dimens.spacingDefault))
    }
}
