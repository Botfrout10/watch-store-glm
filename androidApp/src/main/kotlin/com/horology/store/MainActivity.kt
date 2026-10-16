package com.horology.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.horology.store.shared.App
import com.horology.store.shared.data.AndroidShared

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidShared.init(applicationContext)
        enableEdgeToEdge()
        setContent { App() }
    }
}
