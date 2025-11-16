package com.itapp.auth_impl.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_api.root.RootAuthComponent
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.api.AuthDataSourceImpl
import com.itapp.auth_impl.data.repository.AuthRepositoryImpl
import com.itapp.auth_impl.domain.repository.AuthRepository
import com.itapp.auth_impl.domain.usecase.AuthUseCase
import com.itapp.auth_impl.domain.usecase.AuthUseCaseImpl
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCaseImpl
import com.itapp.auth_impl.presentation.password_validation.component.PasswordValidationComponentImpl
import com.itapp.auth_impl.presentation.phone_validation.component.PhoneValidationComponentImpl
import com.itapp.auth_impl.presentation.root.RootAuthComponentImpl
import com.itapp.auth_impl.presentation.sms_validation.component.SmsValidationComponentImpl
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
    val AuthUseCaseImpl.bind : AuthUseCase

    @Binds
    val AuthRepositoryImpl.bind : AuthRepository

    @Binds
    val AuthDataSourceImpl.bind : AuthDataSource

    @Binds
    val RootAuthComponentImpl.Factory.bind: RootAuthComponent.Factory

    @Binds
    val PasswordValidationComponentImpl.Factory.bind : PasswordValidationComponent.Factory

    @Binds
    val PhoneValidationComponentImpl.Factory.bind : PhoneValidationComponent.Factory

    @Binds
    val SmsValidationComponentImpl.Factory.bind : SmsValidationComponent.Factory

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient()
    }

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides storeFactory: StoreFactory
        ): AuthGraph
    }
}