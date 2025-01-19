package com.example.lightfilm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.ui.theme.LightFilmTheme
import kotlin.math.log2
import androidx.compose.runtime.remember


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LightFilmTheme {
                MyApp(modifier = Modifier)
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

    fun handleArrowBackClick(){
        if (activeScene == Scene.MEASUREMENTS) {
            showMeasurement = false
            activeScene = Scene.PICTURELIST
        } else if (activeScene == Scene.FILMLIST) {

        } else if (activeScene == Scene.PICTURELIST) {
            selectedFilm = -1
            activeScene = Scene.FILMLIST
        } else if (activeScene == Scene.PICTUREDETAILS) {
            selectedPicture = -1
            activeScene = Scene.PICTURELIST
        }
    }

    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            if (activeScene == Scene.MEASUREMENTS) {

            } else if (activeScene == Scene.FILMLIST) {
                Text("LightFilm")
            } else if (activeScene == Scene.PICTURELIST) {
                Text("Film selected - $selectedFilm")
            } else if (activeScene == Scene.PICTUREDETAILS) {
                Text("Picture selected - $selectedPicture")
            }

        }, navigationIcon = {
            if (selectedFilm != -1) {
                IconButton(onClick = ::handleArrowBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }

        })
    }, floatingActionButton = {
        if (selectedPicture == -1) {
            FloatingActionButton(onClick = {
                if (selectedFilm != -1) {
                    showMeasurement = true
                    activeScene = Scene.MEASUREMENTS
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            if (showMeasurement) {
                Measurement()
            } else {
                if (selectedFilm == -1) {
                    FilmList(onFilmClick = ::handleFilmClick)
                } else if (selectedPicture == -1) {
                    PictureList(onPictureClick = ::handlePictureClick)
                } else {
                    PictureDetails()
                }

            }
        }
    }

}

@Composable
fun Film(
    modifier: Modifier = Modifier,
    filmTitle: String = "Example",
    id: Int = -1,
    onFilmClick: (Int) -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable { onFilmClick(id) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Titel
            Text(
                text = "$id - $filmTitle",
            )
        }
    }
}

@Composable
fun FilmList(listItems: List<String> = listOf("a", "b", "c"), onFilmClick: (Int) -> Unit = {}) {
    LazyColumn() {
        itemsIndexed(listItems) { index, a ->
            Film(filmTitle = a, id = index + 1, onFilmClick = onFilmClick)
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun Picture(
    modifier: Modifier = Modifier, pictureId: Int = -1, onPictureClick: (Int) -> Unit = {}
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.primary)
        .clickable { onPictureClick(pictureId) }) {
        // Titel
        Text(
            text = "filmTitle",
        )
        // Untertitel
        Text(
            text = "filmTitle",
        )
    }
}

@Composable
fun PictureList(
    listItems: List<String> = listOf("a", "b", "c"), onPictureClick: (Int) -> Unit = {}
) {
    LazyColumn() {
        itemsIndexed(listItems) { index, a ->
            Picture(pictureId = index + 1, onPictureClick = onPictureClick)
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun PictureDetails() {
    Text("#TODO")
}

@Composable
fun Measurement(modifier: Modifier = Modifier) {
    var showIsoOverlay by remember { mutableStateOf(false) }
    var selectedIsoIndex by rememberSaveable { mutableIntStateOf(18) }
    var showNDOverlay by remember { mutableStateOf(false) }
    var selectedNDIndex by rememberSaveable { mutableIntStateOf(0) }

    var ev = 0

    fun handleIsoValueSelected(value: Int) {
        selectedIsoIndex = value
        showIsoOverlay = false
    }

    fun handleNDValueSelected(value: Int) {
        selectedNDIndex = value
        showNDOverlay = false
    }

    Box() {
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
        Column() {
            Row() {
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
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FilmPreview() {
    LightFilmTheme {
        Film(Modifier.fillMaxSize(), "Hallo")
    }
}

@Preview(showBackground = true)
@Composable
fun PicturePreview() {
    LightFilmTheme {
        Picture(Modifier.fillMaxSize())
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
                        selected = if (selected == index) true else false
                    )
                }
            }
            HorizontalDivider()
            Row() {
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

@Preview(showBackground = true)
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


@Preview(showBackground = true, heightDp = 500, widthDp = 300)
@Composable
fun MyAppPreview() {
    LightFilmTheme {
        MyApp()
    }
}
