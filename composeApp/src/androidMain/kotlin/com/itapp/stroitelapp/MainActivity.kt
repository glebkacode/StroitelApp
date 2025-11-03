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
import com.itapp.stroitelapp.di.AppGraph
import com.itapp.stroitelapp.root.RootComponentImpl
import dev.zacsweers.metro.createGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val graph = createGraph<AppGraph>()
        val root = RootComponentImpl(
            componentContext = defaultComponentContext(),
            authComponentFactory = graph.authComponentFactory
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