package com.begoml.app.datasource

import com.begoml.app.datasource.model.ProfileLocal
import javax.inject.Inject

interface ProfileRepository {

    suspend fun getProfileList(): ProfileLocal
}

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProfileRepository {

    override suspend fun getProfileList(): ProfileLocal {
        return  apiService.getProfileInfo()
    }
}
