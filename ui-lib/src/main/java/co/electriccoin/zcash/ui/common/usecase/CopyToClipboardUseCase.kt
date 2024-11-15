package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.spackle.ClipboardManagerUtil

class CopyToClipboardUseCase(
    private val context: Context
) {
    operator fun invoke(
        tag: String,
        value: String
    ) = ClipboardManagerUtil.copyToClipboard(
        context = context,
        label = tag,
        value = value
    )
}
