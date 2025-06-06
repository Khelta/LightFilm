package com.example.lightfilm.measurement

import android.content.Context
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
import androidx.compose.ui.unit.dp
import com.example.lightfilm.Helper.apertureStringToValue
import com.example.lightfilm.Helper.applyISOandND
import com.example.lightfilm.Helper.calculateEV
import com.example.lightfilm.Helper.deleteFile
import com.example.lightfilm.Helper.shutterSpeedStringToValue
import com.example.lightfilm.database.PictureModel
import com.example.lightfilm.database.UserFilmModel
import com.example.lightfilm.database.viewmodel.PictureViewmodel
import com.example.lightfilm.isoSensitivityOptions
import com.example.lightfilm.ndSensitivityOptions

// TODO Disable UI when preview image waits being saved or rejected

@Composable
fun Measurement(
    modifier: Modifier = Modifier,
    viewmodel: PictureViewmodel,
    currentUserFilm: UserFilmModel,
    filmIsoIndex: Int
) {
    var showIsoOverlay by remember { mutableStateOf(false) }
    var showNDOverlay by remember { mutableStateOf(false) }
    var apertureShutterDialogOpen = rememberSaveable { mutableStateOf(false) }

    var selectedIsoIndex by rememberSaveable { mutableIntStateOf(filmIsoIndex) }
    var selectedNDIndex by rememberSaveable { mutableIntStateOf(0) }

    var imagePath by rememberSaveable { mutableStateOf("") }

    var lensFacing by rememberSaveable { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val configuration = LocalConfiguration.current

    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    var exposureValue by rememberSaveable { mutableDoubleStateOf(-999.0) }
    var aperture by rememberSaveable { mutableDoubleStateOf(-1.0) }
    var shutterSpeed by rememberSaveable { mutableDoubleStateOf(-1.0) }
    var internalIso by rememberSaveable { mutableIntStateOf(-1) }

    var selectedApertureValue by rememberSaveable { mutableStateOf<Double?>(null) }
    var selectedShutterSpeedValue by rememberSaveable { mutableStateOf<Double?>(null) }

    val exposureSliderValue by rememberSaveable { mutableFloatStateOf(0.5f) }
    var zoomSliderValue by rememberSaveable { mutableFloatStateOf(0.5f) }

    fun handleNDValueSelected(value: Int) {
        selectedNDIndex = value
        showNDOverlay = false
        exposureValue = calculateEV(
            aperture,
            shutterSpeed,
            isoSensitivityOptions[selectedIsoIndex],
            ndSensitivityOptions[value]
        )
    }

    fun handleEV(
        evValue: Double,
        apertureValue: Double,
        shutterSpeedValue: Double,
        internalIsoValue: Int,
        imagePathValue: String
    ) {
        exposureValue = applyISOandND(
            evValue, isoSensitivityOptions[selectedIsoIndex], ndSensitivityOptions[selectedNDIndex]
        )
        aperture = apertureValue
        shutterSpeed = shutterSpeedValue
        internalIso = internalIsoValue
        imagePath = imagePathValue
        println("EV: $evValue, Aperture: f$apertureValue, Shutterspeed: $shutterSpeedValue")
    }

    fun handleIsoValueSelected(value: Int) {
        selectedIsoIndex = value
        showIsoOverlay = false
        exposureValue = calculateEV(
            aperture,
            shutterSpeed,
            isoSensitivityOptions[value],
            ndSensitivityOptions[selectedNDIndex]
        )
        println(exposureValue)
        // TODO Update fstoptable
    }

    fun handleImageSaving(title: String) {
        val picture = PictureModel(
            pathToFile = imagePath,
            internalAperture = aperture,
            internalShutterSpeed = shutterSpeed,
            internalIso = internalIso,
            selectedAperture = selectedApertureValue,
            selectedShutterSpeed = selectedShutterSpeedValue,
            selectedIso = isoSensitivityOptions[selectedIsoIndex],
            captureDate = System.currentTimeMillis(),
            userFilmId = currentUserFilm.uid,
            title = if (title == "") null else title
        )
        viewmodel.insert(picture)

        imagePath = ""
    }

    fun handleApertureShutterSelectionOpening() {
        apertureShutterDialogOpen.value = true
    }

    fun handleImageReject(context: Context) {
        deleteFile(imagePath, context)
        imagePath = ""
    }

    fun switchLens() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    fun onZoomSliderChange(value: Float) {
        zoomSliderValue = value
        println("HI! $value")
    }

    Box {
        if (showIsoOverlay) {
            ValueSelectionDialog(
                modifier = Modifier,
                onDismissRequest = { showIsoOverlay = false },
                onConfirmationRequest = ::handleIsoValueSelected,
                isIsoSelection = true,
                valueIndex = selectedIsoIndex
            )
        }
        if (showNDOverlay) {
            ValueSelectionDialog(
                modifier = Modifier,
                onDismissRequest = { showNDOverlay = false },
                onConfirmationRequest = ::handleNDValueSelected,
                isIsoSelection = false,
                valueIndex = selectedNDIndex
            )
        }

        if (apertureShutterDialogOpen.value) {
            ApertureShutterSelectionDialog(
                apertureShutterDialogOpen,
                { apertureShutterDialogOpen.value = false },
                { titleValue, apertureValue, shutterSpeedValue ->
                    selectedApertureValue = apertureStringToValue(apertureValue)
                    selectedShutterSpeedValue = shutterSpeedStringToValue(shutterSpeedValue)
                    apertureShutterDialogOpen.value = false
                    handleImageSaving(titleValue)
                })
        }

        val colorSchemeTopBar = MaterialTheme.colorScheme.outline
        if (isPortrait) {
            Column(modifier = modifier) {
                Surface(
                    modifier = Modifier.padding(bottom = 8.dp), color = colorSchemeTopBar
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
                            lensFacing,
                            zoomSliderValue,
                            imagePath
                        )
                    }
                }

                // TODO implement onZoomSliderChange
                MeasurementContent(
                    Modifier.weight(1F),
                    true,
                    exposureValue,
                    exposureSliderValue,
                    zoomSliderValue,
                    ::onZoomSliderChange
                )

                CameraCaptureButtonBar(
                    true,
                    imageCapture,
                    exposureValue,
                    ::handleEV,
                    ::switchLens,
                    ::handleApertureShutterSelectionOpening,
                    ::handleImageReject,
                    imagePath
                )
            }
        } else {
            Row(modifier = modifier) {
                Surface(
                    modifier = Modifier.padding(bottom = 8.dp), color = colorSchemeTopBar
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
                            lensFacing,
                            zoomSliderValue,
                            imagePath
                        )
                    }
                }

                MeasurementContent(
                    Modifier.weight(1F),
                    false,
                    exposureValue,
                    exposureSliderValue,
                    zoomSliderValue,
                    ::onZoomSliderChange
                )

                CameraCaptureButtonBar(
                    false,
                    imageCapture,
                    exposureValue,
                    ::handleEV,
                    ::switchLens,
                    ::handleApertureShutterSelectionOpening,
                    ::handleImageReject,
                    imagePath
                )
            }
        }
    }
}
