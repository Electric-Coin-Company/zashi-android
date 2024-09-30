package co.electriccoin.zcash.ui.common.model

import cash.z.ecc.android.sdk.model.WalletAddress

data class AddressBookContact(
    val name: String,
    val address: WalletAddress.Unified,
)
