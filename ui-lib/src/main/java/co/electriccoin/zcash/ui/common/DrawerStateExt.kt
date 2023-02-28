package co.electriccoin.zcash.ui.common

import androidx.compose.material3.DrawerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal fun DrawerState.openDrawerMenu(scope: CoroutineScope) {
    if (isOpen) {
        return
    }
    scope.launch { open() }
}

internal fun DrawerState.closeDrawerMenu(scope: CoroutineScope) {
    if (isClosed) {
        return
    }
    scope.launch { close() }
}
