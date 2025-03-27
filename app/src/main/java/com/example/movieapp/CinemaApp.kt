package com.example.movieapp

import com.example.movieapp.repository.SeatRepository
import android.app.Application

import com.example.movieapp.database.SeatDao
import com.example.movieapp.database.SeatDatabase.Companion.getDatabase

class CinemaApp: Application() {

    lateinit var dao: SeatDao
     lateinit var repository: SeatRepository
    override fun onCreate() {
        super.onCreate()
      dao = getDatabase(context =applicationContext).seatDao()
        repository = SeatRepository(cols = 10, rows = 10, dao = dao)

    }
}