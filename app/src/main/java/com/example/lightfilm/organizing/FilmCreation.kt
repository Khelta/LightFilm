package com.example.lightfilm.organizing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lightfilm.database.viewmodel.FilmViewmodel

@Composable
fun FilmCreation(
    modifier: Modifier = Modifier,
    viewmodel: FilmViewmodel,
    onFilmSelected: (Int) -> Unit
) {
    val films = viewmodel.allFilms.observeAsState(emptyList())

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(films.value) { film ->
            FilmItem(filmId = film.uid, filmName = film.name, onClick = onFilmSelected)
        }
    }
}

@Composable
fun FilmItem(modifier: Modifier = Modifier, filmId: Int, filmName: String, onClick: (Int) -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(4.dp),
        shape = MaterialTheme.shapes.medium,
        onClick = { onClick(filmId) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = filmName, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
