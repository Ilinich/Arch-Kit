package com.begoml.app.presentation.loginmvvm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.app.R
import com.begoml.app.presentation.loginmvvm.LoginMvvmViewModel.Event
import com.begoml.app.presentation.loginmvvm.LoginMvvmViewModel.ViewState
import com.begoml.app.tools.ResourceProvider
import com.begoml.app.tools.view.InputView.InputViewState
import com.begoml.archkit.viewmodel.ViewStateDelegate
import com.begoml.archkit.viewmodel.ViewStateDelegateImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginMvvmViewModel(
    private val resourceProvider: ResourceProvider
) : ViewModel(),
    ViewStateDelegate<ViewState, Event> by ViewStateDelegateImpl(initialViewState = ViewState()) {

    data class ViewState(
        val isLoading: Boolean = false,
        val isValidEmail: Boolean = false,
        val isValidFirstPassword: Boolean = false,
        val buttonIsEnabled: Boolean = false,
        val loginState: InputViewState = InputViewState.DefaultState,
        val passwordState: InputViewState = InputViewState.DefaultState,
    )

    sealed class Event {
        object UserIsLoginIn : Event()
    }

    private val wrongPasswordText by lazy {
        resourceProvider.getString(R.string.wrong_field)
    }

    private val errorColor by lazy {
        resourceProvider.getColor(R.color.error)
    }

    private val mediumBlueColor by lazy {
        resourceProvider.getColor(R.color.purple_700)
    }

    private val grayColor by lazy {
        resourceProvider.getColor(R.color.gray)
    }

    fun onLoginFocusChanged(userLogin: String) {
        validateEmail(userLogin)
        validateAllFields()
    }

    fun onUserLoginFocusChanged(firstPassword: String) {
        validatePassword(firstPassword)
        validateAllFields()
    }

    private fun validateEmail(email: String) {
        when {
            email.isEmpty() -> {
                viewModelScope.reduce {
                    it.copy(
                        isValidEmail = false,
                        buttonIsEnabled = false,
                        loginState = InputViewState.ErrorState(
                            messageFooter = null,
                            colorError = mediumBlueColor,
                            colorFooter = null
                        )
                    )
                }
            }
            email.equals("Emma", true).not() -> {
                viewModelScope.reduce {
                    it.copy(
                        isValidEmail = false,
                        loginState = InputViewState.ErrorState(
                            messageFooter = resourceProvider.getString(R.string.login__wrong_email_text),
                            colorError = errorColor,
                            colorFooter = errorColor
                        )
                    )
                }
            }
            else -> {
                viewModelScope.reduce {
                    it.copy(
                        isValidEmail = true,
                        loginState = InputViewState.ErrorState(
                            null,
                            colorError = mediumBlueColor,
                            colorFooter = null
                        )
                    )
                }
            }
        }
    }

    private fun validatePassword(firstPassword: String) {
        when {
            firstPassword.isEmpty() -> {
                Log.d("TAG", "validatePassword: ${firstPassword.isEmpty()}")
                viewModelScope.reduce {
                    it.copy(
                        isValidFirstPassword = false,
                        passwordState = InputViewState.ErrorState(
                            messageFooter = null,
                            colorError = mediumBlueColor,
                            colorFooter = grayColor
                        )
                    )
                }
            }
            firstPassword.equals("12345", true).not() -> {
                Log.d("TAG", "ot: ${firstPassword.equals("12345", true).not()}")
                viewModelScope.reduce {
                    it.copy(
                        isValidFirstPassword = false,
                        passwordState = InputViewState.ErrorState(
                            messageFooter = resourceProvider.getString(R.string.login__wrong_email_text),
                            colorError = errorColor,
                            colorFooter = errorColor
                        )
                    )
                }
            }
            else -> {
                viewModelScope.reduce {
                    it.copy(
                        isValidFirstPassword = true,
                        passwordState = InputViewState.ErrorState(
                            messageFooter = null,
                            colorError = mediumBlueColor,
                            colorFooter = grayColor
                        )
                    )
                }
            }
        }
    }

    fun onRegisterUserButtonClicked() {
        if (!validateAllFields()) {
            return
        }
        viewModelScope.launch {
            reduce {
                it.copy(
                    isLoading = true
                )
            }
            delay(2500)
            sendEvent(
                Event.UserIsLoginIn
            )
        }
    }

    private fun validateAllFields(): Boolean {
        val isValidAllFields =
            stateValue.isValidEmail && stateValue.isValidFirstPassword
        if (isValidAllFields) {
            viewModelScope.reduce {
                it.copy(
                    loginState = InputViewState.DefaultState,
                    passwordState = InputViewState.DefaultState,
                    buttonIsEnabled = true
                )
            }
        } else {
            viewModelScope.reduce {
                it.copy(
                    buttonIsEnabled = false
                )
            }
        }
        return isValidAllFields
    }
}
