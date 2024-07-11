package co.electriccoin.zcash.ui.common.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Changelog(
    val version: String,
    val date: LocalDate,
    val added: ChangelogSection?,
    val changed: ChangelogSection?,
    val fixed: ChangelogSection?,
    val removed: ChangelogSection?,
) {
    companion object {
        fun new(json: String) = Json.decodeFromString<Changelog>(json)
    }
}

@Serializable
data class ChangelogSection(
    val title: String,
    val content: String
)
