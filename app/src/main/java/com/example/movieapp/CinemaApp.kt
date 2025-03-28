package com.example.movieapp

import com.example.movieapp.repository.SeatRepository
import android.app.Application

import com.example.movieapp.database.SeatDao
import com.example.movieapp.database.SeatDatabase.Companion.getDatabase

/**
 * The `CinemaApp` class serves as the Application class for the app.
 * It initializes and provides access to the database and repository
 * throughout the app lifecycle.
 */
class CinemaApp : Application() {

    /**
     * Instance of the SeatDao for accessing database operations related to seats.
     */
    lateinit var seatDatabase: SeatDao

    /**
     * Repository instance for handling business logic related to seat management.
     */
    lateinit var repository: SeatRepository

    override fun onCreate() {
        super.onCreate()

        // Initialize the database and repository when the app starts
        seatDatabase = getDatabase(context = applicationContext).seatDao()
        repository = SeatRepository(seatDatabase = seatDatabase)
    }
}