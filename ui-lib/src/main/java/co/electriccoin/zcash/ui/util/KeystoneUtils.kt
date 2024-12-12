package co.electriccoin.zcash.ui.util

import com.keystone.sdk.KeystoneSDK
import com.keystone.sdk.KeystoneZcashSDK

val KeystoneSDK.zcash: KeystoneZcashSDK
    get() = KeystoneZcashSDK()
