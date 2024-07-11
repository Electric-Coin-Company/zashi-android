package publish

import com.google.gson.GsonBuilder

data class ChangelogEntry(
    val version: String,
    val date: String,
    val added: ChangelogEntrySection?,
    val changed: ChangelogEntrySection?,
    val fixed: ChangelogEntrySection?,
    val removed: ChangelogEntrySection?,
) {
    fun toInAppUpdateReleaseNotesText() =
        buildString {
            if (added != null) {
                appendChangeLogSection(added)
            }
            if (changed != null) {
                appendChangeLogSection(changed)
            }
            if (fixed != null) {
                appendChangeLogSection(fixed)
            }
            if (removed != null) {
                appendChangeLogSection(removed)
            }
        }

    private fun StringBuilder.appendChangeLogSection(section: ChangelogEntrySection) {
        appendLine(section.title)
        appendLine(section.content)
        appendLine()
    }

    fun toJsonString(): String =
        GsonBuilder()
            .serializeNulls()
            .create()
            .toJson(this).replace("\"", "\\\"")
}

data class ChangelogEntrySection(
    val title: String,
    val content: String
)
