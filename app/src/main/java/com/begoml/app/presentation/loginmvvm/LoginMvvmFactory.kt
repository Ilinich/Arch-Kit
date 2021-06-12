package com.begoml.app.presentation.loginmvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.begoml.app.tools.ResourceProvider
import javax.inject.Inject

class LoginMvvmFactory @Inject constructor(
    private val resourceProvider: ResourceProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginMvvmViewModel(
            resourceProvider = resourceProvider
        ) as T
    }
}
