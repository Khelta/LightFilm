package com.example.lightfilm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    var showMeasurement by rememberSaveable { mutableStateOf(value = false) }
    var selectedFilm by rememberSaveable { mutableIntStateOf(value = -1) }
    var selectedPicture by rememberSaveable { mutableIntStateOf(value = -1) }

    Surface(modifier) {
        if (showMeasurement) {
            Measurement()
        } else {
            if (selectedFilm == -1) {
                FilmList()
            }

        }
    }
}

@Composable
fun Film(modifier: Modifier = Modifier, filmTitle: String = "Example", id: Int = -1) {
    Surface(
        color = MaterialTheme.colorScheme.primary, modifier = Modifier
    ) {
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
fun FilmList(listItems: List<String> = listOf("a", "b", "c")) {
    LazyColumn() {
        itemsIndexed(listItems) { index, a ->
            Film(filmTitle = a, id = index + 1)
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun Picture(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
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
fun PictureList(listItems: List<String> = listOf("a", "b", "c")) {
    LazyColumn() {
        items(listItems) { _ -> Picture() }
    }
}

@Composable
fun Measurement(modifier: Modifier = Modifier) {
    Column() {
        Row() {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(5.dp),
                shape = RoundedCornerShape(15)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text("Iso")
                    Text("200")
                }
            }
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.padding(5.dp),
                shape = RoundedCornerShape(15)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text("ND")
                    Text("None")
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
    isIsoSelection: Boolean,
    modifier: Modifier = Modifier
) {
    var selected by rememberSaveable { mutableIntStateOf(if (isIsoSelection) 18 else 0) }
    val selectionItems = if (isIsoSelection) isoSensitivityOptions else ndSensitivityOptions
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        if (isIsoSelection) log2(value.toDouble() / selectionItems[selected]) else
                            if (selectionItems[selected] == 0 && value == 0) 0.0 else
                                if (selectionItems[selected] == 0) 0  - log2(value.toDouble()) else
                                    if (value == 0) log2(selectionItems[selected].toDouble()) else
                                        log2(selectionItems[selected].toDouble()) - log2(value.toDouble())
                    val sign =
                        if (isIsoSelection) if (evValue >= 0) "+" else "" else if (evValue >= 0) "+" else ""

                    DropdownItem(
                        value = if (value == 0) "None" else value.toString(),
                        helperValue = if (evValue == 0.0) "" else String.format(
                            "$sign%.1f EV",
                            evValue
                        ),
                        onClick = { selected = index },
                        selected = if (selected == index) true else false
                    )
                }
            }
            HorizontalDivider()
            Row() {
                Surface(modifier = Modifier.weight(1F)) { }
                TextButton(onClick = {}) {
                    Text("Cancel")
                }
                TextButton(onClick = {}) {
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


@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    LightFilmTheme {
        MyApp()
    }
}
