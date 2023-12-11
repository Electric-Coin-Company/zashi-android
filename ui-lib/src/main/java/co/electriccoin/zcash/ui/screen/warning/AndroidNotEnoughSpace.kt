@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.screen.warning

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.screen.warning.view.NotEnoughSpaceView
import co.electriccoin.zcash.ui.screen.warning.viewmodel.StorageCheckViewModel

@Composable
fun MainActivity.WrapNotEnoughSpace() {
    WrapNotEnoughSpace(this)
}

@Composable
private fun WrapNotEnoughSpace(activity: ComponentActivity) {
    val storageCheckViewModel by activity.viewModels<StorageCheckViewModel>()
    val spaceRequiredToContinue by storageCheckViewModel.spaceRequiredToContinueMegabytes.collectAsStateWithLifecycle()

    NotEnoughSpaceView(
        storageSpaceRequiredGigabytes = storageCheckViewModel.requiredStorageSpaceGigabytes,
        spaceRequiredToContinueMegabytes = spaceRequiredToContinue ?: 0
    )
}
