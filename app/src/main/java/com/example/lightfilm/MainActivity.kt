package com.example.lightfilm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.core.content.ContextCompat
import com.example.lightfilm.database.UserFilmModel
import com.example.lightfilm.database.viewmodel.FilmViewmodel
import com.example.lightfilm.database.viewmodel.PictureViewmodel
import com.example.lightfilm.database.viewmodel.UserFilmViewmodel
import com.example.lightfilm.measurement.Measurement
import com.example.lightfilm.organizing.FilmCreation
import com.example.lightfilm.organizing.PictureDetails
import com.example.lightfilm.organizing.PictureList
import com.example.lightfilm.organizing.UserFilmList
import com.example.lightfilm.ui.theme.LightFilmTheme

class MainActivity : ComponentActivity() {
    private val pictureViewmodel: PictureViewmodel by viewModels()
    private val filmViewmodel: FilmViewmodel by viewModels()
    private val userFilmViewmodel: UserFilmViewmodel by viewModels()

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

        //pictureViewmodel.insert(PictureModel(uid = null, datetime = 0, aperture = 0.0, shutterspeed = 0.0, iso = 0))

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
                    MyApp(
                        Modifier.padding(innerPadding),
                        pictureViewmodel,
                        filmViewmodel,
                        userFilmViewmodel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    pictureViewmodel: PictureViewmodel,
    filmViewmodel: FilmViewmodel,
    userFilmViewmodel: UserFilmViewmodel
) {


    var showMeasurement by rememberSaveable { mutableStateOf(value = false) }
    var selectedFilm by rememberSaveable { mutableIntStateOf(value = -1) }
    var selectedPicture: Int by rememberSaveable { mutableIntStateOf(value = -1) }
    var activeScene by rememberSaveable { mutableStateOf(value = Scene.FILMLIST) }

    fun handleUserFilmClick(userFilmId: Int) {
        selectedFilm = userFilmId
        activeScene = Scene.PICTURELIST
    }

    fun handleUserFilmCreation(filmId: Int) {
        val userFilmInstance = UserFilmModel(film_id = filmId)
        userFilmViewmodel.insert(userFilmInstance)

        activeScene = Scene.FILMLIST
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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
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
        },
        floatingActionButton = {
            if (activeScene in listOf<Scene>(
                    Scene.FILMLIST,
                    Scene.PICTURELIST,
                )
            ) {
                FloatingActionButton(onClick = {
                    when (activeScene) {

                        Scene.FILMLIST ->
                            activeScene = Scene.FILMCREATION

                        Scene.PICTURELIST -> {
                            showMeasurement = true
                            activeScene = Scene.MEASUREMENTS
                        }

                        else -> {}
                    }

                }) {
                    when (activeScene) {
                        else -> Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (activeScene) {
                Scene.MEASUREMENTS -> Measurement()

                Scene.FILMLIST -> UserFilmList(
                    userFilmViewmodel,
                    filmViewmodel,
                    onFilmClick = ::handleUserFilmClick
                )

                Scene.PICTURELIST -> PictureList(
                    pictureViewmodel,
                    onPictureClick = ::handlePictureClick
                )

                Scene.PICTUREDETAILS -> PictureDetails(pictureViewmodel, selectedPicture)

                Scene.FILMCREATION -> FilmCreation(
                    viewmodel = filmViewmodel,
                    onFilmSelected = ::handleUserFilmCreation
                )
            }
        }
    }
}
