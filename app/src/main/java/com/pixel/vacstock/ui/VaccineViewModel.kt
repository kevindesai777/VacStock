package com.pixel.vacstock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixel.vacstock.data.Vaccine
import com.pixel.vacstock.data.VaccineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VaccineViewModel(private val repository: VaccineRepository) : ViewModel() {

    /** Null until the first read from disk finishes. */
    val vaccines: StateFlow<List<Vaccine>?> = repository.vaccines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue = null)

    fun increment(id: String) = launch { repository.changeCount(id, 1) }

    fun decrement(id: String) = launch { repository.changeCount(id, -1) }

    fun add(name: String, count: Int) = launch { repository.add(name, count) }

    fun edit(id: String, name: String, count: Int) = launch { repository.edit(id, name, count) }

    fun delete(id: String) = launch { repository.delete(id) }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
