@file:OptIn(ExperimentalSerializationApi::class)

package co.electriccoin.zcash.ui.common.model.near

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@JsonIgnoreUnknownKeys
@Serializable
data class ErrorDto(
    val message: String,
    val timestamp: String,
    val path: String,
)