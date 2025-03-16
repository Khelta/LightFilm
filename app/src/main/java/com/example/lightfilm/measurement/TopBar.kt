package com.example.lightfilm.measurement

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lightfilm.CameraPreviewScreen
import com.example.lightfilm.isoSensitivityOptions
import com.example.lightfilm.ndSensitivityOptions
import kotlin.math.log2

@Composable
fun TopBarContent(
    isPortrait: Boolean,
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

    CameraPreviewScreen(
        lensFacing = lensFacing,
        imageCapture = imageCapture,
        modifier = Modifier.aspectRatio(if (isPortrait) 0.75f else 1.33f)
    )
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