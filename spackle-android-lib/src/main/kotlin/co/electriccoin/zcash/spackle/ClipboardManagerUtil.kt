package co.electriccoin.zcash.spackle

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.runBlocking

object ClipboardManagerUtil {

    fun copyToClipboard(
        context: Context,
        label: String,
        value: String
    ) {
        Twig.info { "Copied to clipboard: label: $label, value: $value" }
        val clipboardManager = context.getSystemService(ClipboardManager::class.java)
        val data = ClipData.newPlainText(
            label,
            value
        )
        if (AndroidApiVersion.isAtLeastT) {
            // API 33 and later implement their system Toast UI.
            clipboardManager.setPrimaryClip(data)
        } else {
            // Blocking call is fine here, as we just moved to the IO thread to satisfy theStrictMode on an older API
            runBlocking { clipboardManager.setPrimaryClipSuspend(data) }
            Toast.makeText(
                context,
                value,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
