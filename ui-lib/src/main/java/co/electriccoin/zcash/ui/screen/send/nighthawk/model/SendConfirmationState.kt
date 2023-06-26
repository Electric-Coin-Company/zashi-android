package co.electriccoin.zcash.ui.screen.send.nighthawk.model

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import co.electriccoin.zcash.ui.R

sealed class SendConfirmationState(@StringRes val titleResId: Int, @RawRes val animRes: Int) {
    object Sending: SendConfirmationState(R.string.ns_sending, R.raw.lottie_sending)
    object Failed: SendConfirmationState(R.string.ns_failed, R.raw.lottie_send_failure)
    data class Success(val id: Long): SendConfirmationState(R.string.ns_success, R.raw.lottie_send_success)
}