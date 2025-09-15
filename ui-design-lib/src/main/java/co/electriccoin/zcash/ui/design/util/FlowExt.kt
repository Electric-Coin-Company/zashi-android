package co.electriccoin.zcash.ui.design.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

inline fun <reified T> List<Flow<T>>.combineToFlow(): Flow<List<T>> =
    combine(this.map { flow -> flow }) { items -> items.toList() }

@Suppress("UNCHECKED_CAST", "MagicNumber")
fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
        )
    }

@Suppress("UNCHECKED_CAST", "MagicNumber")
fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6, flow7, flow8) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
        )
    }

@Suppress("UNCHECKED_CAST", "MagicNumber")
fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6, flow7) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
        )
    }

@Suppress("UNCHECKED_CAST", "MagicNumber")
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
        )
    }

@Suppress("UNCHECKED_CAST", "MagicNumber")
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
        )
    }

@Suppress("UNCHECKED_CAST", "MagicNumber")
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
        )
    }

@Suppress("UNCHECKED_CAST", "MagicNumber")
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    flow9: Flow<T9>,
    flow10: Flow<T10>,
    flow11: Flow<T11>,
    flow12: Flow<T12>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R
): Flow<R> =
    combine(flow, flow2, flow3, flow4, flow5, flow6, flow7, flow8, flow9, flow10, flow11, flow12) {
        transform(
            it[0] as T1,
            it[1] as T2,
            it[2] as T3,
            it[3] as T4,
            it[4] as T5,
            it[5] as T6,
            it[6] as T7,
            it[7] as T8,
            it[8] as T9,
            it[9] as T10,
            it[10] as T11,
            it[11] as T12,
        )
    }
