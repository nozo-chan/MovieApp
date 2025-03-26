package com.example.movieapp

import android.app.Application

import com.example.movieapp.database.SeatDao
import com.example.movieapp.database.SeatDatabase
import com.example.movieapp.database.SeatDatabase.Companion.getDatabase

class CinemaApp: Application() {

    lateinit var dao: SeatDao
     lateinit var repository: SeatRepository
    override fun onCreate() {
        super.onCreate()
      dao = getDatabase(context =applicationContext).seatDao()
        repository = SeatRepository( dao)

    }
}