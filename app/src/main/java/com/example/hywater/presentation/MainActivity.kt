package com.example.hywater.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.hywater.presentation.navigation.HyWaterNavGraph
import com.example.hywater.presentation.theme.HyWaterTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-Activity architecture — Compose handles all screen transitions.
 * @AndroidEntryPoint allows Hilt to inject into this Activity.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HyWaterTheme {
                val navController = rememberNavController()
                HyWaterNavGraph(navController = navController)
            }
        }
    }
}
