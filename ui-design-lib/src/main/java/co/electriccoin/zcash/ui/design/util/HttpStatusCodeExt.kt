package co.electriccoin.zcash.ui.design.util

import io.ktor.http.HttpStatusCode

fun HttpStatusCode.isServiceUnavailable(): Boolean = value in (501..504)
