package com.example.movieapp.ui.viewmodel

import com.example.movieapp.repository.SeatRepository
import androidx.lifecycle.ViewModel
import com.example.movieapp.database.Seat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SeatViewModel(private val seatRepository: SeatRepository) : ViewModel() {

    private val _seats = MutableStateFlow(seatRepository.getSeats())  // Fetch all seats initially
    val seats: StateFlow<List<List<Seat>>> = _seats

    private val _selectedSeats = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val selectedSeats: StateFlow<List<Pair<Int, Int>>> = _selectedSeats

    fun findAndReserveSeats(numPeople: Int) {
        val reservedSeats = seatRepository.findAndReserveSeats(numPeople) // Find seats

        if (reservedSeats.isNotEmpty()) {
            // Map over the current seats and update the reserved seats
            val updatedSeats = _seats.value.mapIndexed { rowIndex, row ->
                row.mapIndexed { colIndex, seat ->
                    if (reservedSeats.contains(Pair(rowIndex, colIndex))) {
                        seat.copy(isReserved = true) // Mark as reserved
                    } else {
                        seat // Keep unchanged if not reserved
                    }
                }
            }
            _seats.value = updatedSeats // Update the seat data with the new array
            _selectedSeats.value = reservedSeats // Set selected seats
        }
    }

    fun reserveSelectedSeats() {
        seatRepository.reserveUserSelectedSeats(_selectedSeats.value)
        _selectedSeats.value = emptyList() // Clear after reserving
        updateSeats() // Refresh seats if needed
    }

    fun toggleSeatSelection(row: Int, col: Int) {
        _selectedSeats.update { currentList ->
            if (currentList.contains(Pair(row, col))) {
                currentList - Pair(row, col) // Deselect
            } else {
                currentList + Pair(row, col) // Select
            }
        }
    }

    private fun updateSeats() {
        _seats.value = seatRepository.getSeats() // Refresh seat data from repository
    }
}
