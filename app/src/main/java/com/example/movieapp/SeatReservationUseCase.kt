package com.example.movieapp

//class SeatReservationUseCase(val repository: SeatRepository) {
//
//    fun findAndReserveSeats(numPeople: Int, selectedSeats: List<Pair<Int, Int>>? = null): List<Pair<Int, Int>> {
//        val seats = repository.getSeats()  // Get the current seats from the repository
//
//        // If the user has selected specific seats, try to reserve them first
//        if (!selectedSeats.isNullOrEmpty()) {
//            val assignedSeats = userSelectedSeats(seats, selectedSeats)
//
//            // If the selected seats fit the number of people, return them
//            if (assignedSeats.size == numPeople) {
//                return assignedSeats
//            }
//            // If not all selected seats can be reserved, we will try automatic seat allocation
//        }
//
//        // Try to find contiguous seats for the group
//        val groupedSeats = findSeatsForGroup(seats, numPeople)
//        if (groupedSeats.isNotEmpty()) {
//            repository.reserveSeats(groupedSeats)  // Reserve contiguous seats
//            return groupedSeats  // Return the reserved seats
//        }
//
//        // If no contiguous seats are available, try to assign seats separately
//        return assignSeats(seats, numPeople)
//    }
//
//    // Function to reserve the user-selected seats
//    fun userSelectedSeats(
//        seats: Array<Array<Seat>>,
//        selectedSeats: List<Pair<Int, Int>>
//    ): List<Pair<Int, Int>> {
//        val assignedSeats = mutableListOf<Pair<Int, Int>>()  // List to hold the assigned seats
//
//        for ((row, col) in selectedSeats) {
//            // Check if the selected seat is valid and not occupied
//            if (row in seats.indices && col in seats[row].indices && !seats[row][col].isOccupied) {
//                assignedSeats.add(Pair(row, col))  // Add the seat to the assigned list
//            }
//        }
//
//        // If we have any valid selected seats, reserve them
//        if (assignedSeats.isNotEmpty()) {
//            repository.reserveSeats(assignedSeats)  // Reserve the selected seats
//        }
//
//        return assignedSeats  // Return the list of assigned seats
//    }
//
//    // Function to find contiguous seats for the group
//    private fun findSeatsForGroup(seats: Array<Array<Seat>>, numPeople: Int): List<Pair<Int, Int>> {
//        for (row in seats.indices) {
//            for (col in 0..(seats[row].size - numPeople)) {
//                // Check if there are enough empty seats in a row to fit the group
//                val potentialSeats = seats[row].slice(col until col + numPeople)
//                if (potentialSeats.all { !it.isOccupied }) {
//                    // Return the list of contiguous empty seats
//                    return potentialSeats.map { Pair(it.row, it.col) }
//                }
//            }
//        }
//        return emptyList()  // Return an empty list if no contiguous seats are found
//    }
//
//    // Function to assign separate seats when no contiguous group seating is available
//    private fun assignSeats(seats: Array<Array<Seat>>, numPeople: Int): List<Pair<Int, Int>> {
//        val assignedSeats = mutableListOf<Pair<Int, Int>>()  // List to hold the assigned seats
//
//        for (row in seats.indices) {
//            for (col in seats[row].indices) {
//                // Check if the seat is empty
//                if (!seats[row][col].isOccupied) {
//                    assignedSeats.add(Pair(row, col))  // Add the seat to the assigned list
//                    if (assignedSeats.size == numPeople) {
//                        repository.reserveSeats(assignedSeats)  // Reserve the separate seats
//                        return assignedSeats  // Return the assigned seats
//                    }
//                }
//            }
//        }
//
//        return assignedSeats  // Return the list of assigned seats, even if not all are reserved
//    }
//}



//class SeatRepository(private val rows: Int, private val cols: Int) {
//
//    // Initialize the seats array, each seat has a row, column, and random occupancy status
//    private val seats: Array<Array<Seat>> = Array(rows) { row ->
//        Array(cols) { col ->
//            Seat(
//                row,
//                col,
//                Random.nextBoolean()
//            )  // Randomly assigning seat occupancy (true or false)
//        }
//    }
//
//    // Function to retrieve the current seats array
//    fun getSeats(): Array<Array<Seat>> = seats
//
//    // Function to reserve a list of seats based on the given positions (row, col)
//    fun reserveSeats(seatPositions: List<Pair<Int, Int>>) {
//        seatPositions.forEach { (row, col) ->
//            // Mark the seat as occupied
//            seats[row][col].isOccupied = true
//        }
//    }
//}
//    fun findAndReserveSeats(numPeople: Int, selectedSeats: List<Pair<Int, Int>>? = null): List<Pair<Int, Int>> {
//        val seats = getSeats()  // Get the current seats from the repository
//
//        // If the user has selected specific seats, try to reserve them first
//        if (!selectedSeats.isNullOrEmpty()) {
//            val assignedSeats = userSelectedSeats(seats, selectedSeats)
//
//            // If the selected seats fit the number of people, return them
//            if (assignedSeats.size == numPeople) {
//                return assignedSeats
//            }
//        }
//
//        // Try to find contiguous seats for the group
//        val groupedSeats = findSeatsForGroup(seats, numPeople)
//        if (groupedSeats.isNotEmpty()) {
//            reserveSeats(groupedSeats)  // Reserve contiguous seats
//            return groupedSeats  // Return the reserved seats
//        }
//
//        // If no contiguous seats are available, try to assign seats separately
//        return assignSeats(seats, numPeople)
//    }
//
//    // Function to reserve the user-selected seats
//    fun userSelectedSeats(
//        seats: Array<Array<Seat>>,
//        selectedSeats: List<Pair<Int, Int>>
//    ): List<Pair<Int, Int>> {
//        val assignedSeats = mutableListOf<Pair<Int, Int>>()  // List to hold the assigned seats
//
//        for ((row, col) in selectedSeats) {
//            // Check if the selected seat is valid and not occupied
//            if (row in seats.indices && col in seats[row].indices && !seats[row][col].isOccupied) {
//                assignedSeats.add(Pair(row, col))  // Add the seat to the assigned list
//            }
//        }
//
//        // If we have any valid selected seats, reserve them
//        if (assignedSeats.isNotEmpty()) {
//            reserveSeats(assignedSeats)  // Reserve the selected seats
//        }
//
//        return assignedSeats  // Return the list of assigned seats
//    }
//
//    // Function to find contiguous seats for the group
//    private fun findSeatsForGroup(seats: Array<Array<Seat>>, numPeople: Int): List<Pair<Int, Int>> {
//        for (row in seats.indices) {
//            for (col in 0..(seats[row].size - numPeople)) {
//                // Check if there are enough empty seats in a row to fit the group
//                val potentialSeats = seats[row].slice(col until col + numPeople)
//                if (potentialSeats.all { !it.isOccupied }) {
//                    // Return the list of contiguous empty seats
//                    return potentialSeats.map { Pair(it.row, it.col) }
//                }
//            }
//        }
//        return emptyList()  // Return an empty list if no contiguous seats are found
//    }
//
//    // Function to assign separate seats when no contiguous group seating is available
//    private fun assignSeats(seats: Array<Array<Seat>>, numPeople: Int): List<Pair<Int, Int>> {
//        val assignedSeats = mutableListOf<Pair<Int, Int>>()  // List to hold the assigned seats
//
//        for (row in seats.indices) {
//            for (col in seats[row].indices) {
//                // Check if the seat is empty
//                if (!seats[row][col].isOccupied) {
//                    assignedSeats.add(Pair(row, col))  // Add the seat to the assigned list
//                    if (assignedSeats.size == numPeople) {
//                        reserveSeats(assignedSeats)  // Reserve the separate seats
//                        return assignedSeats  // Return the assigned seats
//                    }
//                }
//            }
//        }
//        return assignedSeats  // Return the list of assigned seats, even if not all are reserved
//    }
//}
//}