package co.electriccoin.zcash.ui.common.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Changelog(
    val version: Int,
    val date: String,
    val added: String,
    val changed: String,
    val fixed: String,
    val removed: String,
) {
    companion object {
        fun new(json: String) = Json.decodeFromString<Changelog>(json)
    }
}
