package com.example.arcfind.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arcfind.repository.HistoryRepository
import com.example.arcfind.repository.SettingsRepository

class ArcFindViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArcFindViewModel::class.java)) {
            val settingsRepo = SettingsRepository(context.applicationContext)
            val historyRepo = HistoryRepository(context.applicationContext)
            return ArcFindViewModel(settingsRepo, historyRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
