package com.pixel.vacstock.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Reads and saves the vaccine list. The whole list is one JSON value, in shelf order. */
class VaccineRepository(private val dataStore: DataStore<Preferences>) {

    /** The saved list, or the default list before anything has been saved. */
    val vaccines: Flow<List<Vaccine>> = dataStore.data.map { it.vaccines() }

    suspend fun changeCount(id: String, delta: Int) = update { it.withCountChanged(id, delta) }

    suspend fun add(name: String, count: Int) = update { it.withAdded(name, count) }

    suspend fun edit(id: String, name: String, count: Int) = update { it.withEdited(id, name, count) }

    suspend fun delete(id: String) = update { it.without(id) }

    // edit() runs read-modify-write as one transaction, so fast repeated taps never lose a change.
    private suspend fun update(transform: (List<Vaccine>) -> List<Vaccine>) {
        dataStore.edit { prefs -> prefs[KEY] = VaccineCodec.encode(transform(prefs.vaccines())) }
    }

    private fun Preferences.vaccines(): List<Vaccine> =
        this[KEY]?.let(VaccineCodec::decode) ?: DEFAULT_VACCINES

    private companion object {
        val KEY = stringPreferencesKey("vaccines")
    }
}
