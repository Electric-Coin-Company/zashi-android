@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cash.z.ecc.android.sdk.ext.convertZatoshiToZecString
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.Zatoshi
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.android.sdk.model.ZecSendExt
import cash.z.ecc.android.sdk.type.AddressType
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import cash.z.ecc.sdk.type.ZcashCurrency
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceState
import co.electriccoin.zcash.ui.common.compose.BalanceWidget
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.model.canSpend
import co.electriccoin.zcash.ui.common.model.spendableBalance
import co.electriccoin.zcash.ui.common.test.CommonTag
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.BodySmall
import co.electriccoin.zcash.ui.design.component.FormTextField
import co.electriccoin.zcash.ui.design.component.PrimaryButton
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.send.SendTag
import co.electriccoin.zcash.ui.screen.send.model.AmountState
import co.electriccoin.zcash.ui.screen.send.model.MemoState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendStage
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
@Preview("SendForm")
private fun PreviewSendForm() {
    ZcashTheme(forceDarkMode = false) {
        Send(
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
            memoState = MemoState.new("Test message"),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            walletSnapshot = WalletSnapshotFixture.new(),
            balanceState = BalanceStateFixture.new()
        )
    }
}

// TODO [#1260]: Cover Send screens UI with tests
// TODO [#1260]: https://github.com/Electric-Coin-Company/zashi-android/issues/1260

@Suppress("LongParameterList")
@Composable
fun Send(
    balanceState: BalanceState,
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
    topAppBarSubTitleState: TopAppBarSubTitleState,
    walletSnapshot: WalletSnapshot,
) {
    BlankBgScaffold(topBar = {
        SendTopAppBar(
            subTitleState = topAppBarSubTitleState,
            onSettings = onSettings
        )
    }) { paddingValues ->
        SendMainContent(
            balanceState = balanceState,
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
private fun SendTopAppBar(
    onSettings: () -> Unit,
    subTitleState: TopAppBarSubTitleState
) {
    SmallTopAppBar(
        subTitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
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
        },
    )
}

@Suppress("LongParameterList")
@Composable
private fun SendMainContent(
    balanceState: BalanceState,
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
        balanceState = balanceState,
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
    balanceState: BalanceState,
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
    // TODO [#1171]: Remove default MonetarySeparators locale
    // TODO [#1171]: https://github.com/Electric-Coin-Company/zashi-android/issues/1171
    val monetarySeparators = MonetarySeparators.current(Locale.US)

    val scrollState = rememberScrollState()

    val (scrollToFeePixels, setScrollToFeePixels) = rememberSaveable { mutableIntStateOf(0) }

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
            balanceState = balanceState,
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
            focusManager = focusManager,
            imeAction =
                if (recipientAddressState.type == AddressType.Transparent) {
                    ImeAction.Done
                } else {
                    ImeAction.Next
                },
            isTransparentRecipient = recipientAddressState.type?.let { it == AddressType.Transparent } ?: false,
            monetarySeparators = monetarySeparators,
            setAmountState = setAmountState,
            walletSnapshot = walletSnapshot,
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
            scrollState = scrollState,
            scrollTo = scrollToFeePixels
        )

        Spacer(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .weight(MINIMAL_WEIGHT)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        SendButton(
            amountState = amountState,
            memoState = memoState,
            monetarySeparators = monetarySeparators,
            onCreateZecSend = onCreateZecSend,
            recipientAddressState = recipientAddressState,
            walletSnapshot = walletSnapshot,
            setScrollToFeePixels = setScrollToFeePixels
        )
    }
}

@Composable
@Suppress("LongParameterList")
fun SendButton(
    amountState: AmountState,
    memoState: MemoState,
    monetarySeparators: MonetarySeparators,
    onCreateZecSend: (ZecSend) -> Unit,
    recipientAddressState: RecipientAddressState,
    setScrollToFeePixels: (Int) -> Unit,
    walletSnapshot: WalletSnapshot,
) {
    val context = LocalContext.current

    // Common conditions continuously checked for validity
    val sendButtonEnabled =
        recipientAddressState.type !is AddressType.Invalid &&
            recipientAddressState.address.isNotEmpty() &&
            amountState is AmountState.Valid &&
            amountState.value.isNotBlank() &&
            walletSnapshot.canSpend(amountState.zatoshi) &&
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
                    Zatoshi(DEFAULT_LESS_THAN_FEE).convertZatoshiToZecString(maxDecimals = 3)
                ),
            textFontWeight = FontWeight.SemiBold,
            modifier =
                Modifier.onGloballyPositioned {
                    setScrollToFeePixels(it.positionInRoot().y.toInt())
                }
        )
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
    imeAction: ImeAction,
    isTransparentRecipient: Boolean,
    monetarySeparators: MonetarySeparators,
    setAmountState: (AmountState) -> Unit,
    walletSnapshot: WalletSnapshot,
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
                setAmountState(
                    AmountState.new(
                        context = context,
                        value = newValue,
                        monetarySeparators = monetarySeparators,
                        isTransparentRecipient = isTransparentRecipient
                    )
                )
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
                    onDone = {
                        focusManager.clearFocus(true)
                    },
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
            bringIntoViewRequester = bringIntoViewRequester,
        )
    }
}

// TODO [#1259]: Send.Form screen Memo field stroke bubble style
// TODO [#1259]: https://github.com/Electric-Coin-Company/zashi-android/issues/1259
@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongMethod", "LongParameterList")
@Composable
fun SendFormMemoTextField(
    focusManager: FocusManager,
    isMemoFieldAvailable: Boolean,
    memoState: MemoState,
    setMemoState: (MemoState) -> Unit,
    scrollState: ScrollState,
    scrollTo: Int
) {
    val scope = rememberCoroutineScope()

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
                    onDone = {
                        focusManager.clearFocus(true)
                        // Scroll down to make sure the Send button is visible on small screens
                        if (scrollTo > 0) {
                            scope.launch {
                                scrollState.animateScrollTo(scrollTo)
                            }
                        }
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
                        ZcashTheme.colors.textFieldWarning
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
@Preview("SendFailure")
private fun PreviewSendFailure() {
    ZcashTheme(forceDarkMode = false) {
        BlankSurface {
            SendFailure(
                onDone = {},
                reason = "Insufficient balance"
            )
        }
    }
}

@Composable
private fun SendFailure(
    onDone: () -> Unit,
    reason: String,
    modifier: Modifier = Modifier
) {
    // TODO [#1276]: Once we ensure that the reason contains a localized message, we can leverage it for the UI prompt
    // TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
    // TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

    AppAlertDialog(
        title = stringResource(id = R.string.send_dialog_error_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(text = stringResource(id = R.string.send_dialog_error_text))

                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                Text(
                    text = reason,
                    fontStyle = FontStyle.Italic
                )
            }
        },
        confirmButtonText = stringResource(id = R.string.send_dialog_error_btn),
        onConfirmButtonClick = onDone,
        modifier = modifier
    )
}
