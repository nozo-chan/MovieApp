package com.example.movieapp

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "seats")
data class Seat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val row: Int,
    val col: Int,
    var isOccupied: Boolean,
    val isReserved: Boolean = false
)
