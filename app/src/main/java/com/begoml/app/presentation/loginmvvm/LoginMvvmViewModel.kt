package com.begoml.app.presentation.loginmvvm

import androidx.lifecycle.ViewModel
import com.begoml.app.presentation.loginmvvm.LoginMvvmViewModel.*
import com.begoml.archkit.viewmodel.ViewStateDelegate
import com.begoml.archkit.viewmodel.ViewStateDelegateImpl

class LoginMvvmViewModel : ViewModel(), ViewStateDelegate<ViewState, Event> by ViewStateDelegateImpl(initialViewState = ViewState()) {

    data class ViewState(
        val isDataLoading: Boolean = false
    )

    sealed class Event {
        object UserIsLoginIn : Event()
    }

    fun onLoginClicked(login: String, password: String) {

    }
}
