package com.example.lightfilm.measurement

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.lightfilm.FPrefixVisualTransformation
import com.example.lightfilm.decimalStringClean
import com.example.lightfilm.onImageCaptureClick

@Composable
fun CameraCaptureButton(
    imageCapture: ImageCapture,
    context: Context,
    exposureValue: Double,
    handleEV: (Double, Double, Double, Int, String) -> Unit
) {
    IconButton(
        onClick = {
            onImageCaptureClick(
                imageCapture = imageCapture,
                applicationContext = context,
                onEVCalculated = handleEV
            )
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
                                "%.1f", exposureValue, Modifier.align(Alignment.CenterHorizontally)
                            )
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
    exposureValue: Double,
    handleEV: (Double, Double, Double, Int, String) -> Unit,
    switchLens: () -> Unit,
    handleImageSaving: () -> Unit,
    handleImageRejection: (Context) -> Unit,
    imagePath: String = ""
) {
    val context = LocalContext.current
    val leftButton: @Composable () -> Unit =
        {
            if (imagePath != "") CameraOptionButton(
                { handleImageRejection(context) },
                Icons.Filled.Delete,
                "Delete preview image"
            ) else CameraOptionButton(switchLens, Icons.Filled.Cached, "Lens switching")
        }
    val rightButton: @Composable () -> Unit =
        {
            if (imagePath != "") CameraOptionButton(
                { handleImageSaving() },
                Icons.Filled.Save,
                "Save preview image"
            )
            else CameraOptionButton({/*TODO*/ }, Icons.Filled.QuestionMark, "")
        }

    if (isPortrait) leftButton() else rightButton()
    CameraCaptureButton(imageCapture, context, exposureValue, handleEV)
    if (isPortrait) rightButton() else leftButton()

}

@Composable
fun CameraCaptureButtonBar(
    isPortrait: Boolean,
    imageCapture: ImageCapture,
    exposureValue: Double,
    handleEV: (Double, Double, Double, Int, String) -> Unit,
    switchLens: () -> Unit,
    handleImageSaving: () -> Unit,
    handleImageRejection: (Context) -> Unit,
    imagePath: String = ""
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
                    exposureValue,
                    handleEV,
                    switchLens,
                    handleImageSaving,
                    handleImageRejection,
                    imagePath
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
                    exposureValue,
                    handleEV,
                    switchLens,
                    handleImageSaving,
                    handleImageRejection,
                    imagePath
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApertureShutterSelectionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit,
) {
    var apertureValue by remember { mutableStateOf("1.2") }
    var shutterSpeedValue by remember { mutableStateOf("0.25") }

    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.66f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1F), contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = "Input the values used for aperture and shutter speed."
                        )
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(1F)
                                    .padding(16.dp),
                                value = apertureValue,
                                label = { Text("Aperture") },
                                maxLines = 1,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = FPrefixVisualTransformation(),
                                onValueChange = {
                                    apertureValue = decimalStringClean(apertureValue, it)
                                },
                            )

                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(1F)
                                    .padding(16.dp),
                                value = shutterSpeedValue,
                                label = { Text("Shutter speed") },
                                maxLines = 1,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                onValueChange = {
                                    shutterSpeedValue = decimalStringClean(shutterSpeedValue, it)
                                },
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation(apertureValue, shutterSpeedValue) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview()
@Composable
fun Test() {
    ApertureShutterSelectionDialog({}, { _, _ -> })
}