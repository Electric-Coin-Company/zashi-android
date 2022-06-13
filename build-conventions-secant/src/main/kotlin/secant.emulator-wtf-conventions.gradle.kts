// Emulator WTF has min and max values that might differ from our project's
// These are determined by `ew-cli --models`

@Suppress("MagicNumber", "PropertyName", "VariableNaming")
val EMULATOR_WTF_MIN_SDK = 23

@Suppress("MagicNumber", "PropertyName", "VariableNaming")
val EMULATOR_WTF_MAX_SDK = 31

pluginManager.withPlugin("wtf.emulator.gradle") {
    project.the<wtf.emulator.EwExtension>().apply {
        val tokenString = project.properties["ZCASH_EMULATOR_WTF_API_KEY"].toString()
        if (tokenString.isNotEmpty()) {
            token.set(tokenString)
        }

        val libraryMinSdkVersion = run {
            val buildMinSdk = project.properties["ANDROID_LIB_MIN_SDK_VERSION"].toString().toInt()
            buildMinSdk.coerceAtLeast(EMULATOR_WTF_MIN_SDK).toString()
        }
        val targetSdkVersion = run {
            val buildTargetSdk = project.properties["ANDROID_TARGET_SDK_VERSION"].toString().toInt()
            buildTargetSdk.coerceAtMost(EMULATOR_WTF_MAX_SDK).toString()
        }

        devices.set(
            listOf(
                mapOf("model" to "Pixel2", "version" to libraryMinSdkVersion),
                mapOf("model" to "Pixel2", "version" to targetSdkVersion)
            )
        )
    }
}
