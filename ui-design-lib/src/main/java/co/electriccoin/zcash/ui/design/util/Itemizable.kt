package co.electriccoin.zcash.ui.design.util

/**
 * Interface to help compose LazyList to work efficiently
 */
interface Itemizable {
    val contentType: Any
    val key: Any
}
