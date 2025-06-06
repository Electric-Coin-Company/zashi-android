package co.electriccoin.zcash.ui.screen.restore.info

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import co.electriccoin.zcash.ui.NavigationRouter
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidSeedInfo() {
    val navigationRouter = koinInject<NavigationRouter>()
    SeedInfoView(
        state = remember { SeedInfoState(onBack = { navigationRouter.back() }) },
    )
}

@Serializable
object SeedInfo
