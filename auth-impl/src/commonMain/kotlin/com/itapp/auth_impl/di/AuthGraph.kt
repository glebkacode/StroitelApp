package com.itapp.auth_impl.di

import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_api.root.AuthRootComponent
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.auth_impl.password_validation.PasswordValidationComponentImpl
import com.itapp.auth_impl.phone_validation.presentation.component.PhoneValidationComponentImpl
import com.itapp.auth_impl.root.AuthRootComponentImpl
import com.itapp.auth_impl.sms_validation.SmsValidationComponentImpl
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.GraphExtension

@GraphExtension
interface AuthGraph {
    val authComponentFactory: Lazy<AuthRootComponent.Factory>

    @Binds
    val AuthRootComponentImpl.Factory.bind: AuthRootComponent.Factory

    @Binds
    val PasswordValidationComponentImpl.Factory.bind : PasswordValidationComponent.Factory

    @Binds
    val PhoneValidationComponentImpl.Factory.bind : PhoneValidationComponent.Factory

    @Binds
    val SmsValidationComponentImpl.Factory.bind : SmsValidationComponent.Factory
}