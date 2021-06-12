package com.begoml.archkit.viewmodel

import java.io.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface CloseableScopeDelegate {

    val scope: CoroutineScope

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed in scope
     */
    fun close()
}

class CloseableScopeDelegateImpl(
    override val scope: CloseableCoroutineScope =
        CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
) : CloseableScopeDelegate {

    override fun close() {
        (scope as Closeable).close()
    }
}
