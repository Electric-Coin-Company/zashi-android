enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        val isRepoRestrictionEnabled = true

        google {
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("androidx.navigation")
                    includeGroup("com.android.tools")
                    includeGroup("com.google.testing.platform")
                    includeGroupByRegex("androidx.*")
                    includeGroupByRegex("com\\.android.*")
                    includeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
        gradlePluginPortal {
            if (isRepoRestrictionEnabled) {
                content {
                    excludeGroup("androidx.navigation")
                    excludeGroup("com.android.tools")
                    excludeGroup("com.google.testing.platform")
                    excludeGroupByRegex("androidx.*")
                    excludeGroupByRegex("com\\.android.*")
                    excludeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
    }

    plugins {
        val androidGradlePluginVersion = extra["ANDROID_GRADLE_PLUGIN_VERSION"].toString()
        val detektVersion = extra["DETEKT_VERSION"].toString()
        val fulladleVersion = extra["FULLADLE_VERSION_MATCHER"].toString()
        val gradleVersionsPluginVersion = extra["GRADLE_VERSIONS_PLUGIN_VERSION"].toString()
        val kotlinVersion = extra["KOTLIN_VERSION"].toString()
        val playPublisherVersion = extra["PLAY_PUBLISHER_PLUGIN_VERSION_MATCHER"].toString()

        id("com.android.application") version (androidGradlePluginVersion) apply (false)
        id("com.android.library") version (androidGradlePluginVersion) apply (false)
        id("com.github.ben-manes.versions") version (gradleVersionsPluginVersion) apply (false)
        id("com.github.triplet.play") version (playPublisherVersion) apply (false)
        id("com.osacky.fulladle") version (fulladleVersion) apply (false)
        id("io.gitlab.arturbosch.detekt") version (detektVersion) apply (false)
        kotlin("android") version (kotlinVersion) apply (false)
        kotlin("jvm") version (kotlinVersion)
        kotlin("multiplatform") version (kotlinVersion)
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        val isRepoRestrictionEnabled = true

        google {
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("android.arch.lifecycle")
                    includeGroup("android.arch.core")
                    includeGroup("com.google.android.material")
                    includeGroup("com.google.testing.platform")
                    includeGroupByRegex("androidx.*")
                    includeGroupByRegex("com\\.android.*")
                }
            }
        }
        mavenCentral {
            if (isRepoRestrictionEnabled) {
                content {
                    excludeGroup("android.arch.lifecycle")
                    excludeGroup("android.arch.core")
                    excludeGroup("com.google.android.material")
                    excludeGroupByRegex("androidx.*")
                    excludeGroupByRegex("com\\.android.*")
                }
            }
        }
    }

    @Suppress("UnstableApiUsage", "MaxLineLength")
    versionCatalogs {
        create("libs") {
            val androidxActivityVersion = extra["ANDROIDX_ACTIVITY_VERSION"].toString()
            val androidxAnnotationVersion = extra["ANDROIDX_ANNOTATION_VERSION"].toString()
            val androidxAppcompatVersion = extra["ANDROIDX_APPCOMPAT_VERSION"].toString()
            val androidxComposeCompilerVersion = extra["ANDROIDX_COMPOSE_COMPILER_VERSION"].toString()
            val androidxComposeMaterial3Version = extra["ANDROIDX_COMPOSE_MATERIAL3_VERSION"].toString()
            val androidxComposeVersion = extra["ANDROIDX_COMPOSE_VERSION"].toString()
            val androidxCoreVersion = extra["ANDROIDX_CORE_VERSION"].toString()
            val androidxEspressoVersion = extra["ANDROIDX_ESPRESSO_VERSION"].toString()
            val androidxLifecycleVersion = extra["ANDROIDX_LIFECYCLE_VERSION"].toString()
            val androidxNavigationComposeVersion = extra["ANDROIDX_NAVIGATION_COMPOSE_VERSION"].toString()
            val androidxSecurityCryptoVersion = extra["ANDROIDX_SECURITY_CRYPTO_VERSION"].toString()
            val androidxSplashScreenVersion = extra["ANDROIDX_SPLASH_SCREEN_VERSION"].toString()
            val androidxTestCoreVersion = extra["ANDROIDX_TEST_CORE_VERSION"].toString()
            val androidxTestJunitVersion = extra["ANDROIDX_TEST_JUNIT_VERSION"].toString()
            val androidxTestOrchestratorVersion = extra["ANDROIDX_TEST_ORCHESTRATOR_VERSION"].toString()
            val androidxTestRunnerVersion = extra["ANDROIDX_TEST_RUNNER_VERSION"].toString()
            val androidxUiAutomatorVersion = extra["ANDROIDX_UI_AUTOMATOR_VERSION"].toString()
            val androidxWorkManagerVersion = extra["ANDROIDX_WORK_MANAGER_VERSION"].toString()
            val coreLibraryDesugaringVersion = extra["CORE_LIBRARY_DESUGARING_VERSION"].toString()
            val jacocoVersion = extra["JACOCO_VERSION"].toString()
            val javaVersion = extra["ANDROID_JVM_TARGET"].toString()
            val kotlinVersion = extra["KOTLIN_VERSION"].toString()
            val kotlinxCoroutinesVersion = extra["KOTLINX_COROUTINES_VERSION"].toString()
            val zcashBip39Version = extra["ZCASH_BIP39_VERSION"].toString()
            val zcashSdkVersion = extra["ZCASH_SDK_VERSION"].toString()
            val zxingVersion = extra["ZXING_VERSION"].toString()

            // Standalone versions
            version("jacoco", jacocoVersion)
            version("java", javaVersion)

            // Aliases
            alias("androidx-activity").to("androidx.activity:activity-ktx:$androidxActivityVersion")
            alias("androidx-activity-compose").to("androidx.activity:activity-compose:$androidxActivityVersion")
            alias("androidx-annotation").to("androidx.annotation:annotation:$androidxAnnotationVersion")
            alias("androidx-appcompat").to("androidx.appcompat:appcompat:$androidxAppcompatVersion")
            alias("androidx-compose-foundation").to("androidx.compose.foundation:foundation:$androidxComposeVersion")
            alias("androidx-compose-material").to("androidx.compose.material:material:$androidxComposeVersion")
            alias("androidx-compose-material3").to("androidx.compose.material3:material3:$androidxComposeMaterial3Version")
            alias("androidx-compose-material-icons-core").to("androidx.compose.material:material-icons-core:$androidxComposeVersion")
            alias("androidx-compose-material-icons-extended").to("androidx.compose.material:material-icons-extended:$androidxComposeVersion")
            alias("androidx-compose-tooling").to("androidx.compose.ui:ui-tooling:$androidxComposeVersion")
            alias("androidx-compose-ui").to("androidx.compose.ui:ui:$androidxComposeVersion")
            alias("androidx-compose-compiler").to("androidx.compose.compiler:compiler:$androidxComposeCompilerVersion")
            alias("androidx-core").to("androidx.core:core-ktx:$androidxCoreVersion")
            alias("androidx-lifecycle-livedata").to("androidx.lifecycle:lifecycle-livedata-ktx:$androidxLifecycleVersion")
            alias("androidx-navigation-compose").to("androidx.navigation:navigation-compose:$androidxNavigationComposeVersion")
            alias("androidx-security-crypto").to("androidx.security:security-crypto-ktx:$androidxSecurityCryptoVersion")
            alias("androidx-splash").to("androidx.core:core-splashscreen:$androidxSplashScreenVersion")
            alias("androidx-viewmodel-compose").to("androidx.lifecycle:lifecycle-viewmodel-compose:$androidxLifecycleVersion")
            alias("androidx-workmanager").to("androidx.work:work-runtime-ktx:$androidxWorkManagerVersion")
            alias("desugaring").to("com.android.tools:desugar_jdk_libs:$coreLibraryDesugaringVersion")
            alias("kotlin-stdlib").to("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
            alias("kotlin-reflect").to("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
            alias("kotlin-test").to("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
            alias("kotlinx-coroutines-android").to("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")
            alias("kotlinx-coroutines-core").to("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            alias("zcash-sdk").to("cash.z.ecc.android:zcash-android-sdk:$zcashSdkVersion")
            alias("zcash-bip39").to("cash.z.ecc.android:kotlin-bip39:$zcashBip39Version")
            alias("zcash-walletplgns").to("cash.z.ecc.android:zcash-android-wallet-plugins:$zcashBip39Version")
            alias("zxing").to("com.google.zxing:core:$zxingVersion")
            // Test libraries
            alias("androidx-compose-test-junit").to("androidx.compose.ui:ui-test-junit4:$androidxComposeVersion")
            alias("androidx-compose-test-manifest").to("androidx.compose.ui:ui-test-manifest:$androidxComposeVersion")
            // Cannot use espresso-contrib, because it causes a build failure
            //alias("androidx-espresso-contrib").to("androidx.test.espresso:espresso-contrib:$androidxEspressoVersion")
            alias("androidx-espresso-core").to("androidx.test.espresso:espresso-core:$androidxEspressoVersion")
            alias("androidx-espresso-intents").to("androidx.test.espresso:espresso-intents:$androidxEspressoVersion")
            alias("androidx-test-core").to("androidx.test:core:$androidxTestCoreVersion")
            alias("androidx-test-junit").to("androidx.test.ext:junit:$androidxTestJunitVersion")
            alias("androidx-test-orchestrator").to("androidx.test:orchestrator:$androidxTestOrchestratorVersion")
            alias("androidx-test-runner").to("androidx.test:runner:$androidxTestRunnerVersion")
            alias("androidx-uiAutomator").to("androidx.test.uiautomator:uiautomator-v18:$androidxUiAutomatorVersion")
            alias("kotlinx-coroutines-test").to("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")

            // Bundles
            bundle(
                "androidx-compose-core",
                listOf(
                    "androidx-compose-compiler",
                    "androidx-compose-foundation",
                    "androidx-compose-material3",
                    "androidx-compose-tooling",
                    "androidx-compose-ui",
                )
            )
            bundle(
                "androidx-compose-extended",
                listOf(
                    "androidx-activity-compose",
                    "androidx-compose-material-icons-core",
                    "androidx-compose-material-icons-extended",
                    "androidx-navigation-compose",
                    "androidx-viewmodel-compose"
                )
            )
            bundle(
                "androidx-test",
                listOf(
                    "androidx-espresso-core",
                    "androidx-espresso-intents",
                    "androidx-test-core",
                    "androidx-test-junit",
                    "androidx-test-runner"
                )
            )
        }
    }
}

rootProject.name = "zcash-android-app"

includeBuild("build-convention")

include("app")
include("build-info-lib")
include("preference-api-lib")
include("preference-impl-android-lib")
include("sdk-ext-lib")
include("spackle-lib")
include("ui-design-lib")
include("ui-lib")

if (extra["IS_SDK_INCLUDED_BUILD"].toString().toBoolean()) {
    // Currently assume the SDK is up one level with a hardcoded directory name
    // If this becomes problematic, `IS_SDK_INCLUDED_BUILD` could be turned into a path
    // instead.
    includeBuild("../zcash-android-sdk") {
        dependencySubstitution {
            substitute(module("cash.z.ecc.android:zcash-android-sdk")).using(project(":sdk-lib"))
        }
    }
}
