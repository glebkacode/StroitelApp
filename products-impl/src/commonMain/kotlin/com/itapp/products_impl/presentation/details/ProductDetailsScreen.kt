package com.itapp.products_impl.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.itapp.products_api.details.ProductDetailsComponent

@Composable
fun ProductDetailsScreen(
    modifier: Modifier,
    component: ProductDetailsComponent
) {
    Column(modifier = modifier) {
        Children(
            modifier = Modifier
                .weight(1F)
                .consumeWindowInsets(WindowInsets.navigationBars),
            component = component
        )
        BottomBar(
            modifier = Modifier.fillMaxWidth(),
            component = component
        )
    }
}

@Composable
fun Children(
    modifier: Modifier,
    component: ProductDetailsComponent
) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(fade()),
    ) {
        when (val child = it.instance) {
            is ProductDetailsComponent.Child.Characteristics -> child.component.render(modifier)
            is ProductDetailsComponent.Child.Description -> child.component.render(modifier)
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    component: ProductDetailsComponent
) {
    val stack by component.stack.subscribeAsState()
    val activeComponent = stack.active.instance

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        NavigationBarItem(
            selected = activeComponent is ProductDetailsComponent.Child.Description,
            onClick = { component.onDescriptionTabClicked() },
            icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings") }
        )
        NavigationBarItem(
            selected = activeComponent is ProductDetailsComponent.Child.Characteristics,
            onClick = { component.onCharacteristicsTabClicked() },
            icon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = "Settings") }
        )
    }
}