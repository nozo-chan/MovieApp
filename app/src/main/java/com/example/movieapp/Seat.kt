package com.example.movieapp

data class Seat(
    val row: Int,
    val col: Int,
    var isOccupied: Boolean,
    val isReserved: Boolean = false
)
