package publish

import com.google.gson.GsonBuilder

private const val RELEASE_NOTES_MAX_LENGTH = 500
private const val NEW_LINE_SIGN = "\n"

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
        appendLineIfCan(section.title)
        appendLineIfCan(section.content)
    }

    private fun StringBuilder.appendLineIfCan(line: String) {
        if (length + line.length <= RELEASE_NOTES_MAX_LENGTH) {
            append(line)
        }
        if (length + NEW_LINE_SIGN.length <= RELEASE_NOTES_MAX_LENGTH) {
            append(NEW_LINE_SIGN)
        }
    }

    fun toJsonString(): String =
        GsonBuilder()
            .serializeNulls()
            .create()
            .toJson(this)
            .replace("\"", "\\\"")
}

data class ChangelogEntrySection(
    val title: String,
    val content: String
)
