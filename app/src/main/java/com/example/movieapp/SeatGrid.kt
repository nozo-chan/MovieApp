package com.example.movieapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


//@Composable
//fun SeatGrid(viewModel: SeatViewModel) {
//    val seats by viewModel.seats.collectAsState()
//    val selectedSeats by viewModel.selectedSeats.collectAsState()
//
//    Spacer(modifier = Modifier.height(50.dp))
//
//    Column {
//        seats.forEachIndexed { rowIndex, row ->
//            Row(modifier = Modifier.fillMaxWidth()) {
//                // Row Number on the left side
//                Text(
//                    text = "Row ${rowIndex + 1}",
//                    modifier = Modifier
//                        .align(Alignment.CenterVertically)
//                        .padding(end = 8.dp)
//                )
//
//                row.forEachIndexed { colIndex, seat ->
//                    val isSelected = selectedSeats.contains(Pair(rowIndex, colIndex))
//                    Box(
//                        modifier = Modifier
//                            .size(40.dp)
//                            .padding(4.dp)
//                            .background(
//                                when {
//                                    seat.isReserved -> Color.Blue // Reserved seats (add a color here)
//                                    seat.isOccupied -> Color.Gray // Already reserved
//                                    isSelected -> Color.Green // Selected by user
//                                    else -> Color.White // Available seat
//                                }
//                            )
//                            .clickable {
//                                if (!seat.isOccupied && !seat.isReserved) {
//                                    viewModel.toggleSeatSelection(rowIndex, colIndex)
//                                }
//                            },
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "S${colIndex + 1}",
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                }
//            }
//        }
//}

@Composable
fun SeatGrid(
    viewModel: SeatViewModel,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(Unit) {
        viewModel.loadSeats()
    }
    // Observe StateFlow as State
    val seats by viewModel.seats.collectAsState(emptyList())
    val selectedSeats by viewModel.selectedSeats.collectAsState(emptyList())

    // Set the width and height for the grid
    LazyColumn(
        modifier = modifier
            .fillMaxSize() // This makes sure the LazyColumn takes up the entire available space
            .padding(16.dp), // Add padding for spacing
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        itemsIndexed(seats) { rowIndex, row ->
            SeatRow(
                rowIndex = rowIndex,
                row = row,
                selectedSeats = selectedSeats,
                modifier = Modifier
                    .fillMaxWidth()  // Ensure each row fills the width
                    .height(70.dp),  // Set a height for the row (you can adjust this)
                onSeatClick = { colIndex ->
                    val seat = row[colIndex]
                    if (!seat.isOccupied && !seat.isReserved) {
                        viewModel.toggleSeatSelection(rowIndex, colIndex)
                    }
                }
            )
        }
    }
}

@Composable
fun SeatRow(
    rowIndex: Int,
    row: List<Seat>,
    selectedSeats: List<Pair<Int, Int>>,
    modifier: Modifier = Modifier,
    onSeatClick: (Int) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Row Number
        Text(
            text = "Row ${rowIndex + 1}",
            modifier = Modifier
                .width(50.dp)
                .padding(end = 8.dp),
            textAlign = TextAlign.Center
        )

        // LazyRow for displaying seats in a row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()  // Ensure each row fills the width of the parent
                .height(70.dp),  // Set the height for the seats (adjust as needed)
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(row) { colIndex, seat ->
                val isSelected = selectedSeats.contains(Pair(rowIndex, colIndex))
                SeatItem(
                    seat = seat,
                    isSelected = isSelected,
                    modifier = Modifier
                        .aspectRatio(1f) // Square seats
                        .height(60.dp) // Set the height for the seat item (adjustable)
                        .width(60.dp), // Set the width for the seat item (adjustable)
                    onClick = { onSeatClick(colIndex) }
                )
            }
        }
    }
}

@Composable
fun SeatItem(
    seat: Seat,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, borderColor, textColor) = when {
        seat.isReserved -> Triple(
            Color.Blue.copy(alpha = 0.3f),  // Lighter reserved color
            Color.Blue.copy(alpha = 0.7f),
            Color.White
        )
        seat.isOccupied -> Triple(
            Color.Gray.copy(alpha = 0.3f),  // Lighter occupied color
            Color.Gray.copy(alpha = 0.7f),
            Color.White
        )
        isSelected -> Triple(
            Color.Green.copy(alpha = 0.7f),  // Bright selection color
            Color.Green.copy(alpha = 1f),
            Color.White
        )
        else -> Triple(
            Color.White,
            Color.Black.copy(alpha = 0.3f),
            Color.Black
        )
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                enabled = !seat.isOccupied && !seat.isReserved,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "S${seat.col + 1}",
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewSeatGrid() {
//    // Mock data for preview
//    val mockSeats = List(10) { rowIndex ->
//        List(10) { colIndex ->
//            Seat(
//                row = rowIndex,
//                col = colIndex,
//                isOccupied = false,
//                isReserved = false
//            )
//        }
//    }
//
//    val viewModel = SeatViewModel() // Create an instance of your ViewModel
//    // Set the mock data for the preview (you might want to replace this with your actual ViewModel logic in a real app)
//    viewModel._seats.value = mockSeats
//
//    // Pass the ViewModel into the SeatGrid composable
//    SeatGrid(viewModel = viewModel)
//}