package com.begoml.archkit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.archkit.MockViewStateViewModel.Event
import com.begoml.archkit.MockViewStateViewModel.ViewState
import com.begoml.archkit.viewmodel.ViewStateDelegate
import com.begoml.archkit.viewmodel.ViewStateDelegateImpl

class MockViewStateViewModel :
    ViewModel(),
    ViewStateDelegate<ViewState, Event> by ViewStateDelegateImpl(
        initialViewState = ViewState(),
    ) {

    data class ViewState(
        val isLoading: Boolean = false,
        val userName: String = ""
    )

    sealed class Event {
        object NavigateToSettings : Event()
    }

    fun onSettingsClicked() {
        viewModelScope.sendEvent(Event.NavigateToSettings)
    }

    fun onRunLoadingClicked() {
        viewModelScope.reduce {
            it.copy(isLoading = true)
        }
    }

    fun changedUserName(userName: String) {
        viewModelScope.reduce {
            it.copy(userName = userName)
        }
    }
}
