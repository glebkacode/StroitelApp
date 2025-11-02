package com.itapp.auth_impl.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.root.AuthRootComponent
import com.itapp.core_navigation.BaseUiComponent

internal class AuthRootComponentImpl(
    componentContext: ComponentContext
) : BaseUiComponent(componentContext), AuthRootComponent {

    @Composable
    override fun render(modifier: Modifier) {
        AuthScreen(modifier)
    }
}