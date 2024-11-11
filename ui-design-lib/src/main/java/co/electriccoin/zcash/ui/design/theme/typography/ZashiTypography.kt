@file:Suppress("MagicNumber")

package co.electriccoin.zcash.ui.design.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

val ZashiTypography: ZashiTypographyInternal
    @Composable get() = LocalZashiTypography.current

@Suppress("CompositionLocalAllowlist")
internal val LocalZashiTypography =
    staticCompositionLocalOf<ZashiTypographyInternal> { error("no typography specified") }
