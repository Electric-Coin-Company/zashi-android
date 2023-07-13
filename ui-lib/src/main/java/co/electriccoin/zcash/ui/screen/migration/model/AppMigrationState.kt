package co.electriccoin.zcash.ui.screen.migration.model

import cash.z.ecc.android.sdk.model.PersistableWallet

sealed interface AppMigrationState {
    object Idle: AppMigrationState
    data class DataRecovered(val persistableWallet: PersistableWallet): AppMigrationState
    data class Error(val message: String? = null, val throwable: Throwable? = null): AppMigrationState
}