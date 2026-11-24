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
    fun promptIsTrimmedAndLimited() {
        assertEquals("remove object", MaskRules.normalizedPrompt("  remove object  "))
        assertEquals(500, MaskRules.normalizedPrompt("x".repeat(700)).length)
    }
}
