import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

pluginManager.withPlugin("com.android.application") {
    project.the<com.android.build.gradle.AppExtension>().apply {
        configureBaseExtension(isLibrary = false)

        defaultConfig {
            minSdk = project.property("ANDROID_APP_MIN_SDK_VERSION").toString().toInt()
            targetSdk = project.property("ANDROID_TARGET_SDK_VERSION").toString().toInt()

            // en_XA and ar_XB are pseudolocales for debugging.
            // The rest of the locales provides an explicit list of the languages to keep in the
            // final app.  Doing this will strip out additional locales from libraries like
            // Google Play Services and Firebase, which add unnecessary bloat.
            resourceConfigurations.addAll(listOf("en", "en-rUS", "en-rGB", "en-rAU", "en_XA", "ar_XB"))

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            testInstrumentationRunnerArguments["useTestStorageService"] = "true"
            if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
                testInstrumentationRunnerArguments["clearPackageData"] = "true"
            }
        }
    }
}

pluginManager.withPlugin("com.android.library") {
    project.the<com.android.build.gradle.LibraryExtension>().apply {
        configureBaseExtension(isLibrary = true)

        defaultConfig {
            minSdk = project.property("ANDROID_LIB_MIN_SDK_VERSION").toString().toInt()
            targetSdk = project.property("ANDROID_TARGET_SDK_VERSION").toString().toInt()

            // The last two are for support of pseudolocales in debug builds.
            // If we add other localizations, they should be included in this list.
            // By explicitly setting supported locales, we strip out unused localizations from third party
            // libraries (e.g. play services)
            resourceConfigurations.addAll(listOf("en", "en-rUS", "en-rGB", "en-rAU", "en_XA", "ar_XB"))

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            consumerProguardFiles("proguard-consumer.txt")

            testInstrumentationRunnerArguments["useTestStorageService"] = "true"
            if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
                testInstrumentationRunnerArguments["clearPackageData"] = "true"
            }
        }
        testCoverage {
            jacocoVersion = project.property("JACOCO_VERSION").toString()
        }
    }
}

pluginManager.withPlugin("com.android.test") {
    project.the<com.android.build.gradle.TestExtension>().apply {
        configureBaseExtension(isLibrary = true)

        defaultConfig {
            minSdk = project.property("ANDROID_LIB_MIN_SDK_VERSION").toString().toInt()
            targetSdk = project.property("ANDROID_TARGET_SDK_VERSION").toString().toInt()

            // The last two are for support of pseudolocales in debug builds.
            // If we add other localizations, they should be included in this list.
            // By explicitly setting supported locales, we strip out unused localizations from third party
            // libraries (e.g. play services)
            resourceConfigurations.addAll(listOf("en", "en-rUS", "en-rGB", "en-rAU", "en_XA", "ar_XB"))

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            testInstrumentationRunnerArguments["useTestStorageService"] = "true"
            if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
                testInstrumentationRunnerArguments["clearPackageData"] = "true"
            }
        }
        testCoverage {
            jacocoVersion = project.property("JACOCO_VERSION").toString()
        }
    }
}

@Suppress("LongMethod")
fun com.android.build.gradle.BaseExtension.configureBaseExtension(isLibrary: Boolean) {
    compileSdkVersion(project.property("ANDROID_COMPILE_SDK_VERSION").toString().toInt())
    ndkVersion = project.property("ANDROID_NDK_VERSION").toString()

    compileOptions {
        val javaVersion = JavaVersion.toVersion(project.property("ANDROID_JVM_TARGET").toString())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    buildTypes {
        getByName("debug").apply {
            val coverageEnabled =
                project.property("IS_ANDROID_INSTRUMENTATION_TEST_COVERAGE_ENABLED").toString().toBoolean()
            isTestCoverageEnabled = coverageEnabled
            enableAndroidTestCoverage = coverageEnabled
            enableUnitTestCoverage = coverageEnabled
        }
    }

    signingConfigs {
        val debugKeystorePath = project.property("ZCASH_DEBUG_KEYSTORE_PATH").toString()
        val isExplicitDebugSigningEnabled = !debugKeystorePath.isNullOrBlank()
        if (isExplicitDebugSigningEnabled) {
            // If this block doesn't execute, the output will still be signed with the default keystore
            getByName("debug").apply {
                storeFile = File(debugKeystorePath)
            }
        }
    }

    testOptions {
        animationsDisabled = true

        if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }

        @Suppress("UnstableApiUsage")
        managedDevices {
            @Suppress("MagicNumber", "PropertyName", "VariableNaming")
            val MANAGED_DEVICES_MIN_SDK = 27

            val testDeviceMinSdkVersion = run {
                val buildMinSdk = if (isLibrary) {
                    project.properties["ANDROID_LIB_MIN_SDK_VERSION"].toString().toInt()
                } else {
                    project.properties["ANDROID_APP_MIN_SDK_VERSION"].toString().toInt()
                }
                buildMinSdk.coerceAtLeast(MANAGED_DEVICES_MIN_SDK)
            }
            val testDeviceMaxSdkVersion = project.properties["ANDROID_TARGET_SDK_VERSION"].toString().toInt()

            devices {
                create<ManagedVirtualDevice>("pixel2Min") {
                    device = "Pixel 2"
                    apiLevel = testDeviceMinSdkVersion
                    systemImageSource = "google"
                }
                create<ManagedVirtualDevice>("pixel2Target") {
                    device = "Pixel 2"
                    apiLevel = testDeviceMaxSdkVersion
                    systemImageSource = "google"
                }
            }

            groups {
                create("defaultDevices") {
                    targetDevices.addAll(devices.toList())
                }
            }
        }
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/AL2.0",
                "META-INF/ASL2.0",
                "META-INF/DEPENDENCIES",
                "META-INF/LGPL2.1",
                "META-INF/LICENSE",
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/license.txt",
                "META-INF/notice.txt"
            )
        )
    }

    if (this is CommonExtension<*, *, *, *>) {
        kotlinOptions {
            jvmTarget = project.property("ANDROID_JVM_TARGET").toString()
            allWarningsAsErrors = project.property("ZCASH_IS_TREAT_WARNINGS_AS_ERRORS").toString().toBoolean()
            freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        }
    }
}

fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
