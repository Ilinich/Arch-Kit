package com.begoml.app.presentation.startfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.app.presentation.startfragment.StartFragmentViewModel.Event
import com.begoml.archkit.viewmodel.ViewStateDelegate
import com.begoml.archkit.viewmodel.ViewStateDelegateImpl

class StartFragmentViewModel : ViewModel(),
    ViewStateDelegate<Unit, Event> by ViewStateDelegateImpl(Unit) {

    sealed class Event {
        object NavigateToMvvmScreen : Event()
        object NavigateToMviScreen : Event()
    }

    fun onBtnMvvmClicked() {
        viewModelScope.sendEvent(
            Event.NavigateToMvvmScreen
        )
    }

    fun onBtnMviClicked() {
        viewModelScope.sendEvent(
            Event.NavigateToMviScreen
        )
    }
}
