package com.example.lightfilm.measurement

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.fNumbers
import com.example.lightfilm.findClosestNumber
import com.example.lightfilm.getShutterSpeedFromAperture
import com.example.lightfilm.shutterSpeeds

@Composable
fun FStopTable(aperture: Double = 2.0, ev: Double = 5.0) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        fNumbers.forEachIndexed { index, f ->
            val shutterSpeed =
                findClosestNumber(shutterSpeeds, getShutterSpeedFromAperture(f, ev, 100))
            CrosshairItem(f, shutterSpeed)
            if (index != fNumbers.size - 1) {
                val f2 = (f + ((fNumbers[index + 1] - f) / 3))
                val s2 = findClosestNumber(shutterSpeeds, getShutterSpeedFromAperture(f2, ev, 100))
                val f3 = (f + ((fNumbers[index + 1] - f) / 3 * 2))
                val s3 = findClosestNumber(shutterSpeeds, getShutterSpeedFromAperture(f3, ev, 100))

                CrosshairItem(f2, s2, false)
                CrosshairItem(f3, s3, false)
            }
        }
    }
}

@Composable
fun CrosshairItem(
    f: Double,
    shutterSpeed: Double,
    isFullStop: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Fixed-width area for Text1
        Text(
            text = String.format("f/%.1f", f),
            modifier = Modifier
                // TODO width independent solution? whats with different font sizes?
                .width(60.dp) // Set a fixed width here
                .padding(end = 8.dp),
            textAlign = TextAlign.End // Align text to the end of the area
        )

        // Spacer to push the crosshair to the right
        Spacer(modifier = Modifier.width(8.dp))

        Canvas(
            modifier = Modifier
                .width(40.dp)
                .height(30.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2
            val centerY = canvasHeight / 2

            // Draw vertical line
            drawLine(
                color = Color.Gray,
                start = Offset(x = centerX, y = 0f),
                end = Offset(x = centerX, y = canvasHeight),
                strokeWidth = 2f
            )

            // Draw horizontal line
            val xStart = if (isFullStop) 0f else canvasWidth / 4
            val xEnd = if (isFullStop) canvasWidth else canvasWidth / 4 * 3
            drawLine(
                color = Color.Gray,
                start = Offset(x = xStart, y = centerY),
                end = Offset(x = xEnd, y = centerY),
                strokeWidth = 2f
            )
        }
        // Spacer to push the text2 to the right
        Spacer(modifier = Modifier.width(8.dp))

        val shutterSpeedText =
            if (shutterSpeed >= 1) {
                String.format(
                    if (shutterSpeed.rem(1).equals(0.0)) "%.0f\"" else "%.1f\"", shutterSpeed
                )
            } else {
                String.format("1/%.0f", (1.0 / shutterSpeed))
            }

        Text(text = shutterSpeedText, modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview
@Composable
fun CrosshairItemPreview() {
    CrosshairItem(1.0, 1.0)
}