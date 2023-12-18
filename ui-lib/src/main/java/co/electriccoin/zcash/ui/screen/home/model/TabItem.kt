package co.electriccoin.zcash.ui.screen.home.model

import androidx.compose.runtime.Composable
import co.electriccoin.zcash.ui.screen.home.HomeScreenIndex

data class TabItem(
    val index: HomeScreenIndex,
    val title: String,
    val testTag: String,
    val screenContent: @Composable () -> Unit
)
