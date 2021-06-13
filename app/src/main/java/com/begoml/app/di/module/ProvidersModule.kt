package com.begoml.app.di.module

import com.begoml.app.tools.ResourceProvider
import com.begoml.app.tools.ResourceProviderImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface ProvidersModule {

    @Binds
    @Singleton
    fun providesResourceProvider(impl: ResourceProviderImpl): ResourceProvider
}
