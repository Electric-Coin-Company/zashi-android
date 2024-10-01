package co.electriccoin.zcash.ui.common.repository

import androidx.compose.runtime.MutableState
import cash.z.ecc.android.sdk.model.WalletAddress
import co.electriccoin.zcash.ui.common.model.AddressBookContact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

interface AddressBookRepository {
    val contacts: StateFlow<List<AddressBookContact>>

    suspend fun saveContact(name: String, address: String)
}

class AddressBookRepositoryImpl: AddressBookRepository {
    override val contacts = MutableStateFlow(
        listOf(
            AddressBookContact(
                name = "Name Surname",
                address = runBlocking {
                    WalletAddress.Unified.new(
                        "u1l9f0l4348negsncgr9pxd9d3qaxagmqv3lnexcplmufpq7muffvfaue6ksevfvd7wrz7xrvn95rc5zjtn7ugkmgh5rnxswmcj30y0pw52pn0zjvy38rn2esfgve64rj5pcmazxgpyuj"
                    )
                }
            )
        )
    )

    override suspend fun saveContact(name: String, address: String) {
        contacts.update { it + AddressBookContact(name, WalletAddress.Unified.new(address)) }
    }
}
