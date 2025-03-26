package com.example.movieapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movieapp.Seat

@Database(entities = [Seat::class], version = 1)
abstract class SeatDatabase : RoomDatabase() {
    abstract fun seatDao(): SeatDao


    companion object {
        @Volatile
        private var INSTANCE: SeatDatabase? = null

        // Thread-safe Singleton pattern
        fun getDatabase(context: Context): SeatDatabase {
            // Return the existing instance or create a new one
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SeatDatabase::class.java,
                    "seat_database"
                )  .allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}