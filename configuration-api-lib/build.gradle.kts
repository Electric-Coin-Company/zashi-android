plugins {
    kotlin("multiplatform")
    id("secant.kotlin-multiplatform-build-conventions")
    id("secant.dependency-conventions")

    id("org.jetbrains.kotlinx.kover")
    id("secant.kover-conventions")
}

kotlin {
    jvm()
    sourceSets {
        getByName("commonMain") {
            dependencies {
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.immutable)
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                api(libs.kotlinx.coroutines.test)
            }
        }
        getByName("jvmMain") {
            dependencies {
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
