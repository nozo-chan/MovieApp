package com.example.movieapp.ui.component

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieapp.database.Seat
import com.example.movieapp.ui.viewmodel.SeatViewModel

@Composable
fun SeatGrid(viewModel: SeatViewModel, modifier: Modifier = Modifier) {
    val seats by viewModel.seats.collectAsStateWithLifecycle()
    val selectedSeats by viewModel.selectedSeats.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(seats) { rowIndex, row ->
            SeatRow(
                rowIndex = rowIndex,
                row = row,
                selectedSeats = selectedSeats,
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
    onSeatClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(row) { colIndex, seat ->
            SeatItem(
                seat = seat,
                isSelected = selectedSeats.contains(Pair(rowIndex, colIndex)),
                onClick = { onSeatClick(colIndex) }
            )
        }
    }
}

@Composable
fun SeatItem(seat: Seat, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = when {
        seat.isReserved -> Color.Blue.copy(0.3f)
        seat.isOccupied -> Color.Gray.copy(0.3f)
        isSelected -> Color.Green.copy(0.7f)
        else -> Color.White
    }

    val borderColor = when {
        seat.isReserved -> Color.Blue.copy(0.7f)
        seat.isOccupied -> Color.Gray.copy(0.7f)
        isSelected -> Color.Green
        else -> Color.Black.copy(0.3f)
    }

    val textColor = if (seat.isReserved || seat.isOccupied || isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable(enabled = !seat.isOccupied && !seat.isReserved, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("S${seat.col + 1}", style = MaterialTheme.typography.bodySmall, color = textColor)
    }
}
