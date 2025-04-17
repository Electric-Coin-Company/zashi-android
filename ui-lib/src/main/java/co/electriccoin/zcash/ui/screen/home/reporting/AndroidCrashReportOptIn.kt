package co.electriccoin.zcash.ui.screen.home.reporting

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun AndroidCrashReportOptIn() {
    val viewModel = koinViewModel<CrashReportOptInViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler { state.onBack() }
    CrashReportOptInView(state = state)
}

@Serializable
object CrashReportOptIn
