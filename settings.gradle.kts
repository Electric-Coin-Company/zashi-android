enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }

    plugins {
        val detektVersion = extra["DETEKT_VERSION"].toString()
        val gradleVersionsPluginVersion = extra["GRADLE_VERSIONS_PLUGIN_VERSION"].toString()
        val kotlinVersion = extra["KOTLIN_VERSION"].toString()

        kotlin("jvm") version (kotlinVersion)
        id("com.github.ben-manes.versions") version (gradleVersionsPluginVersion) apply (false)
        id("io.gitlab.arturbosch.detekt") version (detektVersion) apply (false)
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    @Suppress("UnstableApiUsage", "MaxLineLength")
    versionCatalogs {
        create("libs") {
            val androidxActivityVersion = extra["ANDROIDX_ACTIVITY_VERSION"].toString()
            val androidxAnnotationVersion = extra["ANDROIDX_ANNOTATION_VERSION"].toString()
            val androidxAppcompatVersion = extra["ANDROIDX_APPCOMPAT_VERSION"].toString()
            val androidxComposeVersion = extra["ANDROIDX_COMPOSE_VERSION"].toString()
            val androidxCoreVersion = extra["ANDROIDX_CORE_VERSION"].toString()
            val androidxEspressoVersion = extra["ANDROIDX_ESPRESSO_VERSION"].toString()
            val androidxTestJunitVersion = extra["ANDROIDX_TEST_JUNIT_VERSION"].toString()
            val androidxTestOrchestratorVersion = extra["ANDROIDX_ESPRESSO_VERSION"].toString()
            val androidxUiAutomatorVersion = extra["ANDROIDX_UI_AUTOMATOR_VERSION"].toString()
            val googleMaterialVersion = extra["GOOGLE_MATERIAL_VERSION"].toString()
            val jacocoVersion = extra["JACOCO_VERSION"].toString()
            val javaVersion = extra["ANDROID_JVM_TARGET"].toString()
            val kotlinVersion = extra["KOTLIN_VERSION"].toString()
            val kotlinxCoroutinesVersion = extra["KOTLINX_COROUTINES_VERSION"].toString()
            val zcashSdkVersion = extra["ZCASH_SDK_VERSION"].toString()

            // Standalone versions
            version("compose", androidxComposeVersion)
            version("jacoco", jacocoVersion)
            version("java", javaVersion)

            // Aliases
            alias("androidx-activity").to("androidx.activity:activity-ktx:${androidxActivityVersion}")
            alias("androidx-activity-compose").to("androidx.activity:activity-compose:$androidxActivityVersion")
            alias("androidx-appcompat").to("androidx.appcompat:appcompat:$androidxAppcompatVersion")
            alias("androidx-annotation").to("androidx.annotation:annotation:${androidxAnnotationVersion}")
            alias("androidx-compose-foundation").to("androidx.compose.foundation:foundation:$androidxComposeVersion")
            alias("androidx-compose-material").to("androidx.compose.material:material:$androidxComposeVersion")
            alias("androidx-compose-material-icons-core").to("androidx.compose.material:material-icons-core:$androidxComposeVersion")
            alias("androidx-compose-tooling").to("androidx.compose.ui:ui-tooling-preview:$androidxComposeVersion")
            alias("androidx-compose-ui").to("androidx.compose.ui:ui:$androidxComposeVersion")
            alias("androidx-core").to("androidx.core:core-ktx:${androidxCoreVersion}")
            alias("androidx-viewmodel-compose").to("androidx.activity:activity-compose:$androidxActivityVersion")
            alias("google-material").to("com.google.android.material:material:${googleMaterialVersion}")
            alias("kotlin").to("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
            alias("kotlinx-coroutines-android").to("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")
            alias("kotlinx-coroutines-core").to("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            alias("zcash").to("cash.z.ecc.android:zcash-android-sdk:${zcashSdkVersion}")

            // Test libraries
            alias("androidx-espresso-contrib").to("androidx.test.espresso:espresso-contrib:${androidxEspressoVersion}")
            alias("androidx-espresso-core").to("androidx.test.espresso:espresso-core:${androidxEspressoVersion}")
            alias("androidx-espresso-intents").to("androidx.test.espresso:espresso-intents:${androidxEspressoVersion}")
            alias("androidx-junit").to("androidx.test.ext:junit:${androidxTestJunitVersion}")
            alias("androidx-testOrchestrator").to("androidx.test:orchestrator:${androidxTestOrchestratorVersion}")
            alias("androidx-uiAutomator").to("androidx.test.uiautomator:uiautomator-v18:${androidxUiAutomatorVersion}")
            alias("kotlinx-coroutines-test").to("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")

            // Bundles
            bundle(
                "androidx-compose",
                listOf(
                    "androidx-activity-compose",
                    "androidx-compose-foundation",
                    "androidx-compose-material",
                    "androidx-compose-material-icons-core",
                    "androidx-compose-tooling",
                    "androidx-compose-ui",
                    "androidx-viewmodel-compose"
                )
            )
            bundle(
                "androidx-test",
                listOf(
                    "androidx-espresso-core",
                    "androidx-espresso-intents",
                    "androidx-espresso-contrib",
                    "androidx-junit"
                )
            )
        }
    }
}

rootProject.name = "zcash-android-app"

includeBuild("build-conventions")

include("app")
