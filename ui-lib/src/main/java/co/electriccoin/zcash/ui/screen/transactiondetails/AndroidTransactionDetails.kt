package co.electriccoin.zcash.ui.screen.transactiondetails

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.MainActivity
import co.electriccoin.zcash.ui.common.onLaunchUrl
import co.electriccoin.zcash.ui.screen.home.viewmodel.WalletViewModel
import co.electriccoin.zcash.ui.screen.transactiondetails.view.TransactionDetails
import co.electriccoin.zcash.ui.screen.transactiondetails.viewmodel.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
internal fun MainActivity.AndroidTransactionDetails(transactionId: Long, onBack: () -> Unit) {
    WrapAndroidTransactionDetails(activity = this, transactionId = transactionId, onBack = onBack)
}

@Composable
internal fun WrapAndroidTransactionDetails(
    activity: ComponentActivity,
    transactionId: Long,
    onBack: () -> Unit
) {
    Twig.info { "TransactionId $transactionId" }
    val walletViewModel by activity.viewModels<WalletViewModel>()
    val transactionUiViewModel = viewModel<TransactionViewModel>()
    val scope = rememberCoroutineScope()
    val synchronizerJob: MutableState<Job?> = remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = Unit) {
        synchronizerJob.value = scope.launch(Dispatchers.IO) {
            val synchronizer = walletViewModel.synchronizer.filterNotNull().first()
            transactionUiViewModel.getTransactionUiModel(transactionId, synchronizer)
        }
    }

    val isNavigateAwayFromAppWarningShown =
        transactionUiViewModel.isNavigateAwayFromWarningShown.collectAsStateWithLifecycle().value
    val transactionDetailsUIModel = transactionUiViewModel.transactionDetailsUIModel.collectAsStateWithLifecycle().value

    Twig.info { "TransactionDetailUiModel: $transactionDetailsUIModel" }

    TransactionDetails(
        transactionDetailsUIModel = transactionDetailsUIModel,
        isNavigateAwayFromAppWarningShown = isNavigateAwayFromAppWarningShown,
        onBack = onBack,
        viewOnBlockExplorer = { url, updateWarningStatus ->
            if (updateWarningStatus) {
                transactionUiViewModel.updateNavigateAwayFromWaringFlag(true)
            }
            activity.onLaunchUrl(url)
        }
    )
}
