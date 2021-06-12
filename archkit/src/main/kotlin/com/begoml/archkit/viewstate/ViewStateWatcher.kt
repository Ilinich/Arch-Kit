package com.begoml.archkit.viewstate

/**
 * ViewStateWatcher for efficient view updates
 */
class ViewStateWatcher<T> private constructor(
    private val watchers: List<Watcher<T, Any?>>
) {

    private var lastViewState: T? = null

    fun render(newViewState: T) {
        val oldViewState = lastViewState

        watchers.forEach { watcher ->
            val property = watcher.property
            val new = property(newViewState)
            if (oldViewState == null || watcher.diffStrategy(property(oldViewState), new)) {
                watcher.callback(new)
            }
        }

        lastViewState = newViewState
    }

    private class Watcher<T, R>( // todo should add doc
        val property: (T) -> R,
        val callback: (R) -> Unit,
        val diffStrategy: DiffStrategy<R>
    )

    /**
     * it's obligatory to clear watcher in onDestroyView
     */
    fun clear() {
        lastViewState = null
    }

    class Builder<T> @PublishedApi internal constructor() {

        private val watchers = mutableListOf<Watcher<T, Any?>>()

        private fun <R> watch(
            property: (T) -> R,
            diffStrategy: DiffStrategy<R>,
            callback: (R) -> Unit
        ) {
            watchers += Watcher(
                property = property,
                callback = callback,
                diffStrategy = diffStrategy
            ) as Watcher<T, Any?>
        }

        /**
         * Represents a class and provides introspection capabilities.
         * Instances of this class are obtainable by the ::class syntax.
         */
        operator fun <R> ((T) -> R).invoke(callback: (R) -> Unit) {
            watch(
                property = this,
                callback = callback,
                diffStrategy = byValue(),
            )
        }

        operator fun <R> (DiffStrategy<R>).invoke(callback: (R) -> Unit) = this to callback

        @PublishedApi
        internal fun build(): ViewStateWatcher<T> = ViewStateWatcher(watchers)
    }
}

internal typealias DiffStrategy<T> = (T, T) -> Boolean

/**
 * Compare using equals
 */
internal fun <T> byValue(): DiffStrategy<T> = { p1, p2 -> p2 != p1 }
