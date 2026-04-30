package com.itapp.auth_impl.di

import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.auth_api.root.RootAuthComponent
import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.api.AuthDataSourceImpl
import com.itapp.auth_impl.data.repository.AuthRepositoryImpl
import com.itapp.auth_impl.domain.repository.AuthRepository
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCaseImpl
import com.itapp.auth_impl.phone_validation.presentation.component.PhoneValidationComponentImpl
import com.itapp.auth_impl.presentation.components.RegistrationComponentImpl
import com.itapp.auth_impl.presentation.root.RootAuthComponentImpl
import com.itapp.core_architecture.tea.TeaFactory
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient

@DependencyGraph
interface AuthGraph {
    val authComponentFactory: Lazy<RootAuthComponent.Factory>

    @Binds
    val ValidatePhoneNumberUseCaseImpl.bind : ValidatePhoneNumberUseCase

    @Binds
    val AuthRepositoryImpl.bind : AuthRepository

    @Binds
    val AuthDataSourceImpl.bind : AuthDataSource

    @Binds
    val RootAuthComponentImpl.Factory.bind: RootAuthComponent.Factory

    @Binds
    val PhoneValidationComponentImpl.Factory.bind : PhoneValidationComponent.Factory

    @Binds
    val RegistrationComponentImpl.Factory.bind : RegistrationComponent.Factory

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient()
    }

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides teaFactory: TeaFactory
        ): AuthGraph
    }
}
