package com.example.lightfilm

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.lightfilm.Helper.DeletionDialog
import com.example.lightfilm.Helper.apertureStringToValue
import com.example.lightfilm.Helper.deleteFile
import com.example.lightfilm.Helper.shutterSpeedStringToValue
import com.example.lightfilm.database.UserFilmModel
import com.example.lightfilm.database.viewmodel.FilmViewmodel
import com.example.lightfilm.database.viewmodel.PictureViewmodel
import com.example.lightfilm.database.viewmodel.UserFilmViewmodel
import com.example.lightfilm.measurement.ApertureShutterSelectionDialog
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

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
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

    var pictureDeletionDialogIsOpen = remember { mutableStateOf(false) }
    var filmDeletionDialogIsOpen = remember { mutableStateOf(false) }
    var pictureEditDialogIsOpen = remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun handleUserFilmClick(userFilmId: Int) {
        selectedFilm = userFilmId
        activeScene = Scene.PICTURELIST
    }

    fun handleUserFilmCreation(filmId: Int) {
        val userFilmInstance = UserFilmModel(filmId = filmId)
        userFilmViewmodel.insert(userFilmInstance)

        activeScene = Scene.FILMLIST
    }

    fun handleUserFilmDeletion(userFilmId: Int) {
        val film = userFilmViewmodel.allUserFilms.value?.find { it.uid == userFilmId }

        val pictures =
            pictureViewmodel.allPictures.value?.filter { it.userFilmId == userFilmId } ?: listOf(
                null
            )
        for (picture in pictures) {
            pictureViewmodel.delete(picture)
            picture?.pathToFile?.let {
                deleteFile(it, context)
            }
        }

        userFilmViewmodel.delete(film)

        filmDeletionDialogIsOpen.value = false

        selectedFilm = -1
        activeScene = Scene.FILMLIST
    }

    fun handlePictureDeletion(pictureId: Int, context: Context) {
        var picture = pictureViewmodel.allPictures.value?.find { it.uid == pictureId }
        pictureViewmodel.delete(picture)
        picture?.pathToFile?.let {
            deleteFile(it, context)
        }
        pictureDeletionDialogIsOpen.value = false

        selectedPicture = -1
        activeScene = Scene.PICTURELIST
    }

    fun handlePictureEdit(title: String, aperture: String, shutterSpeed: String) {
        val picture = pictureViewmodel.allPictures.value?.find { it.uid == selectedPicture }

        picture?.let {
            pictureViewmodel.update(
                picture.copy(
                    title = if (title != "") title else null,
                    selectedAperture = apertureStringToValue(aperture),
                    selectedShutterSpeed = shutterSpeedStringToValue(shutterSpeed)
                )
            )
        }

        pictureEditDialogIsOpen.value = false
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

    val activity = LocalActivity.current
    BackHandler {
        if (activeScene != Scene.FILMLIST) handleArrowBackClick()
        else activity?.finish()
    }

    Scaffold(topBar = {
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
                        val filmId =
                            userFilmViewmodel.allUserFilms.value?.find { it.uid == selectedFilm }?.filmId
                        val filmName = filmViewmodel.allFilms.value?.find { it.uid == filmId }?.name
                        Text("$selectedFilm - $filmName")
                    }

                    Scene.PICTUREDETAILS -> {
                        var title =
                            pictureViewmodel.allPictures.value?.find { it.uid == selectedPicture }?.title
                        Text(title?.let { "$selectedPicture - $title" } ?: "$selectedPicture")
                    }

                    else -> {

                    }
                }

            }, navigationIcon = {
                if (activeScene != Scene.FILMLIST) {
                    IconButton(onClick = ::handleArrowBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Arrow pointing backwards used for navigation"
                        )
                    }
                }
            }, actions = {
                when (activeScene) {
                    Scene.PICTURELIST -> {
                        IconButton(onClick = { filmDeletionDialogIsOpen.value = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete trash can icon"
                            )
                        }
                    }

                    Scene.PICTUREDETAILS -> {
                        IconButton(onClick = { pictureEditDialogIsOpen.value = true }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit icon for editing"
                            )
                        }
                        IconButton(onClick = { pictureDeletionDialogIsOpen.value = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete trash can icon"
                            )
                        }
                    }

                    Scene.MEASUREMENTS -> {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Gear icon for settings menu"
                            )
                        }
                    }

                    else -> {}
                }
            })
    }, floatingActionButton = {
        if (activeScene in listOf<Scene>(
                Scene.FILMLIST,
                Scene.PICTURELIST,
            )
        ) {
            FloatingActionButton(onClick = {
                when (activeScene) {

                    Scene.FILMLIST -> activeScene = Scene.FILMCREATION

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

                Scene.MEASUREMENTS -> {
                    val filmId =
                        userFilmViewmodel.allUserFilms.value?.find { it.uid == selectedFilm }?.filmId
                    val film = filmViewmodel.allFilms.value?.find { it.uid == filmId }
                    val isoIndex = isoSensitivityOptions.indexOfFirst { it == (film?.iso ?: 15) }
                    Measurement(
                        viewmodel = pictureViewmodel,
                        currentUserFilmId = selectedFilm,
                        filmIsoIndex = isoIndex
                    )
                }

                Scene.FILMLIST -> UserFilmList(
                    userFilmViewmodel, filmViewmodel, onFilmClick = ::handleUserFilmClick
                )

                Scene.PICTURELIST -> {
                    DeletionDialog(
                        filmDeletionDialogIsOpen,
                        "Delete film and all photos? This action cannot be undone.",
                        { handleUserFilmDeletion(selectedFilm) })
                    PictureList(
                        pictureViewmodel,
                        filmViewmodel,
                        userFilmViewmodel,
                        selectedFilm,
                        onPictureClick = ::handlePictureClick
                    )
                }

                Scene.PICTUREDETAILS -> {
                    val picture =
                        pictureViewmodel.allPictures.value?.find { it.uid == selectedPicture }
                    ApertureShutterSelectionDialog(
                        pictureEditDialogIsOpen,
                        { pictureEditDialogIsOpen.value = false },
                        ::handlePictureEdit,
                        picture?.selectedAperture.toString(),
                        picture?.selectedShutterSpeed.toString(),
                        picture?.title

                    )
                    DeletionDialog(
                        pictureDeletionDialogIsOpen,
                        "Delete photo? This action cannot be undone.",
                        { handlePictureDeletion(selectedPicture, context) })
                    PictureDetails(pictureViewmodel, selectedPicture)
                }

                Scene.FILMCREATION -> FilmCreation(
                    viewmodel = filmViewmodel, onFilmSelected = ::handleUserFilmCreation
                )
            }
        }
    }
}
