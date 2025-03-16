package com.example.lightfilm.measurement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MeasurementContent(modifier: Modifier, isPortrait: Boolean, exposureValue: Double) {
    if (isPortrait) {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceBright
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                if (exposureValue != -999.0) {
                    Column(modifier = Modifier.fillMaxWidth(0.6f)) { FStopTable(ev = exposureValue) }
                } else {
                    Text("Press the capture button.")
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    // TODO Zoom and exposure customization slider
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
                    // TODO Zoom and exposure customization slider
                }
            }
        }
    }
}