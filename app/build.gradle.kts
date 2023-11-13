import co.electriccoin.zcash.Git
import com.android.build.api.variant.BuildConfigField
import com.android.build.api.variant.ResValue
import java.util.Locale

plugins {
    id("com.android.application")
    kotlin("android")
    id("secant.android-build-conventions")
    id("com.osacky.fladle")
    id("wtf.emulator.gradle")
    id("secant.emulator-wtf-conventions")
    id("secant.publish-conventions")
}

val hasFirebaseApiKeys = run {
    val srcDir = File(project.projectDir, "src")
    val releaseApiKey = File(File(srcDir, "release"), "google-services.json")
    val debugApiKey = File(File(srcDir, "debug"), "google-services.json")

    val result = releaseApiKey.exists() && debugApiKey.exists()

    if (!result) {
        project.logger.info("Firebase API keys not found. Crashlytics will not be enabled. To enable " +
            "Firebase, add the API keys for ${releaseApiKey.path} and ${debugApiKey.path}.")
    }

    result
}

if (hasFirebaseApiKeys) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
}

val packageName = project.property("ZCASH_RELEASE_PACKAGE_NAME").toString()

val testnetNetworkName = "Testnet"

android {
    namespace = "co.electriccoin.zcash.app"

    defaultConfig {
        applicationId = packageName

        // If Google Play deployment is triggered, then these are placeholders which are overwritten
        // when the deployment runs
        versionCode = project.property("ZCASH_VERSION_CODE").toString().toInt()
        versionName = project.property("ZCASH_VERSION_NAME").toString()

        if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
            testInstrumentationRunnerArguments["clearPackageData"] = "true"
        }

        testInstrumentationRunner = "co.electriccoin.zcash.test.ZcashUiTestRunner"
    }

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        testOptions {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions.add("network")

    val testNetFlavorName = "zcashtestnet"
    productFlavors {
        // would rather name them "testnet" and "mainnet" but product flavor names cannot start with the word "test"
        create(testNetFlavorName) {
            dimension = "network"
            applicationId = "$packageName.testnet" // allow to be installed alongside mainnet
            matchingFallbacks.addAll(listOf("zcashtestnet", "debug"))
        }

        create("zcashmainnet") {
            dimension = "network"
            applicationId = packageName
            matchingFallbacks.addAll(listOf("zcashmainnet", "release"))
        }
    }

    val releaseKeystorePath = project.property("ZCASH_RELEASE_KEYSTORE_PATH").toString()
    val releaseKeystorePassword = project.property("ZCASH_RELEASE_KEYSTORE_PASSWORD").toString()
    val releaseKeyAlias = project.property("ZCASH_RELEASE_KEY_ALIAS").toString()
    val releaseKeyAliasPassword =
        project.property("ZCASH_RELEASE_KEY_ALIAS_PASSWORD").toString()
    val isReleaseSigningConfigured = listOf(
        releaseKeystorePath,
        releaseKeystorePassword,
        releaseKeyAlias,
        releaseKeyAliasPassword
    ).all { !it.isNullOrBlank() }

    signingConfigs {
        if (isReleaseSigningConfigured) {
            // If this block doesn't execute, the output will be unsigned
            create("release").apply {
                storeFile = File(releaseKeystorePath)
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyAliasPassword
            }
        }
    }

    buildTypes {
        getByName("debug").apply {
            // Note that the build-conventions defines the res configs
            isPseudoLocalesEnabled = true

            // Suffixing app package name and version to avoid collisions with other installed Zcash
            // apps (e.g. from Google Play)
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"
        }
        getByName("release").apply {
            isMinifyEnabled = project.property("IS_MINIFY_ENABLED").toString().toBoolean()
            isShrinkResources = project.property("IS_MINIFY_ENABLED").toString().toBoolean()
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-project.txt"
            )

            val isReleaseBuildDebuggable = project.property("IS_RELEASE_BUILD_DEBUGGABLE")
                .toString().toBoolean()
            isDebuggable = isReleaseBuildDebuggable

            val isSignReleaseBuildWithDebugKey = project.property("IS_SIGN_RELEASE_BUILD_WITH_DEBUG_KEY")
                .toString().toBoolean()
            if (isReleaseSigningConfigured) {
                signingConfig = signingConfigs.getByName("release")
            } else if (isSignReleaseBuildWithDebugKey) {
                // Warning: in this case is the release build signed with the debug key
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }

    // Resolve final app name
    applicationVariants.all {
        val defaultAppName = project.property("ZCASH_RELEASE_APP_NAME").toString()
        val debugAppNameSuffix = project.property("ZCASH_DEBUG_APP_NAME_SUFFIX").toString()
        val supportEmailAddress = project.property("ZCASH_SUPPORT_EMAIL_ADDRESS").toString()
        when (this.name) {
            "zcashtestnetDebug" -> {
                resValue("string", "app_name", "$defaultAppName ($testnetNetworkName)$debugAppNameSuffix")
            }
            "zcashmainnetDebug" -> {
                resValue("string", "app_name", "$defaultAppName$debugAppNameSuffix")
            }
            "zcashtestnetRelease" -> {
                resValue("string", "app_name", "$defaultAppName ($testnetNetworkName)")
            }
            "zcashmainnetRelease" -> {
                resValue("string", "app_name", defaultAppName)
            }
        }
        resValue("string", "support_email_address", supportEmailAddress)
    }

    testCoverage {
        jacocoVersion = project.property("JACOCO_VERSION").toString()
    }
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core)
    // just to support baseline profile installation needed by benchmark tests
    implementation(libs.androidx.profileinstaller)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.zcash.sdk) // just to configure logging
    implementation(projects.crashAndroidLib)
    implementation(projects.preferenceApiLib)
    implementation(projects.preferenceImplAndroidLib)
    implementation(projects.spackleAndroidLib)
    implementation(projects.uiLib)

    androidTestImplementation(projects.testLib)

    androidTestUtil(libs.androidx.test.services) {
        artifact {
            type = "apk"
        }
    }

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}

androidComponents {
    onVariants { variant ->
        for (output in variant.outputs) {
            // Configure strict mode in runtime
            variant.buildConfigFields.put(
                "IS_STRICT_MODE_CRASH_ENABLED",
                BuildConfigField(
                    type = "boolean",
                    value = project.property("IS_CRASH_ON_STRICT_MODE_VIOLATION").toString(),
                    comment = "Whether is the strict mode enabled"
                )
            )

            variant.resValues.put(
                // Key matches the one in crash-android-lib/src/res/values/bools.xml
                variant.makeResValueKey("bool", "co_electriccoin_zcash_crash_is_firebase_enabled"),
                ResValue(value = hasFirebaseApiKeys.toString())
            )

            if (project.property("ZCASH_GOOGLE_PLAY_SERVICE_ACCOUNT_KEY").toString().isNotEmpty() &&
                project.property("ZCASH_GOOGLE_PLAY_PUBLISHER_API_KEY").toString().isNotEmpty()
            ) {
                // Update the versionName to reflect bumps in versionCode

                val versionCodeOffset = 0  // Change this to zero the final digit of the versionName

                val processedVersionCode = output.versionCode.map { playVersionCode ->
                    val defaultVersionName = project.property("ZCASH_VERSION_NAME").toString()
                    // Version names will look like `myCustomVersionName.123`
                    @Suppress("UNNECESSARY_SAFE_CALL")
                    playVersionCode?.let {
                        val delta = it - versionCodeOffset
                        if (delta < 0) {
                            defaultVersionName
                        } else {
                            "$defaultVersionName ($delta)"
                        }
                    } ?: defaultVersionName
                }

                output.versionName.set(processedVersionCode)

                val gitInfo = Git.newInfo(Git.MAIN, parent!!.projectDir)
                output.versionCode.set(gitInfo.commitCount)
            }
        }

        variant.packaging.resources.excludes.addAll(listOf(
            ".readme",
        ))

        if (variant.name.lowercase(Locale.US).contains("release")) {
            variant.packaging.resources.excludes.addAll(listOf(
                "**/*.kotlin_metadata",
                "DebugProbesKt.bin",
                "META-INF/*.kotlin_module",
                "META-INF/*.version",
                "META-INF/android.arch**",
                "META-INF/androidx**",
                "META-INF/com.android**",
                "META-INF/com.google.android.material_material.version",
                "META-INF/com.google.dagger_dagger.version",
                "build-data.properties",
                "core.properties",
                "firebase-**.properties",
                "kotlin-tooling-metadata.json",
                "kotlin/**",
                "play-services-**.properties",
                "protolite-well-known-types.properties",
                "transport-api.properties",
                "transport-backend-cct.properties",
                "transport-runtime.properties"
            ))
        }
    }
}

fladle {
    // Firebase Test Lab has min and max values that might differ from our project's
    // These are determined by `gcloud firebase test android models list`
    @Suppress("MagicNumber", "VariableNaming")
    val FIREBASE_TEST_LAB_MIN_SDK = 27 // Minimum for Pixel2.arm device

    @Suppress("MagicNumber", "VariableNaming")
    val FIREBASE_TEST_LAB_MAX_SDK = 33

    val minSdkVersion = run {
        val buildMinSdk = project.properties["ANDROID_MIN_SDK_VERSION"].toString().toInt()
        buildMinSdk.coerceAtLeast(FIREBASE_TEST_LAB_MIN_SDK).toString()
    }
    val targetSdkVersion = run {
        val buildTargetSdk = project.properties["ANDROID_TARGET_SDK_VERSION"].toString().toInt()
        buildTargetSdk.coerceAtMost(FIREBASE_TEST_LAB_MAX_SDK).toString()
    }

    val firebaseTestLabKeyPath = project.properties["ZCASH_FIREBASE_TEST_LAB_API_KEY_PATH"].toString()
    val firebaseProject = project.properties["ZCASH_FIREBASE_TEST_LAB_PROJECT"].toString()

    if (firebaseTestLabKeyPath.isNotEmpty()) {
        serviceAccountCredentials.set(File(firebaseTestLabKeyPath))
    } else if (firebaseProject.isNotEmpty()) {
        projectId.set(firebaseProject)
    }

    @Suppress("MagicNumber")
    flakyTestAttempts.set(1)

    configs {
        val buildDirectory = layout.buildDirectory.get().asFile
        create("sanityConfigDebug") {
            clearPropertiesForSanityRobo()

            debugApk.set(
                project.provider {
                    "${buildDirectory}/outputs/apk/zcashmainnet/debug/app-zcashmainnet-debug.apk"
                }
            )

            testTimeout.set("3m")

            devices.addAll(
                mapOf("model" to "Pixel2.arm", "version" to minSdkVersion),
                mapOf("model" to "Pixel2.arm", "version" to targetSdkVersion)
            )

            flankVersion.set(libs.versions.flank.get())
        }
        create("sanityConfigRelease") {
            clearPropertiesForSanityRobo()

            debugApk.set(
                project.provider {
                    "$buildDirectory" +
                        "/outputs/apk_from_bundle/zcashmainnetRelease/app-zcashmainnet-release-universal.apk"
                }
            )

            testTimeout.set("3m")

            devices.addAll(
                mapOf("model" to "Pixel2", "version" to minSdkVersion),
                mapOf("model" to "Pixel2.arm", "version" to targetSdkVersion)
            )

            flankVersion.set(libs.versions.flank.get())
        }
    }
}
