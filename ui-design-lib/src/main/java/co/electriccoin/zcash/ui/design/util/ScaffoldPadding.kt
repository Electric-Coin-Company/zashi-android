package co.electriccoin.zcash.ui.design.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.theme.dimensions.ZashiDimensions

@Stable
fun Modifier.scaffoldPadding(paddingValues: PaddingValues) =
    this.padding(
        top = paddingValues.calculateTopPadding() + ZashiDimensions.Spacing.spacingLg,
        bottom = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
        start = ZashiDimensions.Spacing.spacing3xl,
        end = ZashiDimensions.Spacing.spacing3xl
    )

fun Modifier.scaffoldScrollPadding(paddingValues: PaddingValues) =
    this.padding(
        top = paddingValues.calculateTopPadding(),
        bottom = paddingValues.calculateBottomPadding() + ZashiDimensions.Spacing.spacing3xl,
        start = 4.dp,
        end = 4.dp
    )
