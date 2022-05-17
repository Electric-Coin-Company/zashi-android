plugins {
    id("com.android.library")
    kotlin("android")
    id("zcash.android-build-conventions")
}

android {
    resourcePrefix = "co_electriccoin_zcash_"
}

dependencies {
    api(libs.bundles.androidx.test)
}
