package com.example.lightfilm.organizing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.ui.theme.LightFilmTheme

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
    LazyColumn {
        itemsIndexed(listItems) { index, a ->
            Film(filmTitle = a, id = index + 1, onFilmClick = onFilmClick)
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
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
