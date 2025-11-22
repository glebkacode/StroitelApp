package com.itapp.stroitelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import com.itapp.auth_impl.di.AuthGraph
import com.itapp.products_impl.di.ProductsGraph
import com.itapp.shelves_impl.di.ShelvesGraph
import com.itapp.shelves_render_impl.di.ShelvesRenderGraph
import com.itapp.stroitelapp.di.AppGraph
import com.itapp.stroitelapp.root.RootComponentImpl
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metro.createGraphFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val appGraph = createGraph<AppGraph>()
        val authGraph = createGraphFactory<AuthGraph.Factory>().create(appGraph.storeFactory)
        val shelvesRenderGraph = createGraphFactory<ShelvesRenderGraph.Factory>().create(appGraph.storeFactory)
        val shelvesDomainGraph = createGraphFactory<ShelvesGraph.Factory>().create()
        val productsGraph = createGraphFactory<ProductsGraph.Factory>().create(
            storeFactory = appGraph.storeFactory,
            shelvesRenderComponentFactory = shelvesRenderGraph.shelvesRenderComponentFactory,
            getShelvesUseCase = shelvesDomainGraph.getShelvesUseCase
        )
        val root = RootComponentImpl(
            componentContext = defaultComponentContext(),
            authComponentFactory = authGraph.authComponentFactory,
            productsComponentFactory = productsGraph.productsComponentFactory
        )
        setContent {
            root.render(modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    //App()
}