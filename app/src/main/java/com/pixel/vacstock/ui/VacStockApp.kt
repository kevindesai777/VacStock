package com.pixel.vacstock.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** The whole app: one screen, wired to the ViewModel. */
@Composable
fun VacStockApp(viewModel: VaccineViewModel) {
    val vaccines by viewModel.vaccines.collectAsStateWithLifecycle()
    // The first disk read takes a few milliseconds; the window background covers that frame.
    vaccines?.let { list ->
        VaccineListScreen(
            vaccines = list,
            onIncrement = viewModel::increment,
            onDecrement = viewModel::decrement,
            onAdd = viewModel::add,
            onEdit = viewModel::edit,
            onDelete = viewModel::delete,
        )
    }
}
