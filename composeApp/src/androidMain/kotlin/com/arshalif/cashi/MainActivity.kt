package com.arshalif.cashi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arshalif.cashi.di.appModule
import com.arshalif.cashi.presentation.navigation.AppNavigation
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule)
        }
        
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    AppNavigation()
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}