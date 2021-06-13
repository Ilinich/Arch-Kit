package com.begoml.app.presentation.profile.mapper

import com.begoml.app.datasource.model.ProfileLocal
import com.begoml.app.presentation.profile.model.MoviesUi
import com.begoml.app.presentation.profile.model.ProfileUi
import javax.inject.Inject

class ProfileMapper @Inject constructor() {

    fun map(data: ProfileLocal): ProfileUi {
        return ProfileUi(
            user = data.user,
            imgUrl = data.imgUrl,
            userInfo = data.userInfo,
            born = data.born,
            education = data.education,
            movies = data.movies.map {
                MoviesUi(
                    name = it.name,
                    year = it.year
                )
            }
        )
    }
}
