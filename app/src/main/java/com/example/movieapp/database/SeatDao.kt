package com.example.movieapp.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SeatDao {
    @Query("SELECT * FROM seats")
    fun getAllSeats(): List<Seat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeats(seats: List<Seat>)

    @Update
    suspend fun updateSeats(seat: List<Seat>)

    @Query("UPDATE seats SET isOccupied = 1 WHERE row = :row AND col = :col")
    suspend fun reserveSeat(row: Int, col: Int)

    @Query("DELETE FROM seats")
    suspend fun clearSeats()
}