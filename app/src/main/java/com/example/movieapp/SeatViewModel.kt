package com.example.movieapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SeatViewModel(private val useCase: SeatRepository) : ViewModel() {

    private val _seats = MutableStateFlow(useCase.getSeats())  // Fetch all seats initially
    val seats: StateFlow<List<List<Seat>>> get() = _seats

    private val _selectedSeats = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val selectedSeats: StateFlow<List<Pair<Int, Int>>> get() = _selectedSeats

    fun findAndReserveSeats(numPeople: Int) {
        val reservedSeats = useCase.findAndReserveSeats(numPeople) // Find seats

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
        useCase.reserveUserSelectedSeats(_selectedSeats.value)
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
        _seats.value = useCase.getSeats() // Refresh seat data from repository
    }

    fun clearSelectedSeats() {
        _selectedSeats.value = emptyList() // Clear all selected seats
    }

    fun clear(numPeople: Int) {
        val reservedSeats = useCase.findAndReserveSeats(numPeople) // Find seats
        if (reservedSeats.isNotEmpty())  _selectedSeats.value = emptyList()
    }
}