@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.send.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.model.Memo
import cash.z.ecc.android.sdk.model.MonetarySeparators
import cash.z.ecc.android.sdk.model.WalletAddress
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
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.MINIMAL_WEIGHT
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.BlankSurface
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.TopAppBarHideBalancesNavigation
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTextFieldDefaults
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.fixture.BalanceStateFixture
import co.electriccoin.zcash.ui.fixture.WalletSnapshotFixture
import co.electriccoin.zcash.ui.screen.send.SendTag
import co.electriccoin.zcash.ui.screen.send.model.AmountState
import co.electriccoin.zcash.ui.screen.send.model.MemoState
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import co.electriccoin.zcash.ui.screen.send.model.SendAddressBookState
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
            onBack = {},
            onHideBalances = {},
            onSettings = {},
            onQrScannerOpen = {},
            goBalances = {},
            hasCameraFeature = true,
            recipientAddressState = RecipientAddressState("invalid_address", AddressType.Invalid()),
            onRecipientAddressChange = {},
            setAmountState = {},
            amountState =
                AmountState.Valid(
                    value = ZatoshiFixture.ZATOSHI_LONG.toString(),
                    fiatValue = "",
                    zatoshi = ZatoshiFixture.new()
                ),
            setMemoState = {},
            memoState = MemoState.new("Test message "),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            walletSnapshot = WalletSnapshotFixture.new(),
            balanceState = BalanceStateFixture.new(),
            isHideBalances = false,
            exchangeRateState = ExchangeRateState.OptedOut,
            sendAddressBookState =
                SendAddressBookState(
                    mode = SendAddressBookState.Mode.ADD_TO_ADDRESS_BOOK,
                    isHintVisible = true,
                    onButtonClick = {}
                )
        )
    }
}

@Composable
@Preview
private fun SendFormTransparentAddressPreview() {
    ZcashTheme(forceDarkMode = false) {
        Send(
            sendStage = SendStage.Form,
            onCreateZecSend = {},
            onBack = {},
            onHideBalances = {},
            onSettings = {},
            onQrScannerOpen = {},
            goBalances = {},
            hasCameraFeature = true,
            recipientAddressState =
                RecipientAddressState(
                    address = "tmCxJG72RWN66xwPtNgu4iKHpyysGrc7rEg",
                    type = AddressType.Transparent
                ),
            onRecipientAddressChange = {},
            setAmountState = {},
            amountState =
                AmountState.Valid(
                    value = ZatoshiFixture.ZATOSHI_LONG.toString(),
                    fiatValue = "",
                    zatoshi = ZatoshiFixture.new()
                ),
            setMemoState = {},
            memoState = MemoState.new("Test message"),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            walletSnapshot = WalletSnapshotFixture.new(),
            balanceState = BalanceStateFixture.new(),
            isHideBalances = false,
            exchangeRateState = ExchangeRateState.OptedOut,
            sendAddressBookState =
                SendAddressBookState(
                    mode = SendAddressBookState.Mode.ADD_TO_ADDRESS_BOOK,
                    isHintVisible = true,
                    onButtonClick = {}
                )
        )
    }
}

// TODO [#1260]: Cover Send screens UI with tests
// TODO [#1260]: https://github.com/Electric-Coin-Company/zashi-android/issues/1260
@Suppress("LongParameterList")
@Composable
fun Send(
    balanceState: BalanceState,
    isHideBalances: Boolean,
    onHideBalances: () -> Unit,
    sendStage: SendStage,
    onCreateZecSend: (ZecSend) -> Unit,
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
    exchangeRateState: ExchangeRateState,
    sendAddressBookState: SendAddressBookState,
) {
    BlankBgScaffold(topBar = {
        SendTopAppBar(
            isHideBalances = isHideBalances,
            onHideBalances = onHideBalances,
            subTitleState = topAppBarSubTitleState,
            onSettings = onSettings
        )
    }) { paddingValues ->
        SendMainContent(
            balanceState = balanceState,
            isHideBalances = isHideBalances,
            walletSnapshot = walletSnapshot,
            onBack = onBack,
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
                        top = paddingValues.calculateTopPadding() + ZashiDimensions.Spacing.spacingLg,
                        bottom = ZashiDimensions.Spacing.spacing4xl,
                        start = ZashiDimensions.Spacing.spacing3xl,
                        end = ZashiDimensions.Spacing.spacing3xl
                    ),
            exchangeRateState = exchangeRateState,
            sendState = sendAddressBookState
        )
    }
}

@Composable
private fun SendTopAppBar(
    isHideBalances: Boolean,
    onHideBalances: () -> Unit,
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
                Image(
                    painter = painterResource(id = co.electriccoin.zcash.ui.design.R.drawable.ic_hamburger_menu),
                    contentDescription = stringResource(id = R.string.settings_menu_content_description)
                )
            }
        },
        navigationAction = {
            TopAppBarHideBalancesNavigation(
                contentDescription = stringResource(id = R.string.hide_balances_content_description),
                iconVector =
                    ImageVector.vectorResource(
                        if (isHideBalances) {
                            R.drawable.ic_hide_balances_on
                        } else {
                            R.drawable.ic_hide_balances_off
                        }
                    ),
                onClick = onHideBalances,
                modifier = Modifier.testTag(CommonTag.HIDE_BALANCES_TOP_BAR_BUTTON)
            )
        },
    )
}

@Suppress("LongParameterList")
@Composable
private fun SendMainContent(
    balanceState: BalanceState,
    isHideBalances: Boolean,
    walletSnapshot: WalletSnapshot,
    exchangeRateState: ExchangeRateState,
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
    sendState: SendAddressBookState,
    modifier: Modifier = Modifier,
) {
    // For now, we merge [SendStage.Form] and [SendStage.Proposing] into one stage. We could eventually display a
    // loader if calling the Proposal API takes longer than expected

    SendForm(
        balanceState = balanceState,
        isHideBalances = isHideBalances,
        walletSnapshot = walletSnapshot,
        recipientAddressState = recipientAddressState,
        onRecipientAddressChange = onRecipientAddressChange,
        amountState = amountState,
        setAmountState = setAmountState,
        memoState = memoState,
        setMemoState = setMemoState,
        onCreateZecSend = onCreateZecSend,
        onQrScannerOpen = onQrScannerOpen,
        goBalances = goBalances,
        hasCameraFeature = hasCameraFeature,
        modifier = modifier,
        exchangeRateState = exchangeRateState,
        sendState = sendState
    )

    if (sendStage is SendStage.SendFailure) {
        SendFailure(
            reason = sendStage.error,
            onDone = onBack
        )
    }
}

// TODO [#217]: Need to handle changing of Locale after user input, but before submitting the button.
// TODO [#217]: https://github.com/Electric-Coin-Company/zashi-android/issues/217

// TODO [#1257]: Send.Form TextFields not persisted on a configuration change when the underlying ViewPager is on the
//  Balances page
// TODO [#1257]: https://github.com/Electric-Coin-Company/zashi-android/issues/1257
@Suppress("LongParameterList", "LongMethod")
@Composable
private fun SendForm(
    balanceState: BalanceState,
    isHideBalances: Boolean,
    walletSnapshot: WalletSnapshot,
    recipientAddressState: RecipientAddressState,
    exchangeRateState: ExchangeRateState,
    onRecipientAddressChange: (String) -> Unit,
    amountState: AmountState,
    setAmountState: (AmountState) -> Unit,
    memoState: MemoState,
    setMemoState: (MemoState) -> Unit,
    onCreateZecSend: (ZecSend) -> Unit,
    onQrScannerOpen: () -> Unit,
    goBalances: () -> Unit,
    hasCameraFeature: Boolean,
    sendState: SendAddressBookState,
    modifier: Modifier = Modifier,
) {
    val monetarySeparators = MonetarySeparators.current(Locale.getDefault())

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        BalanceWidget(
            balanceState = balanceState,
            isHideBalances = isHideBalances,
            isReferenceToBalances = true,
            onReferenceClick = goBalances
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TODO [#1256]: Consider Send.Form TextFields scrolling
        // TODO [#1256]: https://github.com/Electric-Coin-Company/zashi-android/issues/1256

        SendFormAddressTextField(
            hasCameraFeature = hasCameraFeature,
            onQrScannerOpen = onQrScannerOpen,
            recipientAddressState = recipientAddressState,
            setRecipientAddress = onRecipientAddressChange,
            sendAddressBookState = sendState
        )

        AnimatedVisibility(visible = sendState.isHintVisible) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                SendAddressBookHint(Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.size(ZcashTheme.dimens.spacingDefault))

        val isMemoFieldAvailable =
            recipientAddressState.address.isEmpty() ||
                recipientAddressState.type is AddressType.Invalid ||
                (
                    recipientAddressState.type is AddressType.Valid &&
                        recipientAddressState.type !is AddressType.Transparent &&
                        recipientAddressState.type !is AddressType.Tex
                )

        SendFormAmountTextField(
            amountState = amountState,
            imeAction =
                if (recipientAddressState.type == AddressType.Transparent || !isMemoFieldAvailable) {
                    ImeAction.Done
                } else {
                    ImeAction.Next
                },
            isTransparentOrTextRecipient =
                recipientAddressState.type?.let {
                    it == AddressType.Transparent || it == AddressType.Tex
                } ?: false,
            monetarySeparators = monetarySeparators,
            setAmountState = setAmountState,
            walletSnapshot = walletSnapshot,
            exchangeRateState = exchangeRateState
        )

        Spacer(Modifier.size(ZcashTheme.dimens.spacingDefault))

        SendFormMemoTextField(
            memoState = memoState,
            setMemoState = setMemoState,
            isMemoFieldAvailable = isMemoFieldAvailable,
        )

        Spacer(
            modifier =
                Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing3xl))

        SendButton(
            amountState = amountState,
            memoState = memoState,
            onCreateZecSend = onCreateZecSend,
            recipientAddressState = recipientAddressState,
            walletSnapshot = walletSnapshot,
        )
    }
}

@Suppress("CyclomaticComplexMethod")
@Composable
fun SendButton(
    amountState: AmountState,
    memoState: MemoState,
    onCreateZecSend: (ZecSend) -> Unit,
    recipientAddressState: RecipientAddressState,
    walletSnapshot: WalletSnapshot,
) {
    val scope = rememberCoroutineScope()
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

    ZashiButton(
        onClick = {
            scope.launch {
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
                    )

                when (zecSendValidation) {
                    is ZecSendExt.ZecSendValidation.Valid ->
                        onCreateZecSend(
                            zecSendValidation.zecSend.copy(
                                destination =
                                    when (recipientAddressState.type) {
                                        is AddressType.Invalid ->
                                            WalletAddress.Unified.new(recipientAddressState.address)

                                        AddressType.Shielded ->
                                            WalletAddress.Unified.new(recipientAddressState.address)

                                        AddressType.Tex ->
                                            WalletAddress.Tex.new(recipientAddressState.address)
                                        AddressType.Transparent ->
                                            WalletAddress.Transparent.new(recipientAddressState.address)
                                        AddressType.Unified ->
                                            WalletAddress.Unified.new(recipientAddressState.address)
                                        null -> WalletAddress.Unified.new(recipientAddressState.address)
                                    }
                            )
                        )

                    is ZecSendExt.ZecSendValidation.Invalid -> {
                        // We do not expect this validation to fail, so logging is enough here
                        // An error popup could be reasonable here as well
                        Twig.warn { "Send failed with: ${zecSendValidation.validationErrors}" }
                    }
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
}

@Suppress("LongMethod")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SendFormAddressTextField(
    sendAddressBookState: SendAddressBookState,
    hasCameraFeature: Boolean,
    onQrScannerOpen: () -> Unit,
    recipientAddressState: RecipientAddressState,
    setRecipientAddress: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()

    Column(
        modifier =
        Modifier
            // Animate error show/hide
            .animateContentSize()
            // Scroll TextField above ime keyboard
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        Text(
            text = stringResource(id = R.string.send_address_label),
            color = ZashiColors.Inputs.Default.label,
            style = ZashiTypography.textMd
        )

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

        ZashiTextField(
            singleLine = true,
            maxLines = 1,
            value = recipientAddressValue,
            onValueChange = {
                setRecipientAddress(it)
            },
            modifier =
            Modifier
                .fillMaxWidth()
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        scope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            error = recipientAddressError,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.send_address_hint),
                    style = ZashiTypography.textMd,
                    color = ZashiColors.Inputs.Default.text
                )
            },
            suffix =
                if (hasCameraFeature) {
                    {
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Image(
                                modifier =
                                    Modifier.clickable(
                                        onClick = sendAddressBookState.onButtonClick,
                                        role = Role.Button,
                                        indication = rememberRipple(radius = 4.dp),
                                        interactionSource = remember { MutableInteractionSource() }
                                    ),
                                painter = painterResource(sendAddressBookState.mode.icon),
                                contentDescription = "",
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Image(
                                modifier =
                                    Modifier.clickable(
                                        onClick = onQrScannerOpen,
                                        role = Role.Button,
                                        indication = rememberRipple(radius = 4.dp),
                                        interactionSource = remember { MutableInteractionSource() }
                                    ),
                                painter = painterResource(R.drawable.qr_code_icon),
                                contentDescription = stringResource(R.string.send_scan_content_description),
                            )

                            // Spacer(modifier = Modifier.width(6.dp))
                        }
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
                        focusManager.moveFocus(FocusDirection.Next)
                    }
                ),
        )
    }
}

@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SendFormAmountTextField(
    amountState: AmountState,
    imeAction: ImeAction,
    isTransparentOrTextRecipient: Boolean,
    monetarySeparators: MonetarySeparators,
    exchangeRateState: ExchangeRateState,
    setAmountState: (AmountState) -> Unit,
    walletSnapshot: WalletSnapshot,
) {
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    val zcashCurrency = ZcashCurrency.getLocalizedName(context)

    val amountError =
        when (amountState) {
            is AmountState.Invalid -> {
                if (amountState.value.isEmpty()) {
                    null
                } else {
                    stringResource(id = R.string.send_amount_invalid)
                }
            }

            is AmountState.Valid -> {
                if (walletSnapshot.spendableBalance() < amountState.zatoshi) {
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
        Text(
            text = stringResource(id = R.string.send_amount_label),
            color = ZashiColors.Inputs.Default.label,
            style = ZashiTypography.textMd
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Row {
            ZashiTextField(
                singleLine = true,
                maxLines = 1,
                value = amountState.value,
                onValueChange = { newValue ->
                    setAmountState(
                        AmountState.newFromZec(
                            context = context,
                            value = newValue,
                            monetarySeparators = monetarySeparators,
                            isTransparentOrTextRecipient = isTransparentOrTextRecipient,
                            fiatValue = amountState.fiatValue,
                            exchangeRateState = exchangeRateState
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                innerModifier = Modifier.testTag(SendTag.SEND_AMOUNT_FIELD),
                error = amountError,
                placeholder = {
                    Text(
                        text =
                            stringResource(
                                id = R.string.send_amount_hint,
                                zcashCurrency
                            ),
                        style = ZashiTypography.textMd,
                        color = ZashiColors.Inputs.Default.text
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
                prefix = {
                    Image(
                        painter = painterResource(R.drawable.ic_send_zashi),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(color = ZashiColors.Inputs.Default.text),
                    )
                }
            )

            if (exchangeRateState is ExchangeRateState.Data) {
                Spacer(modifier = Modifier.width(12.dp))
                Image(
                    modifier = Modifier.padding(top = 12.dp),
                    painter = painterResource(id = R.drawable.ic_send_convert),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
                )
                Spacer(modifier = Modifier.width(12.dp))
                ZashiTextField(
                    singleLine = true,
                    maxLines = 1,
                    isEnabled = !exchangeRateState.isStale,
                    value = amountState.fiatValue,
                    onValueChange = { newValue ->
                        setAmountState(
                            AmountState.newFromFiat(
                                context = context,
                                value = amountState.value,
                                monetarySeparators = monetarySeparators,
                                isTransparentOrTextRecipient = isTransparentOrTextRecipient,
                                fiatValue = newValue,
                                exchangeRateState = exchangeRateState
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text =
                                stringResource(
                                    id = R.string.send_usd_amount_hint
                                ),
                            style = ZashiTypography.textMd,
                            color = ZashiColors.Inputs.Default.text
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
                    prefix = {
                        Image(
                            painter = painterResource(R.drawable.ic_send_usd),
                            contentDescription = "",
                            colorFilter =
                                if (!exchangeRateState.isStale) {
                                    ColorFilter.tint(color = ZashiColors.Inputs.Default.text)
                                } else {
                                    ColorFilter.tint(color = ZashiColors.Inputs.Disabled.text)
                                }
                        )
                    }
                )
            }
        }
    }
}

@Suppress("LongMethod")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SendFormMemoTextField(
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
        Text(
            text = stringResource(id = R.string.send_memo_label),
            color = ZashiColors.Inputs.Default.label,
            style = ZashiTypography.textMd
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        ZashiTextField(
            minLines = if (isMemoFieldAvailable) 2 else 1,
            isEnabled = isMemoFieldAvailable,
            value =
                if (isMemoFieldAvailable) {
                    memoState.text
                } else {
                    ""
                },
            error = if (memoState is MemoState.Correct) null else "",
            onValueChange = {
                setMemoState(MemoState.new(it))
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default,
                    capitalization = KeyboardCapitalization.Sentences
                ),
            placeholder = {
                if (isMemoFieldAvailable) {
                    Text(
                        text = stringResource(id = R.string.send_memo_hint),
                        style = ZashiTypography.textMd,
                        color = ZashiColors.Inputs.Default.text
                    )
                } else {
                    Text(
                        text = stringResource(R.string.send_transparent_memo),
                        style = ZashiTypography.textSm,
                        color = ZashiColors.Utility.Gray.utilityGray700
                    )
                }
            },
            leadingIcon =
                if (isMemoFieldAvailable) {
                    null
                } else {
                    {
                        Image(
                            painter = painterResource(id = R.drawable.ic_confirmation_message_info),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(ZashiColors.Utility.Gray.utilityGray500)
                        )
                    }
                },
            colors =
                if (isMemoFieldAvailable) {
                    ZashiTextFieldDefaults.defaultColors()
                } else {
                    ZashiTextFieldDefaults.defaultColors(
                        disabledTextColor = ZashiColors.Inputs.Disabled.text,
                        disabledHintColor = ZashiColors.Inputs.Disabled.hint,
                        disabledBorderColor = Color.Unspecified,
                        disabledContainerColor = ZashiColors.Inputs.Disabled.bg,
                        disabledPlaceholderColor = ZashiColors.Inputs.Disabled.text,
                    )
                },
            modifier = Modifier.fillMaxWidth(),
        )

        if (isMemoFieldAvailable) {
            Text(
                text =
                    stringResource(
                        id = R.string.send_memo_bytes_counter,
                        Memo.MAX_MEMO_LENGTH_BYTES - memoState.byteSize,
                        Memo.MAX_MEMO_LENGTH_BYTES
                    ),
                color =
                    if (memoState is MemoState.Correct) {
                        ZashiColors.Inputs.Default.hint
                    } else {
                        ZashiColors.Inputs.Filled.required
                    },
                textAlign = TextAlign.End,
                style = ZashiTypography.textSm,
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
                Text(
                    text = stringResource(id = R.string.send_dialog_error_text),
                    color = ZcashTheme.colors.textPrimary,
                )

                Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                Text(
                    text = reason,
                    fontStyle = FontStyle.Italic,
                    color = ZcashTheme.colors.textPrimary,
                )
            }
        },
        confirmButtonText = stringResource(id = R.string.send_dialog_error_btn),
        onConfirmButtonClick = onDone,
        modifier = modifier
    )
}
