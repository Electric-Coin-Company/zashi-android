@file:Suppress("ktlint:standard:filename")

package co.electriccoin.zcash.ui.common

fun <T> List<T>.first(count: Int) = subList(0, minOf(size, count))
