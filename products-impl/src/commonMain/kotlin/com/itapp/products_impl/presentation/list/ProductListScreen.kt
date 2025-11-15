package com.itapp.products_impl.presentation.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itapp.core_navigation.childItems.ChildLazyLists
import com.itapp.core_navigation.childItems.childLazyItems
import com.itapp.products_api.ProductListComponent

@Composable
fun ProductListScreen(
    modifier: Modifier,
    component: ProductListComponent
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ChildLazyLists(
            modifier = Modifier.weight(1f),
            listItems = component.shelves,
            onFirstIndexVisibleChanged = component::onFirstVisibleElementChange,
            onLastIndexVisibleChanged = component::onLastVisibleElementChange,
        ) { childItems ->
            childLazyItems(childItems) { item ->
                when (item) {
                    is ProductListComponent.ChildShelf.Grid -> item.component.render(Modifier)
                    is ProductListComponent.ChildShelf.Horizontal -> item.component.render(Modifier)
                    is ProductListComponent.ChildShelf.Video -> item.component.render(Modifier)
                }
            }
        }
    }
}