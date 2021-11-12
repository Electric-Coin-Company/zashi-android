plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("zcash.android-build-conventions")
}

val packageName = "cash.z.ecc"

android {
    defaultConfig {
        applicationId = packageName
        versionCode = 1
        versionName = "1.0"
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
