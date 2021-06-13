package com.begoml.app.mvi

import com.begoml.app.mvi.LoginMviFeature.*
import com.begoml.archkit.mvi.*
import kotlinx.coroutines.CoroutineScope

class LoginMviFeature : MviViewModel<ViewState, Event, Command, Effect, News>(
    viewState = ViewState(),
    eventTransformer = UiEventTransformer(),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl(),
    actor = ActorImpl(),
    bootstrapper = BootstrapperImpl(),
//    scope = this.viewModelScope
) {

    data class ViewState(
        val isLoading: Boolean = false
    )

    sealed class Effect {
        object StartLoading : Effect()
        object StopLoading : Effect()
    }

    sealed class Event {
        data class StartLogin(
            val login: String,
            val password: String,
        ) : Event()
    }

    sealed class Command {
        object TrackStartLoginAnalyticEvent : Command()

        object StartLogin : Command()
    }

    sealed class News

    class UiEventTransformer : EventTransformer<Event, Command> {
        override fun invoke(event: Event): Command {
            TODO("Not yet implemented")
        }

    }

    class ReducerImpl : Reducer<ViewState, Effect> {
        override fun invoke(state: ViewState, effect: Effect): ViewState {
            return when (effect) {
                else -> state.copy()
            }
        }

    }

    class NewsPublisherImpl : NewsPublisher<ViewState, Effect, News> {
        override fun invoke(state: ViewState, effect: Effect): News? {
            return when (effect) {
                else -> null
            }
        }

    }

    class ActorImpl : Actor<ViewState, Command, Effect> {

        override fun invoke(
            state: ViewState,
            command: Command,
            viewModelScope: CoroutineScope,
            sendEffect: (effect: Effect) -> Unit
        ) {
            when (command) {

            }
        }

    }

    class BootstrapperImpl : Bootstrapper<Command> {

        override fun invoke(): List<Command> = listOf(
            Command.TrackStartLoginAnalyticEvent
        )
    }
}
