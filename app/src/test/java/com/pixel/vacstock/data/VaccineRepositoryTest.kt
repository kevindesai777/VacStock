package com.pixel.vacstock.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class VaccineRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val repository by lazy {
        VaccineRepository(createVaccineDataStore { folder.root.resolve(VACCINE_STORE_FILE).absolutePath })
    }

    @Test
    fun startsWithTheDefaultList() = runBlocking {
        assertEquals(DEFAULT_VACCINES, repository.vaccines.first())
    }

    @Test
    fun savesEveryKindOfChange() = runBlocking {
        val hexasil = DEFAULT_VACCINES.first().id
        val bcg = DEFAULT_VACCINES.last().id
        repeat(3) { repository.changeCount(hexasil, 1) }
        repository.changeCount(hexasil, -1)
        repository.add("Varivax", 4)
        repository.edit(bcg, "BCG (SII)", 7)
        repository.delete(DEFAULT_VACCINES[1].id)

        val saved = repository.vaccines.first()
        assertEquals(2, saved.first { it.id == hexasil }.count)
        assertEquals("Varivax", saved.last().name)
        assertEquals(4, saved.last().count)
        assertEquals(7, saved.first { it.id == bcg }.count)
        assertEquals("BCG (SII)", saved.first { it.id == bcg }.name)
        assertEquals(DEFAULT_VACCINES.size, saved.size)
    }
}
