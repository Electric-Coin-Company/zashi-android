package co.electriccoin.zcash.ui.design.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

@Composable
@ReadOnlyComposable
infix fun <T> T.orDark(dark: T): T = if (isSystemInDarkTheme()) dark else this
