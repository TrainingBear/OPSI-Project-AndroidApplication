package com.trbear9.openfarm

import androidx.lifecycle.ViewModelProvider

class PlantResultViewModelFactory
    (val plants : MutableList<Pair<Int, String>>)
    : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlantResultViewModel::class.java)) {
                return PlantResultViewModel(plants) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    init {
        "Created contents with size: ${plants.size}".info("PlantResultViewModelFactory")
    }
}