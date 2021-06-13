package com.begoml.archkit.viewstate

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.begoml.archkit.mvi.Feature
import com.begoml.archkit.viewmodel.ViewStateDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

inline fun <T> Fragment.viewStateWatcher(
    init: ViewStateWatcher.Builder<T>.() -> Unit
): ViewStateWatcher<T> {

    var watcher: ViewStateWatcher<T>? = null

    lifecycle.addObserver(object : DefaultLifecycleObserver {
        val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
            val viewLifecycleOwner = it ?: return@Observer

            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    watcher?.clear()
                }
            })
        }

        override fun onCreate(owner: LifecycleOwner) {
            viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
            watcher = null
        }
    })

    return ViewStateWatcher.Builder<T>()
        .apply(init)
        .build().apply {
            watcher = this
        }
}

inline fun <T> AppCompatActivity.viewStateWatcher(
    init: ViewStateWatcher.Builder<T>.() -> Unit
): ViewStateWatcher<T> {

    var watcher: ViewStateWatcher<T>? = null

    lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onDestroy(owner: LifecycleOwner) {
            watcher?.clear()
            watcher = null
        }
    })

    return ViewStateWatcher.Builder<T>()
        .apply(init)
        .build().apply {
            watcher = this
        }
}

/**
 * render [State] with [lifecycleState]
 * The UI re-renders based on the new state
 **/
fun <State, Event> ViewStateDelegate<State, Event>.render(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    watcher: ViewStateWatcher<State>
): Job = lifecycleOwner.lifecycleScope.launch {
    viewState.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = lifecycleState,
    ).collect(watcher::render)
}

fun <State, Event, UiEvent> Feature<State, Event, UiEvent>.render(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    watcher: ViewStateWatcher<State>
): Job = lifecycleOwner.lifecycleScope.launch {
    viewState.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = lifecycleState,
    ).collect(watcher::render)
}

/**
 * render [State] with [AppCompatActivity]
 * The UI re-renders based on the new state
 **/
fun <State, Event> ViewStateDelegate<State, Event>.render(
    lifecycle: Lifecycle,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    watcher: ViewStateWatcher<State>
): Job = lifecycle.coroutineScope.launch {
    viewState.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = lifecycleState,
    ).collect(watcher::render)
}

/**
 * send [Event] with [lifecycleState]
 * The UI re-renders based on the new event
 **/
fun <State, Event> ViewStateDelegate<State, Event>.collectEvent(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: (event: Event) -> Unit
): Job = lifecycleOwner.lifecycleScope.launch {
    singleEvents.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = lifecycleState,
    ).collect(block::invoke)
}

fun <State, Event, UiEvent> Feature<State, Event, UiEvent>.collectEvent(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: (event: Event) -> Unit
): Job = lifecycleOwner.lifecycleScope.launch {
    singleEvents.flowWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = lifecycleState,
    ).collect(block::invoke)
}

/**
 * send [Event] with [AppCompatActivity]
 * The UI re-renders based on the new event
 **/
fun <State, Event> ViewStateDelegate<State, Event>.collectEvent(
    lifecycle: Lifecycle,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: (event: Event) -> Unit
): Job = lifecycle.coroutineScope.launch {
    singleEvents.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = lifecycleState,
    ).collect(block::invoke)
}

fun <State, Event, UiEvent> Feature<State, Event, UiEvent>.collectEvent(
    lifecycle: Lifecycle,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    block: (event: Event) -> Unit
): Job = lifecycle.coroutineScope.launch {
    singleEvents.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = lifecycleState,
    ).collect(block::invoke)
}
