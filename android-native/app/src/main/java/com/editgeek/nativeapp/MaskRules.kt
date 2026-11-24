package com.editgeek.nativeapp

data class MaskPoint(val x: Float, val y: Float)

object MaskRules {
    fun hasVisibleStroke(strokes: List<List<MaskPoint>>): Boolean =
        strokes.any { stroke -> stroke.size >= 2 }

    fun normalizedPrompt(prompt: String): String = prompt.trim().take(500)
}
