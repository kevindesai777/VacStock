package com.pixel.vacstock.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/** File name of the store; each platform decides which folder it lives in. */
internal const val VACCINE_STORE_FILE = "vaccines.preferences_pb"

/**
 * Creates the store at the path [producePath] returns. Platform-neutral on purpose: a future
 * iOS or desktop target only needs its own folder, as Android does in VaccineDataStore.android.kt.
 */
fun createVaccineDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
