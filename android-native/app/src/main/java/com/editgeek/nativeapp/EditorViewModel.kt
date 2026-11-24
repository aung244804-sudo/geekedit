package com.editgeek.nativeapp

import android.graphics.Bitmap
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel

/**
 * Holds editor data through configuration changes such as rotation and window resizing.
 * Bitmap persistence across force-stop/process death is intentionally not claimed.
 */
class EditorViewModel : ViewModel() {
    val bitmapState = mutableStateOf<Bitmap?>(null)
    val originalBitmapState = mutableStateOf<Bitmap?>(null)
    val brightnessState = mutableFloatStateOf(0f)
    val contrastState = mutableFloatStateOf(1f)
    val saturationState = mutableFloatStateOf(1f)
    val promptState = mutableStateOf("")
    val showMaskState = mutableStateOf(true)
    val busyState = mutableStateOf(false)
    val strokes = mutableStateListOf<List<Offset>>()
}
