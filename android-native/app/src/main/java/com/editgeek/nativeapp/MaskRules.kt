package com.editgeek.nativeapp

data class MaskPoint(val x: Float, val y: Float)

object MaskRules {
    fun hasVisibleStroke(strokes: List<List<MaskPoint>>): Boolean =
        strokes.any { stroke -> stroke.size >= 2 }

    /**
     * Keep the raw, user-entered text while typing. Trimming belongs at submission
     * time; trimming on every keystroke makes it impossible to type a trailing space.
     */
    fun limitPromptInput(prompt: String, maxCodePoints: Int = 500): String {
        if (maxCodePoints <= 0 || prompt.isEmpty()) return ""
        val codePoints = prompt.codePointCount(0, prompt.length)
        if (codePoints <= maxCodePoints) return prompt
        return prompt.substring(0, prompt.offsetByCodePoints(0, maxCodePoints))
    }

    fun normalizedPrompt(prompt: String): String = limitPromptInput(prompt.trim())
}
