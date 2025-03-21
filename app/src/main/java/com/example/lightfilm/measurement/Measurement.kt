package com.example.lightfilm.measurement

import android.content.res.Configuration
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.ui.theme.LightFilmTheme


@Composable
fun Measurement(modifier: Modifier = Modifier) {
    var showIsoOverlay by remember { mutableStateOf(false) }
    var selectedIsoIndex by rememberSaveable { mutableIntStateOf(15) }
    var showNDOverlay by remember { mutableStateOf(false) }
    var selectedNDIndex by rememberSaveable { mutableIntStateOf(0) }

    var lensFacing by rememberSaveable { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    var exposureValue by rememberSaveable { mutableDoubleStateOf(-999.0) }

    val exposureSliderValue by rememberSaveable { mutableFloatStateOf(0.5f) }

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

        val colorSchemeTopBar = MaterialTheme.colorScheme.outline
        if (isPortrait) {
            Column(modifier = modifier) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    color = colorSchemeTopBar
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TopBarContent(
                            isPortrait,
                            selectedIsoIndex,
                            { showIsoOverlay = true },
                            selectedNDIndex,
                            { showNDOverlay = true },
                            imageCapture,
                            lensFacing
                        )
                    }
                }


                MeasurementContent(Modifier.weight(1F), true, exposureValue, exposureSliderValue)

                CameraCaptureButtonBar(
                    true,
                    imageCapture,
                    context,
                    exposureValue,
                    ::handleEV,
                    ::switchLens
                )
            }
        } else {
            Row(modifier = modifier) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    color = colorSchemeTopBar
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TopBarContent(
                            isPortrait,
                            selectedIsoIndex,
                            { showIsoOverlay = true },
                            selectedNDIndex,
                            { showNDOverlay = true },
                            imageCapture,
                            lensFacing
                        )
                    }
                }

                MeasurementContent(Modifier.weight(1F), false, exposureValue, exposureSliderValue)

                CameraCaptureButtonBar(
                    false,
                    imageCapture,
                    context,
                    exposureValue,
                    ::handleEV,
                    ::switchLens
                )
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
