package cash.z.ecc.ui.common

fun <T> List<T>.first(count: Int) = subList(0, minOf(size, count))
