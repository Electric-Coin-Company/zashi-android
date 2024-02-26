@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.common.extension

import androidx.compose.ui.text.intl.Locale

fun Locale.toKotlinLocale() = cash.z.ecc.android.sdk.model.Locale(language, region, script)
