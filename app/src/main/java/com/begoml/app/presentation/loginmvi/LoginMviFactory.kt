package com.begoml.app.presentation.loginmvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.begoml.app.tools.ResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class LoginMviFactory @Inject constructor(
    private val resourceProvider: ResourceProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginMviViewModel(
            actor = LoginMviViewModel.ActorImpl(
                resourceProvider = resourceProvider,
            ),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        ) as T
    }
}
