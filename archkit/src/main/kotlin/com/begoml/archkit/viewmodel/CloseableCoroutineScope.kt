package com.begoml.archkit.viewmodel

import java.io.Closeable
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren

class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {

    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancelChildren()
    }
}
