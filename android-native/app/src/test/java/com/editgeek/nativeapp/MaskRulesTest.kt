package com.editgeek.nativeapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MaskRulesTest {
    @Test
    fun emptyMaskIsNotReady() {
        assertFalse(MaskRules.hasVisibleStroke(emptyList()))
        assertFalse(MaskRules.hasVisibleStroke(listOf(listOf(MaskPoint(1f, 1f)))))
    }

    @Test
    fun strokeWithTwoPointsIsReady() {
        assertTrue(
            MaskRules.hasVisibleStroke(
                listOf(listOf(MaskPoint(1f, 1f), MaskPoint(2f, 2f)))
            )
        )
    }

    @Test
    fun promptPreservesSpacesWhileTyping() {
        assertEquals("remove ", MaskRules.limitPromptInput("remove "))
        assertEquals("remove object", MaskRules.normalizedPrompt("  remove object  "))
    }

    @Test
    fun promptLimitDoesNotSplitEmojiSurrogatePairs() {
        val prompt = "a".repeat(499) + "😀" + "z"
        val limited = MaskRules.limitPromptInput(prompt)
        assertEquals(500, limited.codePointCount(0, limited.length))
        assertTrue(limited.endsWith("😀"))
    }

    @Test
    fun promptIsLimitedToFiveHundredCodePoints() {
        assertEquals(500, MaskRules.limitPromptInput("x".repeat(700)).length)
    }
}
