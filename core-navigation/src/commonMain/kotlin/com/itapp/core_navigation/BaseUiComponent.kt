package com.itapp.core_navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext

abstract class BaseUiComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    protected val componentScope = coroutineScope()

    @Composable
    abstract fun render(modifier: Modifier = Modifier)
}