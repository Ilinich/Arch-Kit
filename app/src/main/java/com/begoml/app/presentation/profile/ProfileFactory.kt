package com.begoml.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.begoml.app.datasource.ProfileRepository
import com.begoml.app.presentation.profile.mapper.ProfileMapper
import javax.inject.Inject

class ProfileFactory @Inject constructor(
    private val repository: ProfileRepository,
    private val mapper: ProfileMapper
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            repository = repository,
            mapper = mapper
        ) as T
    }
}
