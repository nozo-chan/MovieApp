package com.example.movieapp

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class SeatRepository(private val rows: Int, private val cols: Int) {

    private val _seats = MutableStateFlow(
        List(rows) { row ->
            List(cols) { col -> Seat(row, col, Random.nextBoolean()) }
        }
    )
    private val seats: StateFlow<List<List<Seat>>> get() = _seats

    // Function to retrieve the current seats list
    fun getSeats(): List<List<Seat>> = _seats.value

    // Function to reserve a list of seats based on given positions (row, col)
    private fun reserveSeats(seatPositions: List<Pair<Int, Int>>) {
        _seats.value = seats.value.map { rowList ->
            rowList.map { seat ->
                seatPositions.find { (row, col) -> row == seat.row && col == seat.col }
                    ?.let { seat.copy(isOccupied = true) }
                    ?: seat
            }
        }
    }

    // Main function to find and reserve seats
    fun findAndReserveSeats(
        numPeople: Int,
        selectedSeats: List<Pair<Int, Int>>? = null
    ): List<Pair<Int, Int>> {
        selectedSeats?.let {
            val assignedSeats = reserveUserSelectedSeats(it)
            if (assignedSeats.size == numPeople) return assignedSeats
        }
        return findSeatsForGroup(numPeople).takeIf { it.isNotEmpty() }
            ?: assignSeparateSeats(numPeople)
    }

    // Function to reserve user-selected seats (avoiding indices)
    fun reserveUserSelectedSeats(selectedSeats: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        val updatedSeats = seats.value.map { rowSeats ->
            rowSeats.map { seat ->
                if (selectedSeats.any { it.first == seat.row && it.second == seat.col } && !seat.isOccupied) {
                    seat.copy(isOccupied = true)  // Create a new Seat object
                } else seat
            }
        }
        _seats.value = updatedSeats  // Update the StateFlow with new seat list
        reserveSeats(selectedSeats)
        return selectedSeats.filter { (row, col) ->
            updatedSeats[row][col].isOccupied
        }
    }

    private fun findSeatsForGroup(numPeople: Int): List<Pair<Int, Int>> {
        val currentSeats = seats.value
        currentSeats.forEach { seats ->
            seats.windowed(numPeople).forEach { window ->
                if (window.all { !it.isOccupied })
                    return window.map { Pair(it.row, it.col) }
            }
        }
        return emptyList()  // Return an empty list if no contiguous seats are found
    }

    // Function to assign separate seats when no group seating is available
    private fun assignSeparateSeats(numPeople: Int): List<Pair<Int, Int>> {
        val availableSeats = seats.value
            .flatten()
            .filter { !it.isOccupied }
            .take(numPeople)

        if (availableSeats.size < numPeople) return emptyList() // Not enough free seats

        val updatedSeats = seats.value.map { rowSeats ->
            rowSeats.map { seat ->
                if (availableSeats.any { it.row == seat.row && it.col == seat.col }) {
                    seat.copy(isOccupied = true) // Create new seat objects
                } else seat
            }
        }
        _seats.value = updatedSeats // Update StateFlow
        return availableSeats.map { it.row to it.col }
    }
}

