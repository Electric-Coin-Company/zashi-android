package publish

import com.google.gson.GsonBuilder

data class ChangelogEntry(
    val version: String,
    val date: String,
    val added: String,
    val changed: String,
    val fixed: String,
    val removed: String,
) {
    fun toInAppUpdateReleaseNotesText() =
        buildString {
            if (added.isNotBlank()) {
                appendLine("Added:")
                appendLine(added)
                appendLine()
            }
            if (changed.isNotBlank()) {
                appendLine("Changed:")
                appendLine(changed)
                appendLine()
            }
            if (fixed.isNotBlank()) {
                appendLine("Fixed:")
                appendLine(fixed)
                appendLine()
            }
            if (removed.isNotBlank()) {
                appendLine("Removed:")
                appendLine(removed)
                appendLine()
            }
        }

    fun toJsonString(): String = GsonBuilder().create().toJson(this).replace("\"", "\\\"")
}
