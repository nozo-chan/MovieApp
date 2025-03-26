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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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

// Updated SeatGrid to accept a modifier
@Composable
fun SeatGrid(
    viewModel: SeatViewModel,
    modifier: Modifier = Modifier
) {
    val seats by viewModel.seats.collectAsState()
    val selectedSeats by viewModel.selectedSeats.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        itemsIndexed(seats) { rowIndex, row ->
            SeatRow(
                rowIndex = rowIndex,
                row = row,
                selectedSeats = selectedSeats,
                modifier = Modifier.height(50.dp), // Fixed height
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
        modifier = modifier
            .fillMaxWidth()
            .then(modifier), // Allows passed-in modifier to override default
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


        LazyRow(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 40.dp, max = 60.dp), // Min and max height
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(row) { colIndex, seat ->
                val isSelected = selectedSeats.contains(Pair(rowIndex, colIndex))
                SeatItem(
                    seat = seat,
                    isSelected = isSelected,
                    modifier = Modifier.aspectRatio(1f), // Square seats
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

