@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.sendconfirmation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.GradientBgScaffold
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
import co.electriccoin.zcash.ui.screen.send.ext.abbreviated
import co.electriccoin.zcash.ui.screen.sendconfirmation.SendConfirmationTag
import co.electriccoin.zcash.ui.screen.sendconfirmation.model.SendConfirmationStage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.runBlocking

// TODO [#1260]: Cover Send screens UI with tests
// TODO [#1260]: https://github.com/Electric-Coin-Company/zashi-android/issues/1260

@Composable
@Suppress("LongParameterList")
fun SendConfirmation(
    onBack: () -> Unit,
    onConfirmation: () -> Unit,
    onContactSupport: (SendConfirmationStage) -> Unit,
    onMultipleTrxFailureIdsCopy: (String) -> Unit,
    onViewTransactions: () -> Unit,
    snackbarHostState: SnackbarHostState,
    stage: SendConfirmationStage,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    topAppBarSubTitleState: TopAppBarSubTitleState,
    zecSend: ZecSend,
    contactName: String?,
    exchangeRate: ExchangeRateState,
) {
    val gradientColors =
        Pair(
            when (stage) {
                SendConfirmationStage.Prepared,
                SendConfirmationStage.Sending -> ZashiColors.Surfaces.bgPrimary
                SendConfirmationStage.Success -> ZashiColors.Utility.SuccessGreen.utilitySuccess100
                is SendConfirmationStage.Failure,
                is SendConfirmationStage.FailureGrpc,
                SendConfirmationStage.MultipleTrxFailure,
                SendConfirmationStage.MultipleTrxFailureReported -> ZashiColors.Utility.ErrorRed.utilityError100
            },
            ZashiColors.Surfaces.bgPrimary
        )

    GradientBgScaffold(
        startColor = gradientColors.first,
        endColor = gradientColors.second,
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
            contactName = contactName,
            exchangeRate = exchangeRate,
            onViewTransactions = onViewTransactions,
            stage = stage,
            submissionResults = submissionResults,
            zecSend = zecSend,
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .scaffoldPadding(paddingValues),
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
        SendConfirmationStage.Sending -> {
            ZashiSmallTopAppBar(subtitle = subTitle)
        }
        SendConfirmationStage.Success -> {
            ZashiSmallTopAppBar(
                subtitle = subTitle,
                colors =
                    ZcashTheme.colors.topAppBarColors.copyColors(
                        containerColor = Color.Transparent
                    ),
            )
        }
        SendConfirmationStage.MultipleTrxFailureReported -> {
            ZashiSmallTopAppBar(
                subtitle = subTitle,
                navigationAction = {
                    TopAppBarBackNavigation(
                        backContentDescriptionText = stringResource(R.string.close_navigation_content_description),
                        onBack = onBack,
                        painter = painterResource(co.electriccoin.zcash.ui.design.R.drawable.ic_close)
                    )
                },
                colors =
                    ZcashTheme.colors.topAppBarColors.copyColors(
                        containerColor = Color.Transparent
                    ),
            )
        }
        SendConfirmationStage.MultipleTrxFailure,
        is SendConfirmationStage.Failure,
        is SendConfirmationStage.FailureGrpc -> {
            ZashiSmallTopAppBar(
                subtitle = subTitle,
                colors =
                    ZcashTheme.colors.topAppBarColors.copyColors(
                        containerColor = Color.Transparent
                    ),
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun SendConfirmationMainContent(
    contactName: String?,
    exchangeRate: ExchangeRateState,
    onViewTransactions: () -> Unit,
    stage: SendConfirmationStage,
    submissionResults: ImmutableList<TransactionSubmitResult>,
    zecSend: ZecSend,
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
                SuccessContent(
                    destination = zecSend.destination,
                    onViewTransactions = onViewTransactions
                )
            }
            is SendConfirmationStage.Failure -> {
                SendFailure(onViewTransactions = onViewTransactions)
            }
            is SendConfirmationStage.FailureGrpc -> {
                SendGrpcFailure()
            }
            is SendConfirmationStage.MultipleTrxFailure,
            SendConfirmationStage.MultipleTrxFailureReported -> {
                MultipleSubmissionFailure(submissionResults = submissionResults)
            }
        }
    }
}

private const val TOP_BLANK_SPACE_RATIO = 0.2f

@Composable
private fun SendingContent(
    destination: WalletAddress,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (content, spaceTop) = createRefs()

        Spacer(
            modifier =
                Modifier.constrainAs(spaceTop) {
                    height = Dimension.percent(TOP_BLANK_SPACE_RATIO)
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(content.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .constrainAs(content) {
                        top.linkTo(spaceTop.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
        ) {
            // TODO [#1667]: Change lottie animation once we have it
            // TODO [#1667]: https://github.com/Electric-Coin-Company/zashi-android/issues/1667
            val lottieRes: Int =
                if (isSystemInDarkTheme()) {
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
                modifier = Modifier.size(200.dp),
                composition = composition,
                progress = { progress },
                maintainOriginalImageBounds = true
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing2xl))

            Text(
                fontWeight = FontWeight.SemiBold,
                style = ZashiTypography.header5,
                text = stringResource(id = R.string.send_confirmation_sending_title),
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

            Text(
                fontWeight = FontWeight.Normal,
                style = ZashiTypography.textSm,
                text = stringResource(id = R.string.send_confirmation_sending_subtitle, destination.abbreviated()),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun provideRandomResourceFrom(resources: List<Int>) = resources.random()

@Composable
private fun SuccessContent(
    destination: WalletAddress,
    onViewTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (content, spaceTop) = createRefs()

        Spacer(
            modifier =
                Modifier.constrainAs(spaceTop) {
                    height = Dimension.percent(TOP_BLANK_SPACE_RATIO)
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(content.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .constrainAs(content) {
                        top.linkTo(spaceTop.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
        ) {
            Image(
                painter =
                    painterResource(
                        id =
                            provideRandomResourceFrom(
                                listOf(
                                    R.drawable.ic_fist_punch,
                                    R.drawable.ic_face_star
                                )
                            )
                    ),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing2xl))

            Text(
                fontWeight = FontWeight.SemiBold,
                style = ZashiTypography.header5,
                text = stringResource(id = R.string.send_confirmation_success_title),
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

            Text(
                fontWeight = FontWeight.Normal,
                style = ZashiTypography.textSm,
                text = stringResource(id = R.string.send_confirmation_success_subtitle, destination.abbreviated()),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

            ZashiButton(
                state =
                    ButtonState(
                        text = stringRes(R.string.send_confirmation_success_view_trx),
                        onClick = onViewTransactions,
                    ),
                modifier =
                    Modifier.wrapContentWidth(),
                colors = ZashiButtonDefaults.tertiaryColors()
            )
        }
    }
}

@Composable
private fun SendFailure(
    onViewTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (content, spaceTop) = createRefs()

        Spacer(
            modifier =
                Modifier.constrainAs(spaceTop) {
                    height = Dimension.percent(TOP_BLANK_SPACE_RATIO)
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(content.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .constrainAs(content) {
                        top.linkTo(spaceTop.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
        ) {
            Image(
                painter =
                    painterResource(
                        id =
                            provideRandomResourceFrom(
                                listOf(
                                    R.drawable.ic_skull,
                                    R.drawable.ic_cloud_eyes,
                                    R.drawable.ic_face_horns
                                )
                            )
                    ),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing2xl))

            Text(
                fontWeight = FontWeight.SemiBold,
                style = ZashiTypography.header5,
                text = stringResource(id = R.string.send_confirmation_failure_title),
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

            Text(
                fontWeight = FontWeight.Normal,
                style = ZashiTypography.textSm,
                text = stringResource(id = R.string.send_confirmation_failure_subtitle),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

            ZashiButton(
                state =
                    ButtonState(
                        text = stringRes(R.string.send_confirmation_failure_view_trx),
                        onClick = onViewTransactions,
                    ),
                modifier =
                    Modifier.wrapContentWidth(),
                colors = ZashiButtonDefaults.tertiaryColors()
            )
        }
    }
}

@Composable
private fun SendGrpcFailure(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (content, spaceTop) = createRefs()

        Spacer(
            modifier =
                Modifier.constrainAs(spaceTop) {
                    height = Dimension.percent(TOP_BLANK_SPACE_RATIO)
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(content.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .constrainAs(content) {
                        top.linkTo(spaceTop.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
        ) {
            Image(
                painter =
                    painterResource(
                        provideRandomResourceFrom(
                            listOf(
                                R.drawable.ic_frame,
                                R.drawable.ic_phone
                            )
                        )
                    ),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing2xl))

            Text(
                fontWeight = FontWeight.SemiBold,
                style = ZashiTypography.header5,
                text = stringResource(id = R.string.send_confirmation_failure_grpc_title),
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

            Text(
                fontWeight = FontWeight.Normal,
                style = ZashiTypography.textSm,
                text = stringResource(id = R.string.send_confirmation_failure_grpc_subtitle),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MultipleSubmissionFailure(
    submissionResults: ImmutableList<TransactionSubmitResult>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_multi_trx_send_failed),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

        Text(
            fontWeight = FontWeight.SemiBold,
            style = ZashiTypography.header6,
            text = stringResource(id = R.string.send_confirmation_multiple_trx_failure_title),
            color = ZashiColors.Text.textPrimary
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

        Text(
            style = ZashiTypography.textSm,
            text = stringResource(id = R.string.send_confirmation_multiple_trx_failure_text_1),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = ZashiColors.Text.textPrimary
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

        Text(
            style = ZashiTypography.textSm,
            text = stringResource(id = R.string.send_confirmation_multiple_trx_failure_text_2),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = ZashiColors.Text.textPrimary
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing4xl))

        if (submissionResults.isNotEmpty()) {
            TransactionSubmitResultWidget(submissionResults)
        }
    }
}

@Composable
fun TransactionSubmitResultWidget(
    submissionResults: ImmutableList<TransactionSubmitResult>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.send_confirmation_multiple_trx_failure_ids_title),
            fontWeight = FontWeight.Medium,
            style = ZashiTypography.textSm,
            color = ZashiColors.Inputs.Default.label
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        submissionResults.forEach { item ->
            Text(
                text = item.txIdString(),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = ZashiTypography.textMd,
                color = ZashiColors.Inputs.Default.text,
                modifier =
                    Modifier
                        .background(
                            shape = RoundedCornerShape(ZashiDimensions.Radius.radiusIg),
                            color = ZashiColors.Inputs.Default.bg
                        )
                        .padding(
                            horizontal = ZashiDimensions.Spacing.spacingLg,
                            vertical = ZashiDimensions.Spacing.spacingMd
                        )
            )
            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingMd))
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
    onReport: (SendConfirmationStage) -> Unit,
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
                    onReport = { onReport(stage) },
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
    onContactSupport: (SendConfirmationStage) -> Unit,
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ZashiDimensions.Spacing.spacing2xl),
            colors = ZashiButtonDefaults.tertiaryColors()
        )

        Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

        ZashiButton(
            state =
                ButtonState(
                    text = stringRes(R.string.send_confirmation_multiple_trx_failure_report_button),
                    onClick = { onContactSupport(SendConfirmationStage.MultipleTrxFailureReported) },
                ),
            modifier =
                Modifier
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
            modifier =
                Modifier
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
            modifier =
                Modifier
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
                    text = stringRes(R.string.send_confirmation_failure_grpc_close_button),
                    onClick = onClose
                ),
            modifier =
                Modifier
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
            onViewTransactions = {},
            onBack = {},
            stage = SendConfirmationStage.Prepared,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _ -> },
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
            onBack = {},
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onViewTransactions = {},
            stage = SendConfirmationStage.Sending,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _ -> },
            submissionResults = emptyList<TransactionSubmitResult>().toImmutableList(),
            exchangeRate = ObserveFiatCurrencyResultFixture.new(),
            contactName = "Mom"
        )
    }
}

@PreviewScreens
@Composable
private fun SuccessPreview() {
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
            onBack = {},
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onViewTransactions = {},
            stage = SendConfirmationStage.Success,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _ -> },
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
            onBack = {},
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onViewTransactions = {},
            stage =
                SendConfirmationStage.Failure(
                    "The transaction has not been successfully created...",
                    "Failed stackTrace..."
                ),
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _ -> },
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
            onBack = {},
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onViewTransactions = {},
            stage = SendConfirmationStage.FailureGrpc,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _ -> },
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
            onBack = {},
            onConfirmation = {},
            onMultipleTrxFailureIdsCopy = {},
            onViewTransactions = {},
            stage = SendConfirmationStage.MultipleTrxFailure,
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            onContactSupport = { _ -> },
            submissionResults =
                listOf(
                    TransactionSubmitResult.Success(FirstClassByteArray("test_transaction_id_1".toByteArray())),
                    TransactionSubmitResult.Failure(
                        FirstClassByteArray("test_transaction_id_2".toByteArray()),
                        true,
                        Int.MIN_VALUE,
                        "test transaction id failure"
                    ),
                    TransactionSubmitResult.NotAttempted(
                        FirstClassByteArray("test_transaction_id_3".toByteArray())
                    ),
                    TransactionSubmitResult.NotAttempted(
                        FirstClassByteArray("test_transaction_id_4".toByteArray())
                    )
                ).toImmutableList(),
            exchangeRate = ObserveFiatCurrencyResultFixture.new(),
            contactName = "Romek"
        )
    }
}
