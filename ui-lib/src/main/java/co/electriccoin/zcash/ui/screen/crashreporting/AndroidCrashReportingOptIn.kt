package co.electriccoin.zcash.ui.screen.crashreporting

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.screen.crashreporting.view.CrashReportingOptIn
import co.electriccoin.zcash.ui.screen.crashreporting.viewmodel.CrashReportingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidCrashReportingOptIn() {
    val crashReportingViewModel = koinViewModel<CrashReportingViewModel>()
    val state = crashReportingViewModel.state.collectAsStateWithLifecycle().value

    BackHandler {
        state.onBack()
    }

    CrashReportingOptIn(state = state)
}
