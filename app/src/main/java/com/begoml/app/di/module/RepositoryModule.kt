package com.begoml.app.di.module

import com.begoml.app.datasource.ProfileRepository
import com.begoml.app.datasource.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindsProfileRepositoryImpl(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository
}
