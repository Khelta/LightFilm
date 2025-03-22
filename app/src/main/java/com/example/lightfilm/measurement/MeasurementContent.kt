package com.example.lightfilm.measurement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// TODO Functionality for zoom and exposure customization slider

@Composable
fun MeasurementContent(
    modifier: Modifier,
    isPortrait: Boolean,
    exposureValue: Double,
    sliderStartPosition: Float,
    zoomStartPosition: Float
) {
    var sliderPosition by rememberSaveable { mutableFloatStateOf(sliderStartPosition) }
    var zoomSliderPosition by rememberSaveable { mutableFloatStateOf(zoomStartPosition) }
    if (isPortrait) {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceBright
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                    if (exposureValue != -999.0) {
                        FStopTable(ev = exposureValue)
                    } else {
                        Text("Press the capture button.")
                    }
                }
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    CustomSlider(
                        sliderPosition,
                        { x -> sliderPosition = x },
                        Icons.Filled.Exposure,
                        ""
                    )
                    CustomSlider(
                        zoomSliderPosition,
                        { x -> zoomSliderPosition = x },
                        Icons.Filled.ZoomIn,
                        ""
                    )
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
            }
        }
    }
}

@Composable
fun CustomSlider(
    sliderPosition: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    iconDescription: String
) {
    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { onValueChange(it) },
            modifier = Modifier.padding(16.dp)
        )
        Icon(icon, iconDescription)
    }
}

@Preview(widthDp = 500)
@Composable
fun MeasurementContentPreview() {
    MeasurementContent(Modifier, true, 0.0, 0f, 0f)
}