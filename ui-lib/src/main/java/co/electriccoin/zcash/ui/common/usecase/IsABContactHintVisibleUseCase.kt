package co.electriccoin.zcash.ui.common.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IsABContactHintVisibleUseCase(
    // private val addressBookRepository: AddressBookRepository
) {
    // fun observe(text: String?): Flow<Boolean> =
    //     flow {
    //         if (text.isNullOrBlank()) {
    //             emit(false)
    //         } else {
    //             addressBookRepository
    //                 .observeContactByAddress(text)
    //                 .map { it == null }
    //                 .collect { isAbleToAddContact ->
    //                     if (isAbleToAddContact) {
    //                         emit(true)
    //                         delay(3.seconds)
    //                         emit(false)
    //                     } else {
    //                         emit(false)
    //                     }
    //                 }
    //         }
    //     }

    fun observe(text: String?): Flow<Boolean> = flowOf(false)
}
