package com.pixel.vacstock

import android.app.Application
import com.pixel.vacstock.data.VaccineRepository
import com.pixel.vacstock.data.createVaccineDataStore

class VacStockApplication : Application() {
    /** One store per process: DataStore refuses two live instances on the same file. */
    val repository: VaccineRepository by lazy { VaccineRepository(createVaccineDataStore(this)) }
}
