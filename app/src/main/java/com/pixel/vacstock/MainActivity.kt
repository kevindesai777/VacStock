package com.pixel.vacstock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixel.vacstock.ui.VacStockApp
import com.pixel.vacstock.ui.VaccineViewModel
import com.pixel.vacstock.ui.theme.VacStockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val repository = (application as VacStockApplication).repository
        setContent {
            VacStockTheme {
                VacStockApp(viewModel = viewModel { VaccineViewModel(repository) })
            }
        }
    }
}
