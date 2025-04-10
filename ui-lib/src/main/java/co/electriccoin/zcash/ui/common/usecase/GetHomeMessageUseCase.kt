package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.Synchronizer
import cash.z.ecc.android.sdk.model.Zatoshi
import co.electriccoin.zcash.ui.common.datasource.WalletBackupAvailability
import co.electriccoin.zcash.ui.common.datasource.WalletBackupDataSource
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.ShieldFundsData
import co.electriccoin.zcash.ui.common.repository.ShieldFundsRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.viewmodel.SynchronizerError
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

class GetHomeMessageUseCase(
    private val walletRepository: WalletRepository,
    private val walletBackupDataSource: WalletBackupDataSource,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val shieldFundsRepository: ShieldFundsRepository,
) {
    @OptIn(FlowPreview::class)
    fun observe(): Flow<HomeMessageData?> = combine(
        walletRepository.currentWalletSnapshot.filterNotNull(),
        walletRepository.walletRestoringState,
        walletBackupDataSource.observe(),
        exchangeRateRepository.state.map { it == ExchangeRateState.OptIn }.distinctUntilChanged(),
        shieldFundsRepository.availability
    ) { walletSnapshot, walletStateInformation, backup, isCCAvailable, shieldFunds ->
        when {
            walletSnapshot.synchronizerError != null -> {
                HomeMessageData.Error(walletSnapshot.synchronizerError)
            }

            walletSnapshot.status == Synchronizer.Status.DISCONNECTED -> {
                HomeMessageData.Disconnected
            }

            walletSnapshot.status in listOf(
                Synchronizer.Status.INITIALIZING,
                Synchronizer.Status.SYNCING,
                Synchronizer.Status.STOPPED
            ) -> {
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
                result
            }

            shieldFunds is ShieldFundsData.Available -> HomeMessageData.ShieldFunds(shieldFunds.amount)

            backup is WalletBackupAvailability.Available -> HomeMessageData.Backup

            isCCAvailable -> HomeMessageData.EnableCurrencyConversion

            else -> null
        }
    }.debounce(.5.seconds)
}

sealed interface HomeMessageData {
    data object EnableCurrencyConversion : HomeMessageData
    data class ShieldFunds(val zatoshi: Zatoshi) : HomeMessageData
    data object Backup : HomeMessageData
    data object Disconnected : HomeMessageData
    data class Error(val synchronizerError: SynchronizerError) : HomeMessageData
    data class Restoring(val progress: Float) : HomeMessageData
    data class Syncing(val progress: Float) : HomeMessageData
    data object Updating : HomeMessageData
}
