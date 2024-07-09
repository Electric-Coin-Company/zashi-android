package co.electriccoin.zcash.spackle

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.PersistableBundle
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object ClipboardManagerUtil {
    private val extraIsSensitive: String
        get() =
            if (AndroidApiVersion.isAtLeastT) {
                ClipDescription.EXTRA_IS_SENSITIVE
            } else {
                "android.content.extra.IS_SENSITIVE"
            }

    fun copyToClipboard(
        context: Context,
        label: String,
        value: String
    ) {
        Twig.info { "Copied to clipboard: label: $label, value: $value" }
        val clipboardManager = context.getSystemService<ClipboardManager>()
        val data =
            ClipData.newPlainText(label, value).apply {
                description.extras =
                    PersistableBundle().apply {
                        putBoolean(extraIsSensitive, true)
                    }
            }
        if (AndroidApiVersion.isAtLeastT) {
            // API 33 and later implement their system Toast UI.
            clipboardManager.setPrimaryClip(data)
        } else {
            // Blocking call is fine here, as we just moved to the IO thread to satisfy theStrictMode on an older API
            runBlocking(Dispatchers.IO) {
                clipboardManager.setPrimaryClip(data)
            }
            Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
        }
    }
}
