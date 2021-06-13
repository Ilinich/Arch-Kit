package com.begoml.archkit.mvi

import com.begoml.archkit.viewmodel.ViewStateDelegate
import com.begoml.archkit.viewmodel.ViewStateDelegateImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


interface Feature<ViewState, Event, UiEvent> {

    val viewState: Flow<ViewState>

    val singleEvents: Flow<Event>

    fun dispatchEvent(event: UiEvent)
}


/**
 * intermediary between Model and View that decides how to handle Events,
 * supply State updates, and new News
 */
class FeatureImpl<ViewState, UiEvent, Command, Effect, News>(
    viewState: ViewState,
    private val eventTransformer: EventTransformer<UiEvent, Command>,
    private val actor: Actor<ViewState, Command, Effect>,
    private val reducer: Reducer<ViewState, Effect>,
    private val postProcessor: PostProcessor<ViewState, Effect, Command>? = null,
    private val newsPublisher: NewsPublisher<ViewState, Effect, News>? = null,
    bootstrapper: Bootstrapper<Command>? = null,
    val scope: CoroutineScope
) : ViewStateDelegate<ViewState, News> by ViewStateDelegateImpl(viewState),
    Feature<ViewState, News, UiEvent>,
    CoroutineScope by scope {

    private val commandSharedFlow = MutableSharedFlow<Command>()
    private val commands: SharedFlow<Command> get() = commandSharedFlow

    private val uiEventSharedFlow = MutableSharedFlow<UiEvent>()
    private val uiEvents: SharedFlow<UiEvent> get() = uiEventSharedFlow

    init {
        launch {
            val bootstrapperFlow = bootstrapper?.invoke()?.asFlow() ?: emptyFlow()
            listOf(
                bootstrapperFlow,
                commands,
                uiEvents.map(eventTransformer::invoke)
            )
                .merge()
                .flatMapMerge { command ->
                    collectCommand(command)
                }
                .onEach { effect ->
                    reduce { state ->
                        reducer(state, effect)
                    }
                }
                .collect(::collectEffect)
        }
    }

    /**
     *
     * @param event - ui intent
     */
    override fun dispatchEvent(event: UiEvent) {
        launch {
            uiEventSharedFlow.emit(event)
        }
    }

    private fun collectCommand(command: Command): Flow<Effect> = callbackFlow {
        actor(stateValue, command, scope) { effect ->
            trySendBlocking(effect)
                .onFailure { throwable ->
                    // has been cancelled or failed
                }
        }
        awaitClose { }
    }

    private fun collectEffect(effect: Effect) {
        postProcessor?.invoke(stateValue, effect)?.let { command ->
            launch {
                commandSharedFlow.emit(command)
            }
        }

        newsPublisher?.invoke(stateValue, effect)?.let { news ->
            sendEvent(news)
        }
    }
}

typealias EventTransformer<Event, Command> = (event: Event) -> Command

typealias Actor<ViewState, Command, Effect> = (state: ViewState, command: Command, scope: CoroutineScope, sendEffect: (effect: Effect) -> Unit) -> Unit

typealias Reducer<ViewState, Effect> = (state: ViewState, effect: Effect) -> ViewState

typealias NewsPublisher<ViewState, Effect, News> = (state: ViewState, effect: Effect) -> News?

typealias PostProcessor<ViewState, Effect, Command> = (state: ViewState, effect: Effect) -> Command?

typealias Bootstrapper<Command> = () -> List<Command>

