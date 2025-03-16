package com.example.lightfilm.measurement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

// TODO Functionality for zoom and exposure customization slider

@Composable
fun MeasurementContent(
    modifier: Modifier,
    isPortrait: Boolean,
    exposureValue: Double,
    sliderStartPosition: Float
) {
    var sliderPosition by rememberSaveable { mutableFloatStateOf(sliderStartPosition) }
    if (isPortrait) {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceBright
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.fillMaxWidth(0.6f)) {
                    if (exposureValue != -999.0) {
                        FStopTable(ev = exposureValue)
                    } else {
                        Text("Press the capture button.")
                    }
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it },
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .rotate(270f)
                        )
                    }
                }
            }
        }
    } else {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceBright
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.6f)
                ) { if (exposureValue != -999.0) FStopTable(ev = exposureValue) else Text("Press the capture button.") }
                Column(modifier = Modifier.fillMaxHeight()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it },
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .rotate(270f)
                        )
                    }
                }
            }
        }
    }
}
