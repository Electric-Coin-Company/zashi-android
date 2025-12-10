package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.tool.DerivationTool
import co.electriccoin.zcash.ui.common.model.VersionInfo
import co.electriccoin.zcash.ui.screen.addressbook.ADDRESS_MAX_LENGTH
import com.keystone.module.ZcashAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeriveKeystoneAccountUnifiedAddressUseCase {
    suspend operator fun invoke(account: ZcashAccount): String =
        withContext(Dispatchers.Default) {
            val address =
                DerivationTool.getInstance().deriveUnifiedAddress(
                    viewingKey = account.ufvk,
                    network = VersionInfo.NETWORK
                )
            "${address.take(ADDRESS_MAX_LENGTH)}..."
        }
}
