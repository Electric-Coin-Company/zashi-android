plugins {
    id("com.android.library")
    kotlin("android")
    id("secant.android-build-conventions")
    id("secant.jacoco-conventions")
}

android {
    namespace = "co.electriccoin.zcash.test"
    resourcePrefix = "co_electriccoin_zcash_"
}

dependencies {
    api(libs.bundles.androidx.test)
}
