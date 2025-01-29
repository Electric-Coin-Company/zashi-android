package co.electriccoin.zcash.ui.common.serialization.metada

import co.electriccoin.zcash.ui.common.model.Metadata
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

class MetadataSerializer(
    private val gson: Gson
) {
    fun serialize(
        outputStream: OutputStream,
        metadata: Metadata
    ) {
        val json = gson.toJson(metadata)
        outputStream.write(json.toByteArray(Charsets.UTF_8))
    }

    fun deserialize(inputStream: InputStream): Metadata {
        return InputStreamReader(inputStream).use { reader ->
            val temp = JsonParser.parseReader(reader).getAsJsonObject()
            gson.fromJson(temp, object : TypeToken<Metadata>() {}.type)
        }
    }
}
