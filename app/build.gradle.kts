plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("zcash.android-build-conventions")
    id("com.github.triplet.play")
}

val packageName = "cash.z.ecc"

android {
    defaultConfig {
        applicationId = packageName

        // If Google Play deployment is triggered, then these are placeholders which are overwritten
        // when the deployment runs
        versionCode = project.property("ZCASH_VERSION_CODE").toString().toInt()
        versionName = project.property("ZCASH_VERSION_NAME").toString()
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    flavorDimensions.add("network")

    productFlavors {
        // would rather name them "testnet" and "mainnet" but product flavor names cannot start with the word "test"
        create("zcashtestnet") {
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

    buildTypes {
        getByName("release").apply {
            isMinifyEnabled = project.property("IS_MINIFY_ENABLED").toString().toBoolean()
            proguardFiles.addAll(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    File("proguard-project.txt")
                )
            )
        }
    }

    signingConfigs {
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

    // TODO [#6]: Figure out how to move this into the build-conventions
    kotlinOptions {
        jvmTarget = libs.versions.java.get()
        allWarningsAsErrors = project.property("IS_TREAT_WARNINGS_AS_ERRORS").toString().toBoolean()
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugaring)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.uiLib)

    androidTestImplementation(libs.bundles.androidx.test)

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}

val googlePlayServiceKeyFilePath = project.property("ZCASH_GOOGLE_PLAY_SERVICE_KEY_FILE_PATH").toString()
if (googlePlayServiceKeyFilePath.isNotEmpty()) {
    // Update the versionName to reflect bumps in versionCode
    androidComponents {
        val versionCodeOffset = 0  // Change this to zero the final digit of the versionName
        onVariants { variant ->
            for (output in variant.outputs) {
                val processedVersionCode = output.versionCode.map { playVersionCode ->
                    val defaultVersionName = project.property("ZCASH_VERSION_NAME").toString()
                    // Version names will look like `myCustomVersionName.123`
                    playVersionCode?.let {
                        val delta = it - versionCodeOffset
                        if (delta < 0) {
                            defaultVersionName
                        } else {
                            "$defaultVersionName.$delta"
                        }
                    } ?: defaultVersionName
                }

                output.versionName.set(processedVersionCode)
            }
        }
    }

    configure<com.github.triplet.gradle.play.PlayPublisherExtension> {
        serviceAccountCredentials.set(File(googlePlayServiceKeyFilePath))

        // For safety, only allow deployment to internal testing track
        track.set("internal")

        // Automatically manage version incrementing
        resolutionStrategy.set(com.github.triplet.gradle.androidpublisher.ResolutionStrategy.AUTO)

        val deployMode = project.property("ZCASH_GOOGLE_PLAY_DEPLOY_MODE").toString()
        if ("build" == deployMode) {
            releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.DRAFT)
            // Prevent upload; only generates a build with the correct version number
            commit.set(false)
        } else if ("deploy" == deployMode) {
            releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.COMPLETED)
        }
    }
}
