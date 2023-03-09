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
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.datetime)
                implementation(projects.spackleLib)
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        getByName("jvmMain") {
            dependencies {
                implementation(projects.spackleLib)
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
