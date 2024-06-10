package com.bestapp.rice.ui.search

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun View.hideSoftKeyboard() {
    val inputMethodManager = ContextCompat.getSystemService(context, InputMethodManager::class.java)
    inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
}