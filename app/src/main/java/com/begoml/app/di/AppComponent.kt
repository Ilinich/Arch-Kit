package com.begoml.app.di

import android.content.Context
import com.begoml.app.di.module.GsonModule
import com.begoml.app.di.module.ProvidersModule
import com.begoml.app.di.module.RepositoryModule
import com.begoml.app.presentation.ArchKitApplication
import com.begoml.app.presentation.activity.MainActivity
import com.begoml.app.presentation.loginmvvm.LoginMvvmFragment
import com.begoml.app.presentation.profile.ProfileFragment
import com.begoml.app.presentation.startfragment.StartFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        RepositoryModule::class,
        GsonModule::class,
        ProvidersModule::class
    ]
)
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun context(context: Context): Builder
    }

    fun inject(where: ArchKitApplication)
    fun inject(where: ProfileFragment)
    fun inject(where: LoginMvvmFragment)

    companion object {

        private var component: AppComponent? = null

        fun init(context: Context) {
            component = DaggerAppComponent.builder()
                .context(context)
                .build().apply {
                    component = this
                }
        }

        fun get(): AppComponent {
            return component ?: throw NotImplementedError("You should call 'init' method")
        }
    }
}
