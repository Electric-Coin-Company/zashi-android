package co.electriccoin.zcash.ui.common.usecase

import co.electriccoin.zcash.ui.common.model.AddressBookContact
import co.electriccoin.zcash.ui.screen.send.model.RecipientAddressState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class ObserveContactPickedUseCase(
    private val getSynchronizer: GetSynchronizerUseCase
) {
    private val bus = MutableSharedFlow<AddressBookContact>()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() =
        bus.mapLatest {
            val synchronizer = getSynchronizer()
            val type = synchronizer.validateAddress(it.address)
            RecipientAddressState.new(
                address = it.address,
                type = type
            )
        }

    fun onContactPicked(contact: AddressBookContact) =
        scope.launch {
            bus.emit(contact)
        }
}
