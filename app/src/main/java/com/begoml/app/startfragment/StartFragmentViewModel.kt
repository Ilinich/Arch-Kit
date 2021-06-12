package com.begoml.app.startfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.app.startfragment.StartFragmentViewModel.*
import com.begoml.archkit.viewmodel.ViewStateDelegate
import com.begoml.archkit.viewmodel.ViewStateDelegateImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartFragmentViewModel() : ViewModel(), ViewStateDelegate<ViewState, Event> by ViewStateDelegateImpl(initialViewState = ViewState()) {

    data class ViewState(
        val isDataLoading: Boolean = false
    )

    sealed class Event {
        object NavigateToNextScreen : Event()
    }


    fun navigateToNextScreen(){
        viewModelScope.launch {
            delay(300)
            sendEvent(
                Event.NavigateToNextScreen
            )
        }
    }
}
