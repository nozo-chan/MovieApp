package com.example.movieapp.ui.viewmodel

import com.example.movieapp.repository.SeatRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SeatViewModelFactory(private val repository: SeatRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SeatViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SeatViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
