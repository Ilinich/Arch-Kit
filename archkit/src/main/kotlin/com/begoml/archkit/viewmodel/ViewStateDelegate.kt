package com.begoml.archkit.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewState - must be Data class, immutable
 */
interface ViewStateDelegate<ViewState, Event> {

    /**
     * declarative description of the UI based on the current state.
     */
    val viewState: Flow<ViewState>

    val singleEvents: Flow<Event>

    /**
     * state is read-only
     * The only way to change the state is to emit[reduce] an action,
     * an object describing what happened.
     */
    val stateValue: ViewState

    /**
     * Reduce are functions that take the current state and an action as arguments,
     * and changed a new state result. In other words, (state: ViewState) => newState.
     */
    fun CoroutineScope.reduce(action: (state: ViewState) -> ViewState)

    fun CoroutineScope.sendEvent(event: Event)
}

class ViewStateDelegateImpl<ViewState, Event>(
    initialViewState: ViewState,
    singleLiveEventCapacity: Int = Channel.BUFFERED,
) : ViewStateDelegate<ViewState, Event> {

    /**
     * the source of truth that drives our app
     */
    private val stateFlow = MutableStateFlow(initialViewState)

    override val viewState: Flow<ViewState>
        get() = stateFlow.asStateFlow()

    override val stateValue: ViewState
        get() = stateFlow.value

    private val singleEventsChannel = Channel<Event>(singleLiveEventCapacity)

    override val singleEvents: Flow<Event>
        get() = singleEventsChannel.receiveAsFlow()

    override fun CoroutineScope.reduce(action: (state: ViewState) -> ViewState) {
        launch {
            stateFlow.emit(action(stateValue))
        }
    }

    override fun CoroutineScope.sendEvent(event: Event) {
        launch {
            singleEventsChannel.send(event)
        }
    }
}
