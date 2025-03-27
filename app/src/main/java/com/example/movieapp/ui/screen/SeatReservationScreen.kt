package com.example.movieapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
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
fun SeatReservationTopBar(
    currentGroupSize: Int,
    onGroupSizeChange: (Int) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Seat Reservation") },
        actions = {
            // Group Size Dropdown
            Box {
                // Clickable row to trigger dropdown
                Row(
                    modifier = Modifier
                        .clickable { isDropdownExpanded = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Select Group Size",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "$currentGroupSize People",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    (1..10).forEach { count ->
                        DropdownMenuItem(
                            text = { Text("$count People") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatReservationScreen(viewModel: SeatViewModel) {
    var groupSize by remember { mutableIntStateOf(1) }
    val selectedSeats by viewModel.selectedSeats.collectAsState()

    Scaffold(
        topBar = {
            SeatReservationTopBar(
                currentGroupSize = groupSize,
                onGroupSizeChange = { newSize -> groupSize = newSize }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Find Seats Button
                    Button(
                        onClick = { viewModel.findAndReserveSeats(groupSize) },
                        enabled = selectedSeats.isEmpty()
                    ) {
                        Text("Find Seats")
                    }

                    // Reserve Seats Button
                    Button(
                        onClick = { viewModel.reserveSelectedSeats() },
                        enabled = selectedSeats.isNotEmpty()
                    ) {
                        Text("Reserve Seats")
                    }

                    // Clear Selection Button
                    Button(
                        onClick = { viewModel.clearSelectedSeats() },
                        enabled = selectedSeats.isNotEmpty()
                    ) {
                        Text("Clear")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {

                // Seat Selection Status
                Text(
                    text = "Selected Seats: ${selectedSeats.size}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

                // Seat Grid
                SeatGrid(
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}