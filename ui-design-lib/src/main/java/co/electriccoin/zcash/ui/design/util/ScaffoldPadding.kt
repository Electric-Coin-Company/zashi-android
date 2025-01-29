package co.electriccoin.zcash.ui.design.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions

@Stable
fun Modifier.scaffoldPadding(
    paddingValues: PaddingValues,
    top: Dp = paddingValues.calculateTopPadding() + ZashiDimensions.Spacing.spacingLg,
    bottom: Dp = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
    start: Dp = ZashiDimensions.Spacing.spacing3xl,
    end: Dp = ZashiDimensions.Spacing.spacing3xl
) = this.padding(
    top = top,
    bottom = bottom,
    start = start,
    end = end,
)

fun Modifier.scaffoldScrollPadding(
    paddingValues: PaddingValues,
    top: Dp = paddingValues.calculateTopPadding() + ZashiDimensions.Spacing.spacingLg,
    bottom: Dp = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
    start: Dp = 0.dp,
    end: Dp = 0.dp
) = this.padding(
    top = top,
    bottom = bottom,
    start = start,
    end = end,
)

@Stable
fun PaddingValues.asScaffoldPaddingValues() =
    PaddingValues(
        top = calculateTopPadding() + ZashiDimensions.Spacing.spacingLg,
        bottom = calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
        start = ZashiDimensions.Spacing.spacing3xl,
        end = ZashiDimensions.Spacing.spacing3xl
    )

@Stable
fun PaddingValues.asScaffoldScrollPaddingValues(
    top: Dp = calculateTopPadding() + ZashiDimensions.Spacing.spacingLg,
    bottom: Dp = calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
    start: Dp = 0.dp,
    end: Dp = 0.dp
) = PaddingValues(
    top = top,
    bottom = bottom,
    start = start,
    end = end,
)
