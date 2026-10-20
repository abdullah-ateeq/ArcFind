package com.example.arcfind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.arcfind.model.AppTheme
import com.example.arcfind.ui.ArcFindApp
import com.example.arcfind.ui.ArcFindViewModel
import com.example.arcfind.ui.ArcFindViewModelFactory
import com.example.arcfind.ui.theme.ArcFindTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ArcFindViewModel by viewModels {
        ArcFindViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val darkTheme = when (uiState.settings.theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            ArcFindTheme(
                darkTheme = darkTheme,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                ArcFindApp(viewModel = viewModel)
            }
        }
    }
}
