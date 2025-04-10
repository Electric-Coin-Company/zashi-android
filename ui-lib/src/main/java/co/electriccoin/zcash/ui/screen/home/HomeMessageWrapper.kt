package co.electriccoin.zcash.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.electriccoin.zcash.ui.design.component.HorizontalSpacer
import co.electriccoin.zcash.ui.design.component.LocalZashiButtonColors
import co.electriccoin.zcash.ui.design.component.LocalZashiCircularProgressIndicatorColors
import co.electriccoin.zcash.ui.design.component.VerticalSpacer
import co.electriccoin.zcash.ui.design.component.ZashiButtonDefaults
import co.electriccoin.zcash.ui.design.component.ZashiCircularProgressIndicatorDefaults
import co.electriccoin.zcash.ui.design.theme.colors.ZashiDarkColors
import co.electriccoin.zcash.ui.design.theme.colors.ZashiLightColors
import co.electriccoin.zcash.ui.design.theme.typography.ZashiTypography

@Suppress("ModifierNaming", "ModifierWithoutDefault")
@Composable
fun HomeMessageWrapper(
    innerModifier: Modifier,
    contentPadding: PaddingValues,
    onClick: () -> Unit,
    start: @Composable RowScope.() -> Unit,
    title: @Composable () -> Unit,
    subtitle: @Composable () -> Unit,
    end: (@Composable () -> Unit)?,
) {
    Container(
        contentPadding = contentPadding,
        onClick = onClick,
        innerModifier = innerModifier,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides ZashiLightColors.Utility.Purple.utilityPurple50,
            LocalZashiCircularProgressIndicatorColors provides
                ZashiCircularProgressIndicatorDefaults.colors(
                    progressColor = ZashiLightColors.Utility.Purple.utilityPurple50,
                    trackColor = ZashiLightColors.Utility.Purple.utilityPurple400
                )
        ) {
            start()
        }
        HorizontalSpacer(16.dp)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides
                    ZashiTypography.textSm.copy(
                        color = ZashiLightColors.Utility.Purple.utilityPurple50,
                        fontWeight = FontWeight.Medium
                    ),
            ) {
                title()
            }
            VerticalSpacer(2.dp)
            CompositionLocalProvider(
                LocalTextStyle provides
                    ZashiTypography.textXs.copy(
                        color = ZashiLightColors.Utility.Purple.utilityPurple200,
                        fontWeight = FontWeight.Medium
                    ),
            ) {
                subtitle()
            }
        }
        if (end != null) {
            CompositionLocalProvider(
                LocalZashiButtonColors provides
                    ZashiButtonDefaults.primaryColors(
                        if (isSystemInDarkTheme()) ZashiLightColors else ZashiDarkColors
                    )
            ) {
                end()
            }
        }
    }
}

@Suppress("ModifierNaming", "ModifierWithoutDefault")
@Composable
private fun Container(
    innerModifier: Modifier,
    contentPadding: PaddingValues,
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit),
) {
    Box(
        modifier =
            Modifier
                .background(
                    Brush.verticalGradient(
                        0f to ZashiLightColors.Utility.Purple.utilityPurple500,
                        1f to ZashiLightColors.Utility.Purple.utilityPurple900,
                    )
                )
                .clickable(onClick = onClick)
                .padding(contentPadding),
    ) {
        Row(
            modifier =
                innerModifier.padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
