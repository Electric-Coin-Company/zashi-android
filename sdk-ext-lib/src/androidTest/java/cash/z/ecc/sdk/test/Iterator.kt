package cash.z.ecc.sdk.test

fun <T> Iterator<T>.count(): Int {
    var count = 0
    forEach { count++ }

    return count
}
