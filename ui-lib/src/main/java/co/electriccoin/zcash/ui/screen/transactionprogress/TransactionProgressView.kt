@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.GradientBgScaffold
import co.electriccoin.zcash.ui.design.component.OldZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun TransactionProgressView(state: TransactionProgressState) {
    GradientBgScaffold(
        startColor =
            when (state) {
                is SendingTransactionState -> ZashiColors.Surfaces.bgPrimary
                is SuccessfulTransactionState -> ZashiColors.Utility.SuccessGreen.utilitySuccess100
                is FailureTransactionState,
                is GrpcFailureTransactionState,
                is MultipleFailuresTransactionState -> ZashiColors.Utility.ErrorRed.utilityError100
            },
        endColor = ZashiColors.Surfaces.bgPrimary,
        topBar = { TopBar(state) },
        bottomBar = { BottomBar(state) },
        content = { Content(state, it) }
    )
}

@Composable
private fun TopBar(state: TransactionProgressState) {
    ZashiSmallTopAppBar(
        colors =
            ZcashTheme.colors.topAppBarColors.copyColors(
                containerColor = Color.Transparent
            ),
        navigationAction = {
            if (state is MultipleFailuresTransactionState && state.showBackButton) {
                ZashiTopAppBarBackNavigation(onBack = state.onBack)
            }
        }
    )
}

@Composable
private fun BottomBar(state: TransactionProgressState) {
    OldZashiBottomBar {
        when (state) {
            is SendingTransactionState -> {
                // none
            }

            is SuccessfulTransactionState -> SuccessfulTransactionBottomBar(state)
            is FailureTransactionState -> FailureTransactionBottomBar(state)
            is GrpcFailureTransactionState -> GrpcFailureTransactionBottomBar(state)
            is MultipleFailuresTransactionState -> MultipleFailuresTransactionBottomBar(state)
        }
    }
}

@Composable
private fun Content(
    state: TransactionProgressState,
    it: PaddingValues
) {
    when (state) {
        is SendingTransactionState ->
            SendingTransaction(
                state = state,
                modifier = Modifier.scaffoldPadding(it)
            )

        is SuccessfulTransactionState ->
            SuccessfulTransaction(
                state = state,
                modifier = Modifier.scaffoldPadding(it)
            )

        is FailureTransactionState ->
            FailureTransaction(
                state = state,
                modifier = Modifier.scaffoldPadding(it)
            )

        is GrpcFailureTransactionState ->
            GrpcFailureTransaction(
                modifier = Modifier.scaffoldPadding(it)
            )

        is MultipleFailuresTransactionState ->
            MultipleFailureTransaction(
                state = state,
                modifier = Modifier.scaffoldPadding(it)
            )
    }
}

@Composable
private fun SuccessfulTransactionBottomBar(state: SuccessfulTransactionState) {
    ZashiButton(
        state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_success_btn_close),
                onClick = state.onCloseClick
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
    )
}

@Composable
private fun ColumnScope.FailureTransactionBottomBar(state: FailureTransactionState) {
    ZashiButton(
        state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_failure_report_button),
                onClick = state.onReportClick
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl),
        colors = ZashiButtonDefaults.tertiaryColors()
    )
    Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))
    ZashiButton(
        state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_failure_close_button),
                onClick = state.onCloseClick
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
    )
}

@Composable
private fun GrpcFailureTransactionBottomBar(state: GrpcFailureTransactionState) {
    ZashiButton(
        state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_failure_grpc_close_button),
                onClick = state.onCloseClick
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
    )
}

@Composable
private fun ColumnScope.MultipleFailuresTransactionBottomBar(state: MultipleFailuresTransactionState) {
    ZashiButton(
        state =
            ButtonState(
                text = stringRes(R.string.send_confirmation_multiple_trx_failure_copy_button),
                onClick = state.onCopyClick
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
                onClick = state.onSupportClick,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ZashiDimensions.Spacing.spacing2xl),
    )
}

@Composable
private fun SendingTransaction(
    state: SendingTransactionState,
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
            val lottieRes: Int =
                if (isSystemInDarkTheme()) {
                    R.raw.send_confirmation_sending_dark_v1
                } else {
                    R.raw.send_confirmation_sending_v1
                }

            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
            val progress by animateLottieCompositionAsState(
                iterations = LottieConstants.IterateForever,
                composition = composition
            )

            LottieAnimation(
                modifier = Modifier.size(150.dp),
                composition = composition,
                progress = { progress },
                maintainOriginalImageBounds = true
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacing2xl))

            Text(
                fontWeight = FontWeight.SemiBold,
                style = ZashiTypography.header5,
                text = state.title.getValue(),
                color = ZashiColors.Text.textPrimary
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

            Text(
                fontWeight = FontWeight.Normal,
                style = ZashiTypography.textSm,
                text = state.text.getValue(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = ZashiColors.Text.textPrimary
            )
        }
    }
}

@Composable
private fun SuccessfulTransaction(
    state: SuccessfulTransactionState,
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
                text = state.title.getValue(),
                color = ZashiColors.Text.textPrimary
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

            Text(
                fontWeight = FontWeight.Normal,
                style = ZashiTypography.textSm,
                text = state.text.getValue(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = ZashiColors.Text.textPrimary
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

            ZashiButton(
                state =
                    ButtonState(
                        text = stringRes(R.string.send_confirmation_success_view_trx),
                        onClick = state.onViewTransactionClick,
                    ),
                modifier =
                    Modifier.wrapContentWidth(),
                colors = ZashiButtonDefaults.tertiaryColors()
            )
        }
    }
}

@Composable
private fun FailureTransaction(
    state: FailureTransactionState,
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
                text = state.title.getValue(),
                color = ZashiColors.Text.textPrimary
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

            Text(
                fontWeight = FontWeight.Normal,
                style = ZashiTypography.textSm,
                text = state.text.getValue(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = ZashiColors.Text.textPrimary
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))

            ZashiButton(
                state =
                    ButtonState(
                        text = stringRes(R.string.send_confirmation_failure_view_trx),
                        onClick = state.onViewTransactionClick,
                    ),
                modifier =
                    Modifier.wrapContentWidth(),
                colors = ZashiButtonDefaults.tertiaryColors()
            )
        }
    }
}

@Composable
private fun GrpcFailureTransaction(modifier: Modifier = Modifier) {
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
                color = ZashiColors.Text.textPrimary
            )

            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingLg))

            Text(
                fontWeight = FontWeight.Normal,
                style = ZashiTypography.textSm,
                text = stringResource(id = R.string.send_confirmation_failure_grpc_subtitle),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = ZashiColors.Text.textPrimary
            )
        }
    }
}

@Composable
private fun MultipleFailureTransaction(
    state: MultipleFailuresTransactionState,
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

        if (state.transactionIds.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.send_confirmation_multiple_trx_failure_ids_title),
                    fontWeight = FontWeight.Medium,
                    style = ZashiTypography.textSm,
                    color = ZashiColors.Inputs.Default.label
                )

                Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingSm))

                state.transactionIds.forEach { item ->
                    Text(
                        text = item,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = ZashiTypography.textMd,
                        color = ZashiColors.Inputs.Default.text,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    shape = RoundedCornerShape(ZashiDimensions.Radius.radiusLg),
                                    color = ZashiColors.Inputs.Default.bg
                                ).padding(
                                    horizontal = ZashiDimensions.Spacing.spacingLg,
                                    vertical = ZashiDimensions.Spacing.spacingMd
                                )
                    )
                    Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingMd))
                }
            }
        }
    }
}

private fun provideRandomResourceFrom(resources: List<Int>) = resources.random()

private const val TOP_BLANK_SPACE_RATIO = 0.35f

@PreviewScreens
@Composable
private fun SendingPreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                SendingTransactionState(
                    title = stringRes(R.string.send_confirmation_sending_title),
                    text = stringRes("Your tokens are being sent to <address>"),
                    onBack = {}
                )
        )
    }

@PreviewScreens
@Composable
private fun ShieldingPreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                SendingTransactionState(
                    title = stringRes(R.string.send_confirmation_sending_title_transparent),
                    text = stringRes(R.string.send_confirmation_sending_subtitle_transparent),
                    onBack = {}
                )
        )
    }

@PreviewScreens
@Composable
private fun SuccessPreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                SuccessfulTransactionState(
                    title = stringRes(R.string.send_confirmation_success_title),
                    text = stringRes("Your tokens were successfully sent to <address>"),
                    onViewTransactionClick = {},
                    onCloseClick = {},
                    onBack = {}
                )
        )
    }

@PreviewScreens
@Composable
private fun SuccessShieldingPreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                SuccessfulTransactionState(
                    title = stringRes(R.string.send_confirmation_success_title_transparent),
                    text = stringRes(R.string.send_confirmation_success_subtitle_transparent),
                    onViewTransactionClick = {},
                    onCloseClick = {},
                    onBack = {}
                )
        )
    }

@PreviewScreens
@Composable
private fun FailurePreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                FailureTransactionState(
                    onViewTransactionClick = {},
                    onCloseClick = {},
                    onReportClick = {},
                    onBack = {},
                    title = stringRes(R.string.send_confirmation_failure_title),
                    text = stringRes(R.string.send_confirmation_failure_subtitle),
                )
        )
    }

@PreviewScreens
@Composable
private fun FailureShieldingPreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                FailureTransactionState(
                    onViewTransactionClick = {},
                    onCloseClick = {},
                    onReportClick = {},
                    onBack = {},
                    title = stringRes(R.string.send_confirmation_failure_title_transparent),
                    text = stringRes(R.string.send_confirmation_failure_subtitle_transparent),
                )
        )
    }

@PreviewScreens
@Composable
private fun GrpcFailurePreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                GrpcFailureTransactionState(
                    onCloseClick = {},
                    onBack = {}
                )
        )
    }

@PreviewScreens
@Composable
private fun MultipleFailuresPreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                MultipleFailuresTransactionState(
                    transactionIds = listOf("id", "id", "id"),
                    onCopyClick = {},
                    onSupportClick = {},
                    onBack = {},
                    showBackButton = false
                )
        )
    }

@PreviewScreens
@Composable
private fun MultipleFailuresWithCloseButtonPreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                MultipleFailuresTransactionState(
                    transactionIds = listOf("id", "id", "id"),
                    onCopyClick = {},
                    onSupportClick = {},
                    onBack = {},
                    showBackButton = true
                )
        )
    }
