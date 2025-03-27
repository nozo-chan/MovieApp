package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.movieapp.ui.screen.SeatReservationScreen
import com.example.movieapp.ui.theme.MovieAppTheme
import com.example.movieapp.ui.viewmodel.SeatViewModel
import com.example.movieapp.ui.viewmodel.SeatViewModelFactory

class MainActivity : ComponentActivity() {

    protected fun application(): CinemaApp = application as CinemaApp

    private val seatViewModel: SeatViewModel by viewModels { seatViewModelFactory }

    private val seatViewModelFactory: SeatViewModelFactory
        get() = SeatViewModelFactory(application().repository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MovieAppTheme {
                SeatReservationScreen(seatViewModel)
            }
        }
    }
}
