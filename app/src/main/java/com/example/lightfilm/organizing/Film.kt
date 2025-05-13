package com.example.lightfilm.organizing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lightfilm.database.FilmModel
import com.example.lightfilm.database.UserFilmModel
import com.example.lightfilm.database.viewmodel.FilmViewmodel
import com.example.lightfilm.database.viewmodel.UserFilmViewmodel

@Composable
fun UserFilm(
    modifier: Modifier = Modifier,
    film: FilmModel?,
    userFilm: UserFilmModel,
    onFilmClick: (UserFilmModel) -> Unit = {}
) {

    userFilm.filmId?.let {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = modifier.clickable { onFilmClick(userFilm) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Titel
                Text(
                    text = "${userFilm.uid} - ${film?.name}",
                )
            }
        }
    }
}

@Composable
fun UserFilmList(
    userFilmViewmodel: UserFilmViewmodel,
    filmViewmodel: FilmViewmodel,
    onFilmClick: (UserFilmModel) -> Unit = {}
) {
    val allUserFilms = userFilmViewmodel.allUserFilms.observeAsState(emptyList())
    // To populate
    filmViewmodel.allFilms.observeAsState(emptyList())

    LazyColumn {
        itemsIndexed(allUserFilms.value) { index, userFilm ->
            UserFilm(
                film = userFilm.filmId?.let { filmViewmodel.getFilmById(userFilm.filmId) },
                userFilm = userFilm,
                onFilmClick = onFilmClick
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        }
    }
}
