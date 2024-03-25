@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.ZecSendExt
import cash.z.ecc.android.sdk.model.toZecString
import cash.z.ecc.android.sdk.type.AddressType
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidget
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.GradientSurface
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.send.SendTag
import co.electriccoin.zcash.ui.screen.send.model.AmountState
import co.electriccoin.zcash.ui.screen.send.model.MemoState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import java.util.Locale

@Composable
@Preview("SendForm")
private fun PreviewSendForm() {
    ZcashTheme(forceDarkMode = false) {
        GradientSurface {
            Send(
                walletSnapshot = WalletSnapshotFixture.new(),
                sendStage = SendStage.Form,
                onCreateZecSend = {},
                focusManager = LocalFocusManager.current,
                onBack = {},
                onSettings = {},
                onQrScannerOpen = {},
                goBalances = {},
                hasCameraFeature = true,
                recipientAddressState = RecipientAddressState("invalid_address", AddressType.Invalid()),
                onRecipientAddressChange = {},
                setAmountState = {},
                amountState = AmountState.Valid(ZatoshiFixture.ZATOSHI_LONG.toString(), ZatoshiFixture.new()),
                setMemoState = {},
                memoState = MemoState.new("Test message")
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
                onDone = {},
                reason = "Insufficient balance"
            )
        }
    }
}

// TODO [#1260]: Cover Send screens UI with tests
// TODO [#1260]: https://github.com/Electric-Coin-Company/zashi-android/issues/1260

@Suppress("LongParameterList")
@Composable
fun Send(
    walletSnapshot: WalletSnapshot,
    sendStage: SendStage,
    onCreateZecSend: (ZecSend) -> Unit,
    focusManager: FocusManager,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onQrScannerOpen: () -> Unit,
    goBalances: () -> Unit,
    hasCameraFeature: Boolean,
    recipientAddressState: RecipientAddressState,
    onRecipientAddressChange: (String) -> Unit,
    setAmountState: (AmountState) -> Unit,
    amountState: AmountState,
    setMemoState: (MemoState) -> Unit,
    memoState: MemoState,
) {
    Scaffold(topBar = {
        SendTopAppBar(onSettings = onSettings)
    }) { paddingValues ->
        SendMainContent(
            walletSnapshot = walletSnapshot,
            onBack = onBack,
            focusManager = focusManager,
            sendStage = sendStage,
            onCreateZecSend = onCreateZecSend,
            recipientAddressState = recipientAddressState,
            onRecipientAddressChange = onRecipientAddressChange,
            amountState = amountState,
            setAmountState = setAmountState,
            memoState = memoState,
            setMemoState = setMemoState,
            onQrScannerOpen = onQrScannerOpen,
            goBalances = goBalances,
            hasCameraFeature = hasCameraFeature,
            modifier =
                Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding() + ZcashTheme.dimens.spacingDefault,
                        bottom = paddingValues.calculateBottomPadding() + ZcashTheme.dimens.spacingHuge,
                        start = ZcashTheme.dimens.screenHorizontalSpacingRegular,
                        end = ZcashTheme.dimens.screenHorizontalSpacingRegular
                    )
        )
    }
}

@Composable
private fun SendTopAppBar(onSettings: () -> Unit) {
    SmallTopAppBar(
        titleText = stringResource(id = R.string.send_stage_send_title),
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
    walletSnapshot: WalletSnapshot,
    focusManager: FocusManager,
    onBack: () -> Unit,
    goBalances: () -> Unit,
    onCreateZecSend: (ZecSend) -> Unit,
    sendStage: SendStage,
    onQrScannerOpen: () -> Unit,
    recipientAddressState: RecipientAddressState,
    onRecipientAddressChange: (String) -> Unit,
    hasCameraFeature: Boolean,
    amountState: AmountState,
    setAmountState: (AmountState) -> Unit,
    memoState: MemoState,
    setMemoState: (MemoState) -> Unit,
    modifier: Modifier = Modifier,
) {
    // For now, we merge [SendStage.Form] and [SendStage.Proposing] into one stage. We could eventually display a
    // loader if calling the Proposal API takes longer than expected

    SendForm(
        walletSnapshot = walletSnapshot,
        recipientAddressState = recipientAddressState,
        onRecipientAddressChange = onRecipientAddressChange,
        amountState = amountState,
        setAmountState = setAmountState,
        memoState = memoState,
        setMemoState = setMemoState,
        onCreateZecSend = onCreateZecSend,
        focusManager = focusManager,
        onQrScannerOpen = onQrScannerOpen,
        goBalances = goBalances,
        hasCameraFeature = hasCameraFeature,
        modifier = modifier
    )

    if (sendStage is SendStage.SendFailure) {
        SendFailure(
            reason = sendStage.error,
            onDone = onBack
        )
    }
}

const val DEFAULT_LESS_THAN_FEE = 100_000L

// TODO [#217]: Need to handle changing of Locale after user input, but before submitting the button.
// TODO [#217]: https://github.com/Electric-Coin-Company/zashi-android/issues/217

// TODO [#1257]: Send.Form TextFields not persisted on a configuration change when the underlying ViewPager is on the
//  Balances page
// TODO [#1257]: https://github.com/Electric-Coin-Company/zashi-android/issues/1257
@Suppress("LongMethod", "LongParameterList")
@Composable
private fun SendForm(
    walletSnapshot: WalletSnapshot,
    focusManager: FocusManager,
    recipientAddressState: RecipientAddressState,
    onRecipientAddressChange: (String) -> Unit,
    amountState: AmountState,
    setAmountState: (AmountState) -> Unit,
    memoState: MemoState,
    setMemoState: (MemoState) -> Unit,
    onCreateZecSend: (ZecSend) -> Unit,
    onQrScannerOpen: () -> Unit,
    goBalances: () -> Unit,
    hasCameraFeature: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    // TODO [#1171]: Remove default MonetarySeparators locale
    // TODO [#1171]: https://github.com/Electric-Coin-Company/zashi-android/issues/1171
    val monetarySeparators = MonetarySeparators.current(Locale.US)

    val scrollState = rememberScrollState()

    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                .verticalScroll(scrollState)
                .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        BalanceWidget(
            walletSnapshot = walletSnapshot,
            isReferenceToBalances = true,
            onReferenceClick = goBalances
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))

        // TODO [#1256]: Consider Send.Form TextFields scrolling
        // TODO [#1256]: https://github.com/Electric-Coin-Company/zashi-android/issues/1256

        SendFormAddressTextField(
            focusManager = focusManager,
            hasCameraFeature = hasCameraFeature,
            onQrScannerOpen = onQrScannerOpen,
            recipientAddressState = recipientAddressState,
            setRecipientAddress = onRecipientAddressChange
        )

        Spacer(Modifier.size(ZcashTheme.dimens.spacingDefault))

        SendFormAmountTextField(
            amountSate = amountState,
            setAmountState = setAmountState,
            monetarySeparators = monetarySeparators,
            focusManager = focusManager,
            walletSnapshot = walletSnapshot,
            imeAction =
                if (recipientAddressState.type == AddressType.Transparent) {
                    ImeAction.Done
                } else {
                    ImeAction.Next
                }
        )

        Spacer(Modifier.size(ZcashTheme.dimens.spacingDefault))

        SendFormMemoTextField(
            memoState = memoState,
            setMemoState = setMemoState,
            focusManager = focusManager,
            isMemoFieldAvailable = (
                recipientAddressState.address.isEmpty() ||
                    recipientAddressState.type is AddressType.Invalid ||
                    (
                        recipientAddressState.type is AddressType.Valid &&
                            recipientAddressState.type !is AddressType.Transparent
                    )
            ),
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        // Common conditions continuously checked for validity
        val sendButtonEnabled =
            recipientAddressState.type !is AddressType.Invalid &&
                recipientAddressState.address.isNotEmpty() &&
                amountState is AmountState.Valid &&
                amountState.value.isNotBlank() &&
                walletSnapshot.spendableBalance() >= amountState.zatoshi &&
                // A valid memo is necessary only for non-transparent recipient
                (recipientAddressState.type == AddressType.Transparent || memoState is MemoState.Correct)

        Column(
            modifier = Modifier.padding(horizontal = ZcashTheme.dimens.screenHorizontalSpacingRegular),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                onClick = {
                    // SDK side validations
                    val zecSendValidation =
                        ZecSendExt.new(
                            context = context,
                            destinationString = recipientAddressState.address,
                            zecString = amountState.value,
                            // Take memo for a valid non-transparent receiver only
                            memoString =
                                if (recipientAddressState.type == AddressType.Transparent) {
                                    ""
                                } else {
                                    memoState.text
                                },
                            monetarySeparators = monetarySeparators
                        )

                    when (zecSendValidation) {
                        is ZecSendExt.ZecSendValidation.Valid -> onCreateZecSend(zecSendValidation.zecSend)
                        is ZecSendExt.ZecSendValidation.Invalid -> {
                            // We do not expect this validation to fail, so logging is enough here
                            // An error popup could be reasonable here as well
                            Twig.warn { "Send failed with: ${zecSendValidation.validationErrors}" }
                        }
                    }
                },
                text = stringResource(id = R.string.send_create),
                enabled = sendButtonEnabled,
                modifier =
                    Modifier
                        .testTag(SendTag.SEND_FORM_BUTTON)
                        .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

            BodySmall(
                text =
                    stringResource(
                        id = R.string.send_fee,
                        // TODO [#1047]: Representing Zatoshi amount
                        // TODO [#1047]: https://github.com/Electric-Coin-Company/zashi-android/issues/1047
                        Zatoshi(DEFAULT_LESS_THAN_FEE).toZecString()
                    ),
                textFontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongMethod")
@Composable
fun SendFormAddressTextField(
    focusManager: FocusManager,
    hasCameraFeature: Boolean,
    onQrScannerOpen: () -> Unit,
    recipientAddressState: RecipientAddressState,
    setRecipientAddress: (String) -> Unit,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier =
            Modifier
                // Animate error show/hide
                .animateContentSize()
                // Scroll TextField above ime keyboard
                .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        Small(text = stringResource(id = R.string.send_address_label))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        val recipientAddressValue = recipientAddressState.address
        val recipientAddressError =
            if (
                recipientAddressValue.isNotEmpty() &&
                recipientAddressState.type is AddressType.Invalid
            ) {
                stringResource(id = R.string.send_address_invalid)
            } else {
                null
            }

        FormTextField(
            value = recipientAddressValue,
            onValueChange = {
                setRecipientAddress(it)
            },
            modifier =
                Modifier
                    .fillMaxWidth(),
            error = recipientAddressError,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.send_address_hint),
                    style = ZcashTheme.extendedTypography.textFieldHint,
                    color = ZcashTheme.colors.textFieldHint
                )
            },
            trailingIcon =
                if (hasCameraFeature) {
                    {
                        IconButton(
                            onClick = onQrScannerOpen,
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.qr_code_icon),
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
                ),
            bringIntoViewRequester = bringIntoViewRequester,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongParameterList", "LongMethod")
@Composable
fun SendFormAmountTextField(
    amountSate: AmountState,
    focusManager: FocusManager,
    monetarySeparators: MonetarySeparators,
    setAmountState: (AmountState) -> Unit,
    walletSnapshot: WalletSnapshot,
    imeAction: ImeAction,
) {
    val context = LocalContext.current

    val zcashCurrency = ZcashCurrency.getLocalizedName(context)

    val amountError =
        when (amountSate) {
            is AmountState.Invalid -> {
                if (amountSate.value.isEmpty()) {
                    null
                } else {
                    stringResource(id = R.string.send_amount_invalid)
                }
            }
            is AmountState.Valid -> {
                if (walletSnapshot.spendableBalance() < amountSate.zatoshi) {
                    stringResource(id = R.string.send_amount_insufficient_balance)
                } else {
                    null
                }
            }
        }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier =
            Modifier
                // Animate error show/hide
                .animateContentSize()
                // Scroll TextField above ime keyboard
                .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        Small(text = stringResource(id = R.string.send_amount_label))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        FormTextField(
            value = amountSate.value,
            onValueChange = { newValue ->
                setAmountState(AmountState.new(context, newValue, monetarySeparators))
            },
            modifier = Modifier.fillMaxWidth(),
            error = amountError,
            placeholder = {
                Text(
                    text =
                        stringResource(
                            id = R.string.send_amount_hint,
                            zcashCurrency
                        ),
                    style = ZcashTheme.extendedTypography.textFieldHint,
                    color = ZcashTheme.colors.textFieldHint
                )
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = imeAction
                ),
            keyboardActions =
                KeyboardActions(
                    onNext = {
                        if (imeAction == ImeAction.Done) {
                            focusManager.clearFocus(true)
                        } else {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    }
                ),
            bringIntoViewRequester = bringIntoViewRequester,
        )
    }
}

// TODO [#1259]: Send.Form screen Memo field stroke bubble style
// TODO [#1259]: https://github.com/Electric-Coin-Company/zashi-android/issues/1259
@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongMethod")
@Composable
fun SendFormMemoTextField(
    focusManager: FocusManager,
    isMemoFieldAvailable: Boolean,
    memoState: MemoState,
    setMemoState: (MemoState) -> Unit,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier =
            Modifier
                // Animate error show/hide
                .animateContentSize()
                // Scroll TextField above ime keyboard
                .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.send_paper_plane),
                contentDescription = null,
                tint =
                    if (isMemoFieldAvailable) {
                        ZcashTheme.colors.textCommon
                    } else {
                        ZcashTheme.colors.textDisabled
                    }
            )

            Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingSmall))

            Small(
                text = stringResource(id = R.string.send_memo_label),
                color =
                    if (isMemoFieldAvailable) {
                        ZcashTheme.colors.textCommon
                    } else {
                        ZcashTheme.colors.textDisabled
                    }
            )
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        FormTextField(
            enabled = isMemoFieldAvailable,
            value =
                if (isMemoFieldAvailable) {
                    memoState.text
                } else {
                    ""
                },
            onValueChange = {
                setMemoState(MemoState.new(it))
            },
            bringIntoViewRequester = bringIntoViewRequester,
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
            placeholder = {
                Text(
                    text = stringResource(id = R.string.send_memo_hint),
                    style = ZcashTheme.extendedTypography.textFieldHint,
                    color = ZcashTheme.colors.textFieldHint
                )
            },
            modifier = Modifier.fillMaxWidth(),
            minHeight = ZcashTheme.dimens.textFieldMemoPanelDefaultHeight,
        )

        if (isMemoFieldAvailable) {
            Body(
                text =
                    stringResource(
                        id = R.string.send_memo_bytes_counter,
                        Memo.MAX_MEMO_LENGTH_BYTES - memoState.byteSize,
                        Memo.MAX_MEMO_LENGTH_BYTES
                    ),
                textFontWeight = FontWeight.Bold,
                color =
                    if (memoState is MemoState.Correct) {
                        ZcashTheme.colors.textFieldHint
                    } else {
                        ZcashTheme.colors.textFieldError
                    },
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = ZcashTheme.dimens.spacingTiny)
            )
        }
    }
}

@Composable
@Suppress("UNUSED_PARAMETER")
private fun SendFailure(
    onDone: () -> Unit,
    reason: String,
) {
    // Once we ensure that the [reason] contains a localized message, we can leverage it for the UI prompt

    AppAlertDialog(
        title = stringResource(id = R.string.send_dialog_error_title),
        text = stringResource(id = R.string.send_dialog_error_text),
        confirmButtonText = stringResource(id = R.string.send_dialog_error_btn),
        onConfirmButtonClick = onDone
    )
}
