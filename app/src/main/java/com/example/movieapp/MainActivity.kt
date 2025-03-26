package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.movieapp.database.SeatDao
import com.example.movieapp.database.SeatDatabase.Companion.getDatabase
import com.example.movieapp.ui.theme.MovieAppTheme

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
