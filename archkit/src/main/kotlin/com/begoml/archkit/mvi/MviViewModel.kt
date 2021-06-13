package com.begoml.archkit.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * intermediary between Model and View that decides how to handle Events,
 * supply State updates, and new News
 */
abstract class MviViewModel<ViewState, UiEvent, Command, Effect, News>(
    viewState: ViewState,
    private val eventTransformer: EventTransformer<UiEvent, Command>,
    private val actor: Actor<ViewState, Command, Effect>,
    private val reducer: Reducer<ViewState, Effect>,
    private val postProcessor: PostProcessor<ViewState, Effect, Command>? = null,
    private val newsPublisher: NewsPublisher<ViewState, Effect, News>? = null,
    bootstrapper: Bootstrapper<Command>? = null,
) : ViewModel() {

    private val state = MutableStateFlow(viewState)

    open val viewState: Flow<ViewState>
        get() = state.asStateFlow()

    private val singleLiveEvent = Channel<News>(Channel.BUFFERED)

    open val singleEvent: Flow<News>
        get() = singleLiveEvent.receiveAsFlow()

    private val commandSharedFlow = MutableSharedFlow<Command>(replay = 10)
    private val commands: SharedFlow<Command> get() = commandSharedFlow

    private val uiEventSharedFlow = MutableSharedFlow<UiEvent>()
    private val uiEvents: SharedFlow<UiEvent> get() = uiEventSharedFlow

    private val effectSharedFlow = MutableSharedFlow<Effect>()
    private val effects: SharedFlow<Effect> get() = effectSharedFlow

    init {
        viewModelScope.launch {
            val bootstrapperFlow = bootstrapper?.invoke()?.asFlow() ?: emptyFlow()
            listOf(
                bootstrapperFlow,
                commands,
                uiEvents.map(eventTransformer::invoke)
            )
                .merge()
                .collect(::nextCommand)
        }
    }

    /**
     *
     * @param event - ui intent
     */
    open fun dispatchEvent(event: UiEvent) {
        viewModelScope.launch {
            uiEventSharedFlow.emit(event)
        }
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

typealias EventTransformer<Event, Command> = (event: Event) -> Command

typealias Actor<ViewState, Command, Effect> = (state: ViewState, command: Command, viewModelScope: CoroutineScope, sendEffect: (effect: Effect) -> Unit) -> Unit

typealias Reducer<ViewState, Effect> = (state: ViewState, effect: Effect) -> ViewState

typealias NewsPublisher<ViewState, Effect, News> = (state: ViewState, effect: Effect) -> News?

typealias PostProcessor<ViewState, Effect, Command> = (state: ViewState, effect: Effect) -> Command?

typealias Bootstrapper<Command> = () -> List<Command>

