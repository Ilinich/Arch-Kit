package com.begoml.archkit.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * intermediary between Model and View that decides how to handle Events,
 * supply State updates, and new News
 */
abstract class MviViewModel<ViewState, UiEvent, Command, Effect, News>(
    viewState: ViewState,
    private val eventHandler: EventHandler<UiEvent, Command>,
    private val actor: Actor<ViewState, Command, Effect>,
    private val reducer: Reducer<ViewState, Effect>,
    private val postProcessor: PostProcessor<ViewState, Effect, Command>? = null,
    private val newsPublisher: NewsPublisher<ViewState, Effect, News>? = null,
    bootstrapper: Bootstrapper<Command>? = null
) : ViewModel() {

    private val state = MutableStateFlow(viewState)

    open val viewState: Flow<ViewState>
        get() = state.asStateFlow()

    private val singleLiveEvent = Channel<News>(Channel.BUFFERED)

    open val singleEvent: Flow<News>
        get() = singleLiveEvent.receiveAsFlow()

    init {
        bootstrapper?.let { bootstrapper ->
            bootstrapper { command ->
                nextCommand(command)
            }
        }
    }

    /**
     *
     * @param event - ui intent
     */
    open fun dispatchEvent(event: UiEvent) {
        val command = eventHandler(event)
        nextCommand(command)
    }

    private fun nextCommand(command: Command) {
        actor(state.state(), command, viewModelScope) { effect ->
            nextEffect(effect)
        }
    }

    private fun nextEffect(effect: Effect) {
        val viewState = reducer(state.state(), effect)
        state.value = viewState

        postProcessor?.let { postProcessor ->
            postProcessor(state.state(), effect)?.let { command ->
                nextCommand(command)
            }
        }

        newsPublisher?.let { newsPublisher ->
            newsPublisher(state.state(), effect)?.let { news ->
                viewModelScope.launch {
                    singleLiveEvent.send(news)
                }
            }
        }
    }
}

private fun <T> MutableStateFlow<T>.state() = this.value

typealias EventHandler<Event, Command> = (event: Event) -> Command

/**
 * Lives while the ViewModel lives
 */
typealias Actor<ViewState, Command, Effect> = (state: ViewState, command: Command, viewModelScope: CoroutineScope, sendEffect: (effect: Effect) -> Unit) -> Unit

typealias Reducer<ViewState, Effect> = (state: ViewState, effect: Effect) -> ViewState

typealias NewsPublisher<ViewState, Effect, News> = (state: ViewState, effect: Effect) -> News?

typealias PostProcessor<ViewState, Effect, Command> = (state: ViewState, effect: Effect) -> Command?

typealias Bootstrapper<Command> = (sendCommand: (command: Command) -> Unit) -> Unit

