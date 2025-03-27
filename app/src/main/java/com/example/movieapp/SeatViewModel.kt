package com.example.movieapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeatViewModel(private val useCase: SeatRepository) : ViewModel() {


    private val _seats = MutableStateFlow(useCase.getSeats())  // Fetch all seats initially
    val seats: StateFlow<List<List<Seat>>> get() = _seats

    private val _selectedSeats = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val selectedSeats: StateFlow<List<Pair<Int, Int>>> get() = _selectedSeats

    init {
        loadSeats()
    }

    fun loadSeats() {
        viewModelScope.launch {
            try {
                val existingSeats = useCase.getSeats()

                if (existingSeats.isEmpty()) {
                    // Generate and save mock seats if database is empty
                    val generatedSeats = generateMockSeats()

                    // Save all generated seats to the database
                    generatedSeats.forEach { row ->
                        useCase.insertSeats(row)
                    }

                    // Update the seats state with generated seats
                    _seats.value = generatedSeats
                } else {
                    // Load existing seats from the database
                    _seats.value = existingSeats
                }
            } catch (e: Exception) {
                // Handle any errors during seat loading
                _seats.value = emptyList()
                // Log the error if needed
            }
        }
    }

    fun reserveSelectedSeats() {
        if (_selectedSeats.value.isNotEmpty()) {
            // Update the status of selected seats in the database
            val updatedSeats = _seats.value.mapIndexed { rowIndex, row ->
                row.mapIndexed { colIndex, currentSeat ->
                    val isSelected = _selectedSeats.value.contains(Pair(rowIndex, colIndex))

                    if (isSelected) {
                        currentSeat.copy(isReserved = true, isOccupied = true)
                    } else {
                        currentSeat
                    }
                }
            }

            // Update the seats value
            _seats.value = updatedSeats

            // Clear selected seats
            _selectedSeats.value = emptyList()
            updateSeats()
        }
    }

    // Function to generate mock seat data (10 rows x 10 columns)
    private fun generateMockSeats(): List<List<Seat>> {
        val rows = 10
        val cols = 10
        return List(rows) { rowIndex ->
            List(cols) { colIndex ->
                Seat(
                    row = rowIndex,
                    col = colIndex,
                    isOccupied = (Math.random() > 0.8), // 20% chance of being occupied
                    isReserved = false
                )
            }
        }
    }


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


    }
}