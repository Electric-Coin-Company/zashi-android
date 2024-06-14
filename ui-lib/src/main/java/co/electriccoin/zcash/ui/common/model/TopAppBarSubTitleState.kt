package co.electriccoin.zcash.ui.common.model

sealed class TopAppBarSubTitleState {
    data object None : TopAppBarSubTitleState()

    data object Disconnected : TopAppBarSubTitleState()

    data object Restoring : TopAppBarSubTitleState()
}
