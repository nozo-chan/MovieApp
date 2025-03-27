package com.example.movieapp.repository

import android.util.Log
import com.example.movieapp.database.Seat
import com.example.movieapp.database.SeatDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SeatRepository(private val rows: Int, private val cols: Int, private val dao: SeatDao) {

    private val _seats = MutableStateFlow(
        List(rows) { row ->
            List(cols) { col ->
                Seat(
                    row = row,
                    col = col,
                    isOccupied = Random.Default.nextBoolean()
                )
            }
        }
    )

    private val seats: StateFlow<List<List<Seat>>> = _seats

    init {
        CoroutineScope(Dispatchers.IO).launch {
            clearDatabase()
        }
    }

    // Function to retrieve the current seats list
    fun getSeats(): List<List<Seat>> = seats.value

    // Function to reserve a list of seats based on given positions (row, col)
    private fun reserveSeats(seatPositions: List<Pair<Int, Int>>) {
        _seats.value = seats.value.map { rowList ->
            rowList.map { seat ->
                seatPositions.find { (row, col) -> row == seat.row && col == seat.col }
                    ?.let { seat.copy(isOccupied = true) }
                    ?: seat
            }
        }
        CoroutineScope(Dispatchers.IO).launch { insertSeats(seats.value)}
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

    // Function to reserve user-selected seats (avoiding indices out of bounds)
    fun reserveUserSelectedSeats(selectedSeats: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        // Ensure the selected seats are within the bounds of the available seats
        val updatedSeats = seats.value.map { rowSeats ->
            rowSeats.map { seat ->
                if (selectedSeats.any { it.first == seat.row && it.second == seat.col } && !seat.isOccupied) {
                    seat.copy(isOccupied = true)  // Mark seat as occupied
                } else seat
            }
        }

        // Ensure no out-of-bounds errors before updating StateFlow
        try {
            _seats.value = updatedSeats // Update the StateFlow with new seat list
            reserveSeats(selectedSeats) // Reserve the seats in memory
            CoroutineScope(Dispatchers.IO).launch { reserveSeatsDB(selectedSeats) } // Save reserved seats to DB
        } catch (e: IndexOutOfBoundsException) {
            Log.e("ReserveSeats", "Attempted to reserve seats out of bounds", e)
        }

        // Ensure the selected seats are valid before filtering
        return selectedSeats.filter { (row, col) ->
            // Check bounds before accessing
            row in updatedSeats.indices && col in updatedSeats[row].indices && updatedSeats[row][col].isOccupied
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

    fun fetchSeats() {
        val seatsFromDb = dao.getAllSeats().chunked(10)
        // Fetch from database
        _seats.value = seatsFromDb
    }

    suspend fun insertSeats(seats: List<List<Seat>>) {
        val allSeats = dao.getAllSeats()
        val flattendSeats = seats.flatten()
        if (allSeats.isNotEmpty()) {
            updateSeats(flattendSeats)
        } else {
            dao.insertSeats(flattendSeats)
        }
    }

    // Reserve seats based on row, col positions
    private suspend fun reserveSeatsDB(seatPositions: List<Pair<Int, Int>>) {
        seatPositions.forEach { (row, col) ->
            dao.reserveSeat(row, col) // Reserve in DB
        }
        fetchSeats()  // After reserving, fetch the updated seats from DB
    }

    // Update seats in the database
    suspend fun updateSeats(seats: List<Seat>) {
        dao.updateSeats(seats) // Update DB with new seat statuses
        fetchSeats()  // After updating, fetch the updated seats
    }
    suspend fun clearDatabase() {
        dao.clearSeats()
    }
}