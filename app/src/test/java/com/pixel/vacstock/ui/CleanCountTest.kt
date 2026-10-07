package com.pixel.vacstock.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class CleanCountTest {
    @Test
    fun keepsDigitsOnly() = assertEquals("12", cleanCount("1a-2 "))

    @Test
    fun dropsLeadingZeros() = assertEquals("10", cleanCount("010"))

    @Test
    fun keepsASingleZero() = assertEquals("0", cleanCount("00"))

    @Test
    fun allowsEmpty() = assertEquals("", cleanCount(""))

    @Test
    fun capsAtFiveDigits() = assertEquals("12345", cleanCount("1234567"))
}
