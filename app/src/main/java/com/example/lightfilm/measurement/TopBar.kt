package com.example.lightfilm.measurement

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.lightfilm.CameraPreviewScreen
import com.example.lightfilm.isoSensitivityOptions
import com.example.lightfilm.ndSensitivityOptions
import java.io.File
import kotlin.math.log2

@Composable
fun TopBarContent(
    isPortrait: Boolean,
    selectedIsoIndex: Int,
    showIsoOverlay: () -> Unit,
    selectedNDIndex: Int,
    showNDOverlay: () -> Unit,
    imageCapture: ImageCapture,
    lensFacing: Int,
    linearZoom: Float,
    imagePath: String = ""
) {
    val previewModifier = Modifier.aspectRatio(if (isPortrait) 0.75f else 1.33f)

    Column {
        Row {
            OptionElement(
                Modifier.padding(8.dp),
                "ISO",
                isoSensitivityOptions[selectedIsoIndex].toString(),
                showIsoOverlay
            )
            OptionElement(
                Modifier.padding(8.dp),
                "ND",
                if (selectedNDIndex == 0) "None" else ndSensitivityOptions[selectedNDIndex].toString(),
                showNDOverlay
            )
        }
    }

    if (imagePath == "") {
        CameraPreviewScreen(
            lensFacing = lensFacing,
            imageCapture = imageCapture,
            modifier = previewModifier,
            linearZoom = linearZoom
        )
    } else {
        CapturedImage(
            previewModifier,
            imagePath
        )
    }
}

@Composable
fun OptionElement(modifier: Modifier, upperText: String, lowerText: String, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
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
fun ValueSelectionDialog(
    modifier: Modifier,
    isIsoSelection: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmationRequest: (Int) -> Unit,
    valueIndex: Int,
) {
    var currentIndex by rememberSaveable { mutableIntStateOf(valueIndex) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(valueIndex) }
    val selectionItems = if (isIsoSelection) isoSensitivityOptions else ndSensitivityOptions
    androidx.compose.material3.AlertDialog(
        modifier = modifier.fillMaxHeight(0.66f),
        icon = {
            Icon(
                if (isIsoSelection) Icons.Default.Exposure else Icons.Default.Brightness6,
                contentDescription = if (isIsoSelection) "ISO Selection" else "ND Selection"
            )
        },
        title = {
            Text(text = if (isIsoSelection) "ISO" else "ND")
        },
        text = {

            Surface(
                shape = RoundedCornerShape(10),
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(if (isIsoSelection) "Film sensitivity" else "Neutral density filter factor")
                    Text("Current value: " + if (!isIsoSelection && currentIndex == 0) "None" else selectionItems[currentIndex])
                    HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1F)
                    ) {

                        selectionItems.forEachIndexed { index, value ->
                            // Calculates the evValues for ISO and ND values
                            val evValue =
                                if (isIsoSelection) log2(value.toDouble() / selectionItems[currentIndex])
                                else
                                    if (selectionItems[currentIndex] == 0 && value == 0) 0.0 else if (selectionItems[currentIndex] == 0) 0 - log2(
                                        value.toDouble()
                                    ) else
                                        if (value == 0) log2(selectionItems[currentIndex].toDouble())
                                        else log2(selectionItems[currentIndex].toDouble()) - log2(
                                            value.toDouble()
                                        )
                            val sign =
                                if (evValue >= 0) "+"
                                else ""

                            DropdownItem(
                                value = if (value == 0 || (value == 1 && !isIsoSelection)) "None" else value.toString(),
                                helperValue = if (evValue == 0.0) "" else String.format(
                                    "$sign%.1f EV", evValue
                                ),
                                onClick = { selectedIndex = index },
                                selected = selectedIndex == index
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
                    onConfirmationRequest(selectedIndex)
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
fun CapturedImage(
    modifier: Modifier = Modifier,
    fileName: String
) {
    // TODO Circle animation while waiting
    val context = LocalContext.current
    val imageFile = remember(fileName) {
        File(context.filesDir, fileName)
    }
    val imageRequest = remember(imageFile) {
        ImageRequest.Builder(context)
            .data(imageFile)
            .crossfade(true)
            .build()
    }
    val painter = rememberAsyncImagePainter(model = imageRequest)

    Surface(
        shape = RoundedCornerShape(15),
        modifier = modifier.padding(10.dp),
        color = MaterialTheme.colorScheme.outline
    ) {
        Image(painter, "")
    }
}
