package publish

import com.google.gson.GsonBuilder

private const val RELEASE_NOTES_MAX_LENGTH = 500
private const val NEW_LINE = "\n"

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
        appendIfCan(section.title)
        appendIfCan(section.content)
        appendIfCan(NEW_LINE)
    }

    private fun StringBuilder.appendIfCan(content: String) {
        if (length + content.length <= RELEASE_NOTES_MAX_LENGTH) {
            append(content)
        } else {
            println("WARN: Some in-app update release notes have been skipped: $content")
        }
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
