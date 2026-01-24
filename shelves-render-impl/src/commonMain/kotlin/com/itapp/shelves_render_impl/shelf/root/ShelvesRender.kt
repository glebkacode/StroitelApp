package com.itapp.shelves_render_impl.shelf.root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itapp.core_navigation.childItems.ChildLazyLists
import com.itapp.core_navigation.childItems.childLazyItems
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent.*

@Composable
fun ShelvesRender(
    modifier: Modifier,
    component: ShelvesRenderComponent
) {
    Column(
        modifier = modifier.fillMaxSize()
            .padding(vertical = 24.dp)
    ) {
        ChildLazyLists(
            modifier = Modifier.weight(1f),
            listItems = component.shelves,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            onFirstIndexVisibleChanged = component::onFirstVisibleElementChange,
            onLastIndexVisibleChanged = component::onLastVisibleElementChange,
        ) { childItems ->
            childLazyItems(childItems) { item ->
                when (item) {
                    is Child.Grid -> item.component.render(Modifier)
                    is Child.Horizontal -> item.component.render(Modifier)
                    is Child.Video -> item.component.render(Modifier)
                }
            }
        }
    }
}