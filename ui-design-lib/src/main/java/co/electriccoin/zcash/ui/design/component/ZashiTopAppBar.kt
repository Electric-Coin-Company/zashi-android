package co.electriccoin.zcash.ui.design.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import co.electriccoin.zcash.ui.design.newcomponent.PreviewScreens
import co.electriccoin.zcash.ui.design.theme.ZcashTheme
import co.electriccoin.zcash.ui.design.theme.internal.SecondaryTypography
import co.electriccoin.zcash.ui.design.theme.internal.TopAppBarColors

@Composable
@Suppress("LongParameterList")
fun ZashiSmallTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    showTitleLogo: Boolean = false,
    colors: TopAppBarColors = ZcashTheme.colors.topAppBarColors,
    navigationAction: @Composable () -> Unit = {},
    hamburgerMenuActions: (@Composable RowScope.() -> Unit)? = null,
    regularActions: (@Composable RowScope.() -> Unit)? = null,
) {
    SmallTopAppBar(
        modifier = modifier,
        colors = colors,
        hamburgerMenuActions = hamburgerMenuActions,
        navigationAction = navigationAction,
        regularActions = regularActions,
        subTitle = subtitle,
        showTitleLogo = showTitleLogo,
        titleText = title,
        titleStyle = SecondaryTypography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
    )
}

@PreviewScreens
@Composable
private fun ZashiSmallTopAppBarPreview() =
    ZcashTheme {
        ZashiSmallTopAppBar(
            title = "Test Title",
            subtitle = "Subtitle",
        )
    }
