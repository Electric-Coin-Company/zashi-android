package co.electriccoin.zcash.ui.common.wallet

import kotlinx.coroutines.flow.Flow

internal interface TimestampFlowLock {
    val state: Flow<Boolean>
}
