package com.pixel.vacstock.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VaccineListTest {
    private val list = listOf(Vaccine("a", "Hexasil", 2), Vaccine("b", "BCG", 0))

    @Test
    fun incrementChangesOnlyTheMatchingVaccine() {
        assertEquals(
            listOf(Vaccine("a", "Hexasil", 3), Vaccine("b", "BCG", 0)),
            list.withCountChanged("a", 1),
        )
    }

    @Test
    fun decrementStopsAtZero() {
        assertEquals(0, list.withCountChanged("b", -1)[1].count)
    }

    @Test
    fun incrementStopsAtMax() {
        assertEquals(MAX_COUNT, listOf(Vaccine("a", "X", MAX_COUNT)).withCountChanged("a", 1)[0].count)
    }

    @Test
    fun addAppendsWithTrimmedName() {
        val result = list.withAdded("  Varivax ", 10, id = "c")
        assertEquals(3, result.size)
        assertEquals(Vaccine("c", "Varivax", 10), result.last())
    }

    @Test
    fun addGivesEachVaccineANewId() {
        val result = list.withAdded("One", 0).withAdded("Two", 0)
        assertEquals(result.size, result.map { it.id }.toSet().size)
    }

    @Test
    fun editRenamesAndSetsExactCount() {
        assertEquals(Vaccine("b", "BCG vaccine", 25), list.withEdited("b", " BCG vaccine ", 25)[1])
    }

    @Test
    fun withoutRemovesOnlyThatVaccine() {
        assertEquals(listOf(Vaccine("b", "BCG", 0)), list.without("a"))
    }

    @Test
    fun hasNameIgnoresCaseSpacesAndTheVaccineBeingEdited() {
        assertTrue(list.hasName(" hexasil "))
        assertFalse(list.hasName("Hexasil", exceptId = "a"))
        assertFalse(list.hasName("Rotasil"))
    }

    @Test
    fun defaultsMatchTheNoteWithUniqueIdsAndNoStock() {
        assertEquals(18, DEFAULT_VACCINES.size)
        assertEquals(DEFAULT_VACCINES.size, DEFAULT_VACCINES.map { it.id }.toSet().size)
        assertTrue(DEFAULT_VACCINES.all { it.count == 0 })
    }

    @Test
    fun codecRoundTrips() {
        assertEquals(list, VaccineCodec.decode(VaccineCodec.encode(list)))
    }

    @Test
    fun codecReturnsNullForUnreadableText() {
        assertNull(VaccineCodec.decode("not json"))
    }
}
