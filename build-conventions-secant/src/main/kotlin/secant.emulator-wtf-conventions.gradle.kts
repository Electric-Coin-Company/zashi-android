import com.android.build.gradle.internal.cxx.configure.buildTypeOf
import com.android.build.gradle.internal.dsl.decorator.androidPluginDslDecorator

// Emulator WTF has min and max values that might differ from our project's
// These are determined by `ew-cli --models`

@Suppress("MagicNumber", "PropertyName", "VariableNaming")
val EMULATOR_WTF_MIN_SDK = 23

@Suppress("MagicNumber", "PropertyName", "VariableNaming")
val EMULATOR_WTF_MAX_SDK = 31

internal val className = this::class.simpleName

pluginManager.withPlugin("wtf.emulator.gradle") {
    project.the<wtf.emulator.EwExtension>().apply {
        val tokenString = project.properties["ZCASH_EMULATOR_WTF_API_KEY"].toString()
        if (tokenString.isNotEmpty()) {
            token.set(tokenString)
        }

        if (project.properties["IS_ANDROID_INSTRUMENTATION_TEST_COVERAGE_ENABLED"].toString().toBoolean()) {
            withCoverage.set(true)
            environmentVariables.set(mapOf("useTestStorageService" to "false"))
        }

        val buildMinSdk = if (pluginManager.hasPlugin("com.android.application")) {
            project.properties["ANDROID_APP_MIN_SDK_VERSION"].toString().toInt()
        } else if (pluginManager.hasPlugin("com.android.test")) {
            project.properties["ANDROID_APP_MIN_SDK_VERSION"].toString().toInt()
        } else if (pluginManager.hasPlugin("com.android.library")) {
            project.properties["ANDROID_LIB_MIN_SDK_VERSION"].toString().toInt()
        } else {
            throw IllegalArgumentException("Unsupported plugin type. Make sure that the plugin type you've added is" +
                " supported by ${this.javaClass.name}.")
        }

        val moduleMinSdkVersion = run {
            buildMinSdk.coerceAtLeast(EMULATOR_WTF_MIN_SDK).toString()
        }
        val targetSdkVersion = run {
            val buildTargetSdk = project.properties["ANDROID_TARGET_SDK_VERSION"].toString().toInt()
            buildTargetSdk.coerceAtMost(EMULATOR_WTF_MAX_SDK).toString()
        }

        devices.set(
            listOf(
                mapOf("model" to "Pixel2", "version" to moduleMinSdkVersion),
                mapOf("model" to "Pixel2", "version" to targetSdkVersion)
            )
        )
    }
}
