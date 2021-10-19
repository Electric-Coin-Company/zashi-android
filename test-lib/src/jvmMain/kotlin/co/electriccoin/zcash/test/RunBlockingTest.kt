package co.electriccoin.zcash.test

import kotlinx.coroutines.runBlocking

actual fun runBlockingTest(test: suspend kotlinx.coroutines.CoroutineScope.() -> Unit) =
    runBlocking(block = test)
