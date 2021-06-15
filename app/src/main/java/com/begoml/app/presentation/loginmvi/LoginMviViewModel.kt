package com.begoml.app.presentation.loginmvi

import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModel
import com.begoml.app.R
import com.begoml.app.presentation.loginmvi.LoginMviViewModel.Event
import com.begoml.app.presentation.loginmvi.LoginMviViewModel.News
import com.begoml.app.presentation.loginmvi.LoginMviViewModel.ViewState
import com.begoml.app.tools.Const.EMPTY_STRING
import com.begoml.app.tools.ResourceProvider
import com.begoml.app.tools.view.InputView.InputViewState
import com.begoml.archkit.mvi.Actor
import com.begoml.archkit.mvi.Bootstrapper
import com.begoml.archkit.mvi.EventTransformer
import com.begoml.archkit.mvi.Feature
import com.begoml.archkit.mvi.FeatureImpl
import com.begoml.archkit.mvi.NewsPublisher
import com.begoml.archkit.mvi.PostProcessor
import com.begoml.archkit.mvi.Reducer
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
    postProcessor = PostProcessorImpl(),
    newsPublisher = NewsPublisherImpl(),
    actor = actor,
    bootstrapper = BootstrapperImpl(),
    scope = scope
) {

    data class ViewState(
        val isLoading: Boolean = false,
        val login: String = EMPTY_STRING,
        val password: String = EMPTY_STRING,
        val isValidEmail: Boolean = false,
        val isValidFirstPassword: Boolean = false,
        val buttonIsEnabled: Boolean = false,
        val loginState: InputViewState = InputViewState.DefaultState,
        val passwordState: InputViewState = InputViewState.DefaultState,
    )

    sealed class Effect {
        object AuthStarted : Effect()
        object AuthFinished : Effect()
        object CheckAllFields : Effect()
        data class LoginChanged(val login: String) : Effect()
        data class PasswordChanged(val password: String) : Effect()
        data class LoginIsEmpty(
            val errorMessage: String?,
            @ColorRes val colorError: Int,
            @ColorRes val colorFooter: Int?
        ) : Effect()

        data class LoginIsValid(
            val errorMessage: String?,
            @ColorRes val colorError: Int,
            @ColorRes val colorFooter: Int?
        ) : Effect()

        data class LoginIsUnValid(
            val errorMessage: String,
            @ColorRes val colorError: Int,
            @ColorRes val colorFooter: Int
        ) : Effect()

        data class PasswordIsEmpty(
            val errorMessage: String?,
            @ColorRes val colorError: Int,
            @ColorRes val colorFooter: Int?
        ) : Effect()

        data class PasswordIsUnValid(
            val errorMessage: String,
            @ColorRes val colorError: Int,
            @ColorRes val colorFooter: Int
        ) : Effect()

        data class PasswordIsValid(
            val errorMessage: String?,
            @ColorRes val colorError: Int,
            @ColorRes val colorFooter: Int?
        ) : Effect()

        object ButtonIsEnabled : Effect()
        object IsAllFieldsValid : Effect()
        object IsAllFieldsUnValid : Effect()
    }

    sealed class Event {
        object OnLoginClicked : Event()
        data class OnLoginChanged(val login: String) : Event()
        data class OnPasswordChanged(val password: String) : Event()
    }

    sealed class Command {
        object TrackStartLoginAnalyticEvent : Command()
        object StartLogin : Command()
        data class CheckLogin(val login: String) : Command()
        data class CheckPassword(val password: String) : Command()
        data class ValidateAllFields(val login: String, val password: String) : Command()
    }

    sealed class News {
        object GoToProfile : News()
    }

    class UiEventTransformer : EventTransformer<Event, Command> {
        override fun invoke(event: Event): Command {
            return when (event) {
                is Event.OnLoginClicked -> Command.StartLogin
                is Event.OnLoginChanged -> Command.CheckLogin(event.login)
                is Event.OnPasswordChanged -> Command.CheckPassword(event.password)
            }
        }
    }

    class ReducerImpl : Reducer<ViewState, Effect> {
        override fun invoke(state: ViewState, effect: Effect): ViewState {
            return when (effect) {
                Effect.AuthStarted -> state.copy(isLoading = true)
                Effect.AuthFinished -> state.copy(isLoading = false)
                is Effect.LoginChanged -> state.copy(login = effect.login)
                is Effect.LoginIsEmpty -> state.copy(
                    loginState = InputViewState.ErrorState(
                        messageFooter = null,
                        colorError = effect.colorError,
                        colorFooter = null
                    )
                )
                is Effect.LoginIsValid -> state.copy(
                    isValidEmail = true,
                    loginState = InputViewState.ErrorState(
                        null,
                        colorError = effect.colorError,
                        colorFooter = null
                    )
                )
                is Effect.LoginIsUnValid -> state.copy(
                    loginState = InputViewState.ErrorState(
                        messageFooter = effect.errorMessage,
                        colorError = effect.colorError,
                        colorFooter = effect.colorFooter
                    )
                )
                is Effect.PasswordIsEmpty -> state.copy(
                    isValidEmail = false,
                    buttonIsEnabled = false,
                    passwordState = InputViewState.ErrorState(
                        messageFooter = null,
                        colorError = effect.colorError,
                        colorFooter = effect.colorFooter
                    )
                )
                is Effect.PasswordIsUnValid -> state.copy(
                    isValidFirstPassword = false,
                    passwordState = InputViewState.ErrorState(
                        messageFooter = effect.errorMessage,
                        colorError = effect.colorError,
                        colorFooter = effect.colorFooter
                    )
                )
                is Effect.PasswordIsValid -> state.copy(
                    isValidFirstPassword = true,
                    buttonIsEnabled = false,
                    passwordState = InputViewState.ErrorState(
                        messageFooter = null,
                        colorError = effect.colorError,
                        colorFooter = null
                    )
                )
                is Effect.IsAllFieldsValid -> state.copy(
                    buttonIsEnabled = true
                )
                is Effect.ButtonIsEnabled -> state.copy(
                    buttonIsEnabled = true
                )
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

    class PostProcessorImpl : PostProcessor<ViewState, Effect, Command> {
        override fun invoke(state: ViewState, effect: Effect): Command? {
            return when (effect) {
                Effect.CheckAllFields -> Command.ValidateAllFields(state.login, state.password)
                else -> null
            }
        }
    }

    class ActorImpl(
        private val resourceProvider: ResourceProvider
    ) : Actor<ViewState, Command, Effect> {

        private val errorColor by lazy {
            resourceProvider.getColor(R.color.error)
        }
        private val mediumBlueColor by lazy {
            resourceProvider.getColor(R.color.purple_700)
        }
        private val wrongPasswordText by lazy {
            resourceProvider.getString(R.string.wrong_field)
        }

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
                is Command.CheckLogin -> {
                    scope.launch {
                        val login = command.login
                        sendEffect(Effect.LoginChanged(login))
                        sendEffect(validateLogin(login))
                    }
                    Unit
                }
                is Command.CheckPassword -> {
                    scope.launch {
                        val password = command.password
                        sendEffect(Effect.PasswordChanged(password))
                        sendEffect(validatePassword(password))
                        sendEffect(Effect.CheckAllFields)
                    }
                    Unit
                }
                is Command.ValidateAllFields -> {
                    scope.launch {
                        sendEffect(
                            validateAllFields(
                                state.isValidEmail,
                                state.isValidFirstPassword
                            )
                        )
                    }
                    Unit
                }
            }
        }

        private fun validateLogin(login: String): Effect {
            return when {
                login.isEmpty() -> {
                    Effect.LoginIsEmpty(
                        errorMessage = null,
                        colorError = mediumBlueColor,
                        colorFooter = null
                    )
                }
                login.equals("Emma", true).not() -> {
                    Effect.LoginIsUnValid(
                        errorMessage = resourceProvider.getString(R.string.login__wrong_email_text),
                        colorError = errorColor,
                        colorFooter = errorColor
                    )
                }
                else -> Effect.LoginIsValid(
                    errorMessage = null,
                    colorError = mediumBlueColor,
                    colorFooter = null
                )
            }
        }

        private fun validatePassword(password: String): Effect {
            return when {
                password.isEmpty() -> {
                    Effect.PasswordIsEmpty(
                        errorMessage = null,
                        colorError = mediumBlueColor,
                        colorFooter = null
                    )
                }
                password.equals("12345", true).not() -> {
                    Effect.PasswordIsUnValid(
                        errorMessage = wrongPasswordText,
                        colorError = errorColor,
                        colorFooter = errorColor
                    )
                }
                else -> {
                    Effect.PasswordIsValid(
                        errorMessage = null,
                        colorError = mediumBlueColor,
                        colorFooter = null
                    )
                }
            }
        }

        private fun validateAllFields(
            isValidEmail: Boolean,
            isValidFirstPassword: Boolean
        ): Effect {
            val isValidAllFields = isValidEmail && isValidFirstPassword
            return if (isValidAllFields) {
                Effect.IsAllFieldsValid
            } else {
                Effect.IsAllFieldsUnValid
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
