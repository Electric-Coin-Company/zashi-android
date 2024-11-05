@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.sendconfirmation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cash.z.ecc.android.sdk.fixture.WalletAddressFixture
import cash.z.ecc.android.sdk.model.FirstClassByteArray
import cash.z.ecc.android.sdk.model.TransactionSubmitResult
import cash.z.ecc.android.sdk.model.WalletAddress
import cash.z.ecc.android.sdk.model.ZecSend
import cash.z.ecc.sdk.extension.toZecStringFull
import cash.z.ecc.sdk.fixture.MemoFixture
import cash.z.ecc.sdk.fixture.ZatoshiFixture
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.BalanceWidgetBigLineOnly
import co.electriccoin.zcash.ui.common.extension.asZecAmountTriple
import co.electriccoin.zcash.ui.common.extension.totalAmount
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.design.component.AppAlertDialog
import co.electriccoin.zcash.ui.design.component.BlankBgScaffold
import co.electriccoin.zcash.ui.design.component.Body
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.Small
import co.electriccoin.zcash.ui.design.component.SmallTopAppBar
import co.electriccoin.zcash.ui.design.component.StyledBalance
import co.electriccoin.zcash.ui.design.component.StyledBalanceDefaults
import co.electriccoin.zcash.ui.design.component.TextFieldState
import co.electriccoin.zcash.ui.design.component.TopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.component.ZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTextField
import co.electriccoin.zcash.ui.design.component.ZashiTextFieldDefaults
import co.electriccoin.zcash.ui.design.component.ZecAmountTriple
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.fixture.ObserveFiatCurrencyResultFixture
import co.electriccoin.zcash.ui.screen.exchangerate.widget.StyledExchangeLabel
import co.electriccoin.zcash.ui.screen.sendconfirmation.SendConfirmationTag
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationStage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.runBlocking

// TODO [#1260]: Cover Send screens UI with tests
// TODO [#1260]: https://github.com/Electric-Coin-Company/zashi-android/issues/1260

@Composable
@Suppress("LongParameterList")
fun SendConfirmation(
    onBack: () -> Unit,
    onConfirmation: () -> Unit,
    onContactSupport: (SendConfirmationStage, String?) -> Unit,
    onMultipleTrxFailureIdsCopy: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    stage: SendConfirmationStage,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    zecSend: ZecSend,
    contactName: String?,
    exchangeRate: ExchangeRateState,
) {
    BlankBgScaffold(
        topBar = {
            SendConfirmationTopAppBar(
                onBack = onBack,
                stage = stage,
                subTitleState = topAppBarSubTitleState,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            SendConfirmationBottomBarContent(
                onClose = onBack,
                onConfirmation = onConfirmation,
                onReport = onContactSupport,
                onCopyTrxIds = {
                    onMultipleTrxFailureIdsCopy(
                        submissionResults.joinToString(separator = ", ") { it.txIdString() }
                    )
                },
                stage = stage,
            )
        }
    ) { paddingValues ->
        SendConfirmationMainContent(
            onBack = onBack,
            onContactSupport = onContactSupport,
            stage = stage,
            submissionResults = submissionResults,
            zecSend = zecSend,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .scaffoldPadding(paddingValues),
            exchangeRate = exchangeRate,
            contactName = contactName
        )
    }
}

@Composable
private fun SendConfirmationTopAppBar(
    onBack: () -> Unit,
    stage: SendConfirmationStage,
    subTitleState: TopAppBarSubTitleState
) {
    val subTitle =
        when (subTitleState) {
            TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
            TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
            TopAppBarSubTitleState.None -> null
        }
    when (stage) {
        SendConfirmationStage.Prepared, -> {
            ZashiSmallTopAppBar(
                title = stringResource(id = R.string.send_stage_confirmation_title),
                subtitle = subTitle,
            )
        }
        SendConfirmationStage.MultipleTrxFailureReported -> {
            SmallTopAppBar(
                subTitle = subTitle,
                navigationAction = {
                    TopAppBarBackNavigation(
                        backContentDescriptionText = stringResource(R.string.close_navigation_content_description),
                        onBack = onBack,
                        painter = painterResource(co.electriccoin.zcash.ui.design.R.drawable.ic_close)
                    )
                },
            )
        }
        SendConfirmationStage.MultipleTrxFailure,
        SendConfirmationStage.Sending,
        SendConfirmationStage.Success,
        is SendConfirmationStage.Failure,
        is SendConfirmationStage.FailureGrpc -> {
            SmallTopAppBar(subTitle = subTitle)
        }
    }
}

@Composable
@Suppress("LongParameterList")
    private fun SendConfirmationMainContent(
    contactName: String?,
    stage: SendConfirmationStage,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    zecSend: ZecSend,
    onBack: () -> Unit,
    onContactSupport: (SendConfirmationStage, String?) -> Unit,
    exchangeRate: ExchangeRateState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (stage) {
            SendConfirmationStage.Prepared -> {
                SendConfirmationContent(
                    contactName = contactName,
                    zecSend = zecSend,
                    exchangeRate = exchangeRate
                )
            }
            SendConfirmationStage.Sending -> {
                SendingContent(destination = zecSend.destination)
            }
            SendConfirmationStage.Success -> {
                SuccessContent(destination = zecSend.destination)
            }
            is SendConfirmationStage.Failure -> {
                SendFailure(
                    onDone = onBack,
                    onReport = { status ->
                        // Using [SendConfirmationStage.Confirmation] to dismiss the error dialog
                        onContactSupport(SendConfirmationStage.Prepared, status.stackTrace)
                    },
                    stage = stage,
                )
            }
            is SendConfirmationStage.FailureGrpc -> {
                SendFailureGrpc(onDone = onBack)
            }
            is SendConfirmationStage.MultipleTrxFailure,
            SendConfirmationStage.MultipleTrxFailureReported -> {
                MultipleSubmissionFailure(
                    onContactSupport = {
                        onContactSupport(SendConfirmationStage.MultipleTrxFailureReported, null)
                    },
                    submissionResults = submissionResults,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun SendingContent(
    destination: WalletAddress,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        // TODO: Change this lottie resource once we have it
        val lottieRes: Int = if (isSystemInDarkTheme()) {
            co.electriccoin.zcash.ui.design.R.raw.lottie_loading_white
        } else {
            co.electriccoin.zcash.ui.design.R.raw.lottie_loading
        }

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
        val progress by animateLottieCompositionAsState(
            iterations = LottieConstants.IterateForever,
            composition = composition
        )

        LottieAnimation(
            modifier = modifier.size(200.dp),
            composition = composition,
            progress = { progress },
            maintainOriginalImageBounds = true
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        Text(
            fontWeight = FontWeight.SemiBold,
            style = ZashiTypography.header5,
            text = stringResource(id = R.string.send_confirmation_sending_title),
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        Text(
            fontWeight = FontWeight.Normal,
            style = ZashiTypography.textSm,
            text = stringResource(id = R.string.send_confirmation_sending_subtitle, destination.address)
        )
    }
}

@Composable
private fun SuccessContent(
    destination: WalletAddress,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = "SUCCESS")
        Text(text = destination.address)
    }
}

@Composable
private fun SendFailure(
    onDone: () -> Unit,
    onReport: (SendConfirmationStage.Failure) -> Unit,
    stage: SendConfirmationStage.Failure,
) {
    // TODO [#1276]: Once we ensure that the reason contains a localized message, we can leverage it for the UI prompt
    // TODO [#1276]: Consider adding support for a specific exception in AppAlertDialog
    // TODO [#1276]: https://github.com/Electric-Coin-Company/zashi-android/issues/1276

    AppAlertDialog(
        title = stringResource(id = R.string.send_confirmation_dialog_error_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.send_confirmation_dialog_error_text),
                    color = ZcashTheme.colors.textPrimary,
                )

                if (stage.error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

                    Text(
                        text = stage.error,
                        fontStyle = FontStyle.Italic,
                        color = ZcashTheme.colors.textPrimary,
                    )
                }
            }
        },
        confirmButtonText = stringResource(id = R.string.send_confirmation_dialog_error_ok_btn),
        onConfirmButtonClick = onDone,
        dismissButtonText = stringResource(id = R.string.send_confirmation_dialog_error_report_btn),
        onDismissButtonClick = { onReport(stage) },
    )
}

@Composable
private fun SendFailureGrpc(onDone: () -> Unit) {
    AppAlertDialog(
        title = stringResource(id = R.string.send_confirmation_dialog_error_grpc_title),
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.send_confirmation_dialog_error_grpc_text),
                    color = ZcashTheme.colors.textPrimary,
                )
            }
        },
        confirmButtonText = stringResource(id = R.string.send_confirmation_dialog_error_grpc_btn),
        onConfirmButtonClick = onDone
    )
}

@Composable
fun MultipleSubmissionFailure(
    onContactSupport: () -> Unit,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_zashi_logo_sign_warn),
            colorFilter = ColorFilter.tint(color = ZcashTheme.colors.secondaryColor),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))

        Body(
            text = stringResource(id = R.string.send_confirmation_multiple_error_text_1),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Body(
            text = stringResource(id = R.string.send_confirmation_multiple_error_text_2),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingBig))

        if (submissionResults.isNotEmpty()) {
            TransactionSubmitResultWidget(submissionResults)
        }

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingDefault))

        Spacer(modifier = Modifier.weight(1f, true))

        ZashiButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onContactSupport,
            text = stringResource(id = R.string.send_confirmation_multiple_error_btn)
        )

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingHuge))
    }
}

@Composable
fun TransactionSubmitResultWidget(
    submissionResults: ImmutableList<TransactionSubmitResult>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Small(text = stringResource(id = R.string.send_confirmation_multiple_error_trx_title))

        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        submissionResults.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Small(
                    text =
                    stringResource(
                        id = R.string.send_confirmation_multiple_error_trx_item,
                        index + 1
                    ),
                    modifier = Modifier.wrapContentSize()
                )
                Spacer(modifier = Modifier.width(ZcashTheme.dimens.spacingTiny))
                Small(text = item.txIdString())
            }
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun SendConfirmationContent(
    contactName: String?,
    zecSend: ZecSend,
    exchangeRate: ExchangeRateState,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingSmall))

        Text(
            stringResource(R.string.send_confirmation_amount),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textPrimary
        )

        BalanceWidgetBigLineOnly(
            parts = zecSend.totalAmount().toZecStringFull().asZecAmountTriple(),
            // We don't hide any balance in confirmation screen
            isHideBalances = false
        )

        StyledExchangeLabel(
            zatoshi = zecSend.amount,
            state = exchangeRate,
            isHideBalances = false,
            style = ZashiTypography.textMd.copy(fontWeight = FontWeight.SemiBold),
            textColor = ZashiColors.Text.textPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            stringResource(R.string.send_confirmation_address),
            style = ZashiTypography.textSm,
            color = ZashiColors.Text.textTertiary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (contactName != null) {
            Text(
                contactName,
                style = ZashiTypography.textSm,
                color = ZashiColors.Inputs.Filled.label,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            zecSend.destination.address,
            style = ZashiTypography.textXs,
            color = ZashiColors.Text.textPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.send_confirmation_amount_item),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary
            )

            StyledBalance(
                // The not-null assertion operator is necessary here even if we check its nullability before
                // due to: "Smart cast to 'Proposal' is impossible, because 'zecSend.proposal' is a public API
                // property declared in different module. See more details on the Kotlin forum.
                balanceParts = zecSend.amount.toZecStringFull().asZecAmountTriple(),
                // We don't hide any balance in confirmation screen
                isHideBalances = false,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        leastSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.send_confirmation_fee),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary
            )

            StyledBalance(
                // The not-null assertion operator is necessary here even if we check its nullability before
                // due to: "Smart cast to 'Proposal' is impossible, because 'zecSend.proposal' is a public API
                // property declared in different module. See more details on the Kotlin forum.
                balanceParts =
                    zecSend.proposal?.totalFeeRequired()?.toZecStringFull()?.asZecAmountTriple()
                        ?: ZecAmountTriple("main", "prefix"),
                // We don't hide any balance in confirmation screen
                isHideBalances = false,
                textStyle =
                    StyledBalanceDefaults.textStyles(
                        mostSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.first,
                        leastSignificantPart = ZcashTheme.extendedTypography.balanceSingleStyles.second
                    ),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        val isMemoFieldAvailable =
            zecSend.destination !is WalletAddress.Transparent &&
                zecSend.destination !is WalletAddress.Tex

        if (zecSend.memo.value.isNotEmpty() || !isMemoFieldAvailable) {
            Text(
                stringResource(R.string.send_confirmation_memo),
                style = ZashiTypography.textSm,
                color = ZashiColors.Text.textTertiary
            )

            Spacer(modifier = Modifier.height(8.dp))

            ZashiTextField(
                state = TextFieldState(value = stringRes(zecSend.memo.value), isEnabled = false) {},
                modifier =
                    Modifier
                        .fillMaxWidth(),
                colors =
                    ZashiTextFieldDefaults.defaultColors(
                        disabledTextColor = ZashiColors.Inputs.Filled.text,
                        disabledHintColor = ZashiColors.Inputs.Disabled.hint,
                        disabledBorderColor = Color.Unspecified,
                        disabledContainerColor = ZashiColors.Inputs.Disabled.bg,
                        disabledPlaceholderColor = ZashiColors.Inputs.Disabled.text,
                    ),
                placeholder =
                    if (isMemoFieldAvailable) {
                        null
                    } else {
                        {
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
                    }
            )

            Spacer(modifier = Modifier.height(ZcashTheme.dimens.spacingUpLarge))
        }
    }
}

@Composable
private fun SendConfirmationBottomBarContent(
    onClose: () -> Unit,
    onConfirmation: () -> Unit,
    onReport: (SendConfirmationStage, String?) -> Unit,
    onCopyTrxIds: () -> Unit,
    stage: SendConfirmationStage,
    modifier: Modifier = Modifier,
) {
    ZashiBottomBar(modifier = modifier) {
        when (stage) {
            SendConfirmationStage.Prepared -> {
                SendConfirmationBottomBar(
                    onConfirmation = onConfirmation,
                    onBack = onClose,
                )
            }
            is SendConfirmationStage.Failure -> {
                SendFailureBottomBar(
                    onClose = onClose,
                    onReport = { onReport(SendConfirmationStage.Prepared, stage.stackTrace) },
                )
            }
            SendConfirmationStage.FailureGrpc -> {
                SendGrpcFailureBottomBar(
                    onClose = onClose,
                )
            }
            SendConfirmationStage.MultipleTrxFailure,
            SendConfirmationStage.MultipleTrxFailureReported -> {
                SendMultipleTrxFailureBottomBar(
                    onCopyTrxIds = onCopyTrxIds,
                    onContactSupport = onReport,
                )
            }
            SendConfirmationStage.Sending -> { /* No bottom bar in this stage */ }
            SendConfirmationStage.Success -> {
                SendSuccessBottomBar(
                    onClose = onClose,
                )
            }
        }
    }
}

@Composable
fun SendConfirmationBottomBar(
    onConfirmation: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ZashiButton(
            state =
                ButtonState(
                    text = stringRes(R.string.send_confirmation_send_button),
                    onClick = onConfirmation
                ),
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
                .testTag(SendConfirmationTag.SEND_CONFIRMATION_SEND_BUTTON)
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

        ZashiButton(
            state =
                ButtonState(
                    text = stringRes(R.string.send_confirmation_back_button),
                    onClick = onBack,
                ),
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
                .testTag(SendConfirmationTag.SEND_CONFIRMATION_BACK_BUTTON),
            colors = ZashiButtonDefaults.tertiaryColors()
        )
    }
}

@Composable
fun SendMultipleTrxFailureBottomBar(
    onCopyTrxIds: () -> Unit,
    onContactSupport: (SendConfirmationStage, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ZashiButton(
            state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_multiple_trx_failure_copy_button),
                onClick = onCopyTrxIds
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl),
            colors = ZashiButtonDefaults.tertiaryColors()
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        ZashiButton(
            state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_multiple_trx_failure_report_button),
                onClick = { onContactSupport(SendConfirmationStage.MultipleTrxFailureReported, null) },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl),
        )
    }
}

@Composable
fun SendSuccessBottomBar(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ZashiButton(
            state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_success_btn_close),
                onClick = onClose
            ),
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
        )
    }
}

@Composable
fun SendFailureBottomBar(
    onClose: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ZashiButton(
            state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_failure_close_button),
                onClick = onClose
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

        ZashiButton(
            state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_failure_report_button),
                onClick = onReport
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl),
            colors = ZashiButtonDefaults.tertiaryColors()
        )
    }
}

@Composable
fun SendGrpcFailureBottomBar(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        ZashiButton(
            state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_grpc_failure_close_button),
                onClick = onClose
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
        )
    }
}

@PreviewScreens
@Composable
private fun SendConfirmationPreview() {
    ZcashTheme {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
            ZecSend(
                destination = runBlocking { WalletAddressFixture.sapling() },
                amount = ZatoshiFixture.new(),
                memo = MemoFixture.new(),
                proposal = null,
            ),
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onBack = {},
            stage = SendConfirmationStage.Prepared,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _, _ -> },
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList(),
            exchangeRate = ObserveFiatCurrencyResultFixture.new(),
            contactName = "Mom"
        )
    }
}

@PreviewScreens
@Composable
private fun SendingPreview() {
    ZcashTheme {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
            ZecSend(
                destination = runBlocking { WalletAddressFixture.sapling() },
                amount = ZatoshiFixture.new(),
                memo = MemoFixture.new(),
                proposal = null,
            ),
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onBack = {},
            stage = SendConfirmationStage.Sending,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _, _ -> },
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList(),
            exchangeRate = ObserveFiatCurrencyResultFixture.new(),
            contactName = "Mom"
        )
    }
}

@PreviewScreens
@Composable
private fun PreviewSendConfirmationFailure() {
    ZcashTheme {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
            ZecSend(
                destination = runBlocking { WalletAddressFixture.sapling() },
                amount = ZatoshiFixture.new(),
                memo = MemoFixture.new(),
                proposal = null,
            ),
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onBack = {},
            stage = SendConfirmationStage.Failure(
                "The transaction has not been successfully created...",
                "Failed stackTrace..."
            ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _, _ -> },
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList(),
            exchangeRate = ObserveFiatCurrencyResultFixture.new(),
            contactName = "Mom"
        )
    }
}

@PreviewScreens
@Composable
private fun PreviewSendConfirmationGrpcFailure() {
    ZcashTheme {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
            ZecSend(
                destination = runBlocking { WalletAddressFixture.sapling() },
                amount = ZatoshiFixture.new(),
                memo = MemoFixture.new(),
                proposal = null,
            ),
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onBack = {},
            stage = SendConfirmationStage.FailureGrpc,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _, _ -> },
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList(),
            exchangeRate = ObserveFiatCurrencyResultFixture.new(),
            contactName = "Mom"
        )
    }
}

@PreviewScreens
@Composable
private fun SendMultipleErrorPreview() {
    ZcashTheme {
        SendConfirmation(
            snackbarHostState = SnackbarHostState(),
            zecSend =
            ZecSend(
                destination = runBlocking { WalletAddressFixture.sapling() },
                amount = ZatoshiFixture.new(),
                memo = MemoFixture.new(),
                proposal = null,
            ),
            onConfirmation = {},
            onBack = {},
            onMultipleTrxFailureIdsCopy = {},
            stage = SendConfirmationStage.MultipleTrxFailure,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _, _ -> },
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList(),
            exchangeRate = ObserveFiatCurrencyResultFixture.new(),
            contactName = "Romek"
        )
    }
}

@PreviewScreens
@Composable
private fun SendMultipleTransactionFailurePreview() {
    ZcashTheme {
        @Suppress("MagicNumber")
        MultipleSubmissionFailure(
            onContactSupport = {},
            // Rework this into a test fixture
            submissionResults =
            persistentListOf(
                TransactionSubmitResult.Failure(
                    FirstClassByteArray("test_transaction_id_1".toByteArray()),
                    true,
                    123,
                    "test transaction id failure"
                ),
                TransactionSubmitResult.NotAttempted(
                    FirstClassByteArray("test_transaction_id_2".toByteArray())
                ),
                TransactionSubmitResult.NotAttempted(
                    FirstClassByteArray("test_transaction_id_3".toByteArray())
                )
            )
        )
    }
}
