package com.example.lightfilm

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.lightfilm.ui.theme.LightFilmTheme
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import com.example.lightfilm.measurement.Measurement

class MainActivity : ComponentActivity() {

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setCameraPreview()
            } else {
                // Camera permission denied
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                setCameraPreview()
            }
            else -> {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }

    }
    private fun setCameraPreview() {
        setContent {
            LightFilmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Measurement(Modifier
                            .padding(innerPadding))

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(modifier: Modifier = Modifier) {
    var showMeasurement by rememberSaveable { mutableStateOf(value = false) }
    var selectedFilm by rememberSaveable { mutableIntStateOf(value = -1) }
    var selectedPicture by rememberSaveable { mutableIntStateOf(value = -1) }
    var activeScene by rememberSaveable { mutableStateOf(value = Scene.FILMLIST) }

    fun handleFilmClick(filmId: Int) {
        selectedFilm = filmId
        activeScene = Scene.PICTURELIST
    }

    fun handlePictureClick(pictureId: Int) {
        selectedPicture = pictureId
        activeScene = Scene.PICTUREDETAILS
    }

    fun handleArrowBackClick() {
        when (activeScene) {
            Scene.MEASUREMENTS -> {
                showMeasurement = false
                activeScene = Scene.PICTURELIST
            }

            Scene.FILMLIST -> {

            }

            Scene.FILMCREATION -> {
                activeScene = Scene.FILMLIST
            }

            Scene.PICTURELIST -> {
                selectedFilm = -1
                activeScene = Scene.FILMLIST
            }

            Scene.PICTUREDETAILS -> {
                selectedPicture = -1
                activeScene = Scene.PICTURELIST
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            when (activeScene) {
                Scene.FILMLIST -> {
                    Text("LightFilm")
                }

                Scene.PICTURELIST -> {
                    Text("Film selected - $selectedFilm")
                }

                Scene.PICTUREDETAILS -> {
                    Text("Picture selected - $selectedPicture")
                }

                else -> {

                }
            }

        }, navigationIcon = {
            if (activeScene != Scene.FILMLIST) {
                IconButton(onClick = ::handleArrowBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }

        })
    }, floatingActionButton = {
        if (activeScene == Scene.FILMLIST || activeScene == Scene.PICTURELIST) {
            FloatingActionButton(onClick = {
                if (activeScene == Scene.PICTURELIST) {
                    showMeasurement = true
                    activeScene = Scene.MEASUREMENTS
                } else if (activeScene == Scene.FILMLIST) {
                    activeScene = Scene.FILMCREATION
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (activeScene) {
                Scene.MEASUREMENTS -> Measurement()

                Scene.FILMLIST -> FilmList(onFilmClick = ::handleFilmClick)

                Scene.PICTURELIST -> PictureList(onPictureClick = ::handlePictureClick)

                Scene.PICTUREDETAILS -> PictureDetails()

                Scene.FILMCREATION -> FilmCreation()
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 500, widthDp = 300)
@Composable
fun MyAppPreview() {
    LightFilmTheme {
        MyApp()
    }
}
