@file:Suppress("TooManyFunctions")

package co.electriccoin.zcash.ui.screen.transactionprogress

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ButtonStyle
import co.electriccoin.zcash.ui.design.component.GradientBgScaffold
import co.electriccoin.zcash.ui.design.component.OldZashiBottomBar
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.ImageResource
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.imageRes
import co.electriccoin.zcash.ui.design.util.loadingImageRes
import co.electriccoin.zcash.ui.design.util.orDark
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgressState.Background.PENDING
import co.electriccoin.zcash.ui.screen.transactionprogress.TransactionProgressState.Background.SUCCESS
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun TransactionProgressView(state: TransactionProgressState) {
    GradientBgScaffold(
        startColor =
            when (state.background) {
                null -> ZashiColors.Surfaces.bgPrimary
                SUCCESS -> ZashiColors.Utility.SuccessGreen.utilitySuccess100
                PENDING -> ZashiColors.Utility.Indigo.utilityIndigo100
            },
        endColor = ZashiColors.Surfaces.bgPrimary,
        bottomBar = { BottomBar(state) },
        content = {
            Content(
                state = state,
                modifier = Modifier.scaffoldPadding(it)
            )
        }
    )
}

@Composable
private fun BottomBar(state: TransactionProgressState) {
    OldZashiBottomBar {
        if (state.secondaryButton != null) {
            ZashiButton(
                state = state.secondaryButton,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ZashiDimensions.Spacing.spacing2xl)
            )
        }
        if (state.primaryButton != null) {
            ZashiButton(
                state = state.primaryButton,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ZashiDimensions.Spacing.spacing2xl),
            )
        }
    }
}

@Composable
private fun Content(state: TransactionProgressState, modifier: Modifier = Modifier) {
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
            ImageOrLoading(state.image)
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
                text = state.subtitle.getValue(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = ZashiColors.Text.textPrimary
            )
            Spacer(modifier = Modifier.height(ZashiDimensions.Spacing.spacingXl))
            if (state.middleButton != null) {
                ZashiButton(
                    state = state.middleButton,
                    modifier = Modifier.wrapContentWidth(),
                    defaultPrimaryColors = ZashiButtonDefaults.tertiaryColors()
                )
            }
        }
    }
}

@Composable
private fun ImageOrLoading(imageResource: ImageResource) {
    when (imageResource) {
        is ImageResource.ByDrawable -> {
            Image(
                painter = painterResource(imageResource.resource),
                contentDescription = null
            )
        }

        ImageResource.Loading -> {
            val lottieRes = R.raw.send_confirmation_sending_v1 orDark R.raw.send_confirmation_sending_dark_v1
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
            val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

            LottieAnimation(
                modifier =
                    Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            scaleX = LOTTIE_ANIM_SCALE
                            scaleY = LOTTIE_ANIM_SCALE
                        }.offset(y = -ZashiDimensions.Spacing.spacing2xl),
                composition = composition,
                progress = { progress },
                maintainOriginalImageBounds = true
            )
        }

        is ImageResource.DisplayString -> {
            // do nothing
        }
    }
}

private const val TOP_BLANK_SPACE_RATIO = .45f
private const val LOTTIE_ANIM_SCALE = 1.54f

@PreviewScreens
@Composable
private fun Preview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                TransactionProgressState(
                    title = stringRes("title"),
                    subtitle = stringRes("subtitle"),
                    middleButton =
                        ButtonState(
                            text = stringRes("middle btn"),
                            onClick = { }
                        ),
                    secondaryButton =
                        ButtonState(
                            text = stringRes("secondary btn"),
                            onClick = {},
                            style = ButtonStyle.SECONDARY
                        ),
                    primaryButton =
                        ButtonState(
                            text = stringRes("primary btn"),
                            onClick = {},
                            style = ButtonStyle.PRIMARY
                        ),
                    onBack = {},
                    background = SUCCESS,
                    image = imageRes(listOf(R.drawable.ic_fist_punch, R.drawable.ic_face_star).random())
                )
        )
    }

@PreviewScreens
@Composable
private fun SendingPreview() =
    ZcashTheme {
        TransactionProgressView(
            state =
                TransactionProgressState(
                    title = stringRes("title"),
                    subtitle = stringRes("subtitle"),
                    middleButton = null,
                    secondaryButton = null,
                    primaryButton = null,
                    onBack = {},
                    background = null,
                    image = loadingImageRes()
                )
        )
    }
