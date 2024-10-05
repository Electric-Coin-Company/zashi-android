package co.electriccoin.zcash.ui.common.usecase

import android.content.Context
import co.electriccoin.zcash.spackle.ClipboardManagerUtil

class CopyToClipboardUseCase() {
    operator fun invoke(
        context: Context,
        tag: String,
        value: String
    ) = ClipboardManagerUtil.copyToClipboard(
        context = context,
        label = tag,
        value = value
    )
}
