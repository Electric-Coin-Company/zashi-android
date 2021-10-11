// Note: due to a few limitations with build-conventions, there is some common Android Gradle Plugin
// configuration happening in the root build.gradle.kts with a subprojects block.

pluginManager.withPlugin("com.android.application") {
    project.the<com.android.build.gradle.AppExtension>().apply {
        configureBaseExtension()

        defaultConfig {
            minSdk = project.property("ANDROID_MIN_SDK_VERSION").toString().toInt()
            targetSdk = project.property("ANDROID_TARGET_SDK_VERSION").toString().toInt()

            // en_XA and ar_XB are pseudolocales for debugging.
            // The rest of the locales provides an explicit list of the languages to keep in the
            // final app.  Doing this will strip out additional locales from libraries like
            // Google Play Services and Firebase, which add unnecessary bloat.
            resourceConfigurations.addAll(listOf("en", "en-rUS", "en-rGB", "en-rAU", "en_XA", "ar_XB"))

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
                testInstrumentationRunnerArguments["clearPackageData"] = "true"
            }
        }
    }
}

pluginManager.withPlugin("com.android.library") {
    project.the<com.android.build.gradle.LibraryExtension>().apply {
        configureBaseExtension()

        defaultConfig {
            minSdk = project.property("ANDROID_MIN_SDK_VERSION").toString().toInt()
            targetSdk = project.property("ANDROID_TARGET_SDK_VERSION").toString().toInt()

            // The last two are for support of pseudolocales in debug builds.
            // If we add other localizations, they should be included in this list.
            // By explicitly setting supported locales, we strip out unused localizations from third party
            // libraries (e.g. play services)
            resourceConfigurations.addAll(listOf("en", "en-rUS", "en-rGB", "en-rAU", "en_XA", "ar_XB"))

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            consumerProguardFiles("proguard-consumer.txt")

            if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
                testInstrumentationRunnerArguments["clearPackageData"] = "true"
            }
        }
        testCoverage {
            jacocoVersion = project.property("JACOCO_VERSION").toString()
        }
    }
}

fun com.android.build.gradle.BaseExtension.configureBaseExtension() {
    compileSdkVersion(project.property("ANDROID_COMPILE_SDK_VERSION").toString().toInt())
    ndkVersion = project.property("ANDROID_NDK_VERSION").toString()

    compileOptions {
        val javaVersion = JavaVersion.toVersion(project.property("ANDROID_JVM_TARGET").toString())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    buildTypes {
        getByName("debug").apply {
            isTestCoverageEnabled = project.property("IS_COVERAGE_ENABLED").toString().toBoolean()
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

    // TODO [#22]: This doesn't work, so there's a duplicate in build.gradle.kts
    testOptions {
        animationsDisabled = true

        if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
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
                "META-INF/notice.txt",
            )
        )
    }
}
