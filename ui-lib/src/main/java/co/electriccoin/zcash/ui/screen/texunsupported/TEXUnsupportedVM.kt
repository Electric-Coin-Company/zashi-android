package co.electriccoin.zcash.ui.screen.texunsupported

import androidx.lifecycle.ViewModel
import co.electriccoin.zcash.ui.NavigationRouter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TEXUnsupportedVM(
    private val navigationRouter: NavigationRouter
) : ViewModel() {
    val state: StateFlow<TEXUnsupportedState?> =
        MutableStateFlow<TEXUnsupportedState?>(
            TEXUnsupportedState(
                onBack = { navigationRouter.back() }
            )
        ).asStateFlow()
}
