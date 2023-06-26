package co.electriccoin.zcash.ui.screen.transactiondetails

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.onLaunchUrl
import co.electriccoin.zcash.ui.screen.home.viewmodel.HomeViewModel
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.transactiondetails.model.TransactionDetailsUIModel
import co.electriccoin.zcash.ui.screen.transactiondetails.view.TransactionDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun MainActivity.AndroidTransactionDetails(transactionId: Long, onBack: () -> Unit) {
    WrapAndroidTransactionDetails(activity = this, transactionId = transactionId, onBack = onBack)
}

@Composable
internal fun WrapAndroidTransactionDetails(activity: ComponentActivity, transactionId: Long, onBack: () -> Unit) {
    Twig.info { "TransactionId $transactionId" }
    val homeViewModel by activity.viewModels<HomeViewModel>()
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val scope = rememberCoroutineScope()
    DisposableEffect(key1 = Unit) {
        val previousVisibility = homeViewModel.isBottomNavBarVisible.value
        // for handling bottomNavBar visibility due to onDispose of this screen parent class
        scope.launch {
            delay(500.milliseconds)
            homeViewModel.onBottomNavBarVisibilityChanged(show = false)
        }
        onDispose {
            homeViewModel.onBottomNavBarVisibilityChanged(show = previousVisibility)
        }
    }
    val synchronizer: MutableState<Synchronizer?> = remember {
        mutableStateOf(null)
    }
    val synchronizerJob: MutableState<Job?> = remember {
        mutableStateOf(null)
    }

    val transactionDetailsUIModel: MutableState<TransactionDetailsUIModel?> = remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = Unit) {
        synchronizerJob.value = scope.launch(Dispatchers.IO) {
            walletViewModel.synchronizer.collectLatest {
                synchronizer.value = it
            }
        }

        scope.launch {
            synchronizer.value?.let {
                walletViewModel.transactionUiModel(transactionId, it).collectLatest { uiModel ->
                    transactionDetailsUIModel.value = uiModel
                }
            }
        }
    }
    Twig.info { "TransactionDetailUiModel: ${transactionDetailsUIModel.value}" }

    TransactionDetails(transactionDetailsUIModel = transactionDetailsUIModel.value, onBack = onBack, viewOnBlockExplorer = { activity.onLaunchUrl(it) })
}
