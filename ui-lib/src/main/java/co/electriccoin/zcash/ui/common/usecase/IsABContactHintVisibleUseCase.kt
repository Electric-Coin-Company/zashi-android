package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.repository.AddressBookRepository
import co.electriccoin.zcash.ui.common.repository.EnhancedABContact
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

class IsABContactHintVisibleUseCase(
    private val addressBookRepository: AddressBookRepository
) {
    fun observe(
        selectedContact: EnhancedABContact?,
        text: String?
    ): Flow<Boolean> =
        flow {
            if (text.isNullOrBlank() || text.length < 3 || selectedContact != null) {
                emit(false)
            } else {
                addressBookRepository
                    .observeContactByAddress(text)
                    .map { it == null }
                    .collect { isAbleToAddContact ->
                        if (isAbleToAddContact) {
                            emit(true)
                            delay(3.seconds)
                            emit(false)
                        } else {
                            emit(false)
                        }
                    }
            }
        }
}
