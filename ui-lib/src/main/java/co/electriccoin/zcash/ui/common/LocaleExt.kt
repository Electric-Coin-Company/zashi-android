@file:Suppress("ktlint:filename")

package co.electriccoin.zcash.ui.common

import androidx.compose.ui.text.intl.Locale

fun Locale.toKotlinLocale() = cash.z.ecc.sdk.ext.ui.model.Locale(language, region, script)
