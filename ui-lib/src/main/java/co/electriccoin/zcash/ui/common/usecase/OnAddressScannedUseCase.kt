package co.electriccoin.zcash.ui.common.usecase

import cash.z.ecc.android.sdk.type.AddressType
import co.electriccoin.zcash.ui.NavigationRouter
import co.electriccoin.zcash.ui.screen.contact.AddContactArgs
import co.electriccoin.zcash.ui.screen.scan.Scan
import co.electriccoin.zcash.ui.screen.scan.ScanFlow
import co.electriccoin.zcash.ui.screen.send.Send

class OnAddressScannedUseCase(
    private val navigationRouter: NavigationRouter,
    private val prefillSend: PrefillSendUseCase
) {
    operator fun invoke(
        address: String,
        addressType: AddressType,
        scanArgs: Scan
    ) {
        require(addressType is AddressType.Valid)

        when (scanArgs.flow) {
            ScanFlow.SEND -> {
                prefillSend.request(PrefillSendData.FromAddressScan(address = address))
                navigationRouter.back()
            }

            ScanFlow.ADDRESS_BOOK -> {
                navigationRouter.replace(AddContactArgs(address))
            }

            ScanFlow.HOMEPAGE -> {
                navigationRouter.replace(
                    Send(
                        address,
                        when (addressType) {
                            AddressType.Shielded -> cash.z.ecc.sdk.model.AddressType.UNIFIED
                            AddressType.Tex -> cash.z.ecc.sdk.model.AddressType.TEX
                            AddressType.Transparent -> cash.z.ecc.sdk.model.AddressType.TRANSPARENT
                            AddressType.Unified -> cash.z.ecc.sdk.model.AddressType.UNIFIED
                            is AddressType.Invalid -> cash.z.ecc.sdk.model.AddressType.UNIFIED
                        }
                    )
                )
            }
        }
    }
}
