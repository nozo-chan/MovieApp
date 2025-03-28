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

class SeatRepository(private val seatDatabase: SeatDao) {

    private val rows = 10
    private val cols = 10

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
        // Clear the database when the repository is created,
        CoroutineScope(Dispatchers.IO).launch {
            clearDatabase()
        }
    }

    /**
     * Retrieves the current seat layout.
     */
    fun getSeats(): List<List<Seat>> = seats.value

    /**
     * Reserves a list of seats based on given positions (row, col).
     * Updates the in-memory seat list and saves changes to the database.
     */
    private fun reserveSeats(seatPositions: List<Pair<Int, Int>>) {
        _seats.value = seats.value.map { rowList ->
            rowList.map { seat ->
                seatPositions.find { (row, col) -> row == seat.row && col == seat.col }
                    ?.let { seat.copy(isOccupied = true) }
                    ?: seat
            }
        }
        CoroutineScope(Dispatchers.IO).launch { saveSeats(seats.value) }
    }

    /**
     * Attempts to find and reserve a specified number of seats.
     * If no contiguous seats are available, it assigns separate seats.
     */
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

    /**
     * Reserves user-selected seats and ensures no index out-of-bounds errors.
     * Updates both in-memory and database storage.
     */
    fun reserveUserSelectedSeats(selectedSeats: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        val updatedSeats = seats.value.map { rowSeats ->
            rowSeats.map { seat ->
                if (selectedSeats.any { it.first == seat.row && it.second == seat.col } && !seat.isOccupied) {
                    seat.copy(isOccupied = true)
                } else seat
            }
        }

        try {
            _seats.value = updatedSeats
            reserveSeats(selectedSeats)
            CoroutineScope(Dispatchers.IO).launch { reserveSeatsInDatabase(selectedSeats) }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("ReserveSeats", "Attempted to reserve seats out of bounds", e)
        }

        return selectedSeats.filter { (row, col) ->
            row in updatedSeats.indices && col in updatedSeats[row].indices && updatedSeats[row][col].isOccupied
        }
    }

    /**
     * Finds a group of seats for a group booking.
     */
    private fun findSeatsForGroup(numPeople: Int): List<Pair<Int, Int>> {
        val currentSeats = seats.value
        currentSeats.forEach { rowSeats ->
            rowSeats.windowed(numPeople).forEach { window ->
                if (window.all { !it.isOccupied }) {
                    return window.map { Pair(it.row, it.col) }
                }
            }
        }
        return emptyList()
    }

    /**
     * Assigns separate seats when no group seating is available.
     */
    private fun assignSeparateSeats(numPeople: Int): List<Pair<Int, Int>> {
        val availableSeats = seats.value
            .flatten()
            .filter { !it.isOccupied }
            .take(numPeople)

        if (availableSeats.size < numPeople) return emptyList()

        val updatedSeats = seats.value.map { rowSeats ->
            rowSeats.map { seat ->
                if (availableSeats.any { it.row == seat.row && it.col == seat.col }) {
                    seat.copy(isOccupied = true)
                } else seat
            }
        }
        _seats.value = updatedSeats

        return availableSeats.map { it.row to it.col }
    }

    /**
     * Fetches seats from the database and updates the StateFlow.
     */
    fun fetchSeats() {
        val seatsFromDb = seatDatabase.getAllSeats().chunked(10)
        _seats.value = seatsFromDb
    }

    /**
     * Saves seats to the database, updating existing entries or inserting new ones.
     */
    suspend fun saveSeats(seats: List<List<Seat>>) {
        val allSeats = seatDatabase.getAllSeats()
        val flattenedSeats = seats.flatten()

        if (allSeats.isNotEmpty()) {
            updateSeats(flattenedSeats)
        } else {
            seatDatabase.insertSeats(flattenedSeats)
        }
    }

    /**
     * Reserves seats in the database based on the given positions.
     */
    private suspend fun reserveSeatsInDatabase(seatPositions: List<Pair<Int, Int>>) {
        seatPositions.forEach { (row, col) ->
            seatDatabase.reserveSeat(row, col)
        }
        fetchSeats()
    }

    /**
     * Updates seat statuses in the database and refreshes the StateFlow.
     */
    suspend fun updateSeats(seats: List<Seat>) {
        seatDatabase.updateSeats(seats)
        fetchSeats()
    }

    /**
     * Clears all seat data from the database.
     */
    suspend fun clearDatabase() {
        seatDatabase.clearSeats()
    }
}