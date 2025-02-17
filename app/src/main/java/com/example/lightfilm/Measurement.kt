package com.example.lightfilm

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.ui.theme.LightFilmTheme
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.pow

@Composable
fun Measurement(modifier: Modifier = Modifier) {
    var showIsoOverlay by remember { mutableStateOf(false) }
    var selectedIsoIndex by rememberSaveable { mutableIntStateOf(15) }
    var showNDOverlay by remember { mutableStateOf(false) }
    var selectedNDIndex by rememberSaveable { mutableIntStateOf(0) }

    val imageCapture = remember{ImageCapture.Builder().build()}
    val context = LocalContext.current

    var exposureValue by remember { mutableDoubleStateOf(7.5) }

    fun handleIsoValueSelected(value: Int) {
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

    Box {
        if (showIsoOverlay) {
            DropdownSelection(selectedValue = selectedIsoIndex,
                onClickSelect = ::handleIsoValueSelected,
                onClickCancel = { showIsoOverlay = false })
        }
        if (showNDOverlay) {
            DropdownSelection(isIsoSelection = false,
                selectedValue = selectedNDIndex,
                onClickSelect = ::handleNDValueSelected,
                onClickCancel = { showNDOverlay = false })
        }
        Column(modifier = modifier) {
            Surface(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.secondary
            ) {
                Row {
                    Surface(color = MaterialTheme.colorScheme.primary,
                        modifier = modifier.padding(5.dp),
                        shape = RoundedCornerShape(15),
                        onClick = { showIsoOverlay = true }) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text("ISO")
                            Text(isoSensitivityOptions[selectedIsoIndex].toString())
                        }
                    }

                    Surface(color = MaterialTheme.colorScheme.primary,
                        modifier = modifier.padding(5.dp),
                        shape = RoundedCornerShape(15),
                        onClick = { showNDOverlay = true }) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text("ND")
                            Text(if (selectedNDIndex == 0) "None" else ndSensitivityOptions[selectedNDIndex].toString())
                        }
                    }

                    CameraPreviewScreen(imageCapture = imageCapture)
                }
            }

            Row(
                modifier = Modifier
                    .weight(1F)
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.6f)) { FStopTable(ev=exposureValue) }
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        String.format(
                            "%.1f EV", exposureValue
                        )
                    )
                }
            }

            // Camera button row
            Surface(color = MaterialTheme.colorScheme.surfaceDim) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(8.dp)
                ) {

                    Spacer(modifier = Modifier.weight(1F))

                    // TODO Additional Button necessary
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.CenterVertically)
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    ) { Icon(imageVector = Icons.Default.Settings, "Settings") }

                    Spacer(modifier = Modifier.weight(1F))

                    IconButton(
                        onClick = {
                            onImageCaptureClick(imageCapture = imageCapture,
                                applicationContext = context,
                                onEVCalculated = { handleEV(it) })
                        },
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.CenterVertically)
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

                    Spacer(modifier = Modifier.weight(1F))

                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.CenterVertically)
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    ) { Icon(imageVector = Icons.Default.Settings, "Settings") }

                    Spacer(modifier = Modifier.weight(1F))
                }
            }
        }
    }
}


@Composable
fun DropdownSelection(
    modifier: Modifier = Modifier,
    isIsoSelection: Boolean = true,
    selectedValue: Int = if (isIsoSelection) 18 else 0,
    onClickSelect: (Int) -> Unit = {},
    onClickCancel: () -> Unit = {}
) {
    var selected by rememberSaveable { mutableIntStateOf(selectedValue) }
    val selectionItems = if (isIsoSelection) isoSensitivityOptions else ndSensitivityOptions
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(if (isIsoSelection) "ISO" else "ND", textAlign = TextAlign.Center)
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
                        if (isIsoSelection) log2(value.toDouble() / selectionItems[selected]) else if (selectionItems[selected] == 0 && value == 0) 0.0 else if (selectionItems[selected] == 0) 0 - log2(
                            value.toDouble()
                        ) else if (value == 0) log2(selectionItems[selected].toDouble()) else log2(
                            selectionItems[selected].toDouble()
                        ) - log2(value.toDouble())
                    val sign =
                        if (isIsoSelection) if (evValue >= 0) "+" else "" else if (evValue >= 0) "+" else ""

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
            Row {
                Surface(modifier = Modifier.weight(1F)) { }
                TextButton(onClick = onClickCancel) {
                    Text("Cancel")
                }
                TextButton(onClick = { onClickSelect(selected) }) {
                    Text("Select")
                }
            }
        }
    }
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
            text = String.format("f/%.1f",f),
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
            val xStart = if (isFullStop) 0f else canvasWidth/4
            val xEnd = if (isFullStop) canvasWidth else canvasWidth/4*3
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
            val shutterSpeed = findClosestNumber(shutterSpeeds, getShutterSpeedFromAperture(f, ev, 100))
            CrosshairItem(f, shutterSpeed)
            if (index != fNumbers.size - 1) {
                val f2 = (f + ((fNumbers[index + 1] - f) / 3))
                val s2 = findClosestNumber(shutterSpeeds, getShutterSpeedFromAperture(f2, ev, 100))
                val f3 = (f + ((fNumbers[index + 1] - f) / 3  * 2))
                val s3 = findClosestNumber(shutterSpeeds, getShutterSpeedFromAperture(f3, ev, 100))

                CrosshairItem(f2, s2, false)
                CrosshairItem(f3, s3, false)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun FStopTablePreview() {
    LightFilmTheme {
        FStopTable()
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
fun MeasurementPreview() {
    LightFilmTheme {
        Measurement()
    }
}

@Preview(heightDp = 500, widthDp = 300)
@Composable
fun DropdownPreview() {
    LightFilmTheme {
        DropdownSelection(
            isIsoSelection = false,
        )
    }
}