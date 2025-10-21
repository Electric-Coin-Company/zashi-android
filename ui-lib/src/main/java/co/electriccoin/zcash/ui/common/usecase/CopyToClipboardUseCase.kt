package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.spackle.ClipboardManagerUtil

class CopyToClipboardUseCase(
    private val context: Context
) {
    operator fun invoke(value: String) {
        ClipboardManagerUtil.copyToClipboard(
            context = context,
            value = value
        )
    }
}
