package co.electriccoin.zcash.ui.screen.home.model

import androidx.compose.runtime.Composable

data class TabItem(
    val index: Int,
    val title: String,
    val screenContent: @Composable () -> Unit
)
