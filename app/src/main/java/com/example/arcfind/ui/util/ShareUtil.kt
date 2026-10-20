package com.example.arcfind.ui.util

import android.content.Context
import android.content.Intent

object ShareUtil {

    fun shareCalculation(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(intent, "Share Arc Calculation")
        context.startActivity(chooser)
    }
}
