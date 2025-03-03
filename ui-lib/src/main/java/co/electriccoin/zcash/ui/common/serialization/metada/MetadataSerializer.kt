package co.electriccoin.zcash.ui.common.serialization.metada

import co.electriccoin.zcash.ui.common.model.Metadata
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

class MetadataSerializer {
    @OptIn(ExperimentalSerializationApi::class)
    fun serialize(
        outputStream: OutputStream,
        metadata: Metadata
    ) {
        Json.encodeToStream(metadata, outputStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun deserialize(inputStream: InputStream): Metadata {
        return Json.decodeFromStream(inputStream)
    }
}
