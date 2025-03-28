package com.example.movieapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.movieapp.ui.component.SeatGrid
import com.example.movieapp.ui.viewmodel.SeatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatReservationTopBar(currentGroupSize: Int, onGroupSizeChange: (Int) -> Unit) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Seat Reservation") },
        actions = {
            Box {
                IconButton(onClick = { isDropdownExpanded = true }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Face, contentDescription = "Select Group Size")
                        Text("$currentGroupSize", modifier = Modifier.padding(start = 4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    (1..10).forEach { count ->
                        DropdownMenuItem(
                            text = { Text("$count People") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            onClick = {
                                onGroupSizeChange(count)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun SeatReservationScreen(viewModel: SeatViewModel) {
    var groupSize by remember { mutableIntStateOf(1) }
    val selectedSeats by viewModel.selectedSeats.collectAsState()

    Scaffold(
        topBar = { SeatReservationTopBar(groupSize) { groupSize = it } },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = { viewModel.findAndReserveSeats(groupSize) },
                    enabled = selectedSeats.isEmpty(),
                    modifier = Modifier.weight(1f)
                ) { Text("Find Seats") }

                Button(
                    onClick = { viewModel.reserveSelectedSeats() },
                    enabled = selectedSeats.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) { Text("Reserve Seats") }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text(
                text = "Selected Seats: ${selectedSeats.size}",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
            SeatGrid(viewModel = viewModel, modifier = Modifier.weight(1f))
        }
    }
}