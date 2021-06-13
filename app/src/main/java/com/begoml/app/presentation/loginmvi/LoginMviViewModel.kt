package com.begoml.app.presentation.loginmvi

import androidx.lifecycle.ViewModel
import com.begoml.app.presentation.loginmvi.LoginMviViewModel.*
import com.begoml.app.tools.ResourceProvider
import com.begoml.archkit.mvi.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginMviViewModel(
    actor: ActorImpl,
    private val scope: CoroutineScope,
) : ViewModel(), Feature<ViewState, News, Event> by FeatureImpl(
    viewState = ViewState(),
    eventTransformer = UiEventTransformer(),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl(),
    actor = actor,
    bootstrapper = BootstrapperImpl(),
    scope = scope
) {

    data class ViewState(
        val isLoading: Boolean = false,
        val login: String = "",
        val password: String = "",
    )

    sealed class Effect {
        object AuthStarted : Effect()
        object AuthFinished : Effect()
        data class LoginChanged(val login: String) : Effect()

        object LoginIsValid : Effect()
        object LoginIsUnValid : Effect()
        object LoginIsEmpty : Effect()
    }

    sealed class Event {
        object OnLoginClicked : Event()
        data class OnLoginChanged(val login: String) : Event()
    }

    sealed class Command {
        object TrackStartLoginAnalyticEvent : Command()
        object StartLogin : Command()
        data class ChangeLogin(val login: String) : Command()
    }

    sealed class News {
        object GoToProfile : News()
    }

    class UiEventTransformer : EventTransformer<Event, Command> {
        override fun invoke(event: Event): Command {
            return when (event) {
                is Event.OnLoginClicked -> Command.StartLogin
                is Event.OnLoginChanged -> Command.ChangeLogin(event.login)
            }
        }
    }

    class ReducerImpl : Reducer<ViewState, Effect> {
        override fun invoke(state: ViewState, effect: Effect): ViewState {
            return when (effect) {
                Effect.AuthStarted -> state.copy(isLoading = true)
                Effect.AuthFinished -> state.copy(isLoading = false)
                is Effect.LoginChanged -> state.copy(login = effect.login)
                is Effect.LoginIsValid -> state.copy() // todo
                is Effect.LoginIsEmpty -> state.copy() // todo
                else -> state.copy()
            }
        }

    }

    class NewsPublisherImpl : NewsPublisher<ViewState, Effect, News> {
        override fun invoke(state: ViewState, effect: Effect): News? {
            return when (effect) {
                Effect.AuthFinished -> News.GoToProfile
                else -> null
            }
        }

    }

    class ActorImpl(
        private val resourceProvider: ResourceProvider
    ) : Actor<ViewState, Command, Effect> {

        override fun invoke(
            state: ViewState,
            command: Command,
            scope: CoroutineScope,
            sendEffect: (effect: Effect) -> Unit
        ) {
            return when (command) {
                is Command.StartLogin -> {
                    scope.launch {
                        sendEffect(Effect.AuthStarted)
                        delay(2500)
                        sendEffect(Effect.AuthFinished)
                    }
                    Unit
                }
                Command.TrackStartLoginAnalyticEvent -> {
                    // todo track event
                }
                is Command.ChangeLogin -> {
                    scope.launch {
                        val login = command.login
                        sendEffect(Effect.LoginChanged(login))
                        sendEffect(validateLogin(login))
                    }
                    Unit
                }
            }
        }

        private fun validateLogin(login: String): Effect {
            return when {
                login.isEmpty() -> {
                    Effect.LoginIsEmpty
                }
                else -> Effect.LoginIsValid
            }
        }
    }

    class BootstrapperImpl : Bootstrapper<Command> {

        override fun invoke(): List<Command> = listOf(
            Command.TrackStartLoginAnalyticEvent
        )
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
