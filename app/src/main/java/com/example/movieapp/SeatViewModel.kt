package com.example.movieapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeatViewModel(private val useCase: SeatRepository) : ViewModel() {


    init {
        // Fetch initial data from the database
        viewModelScope.launch {
            useCase.fetchSeats()
        }
    }
    private val _seats = MutableStateFlow(useCase.getSeats())  // Fetch all seats initially
    val seats: StateFlow<List<List<Seat>>> get() = _seats

    private val _selectedSeats = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val selectedSeats: StateFlow<List<Pair<Int, Int>>> get() = _selectedSeats



    // Function to load seats (either from DB or mock data)
    fun loadSeats() {
        // Check if seats are already in the database
        viewModelScope.launch {
            val existingSeats = useCase.getSeats()
            if (existingSeats.isEmpty()) {
                // No seats in DB, generate mock data
                val generatedSeats = generateMockSeats()
                val seat = generatedSeats.map { it }.first()
                useCase.insertSeats(seat)  // Store in DB
                _seats.value = generatedSeats
            } else {
                // Seats exist in DB, load them
                _seats.value = existingSeats
            }
        }
    }

    // Function to generate mock seat data (10 rows x 10 columns for example)
    private fun generateMockSeats(): List<List<Seat>> {
        val rows = 10
        val cols = 10
        val mockSeats = mutableListOf<List<Seat>>()

        for (rowIndex in 0 until rows) {
            val rowSeats = mutableListOf<Seat>()
            for (colIndex in 0 until cols) {
                val seat = Seat(
                    row = rowIndex,
                    col = colIndex,
                    isOccupied = (Math.random() > 0.8), // Randomly occupied (20% chance)
                    isReserved = false
                )
                rowSeats.add(seat)
            }
            mockSeats.add(rowSeats)
        }
        return mockSeats
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

    fun reserveSelectedSeats() {
        useCase.reserveUserSelectedSeats(_selectedSeats.value)
//        _selectedSeats.value = emptyList() // Clear after reserving
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