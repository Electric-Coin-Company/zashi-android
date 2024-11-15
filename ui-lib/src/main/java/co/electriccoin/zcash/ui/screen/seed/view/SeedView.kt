package co.electriccoin.zcash.ui.screen.seed.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import co.electriccoin.zcash.spackle.AndroidApiVersion
import co.electriccoin.zcash.ui.R
import co.electriccoin.zcash.ui.common.compose.SecureScreen
import co.electriccoin.zcash.ui.common.compose.ZashiTooltip
import co.electriccoin.zcash.ui.common.compose.ZashiTooltipBox
import co.electriccoin.zcash.ui.common.compose.drawCaretWithPath
import co.electriccoin.zcash.ui.common.compose.shouldSecureScreen
import co.electriccoin.zcash.ui.common.model.TopAppBarSubTitleState
import co.electriccoin.zcash.ui.design.component.ButtonState
import co.electriccoin.zcash.ui.design.component.ZashiButton
import co.electriccoin.zcash.ui.design.component.ZashiSmallTopAppBar
import co.electriccoin.zcash.ui.design.component.ZashiTopAppBarBackNavigation
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreenSizes
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.colors.ZashiColors
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography
import co.electriccoin.zcash.ui.design.util.getValue
import co.electriccoin.zcash.ui.design.util.scaffoldPadding
import co.electriccoin.zcash.ui.design.util.stringRes
import co.electriccoin.zcash.ui.screen.seed.model.SeedSecretState
import co.electriccoin.zcash.ui.screen.seed.model.SeedSecretStateTooltip
import co.electriccoin.zcash.ui.screen.seed.model.SeedState
import kotlinx.coroutines.launch

@Composable
fun SeedView(
    topAppBarSubTitleState: TopAppBarSubTitleState,
    state: SeedState,
) {
    if (shouldSecureScreen) {
        SecureScreen()
    }

    Scaffold(
        topBar = {
            SeedRecoveryTopAppBar(
                state = state,
                subTitleState = topAppBarSubTitleState,
            )
        }
    ) { paddingValues ->
        SeedRecoveryMainContent(
            modifier = Modifier.scaffoldPadding(paddingValues),
            state = state,
        )
    }
}

@Composable
private fun SeedRecoveryTopAppBar(
    state: SeedState,
    subTitleState: TopAppBarSubTitleState,
    modifier: Modifier = Modifier,
) {
    ZashiSmallTopAppBar(
        title = stringResource(R.string.seed_recovery_title),
        subtitle =
            when (subTitleState) {
                TopAppBarSubTitleState.Disconnected -> stringResource(id = R.string.disconnected_label)
                TopAppBarSubTitleState.Restoring -> stringResource(id = R.string.restoring_wallet_label)
                TopAppBarSubTitleState.None -> null
            },
        modifier = modifier,
        navigationAction = {
            if (state.onBack != null) {
                ZashiTopAppBarBackNavigation(onBack = state.onBack)
            }
        }
    )
}

@Composable
private fun SeedRecoveryMainContent(
    state: SeedState,
    modifier: Modifier = Modifier,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .then(modifier),
    ) {
        Text(
            text = stringResource(R.string.seed_recovery_header),
            fontWeight = FontWeight.SemiBold,
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.header6
        )

        Spacer(Modifier.height(ZashiDimensions.Spacing.spacingMd))

        Text(
            text = stringResource(R.string.seed_recovery_description),
            color = ZashiColors.Text.textPrimary,
            style = ZashiTypography.textSm
        )

        Spacer(Modifier.height(ZashiDimensions.Spacing.spacing4xl))

        SeedSecret(modifier = Modifier.fillMaxWidth(), state = state.seed)

        Spacer(Modifier.height(ZashiDimensions.Spacing.spacing3xl))

        SeedSecret(modifier = Modifier.fillMaxWidth(), state = state.birthday)

        Spacer(Modifier.weight(1f))

        Spacer(Modifier.height(ZashiDimensions.Spacing.spacing3xl))

        Row {
            Image(
                painterResource(R.drawable.ic_warning),
                contentDescription = null,
                colorFilter = ColorFilter.tint(ZashiColors.Utility.WarningYellow.utilityOrange500)
            )

            Spacer(Modifier.width(ZashiDimensions.Spacing.spacingLg))

            Text(
                text = stringResource(R.string.seed_recovery_warning),
                color = ZashiColors.Utility.WarningYellow.utilityOrange500,
                style = ZashiTypography.textXs,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(ZashiDimensions.Spacing.spacing3xl))

        ZashiButton(
            state = state.button,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeedSecret(
    state: SeedSecretState,
    modifier: Modifier = Modifier,
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
    ) {
        Row(
            modifier =
                if (state.tooltip != null) {
                    Modifier.clickable {
                        scope.launch {
                            if (tooltipState.isVisible) {
                                tooltipState.dismiss()
                            } else {
                                tooltipState.show()
                            }
                        }
                    }
                } else {
                    Modifier
                }
        ) {
            Text(
                text = state.title.getValue(),
                color = ZashiColors.Text.textPrimary,
                style = ZashiTypography.textSm,
                fontWeight = FontWeight.Medium
            )
            if (state.tooltip != null) {
                val density = LocalDensity.current
                val configuration = LocalConfiguration.current
                val containerColor = ZashiColors.HintTooltips.defaultBg
                Spacer(Modifier.width(2.dp))
                ZashiTooltipBox(
                    tooltip = {
                        ZashiTooltip(
                            modifier =
                                Modifier.drawCaret {
                                    drawCaretWithPath(
                                        density = density,
                                        configuration = configuration,
                                        containerColor = containerColor,
                                        anchorLayoutCoordinates = it
                                    )
                                },
                            showCaret = false,
                            title = state.tooltip.title,
                            message = state.tooltip.message,
                            onDismissRequest = {
                                scope.launch {
                                    tooltipState.dismiss()
                                }
                            }
                        )
                    },
                    state = tooltipState,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_zashi_tooltip),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(ZashiColors.Inputs.Default.icon)
                    )
                }
            }
        }
        Spacer(Modifier.height(ZashiDimensions.Spacing.spacingSm))

        SecretContent(state = state)
    }
}

@Composable
private fun SecretContent(state: SeedSecretState) {
    val blur = animateDpAsState(if (state.isRevealed) 0.dp else 14.dp, label = "")
    Surface(
        modifier =
            Modifier
                .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = ZashiColors.Inputs.Default.bg
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier =
                    Modifier then
                        if (state.onClick != null) {
                            Modifier.clickable(onClick = state.onClick)
                        } else {
                            Modifier
                        } then
                        Modifier
                            .blurCompat(blur.value, 14.dp)
                            .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                if (state.mode == SeedSecretState.Mode.SEED) {
                    SecretSeedContent(state)
                } else {
                    SecretBirthdayContent(state)
                }
            }

            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                visible =
                    state.isRevealPhraseVisible &&
                        state.isRevealed.not() &&
                        state.mode == SeedSecretState.Mode.SEED,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_reveal),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(ZashiColors.Text.textPrimary)
                    )

                    Spacer(Modifier.height(ZashiDimensions.Spacing.spacingMd))

                    Text(
                        text = stringResource(R.string.seed_recovery_reveal),
                        style = ZashiTypography.textLg,
                        fontWeight = FontWeight.SemiBold,
                        color = ZashiColors.Text.textPrimary
                    )
                }
            }
        }
    }
}

private fun Modifier.blurCompat(
    radius: Dp,
    max: Dp
): Modifier {
    return if (AndroidApiVersion.isAtLeastS) {
        this.blur(radius)
    } else {
        val progression = 1 - (radius.value / max.value)
        this
            .alpha(progression)
    }
}

@Composable
private fun SecretBirthdayContent(state: SeedSecretState) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        text = state.text.getValue(),
        style = ZashiTypography.textMd,
        fontWeight = FontWeight.Medium,
        color = ZashiColors.Inputs.Filled.text
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SecretSeedContent(state: SeedSecretState) {
    FlowColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(end = 8.dp),
        maxItemsInEachColumn = 8,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = spacedBy(6.dp),
        maxLines = 8
    ) {
        state.text.getValue().split(" ").fastForEachIndexed { i, s ->
            Row(
                modifier = Modifier
            ) {
                Text(
                    modifier = Modifier.width(18.dp),
                    textAlign = TextAlign.End,
                    text = "${i + 1}",
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.Normal,
                    color = ZashiColors.Text.textPrimary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.width(ZashiDimensions.Spacing.spacingLg))

                Text(
                    text = s,
                    style = ZashiTypography.textSm,
                    fontWeight = FontWeight.Normal,
                    color = ZashiColors.Text.textPrimary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
@PreviewScreenSizes
private fun RevealedPreview() =
    ZcashTheme {
        SeedView(
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            state =
                SeedState(
                    seed =
                        SeedSecretState(
                            title = stringRes("Seed"),
                            text = stringRes((1..24).joinToString(" ") { "trala" }),
                            tooltip = null,
                            isRevealed = true,
                            mode = SeedSecretState.Mode.SEED,
                            isRevealPhraseVisible = true
                        ) {},
                    birthday =
                        SeedSecretState(
                            title = stringRes("Birthday"),
                            text = stringRes(value = "asdads"),
                            tooltip = SeedSecretStateTooltip(title = stringRes(""), message = stringRes("")),
                            isRevealed = true,
                            mode = SeedSecretState.Mode.BIRTHDAY,
                            isRevealPhraseVisible = false
                        ) {},
                    button =
                        ButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_seed_show,
                            onClick = {},
                        ),
                    onBack = {}
                )
        )
    }

@Composable
@PreviewScreenSizes
private fun HiddenPreview() =
    ZcashTheme {
        SeedView(
            topAppBarSubTitleState = TopAppBarSubTitleState.None,
            state =
                SeedState(
                    seed =
                        SeedSecretState(
                            title = stringRes("Seed"),
                            text = stringRes((1..24).joinToString(" ") { "trala" }),
                            tooltip = null,
                            isRevealed = false,
                            mode = SeedSecretState.Mode.SEED,
                            isRevealPhraseVisible = true
                        ) {},
                    birthday =
                        SeedSecretState(
                            title = stringRes("Birthday"),
                            text = stringRes(value = "asdads"),
                            tooltip = SeedSecretStateTooltip(title = stringRes(""), message = stringRes("")),
                            isRevealed = false,
                            mode = SeedSecretState.Mode.BIRTHDAY,
                            isRevealPhraseVisible = false
                        ) {},
                    button =
                        ButtonState(
                            text = stringRes("Text"),
                            icon = R.drawable.ic_seed_show,
                            onClick = {},
                        ),
                    onBack = {}
                )
        )
    }
