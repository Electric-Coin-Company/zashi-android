package co.electriccoin.zcash.ui.common.usecase

import android.util.Log
import cash.z.ecc.android.sdk.Synchronizer
import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.MessageAvailabilityDataSource
import co.electriccoin.zcash.ui.common.datasource.WalletBackupAvailability
import co.electriccoin.zcash.ui.common.datasource.WalletBackupDataSource
import co.electriccoin.zcash.ui.common.model.WalletRestoringState
import co.electriccoin.zcash.ui.common.model.WalletSnapshot
import co.electriccoin.zcash.ui.common.repository.ExchangeRateRepository
import co.electriccoin.zcash.ui.common.repository.HomeMessageCacheRepository
import co.electriccoin.zcash.ui.common.repository.HomeMessageData
import co.electriccoin.zcash.ui.common.repository.ReceiveTransaction
import co.electriccoin.zcash.ui.common.repository.RuntimeMessage
import co.electriccoin.zcash.ui.common.repository.ShieldFundsData
import co.electriccoin.zcash.ui.common.repository.ShieldFundsRepository
import co.electriccoin.zcash.ui.common.repository.TransactionRepository
import co.electriccoin.zcash.ui.common.repository.WalletRepository
import co.electriccoin.zcash.ui.common.wallet.ExchangeRateState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.seconds

class GetHomeMessageUseCase(
    walletRepository: WalletRepository,
    walletBackupDataSource: WalletBackupDataSource,
    exchangeRateRepository: ExchangeRateRepository,
    shieldFundsRepository: ShieldFundsRepository,
    transactionRepository: TransactionRepository,
    private val messageAvailabilityDataSource: MessageAvailabilityDataSource,
    private val cache: HomeMessageCacheRepository,
) {
    private val backupFlow = combine(
        transactionRepository.zashiTransactions,
        walletBackupDataSource.observe()
    ) { transactions, backup ->
        if (backup is WalletBackupAvailability.Available && transactions.orEmpty().any { it is ReceiveTransaction }) {
            backup
        } else {
            WalletBackupAvailability.Unavailable
        }
    }.distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val flow = combine(
        walletRepository.currentWalletSnapshot.filterNotNull(),
        walletRepository.walletRestoringState.filterNotNull(),
        backupFlow,
        exchangeRateRepository.state.map { it == ExchangeRateState.OptIn }.distinctUntilChanged(),
        shieldFundsRepository.availability
    ) { walletSnapshot, walletStateInformation, backup, isCCAvailable, shieldFunds ->
        createMessage(walletSnapshot, walletStateInformation, backup, shieldFunds, isCCAvailable)
    }.debounce(.5.seconds)
        .map { message -> prioritizeMessage(message) }

    fun observe(): Flow<HomeMessageData?> = flow

    private fun createMessage(
        walletSnapshot: WalletSnapshot,
        walletStateInformation: WalletRestoringState,
        backup: WalletBackupAvailability,
        shieldFunds: ShieldFundsData,
        isCCAvailable: Boolean
    ) = when {
        walletSnapshot.synchronizerError != null -> HomeMessageData.Error(walletSnapshot.synchronizerError)

        walletSnapshot.status == Synchronizer.Status.DISCONNECTED -> HomeMessageData.Disconnected

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

        backup is WalletBackupAvailability.Available -> HomeMessageData.Backup

        shieldFunds is ShieldFundsData.Available -> HomeMessageData.ShieldFunds(shieldFunds.amount)

        isCCAvailable -> HomeMessageData.EnableCurrencyConversion

        else -> null
    }

    private fun prioritizeMessage(message: HomeMessageData?): HomeMessageData? {
        val isSameMessageUpdate = message?.priority == cache.lastMessage?.priority // same but updated
        val someMessageBeenShown = cache.lastShownMessage != null // has any message been shown while app in fg
        val hasNoMessageBeenShownLately = cache.lastMessage == null // has no message been shown
        val isHigherPriorityMessage = (message?.priority ?: 0) > (cache.lastShownMessage?.priority ?: 0)
        val result = when {
            message == null -> null
            message is RuntimeMessage -> message
            isSameMessageUpdate -> message
            isHigherPriorityMessage -> if (hasNoMessageBeenShownLately) {
                if (someMessageBeenShown) null else message
            } else {
                message
            }

            else -> null
        }

        if (result != null) {
            messageAvailabilityDataSource.onMessageShown()
            cache.lastShownMessage = result
        }
        cache.lastMessage = result

        Twig.debug {
            when {
                message == null -> "Home message: no message to show"
                result == null -> "Home message: ${message::class.simpleName} was filtered out"
                else -> {
                    "Home message: ${result::class.simpleName} shown"
                }
            }
        }

        return result
    }
}
