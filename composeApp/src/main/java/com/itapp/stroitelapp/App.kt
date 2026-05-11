package com.itapp.stroitelapp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.itapp.stroitelapp.root.RootComponent
import com.itapp.uikit.theme.StroitelTheme

@Composable
@Preview
fun App(
    modifier: Modifier,
    component: RootComponent,
) {
    StroitelTheme {
        Children(component.stack) {
            val childModifier = modifier.windowInsetsPadding(WindowInsets.systemBars)
            when (val child = it.instance) {
                is RootComponent.Child.Auth -> child.component.render(childModifier)
            }
        }
    }
}
