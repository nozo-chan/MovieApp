package com.example.movieapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.movieapp.Seat
import kotlinx.coroutines.flow.Flow

@Dao
interface SeatDao {
    @Query("SELECT * FROM seats ORDER BY row ASC, col ASC")
    fun getAllSeats(): List<Seat>

    @Query("SELECT * FROM seats WHERE isReserved = 0 AND isOccupied = 0")
    fun getAvailableSeats(): List<Seat>  // Use Flow to observe changes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeats(seats: List<Seat>)

    @Update
    suspend fun updateSeats(seats: List<Seat>)

    @Query("UPDATE seats SET isOccupied = 1 WHERE row = :row AND col = :col")
    suspend fun reserveSeat(row: Int, col: Int)

    @Query("UPDATE seats SET isOccupied = 0 WHERE row = :row AND col = :col")
    suspend fun unreserveSeat(row: Int, col: Int)
}