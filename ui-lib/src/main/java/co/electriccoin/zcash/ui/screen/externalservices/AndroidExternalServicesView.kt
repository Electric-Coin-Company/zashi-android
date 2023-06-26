package co.electriccoin.zcash.ui.screen.externalservices

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.externalservices.view.ExternalServices
import co.electriccoin.zcash.ui.screen.externalservices.viewmodel.ExternalServicesViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel

@Composable
internal fun MainActivity.AndroidExternalServicesView(onBack: () -> Unit) {
    WrapExternalServiceView(activity = this, onBack = onBack)
}

@Composable
internal fun WrapExternalServiceView(activity: ComponentActivity, onBack: () -> Unit) {
    val homeViewModel by activity.viewModels<HomeViewModel>()
    val externalServicesViewModel by activity.viewModels<ExternalServicesViewModel>()

    DisposableEffect(key1 = Unit) {
        val previousVisibility = homeViewModel.isBottomNavBarVisible.value
        homeViewModel.onBottomNavBarVisibilityChanged(show = false)
        onDispose {
            homeViewModel.onBottomNavBarVisibilityChanged(show = previousVisibility)
        }
    }

    val isUnStoppableChecked = externalServicesViewModel.isUnStoppableServiceEnabled.collectAsStateWithLifecycle().value

    if (isUnStoppableChecked == null) {
        // We can show loader but will not take much time to reflect on ui
    } else {
        ExternalServices(
            onBack = onBack,
            isUnstoppableChecked = isUnStoppableChecked,
            onUnstoppableCheckStateChanged = externalServicesViewModel::updateUnStoppableServiceStatus
        )
    }
}