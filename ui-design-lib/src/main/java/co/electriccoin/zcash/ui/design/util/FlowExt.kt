package co.electriccoin.zcash.ui.design.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

inline fun <reified T> List<Flow<T>>.combineToFlow(): Flow<List<T>> {
    return combine(this.map { flow -> flow }) { items -> items.toList() }
}
