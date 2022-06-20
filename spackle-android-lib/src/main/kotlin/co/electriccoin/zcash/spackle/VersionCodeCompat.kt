package co.electriccoin.zcash.spackle

import android.annotation.TargetApi
import android.content.pm.PackageInfo
import android.os.Build

val PackageInfo.versionCodeCompat
    get() = if (AndroidApiVersion.isAtLeastP) {
        getVersionCodePPlus()
    } else {
        versionCodeLegacy.toLong()
    }

@Suppress("Deprecation")
private val PackageInfo.versionCodeLegacy
    get() = versionCode

@TargetApi(Build.VERSION_CODES.P)
private fun PackageInfo.getVersionCodePPlus() = longVersionCode
