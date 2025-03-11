package com.example.lightfilm

import android.content.Context
import android.content.res.Configuration
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.ui.theme.LightFilmTheme
import kotlin.math.log2

@Composable
fun OptionElement(modifier: Modifier, upperText: String, lowerText: String, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier,
        shape = RoundedCornerShape(15),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(upperText)
            Text(lowerText)
        }
    }
}

@Composable
fun CameraCaptureButton(imageCapture: ImageCapture, context: Context, handleEV: (Double) -> Unit) {
    IconButton(
        onClick = {
            onImageCaptureClick(imageCapture = imageCapture,
                applicationContext = context,
                onEVCalculated = { handleEV(it) })
        },
        modifier = Modifier
            .size(70.dp)
            .background(Color.Black, shape = CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun CameraOptionButton(onClick: () -> Unit, imageVector: ImageVector, contentDescription: String) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(35.dp)
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
    ) { Icon(imageVector = imageVector, contentDescription) }
}

@Composable
fun CameraCaptureButtonContent(
    isPortrait: Boolean,
    imageCapture: ImageCapture,
    context: Context,
    handleEV: (Double) -> Unit,
    switchLens: () -> Unit
) {
    val lensSwitchButton: @Composable () -> Unit =
        { CameraOptionButton(switchLens, Icons.Filled.Cached, "Lens switching") }
    val settingsButton: @Composable () -> Unit =
        { CameraOptionButton({/*TODO*/ }, Icons.Filled.Settings, "Settings") }

    if (isPortrait) lensSwitchButton() else settingsButton()
    CameraCaptureButton(imageCapture, context, handleEV)
    if (isPortrait) settingsButton() else lensSwitchButton()

}

@Composable
fun CameraCaptureButtonBar(
    isPortrait: Boolean,
    imageCapture: ImageCapture,
    context: Context,
    handleEV: (Double) -> Unit,
    switchLens: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surfaceDim) {
        if (isPortrait) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CameraCaptureButtonContent(true, imageCapture, context, handleEV, switchLens)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                CameraCaptureButtonContent(false, imageCapture, context, handleEV, switchLens)
            }
        }
    }
}

@Composable
fun TopBarContent(
    selectedIsoIndex: Int,
    showIsoOverlay: () -> Unit,
    selectedNDIndex: Int,
    showNDOverlay: () -> Unit,
    imageCapture: ImageCapture,
    lensFacing: Int
) {
    Column {
        Row {
            OptionElement(
                Modifier,
                "ISO",
                isoSensitivityOptions[selectedIsoIndex].toString(),
                showIsoOverlay
            )
            OptionElement(
                Modifier,
                "ND",
                if (selectedNDIndex == 0) "None" else ndSensitivityOptions[selectedNDIndex].toString(),
                showNDOverlay
            )
        }
    }

    CameraPreviewScreen(
        lensFacing = lensFacing,
        imageCapture = imageCapture,
        modifier = Modifier
            .height(250.dp)
    )
}

@Composable
fun Measurement(modifier: Modifier = Modifier) {
    var showIsoOverlay by remember { mutableStateOf(false) }
    var selectedIsoIndex by rememberSaveable { mutableIntStateOf(15) }
    var showNDOverlay by remember { mutableStateOf(false) }
    var selectedNDIndex by rememberSaveable { mutableIntStateOf(0) }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    var exposureValue by remember { mutableDoubleStateOf(7.5) }

    fun handleIsoValueSelected(value: Int) {
        // TODO - Update EV when called
        selectedIsoIndex = value
        showIsoOverlay = false
    }

    fun handleNDValueSelected(value: Int) {
        selectedNDIndex = value
        showNDOverlay = false
    }

    fun handleEV(value: Double) {
        exposureValue = value
        println(exposureValue)
    }

    fun switchLens() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    Box {
        if (showIsoOverlay) {
            ValueSelectionDialog(
                modifier = Modifier,
                onDismissRequest = { showIsoOverlay = false },
                onConfirmationRequest = ::handleIsoValueSelected,
                isIsoSelection = true,
                selectedValue = selectedIsoIndex
            )
        }
        if (showNDOverlay) {
            ValueSelectionDialog(
                modifier = Modifier,
                onDismissRequest = { showNDOverlay = false },
                onConfirmationRequest = ::handleNDValueSelected,
                isIsoSelection = false,
                selectedValue = selectedNDIndex
            )
        }

        if (isPortrait) {
            Column(modifier = modifier) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopBarContent(
                            selectedIsoIndex,
                            { showIsoOverlay = true },
                            selectedNDIndex,
                            { showNDOverlay = true },
                            imageCapture,
                            lensFacing
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1F)
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.6f)) { FStopTable(ev = exposureValue) }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            String.format(
                                "%.1f EV", exposureValue
                            )
                        )
                    }
                }
                CameraCaptureButtonBar(true, imageCapture, context, ::handleEV, ::switchLens)
            }
        } else {
            Row(modifier = modifier) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TopBarContent(
                            selectedIsoIndex,
                            { showIsoOverlay = true },
                            selectedNDIndex,
                            { showNDOverlay = true },
                            imageCapture,
                            lensFacing
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1F)
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.6f)
                    ) { FStopTable(ev = exposureValue) }
                    Column(modifier = Modifier.fillMaxHeight()) {
                        Text(
                            String.format(
                                "%.1f EV", exposureValue
                            )
                        )
                    }
                }
                CameraCaptureButtonBar(false, imageCapture, context, ::handleEV, ::switchLens)
            }
        }
    }
}

@Composable
fun ValueSelectionDialog(
    modifier: Modifier,
    isIsoSelection: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmationRequest: (Int) -> Unit,
    selectedValue: Int,
) {
    var selected by rememberSaveable { mutableIntStateOf(selectedValue) }
    val selectionItems = if (isIsoSelection) isoSensitivityOptions else ndSensitivityOptions
    androidx.compose.material3.AlertDialog(
        modifier = modifier.fillMaxHeight(0.66f),
        // TODO - Select fitting Icons
        icon = {
            Icon(
                if (isIsoSelection) Icons.Default.Add else Icons.Default.Settings,
                contentDescription = "Example Icon"
            )
        },
        title = {
            Text(text = if (isIsoSelection) "ISO" else "ND")
        },
        text = {

            Surface(
                shape = RoundedCornerShape(10),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(if (isIsoSelection) "Film sensitivity" else "Neutral density filter factor")
                    HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1F)
                    ) {

                        selectionItems.forEachIndexed { index, value ->
                            // Calculates the evValues for ISO and ND values
                            val evValue =
                                if (isIsoSelection) log2(value.toDouble() / selectionItems[selected])
                                else
                                    if (selectionItems[selected] == 0 && value == 0) 0.0 else if (selectionItems[selected] == 0) 0 - log2(
                                        value.toDouble()
                                    ) else
                                        if (value == 0) log2(selectionItems[selected].toDouble())
                                        else log2(selectionItems[selected].toDouble()) - log2(value.toDouble())
                            val sign =
                                if (evValue >= 0) "+"
                                else ""

                            DropdownItem(
                                value = if (value == 0) "None" else value.toString(),
                                helperValue = if (evValue == 0.0) "" else String.format(
                                    "$sign%.1f EV", evValue
                                ),
                                onClick = { selected = index },
                                selected = selected == index
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmationRequest(selected)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}


@Composable
fun DropdownItem(
    modifier: Modifier = Modifier,
    value: String,
    helperValue: String,
    onClick: () -> Unit,
    selected: Boolean = false,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(value, modifier = modifier.weight(1F))
        Text(helperValue, modifier = modifier.padding(10.dp))
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
                .width(80.dp) // Set a fixed width here
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

@Composable
fun FStopTable(aperture: Double = 2.0, ev: Double = 5.0) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        fNumbers.forEachIndexed() { index, f ->
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


@Preview(showBackground = true, heightDp = 500)
@Composable
fun MeasurementPreview() {
    LightFilmTheme {
        Measurement()
    }
}

@Preview(showBackground = true, heightDp = 500, widthDp = 1000)
@Composable
fun MeasurementLandscapePreview() {
    LightFilmTheme {
        Measurement()
    }
}
