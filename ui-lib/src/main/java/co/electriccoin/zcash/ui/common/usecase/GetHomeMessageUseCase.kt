package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.WalletBackupAvailability
import co.electriccoin.zcash.ui.common.datasource.WalletBackupDataSource
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import co.electriccoin.zcash.ui.util.Quadruple
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

class GetHomeMessageUseCase(
    private val walletRepository: WalletRepository,
    private val walletBackupDataSource: WalletBackupDataSource,
    private val exchangeRateRepository: ExchangeRateRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    fun observe() = combine(
        walletRepository.currentWalletSnapshot.filterNotNull(),
        walletRepository.walletRestoringState,
        walletBackupDataSource.observe(),
        exchangeRateRepository.state.map { it == ExchangeRateState.OptIn }.distinctUntilChanged(),
    ) { walletSnapshot, walletStateInformation, backup, isCCAvailable ->
        Quadruple(walletSnapshot, walletStateInformation, backup, isCCAvailable)
    }.flatMapLatest { (walletSnapshot, walletStateInformation, backup, isCCAvailable) ->
        when {
            walletSnapshot.synchronizerError != null -> {
                flowOf(HomeMessageData.Error(walletSnapshot.synchronizerError))
            }

            walletSnapshot.status == Synchronizer.Status.DISCONNECTED -> {
                flowOf(HomeMessageData.Disconnected)
            }

            walletSnapshot.status in listOf(
                Synchronizer.Status.INITIALIZING,
                Synchronizer.Status.SYNCING,
                Synchronizer.Status.STOPPED
            ) -> {
                flow {
                    val progress = walletSnapshot.progress.decimal * 100f
                    val result = when {
                        walletStateInformation == WalletRestoringState.RESTORING -> {
                            HomeMessageData.Restoring(
                                progress = progress,
                            )
                        }

                        else -> {
                            HomeMessageData.Syncing(progress = progress)
                        }
                    }
                    emit(result)
                }
            }
            backup is WalletBackupAvailability.Available -> flowOf(HomeMessageData.Backup)

            isCCAvailable -> flowOf(HomeMessageData.EnableCurrencyConversion)

            else -> flowOf(null)
        }
    }.debounce(.5.seconds)
}

sealed interface HomeMessageData {
    data object EnableCurrencyConversion : HomeMessageData
    data class TransparentBalance(val zatoshi: Zatoshi) : HomeMessageData
    data object Backup : HomeMessageData
    data object Disconnected : HomeMessageData
    data class Error(val synchronizerError: SynchronizerError) : HomeMessageData
    data class Restoring(val progress: Float) : HomeMessageData
    data class Syncing(val progress: Float) : HomeMessageData
    data object Updating : HomeMessageData
}
