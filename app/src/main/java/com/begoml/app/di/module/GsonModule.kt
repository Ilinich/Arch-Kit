package com.begoml.app.di.module

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GsonModule {

    @Singleton
    @Provides
    fun provideGsonProvider(): Gson {
        return Gson()
    }
}
