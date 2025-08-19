package co.electriccoin.zcash.ui.common.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IsABContactHintVisibleUseCase{
    fun observe(): Flow<Boolean> = flowOf(false)
}
