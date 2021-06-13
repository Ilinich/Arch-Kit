package com.begoml.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.app.datasource.ProfileRepository
import com.begoml.app.presentation.profile.ProfileViewModel.ViewState
import com.begoml.app.presentation.profile.mapper.ProfileMapper
import com.begoml.app.presentation.profile.model.MoviesUi
import com.begoml.app.tools.Const.EMPTY_STRING
import com.begoml.archkit.viewmodel.ViewStateDelegate
import com.begoml.archkit.viewmodel.ViewStateDelegateImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val mapper: ProfileMapper
) : ViewModel(),
    ViewStateDelegate<ViewState, Unit> by ViewStateDelegateImpl(initialViewState = ViewState()) {

    data class ViewState(
        val isImageLoading: Boolean = false,
        val movieList: List<MoviesUi> = emptyList(),
        val imageUrl: String = EMPTY_STRING,
        val userName: String = EMPTY_STRING,
        val userInfo: String = EMPTY_STRING,
        val bornDate: String = EMPTY_STRING,
        val education: String = EMPTY_STRING
    )

    init {
        viewModelScope.launch {
            val profile = withContext(Dispatchers.IO) {
                repository.getProfileList()
            }
            val movieList = mapper.map(profile)
            reduce {
                it.copy(
                    userName = profile.user,
                    userInfo = profile.userInfo,
                    bornDate = profile.born,
                    education = profile.education,
                    imageUrl = profile.imgUrl,
                    movieList = movieList.movies
                )
            }
        }
    }
}
