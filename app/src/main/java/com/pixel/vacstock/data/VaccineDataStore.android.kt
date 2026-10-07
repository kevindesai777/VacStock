package com.pixel.vacstock.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/** Keeps the store in the app's private files folder, which Android Auto Backup includes. */
fun createVaccineDataStore(context: Context): DataStore<Preferences> =
    createVaccineDataStore { context.filesDir.resolve(VACCINE_STORE_FILE).absolutePath }
