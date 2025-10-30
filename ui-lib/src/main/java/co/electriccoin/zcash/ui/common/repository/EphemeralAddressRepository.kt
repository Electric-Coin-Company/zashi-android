package co.electriccoin.zcash.ui.common.repository

import co.electriccoin.zcash.spackle.Twig
import co.electriccoin.zcash.ui.common.datasource.AccountDataSource
import co.electriccoin.zcash.ui.common.model.EphemeralAddress
import co.electriccoin.zcash.ui.common.provider.EphemeralAddressStorageProvider
import co.electriccoin.zcash.ui.common.provider.SynchronizerProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface EphemeralAddressRepository {
    fun observe(): Flow<EphemeralAddress?>
    suspend fun get(): EphemeralAddress?
    suspend fun create(): EphemeralAddress
    suspend fun invalidate()
}

class EphemeralAddressRepositoryImpl(
    private val accountDataSource: AccountDataSource,
    private val synchronizerProvider: SynchronizerProvider,
    private val ephemeralAddressStorageProvider: EphemeralAddressStorageProvider,
) : EphemeralAddressRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observe(): Flow<EphemeralAddress?> = accountDataSource
        .selectedAccount
        .map {
            it?.sdkAccount?.accountUuid
        }
        .distinctUntilChanged()
        .flatMapLatest { uuid ->
            if (uuid != null) ephemeralAddressStorageProvider.observe(uuid) else flowOf(null)
        }
        .distinctUntilChanged()

    override suspend fun invalidate() {
        val account = accountDataSource.getSelectedAccount()
        val existing = ephemeralAddressStorageProvider.get(account.sdkAccount.accountUuid)
        if (existing != null) {
            Twig.debug { "Invalidating ephemeral address $existing" }
        }
        ephemeralAddressStorageProvider.remove(account.sdkAccount.accountUuid)
    }

    override suspend fun get(): EphemeralAddress? {
        val account = accountDataSource.getSelectedAccount()
        return ephemeralAddressStorageProvider.get(account.sdkAccount.accountUuid)
    }

    override suspend fun create(): EphemeralAddress {
        val account = accountDataSource.getSelectedAccount()
        val new = synchronizerProvider.getSynchronizer()
            .getSingleUseTransparentAddress(account.sdkAccount.accountUuid)
            .let {
                EphemeralAddress(
                    address = it.address,
                    gapPosition = it.gapPosition,
                    gapLimit = it.gapLimit
                )
            }

        Twig.debug { "Generated new ephemeral address: $new" }
        ephemeralAddressStorageProvider.store(account.sdkAccount.accountUuid, new)
        return new
    }
}
