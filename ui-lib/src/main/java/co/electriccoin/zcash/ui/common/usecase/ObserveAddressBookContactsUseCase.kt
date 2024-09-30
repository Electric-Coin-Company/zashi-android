package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import kotlinx.coroutines.flow.flow

class ObserveAddressBookContactsUseCase {
    operator fun invoke() = flow {
        emit(
            (1..20).map {
                AddressBookContact(
                    name = if (it % 2 == 0) "Name Surname" else "Name",
                    address = WalletAddress.Unified.new(
                        "u1l9f0l4348negsncgr9pxd9d3qaxagmqv3lnexcplmufpq7muffvfaue6ksevfvd7wrz7xrvn95rc5zjtn7ugkmgh5rnxswmcj30y0pw52pn0zjvy38rn2esfgve64rj5pcmazxgpyuj"
                    )
                )
            }
        )
    }
}
