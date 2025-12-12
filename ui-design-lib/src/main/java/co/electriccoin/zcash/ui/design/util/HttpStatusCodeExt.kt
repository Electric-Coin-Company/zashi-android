package co.electriccoin.zcash.ui.design.util

import io.ktor.http.HttpStatusCode

@Suppress("MagicNumber")
fun HttpStatusCode.isServiceUnavailable(): Boolean = value in (501..504)
