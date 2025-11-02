package com.itapp.stroitelapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.itapp.stroitelapp.root.RootComponent
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    modifier: Modifier,
    component: RootComponent
) {
    MaterialTheme {
        Children(component.stack) {
            when (val child = it.instance) {
                is RootComponent.Child.Auth -> child.component.render(modifier)
            }
        }
    }
}