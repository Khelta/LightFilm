package com.example.lightfilm.measurement

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.lightfilm.onImageCaptureClick

@Composable
fun CameraCaptureButton(
    imageCapture: ImageCapture,
    context: Context,
    exposureValue: Double,
    handleEV: (Double, Double, Double) -> Unit
) {
    IconButton(
        onClick = {
            onImageCaptureClick(imageCapture = imageCapture,
                applicationContext = context,
                onEVCalculated = handleEV)
        },
        modifier = Modifier
            .size(70.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
    ) {

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .align(Alignment.Center)
            )
            {
                Column(Modifier.align(Alignment.Center)) {
                    if (exposureValue != -999.0) {
                        Text(
                            String.format(
                                "%.1f", exposureValue
                                , Modifier.align(Alignment.CenterHorizontally))
                        )
                        Text("EV", Modifier.align(Alignment.CenterHorizontally))
                    }
                }

            }
        }
    }
}

@Composable
fun CameraOptionButton(onClick: () -> Unit, imageVector: ImageVector, contentDescription: String) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(35.dp)
            .background(MaterialTheme.colorScheme.secondary, shape = CircleShape),
    ) { Icon(imageVector = imageVector, contentDescription) }
}

@Composable
fun CameraCaptureButtonContent(
    isPortrait: Boolean,
    imageCapture: ImageCapture,
    context: Context,
    exposureValue: Double,
    handleEV: (Double, Double, Double) -> Unit,
    switchLens: () -> Unit
) {
    val lensSwitchButton: @Composable () -> Unit =
        { CameraOptionButton(switchLens, Icons.Filled.Cached, "Lens switching") }
    val settingsButton: @Composable () -> Unit =
        { CameraOptionButton({/*TODO*/ }, Icons.Filled.Settings, "Settings") }

    if (isPortrait) lensSwitchButton() else settingsButton()
    CameraCaptureButton(imageCapture, context, exposureValue, handleEV)
    if (isPortrait) settingsButton() else lensSwitchButton()

}

@Composable
fun CameraCaptureButtonBar(
    isPortrait: Boolean,
    imageCapture: ImageCapture,
    context: Context,
    exposureValue: Double,
    handleEV: (Double, Double, Double) -> Unit,
    switchLens: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.outline) {
        if (isPortrait) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CameraCaptureButtonContent(
                    true,
                    imageCapture,
                    context,
                    exposureValue,
                    handleEV,
                    switchLens
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                CameraCaptureButtonContent(
                    false,
                    imageCapture,
                    context,
                    exposureValue,
                    handleEV,
                    switchLens
                )
            }
        }
    }
}