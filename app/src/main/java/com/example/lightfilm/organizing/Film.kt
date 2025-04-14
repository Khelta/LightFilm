package com.example.lightfilm.organizing

import androidx.compose.foundation.background
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
import com.example.lightfilm.database.viewmodel.FilmViewmodel
import com.example.lightfilm.database.viewmodel.UserFilmViewmodel

@Composable
fun UserFilm(
    modifier: Modifier = Modifier,
    filmViewmodel: FilmViewmodel,
    userFilmViewmodel: UserFilmViewmodel,
    userFilmId: Int = -1,
    onFilmClick: (Int) -> Unit = {}
) {
    val userFilms = userFilmViewmodel.allUserFilms.observeAsState(emptyList())
    val films = filmViewmodel.allFilms.observeAsState(emptyList())

    val userFilm = userFilms.value.find { it.uid == userFilmId }

    val film = films.value.find { it.uid == userFilm?.film_id }
    val filmTitle = userFilm?.title ?: film?.name ?: ""

    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.clickable { onFilmClick(userFilmId) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Titel
            Text(
                text = "$userFilmId - $filmTitle",
            )
        }
    }
}

@Composable
fun UserFilmList(
    userFilmViewmodel: UserFilmViewmodel,
    filmViewmodel: FilmViewmodel,
    onFilmClick: (Int) -> Unit = {}
) {
    val allUserFilms = userFilmViewmodel.allUserFilms.observeAsState(emptyList())

    LazyColumn {
        itemsIndexed(allUserFilms.value) { index, userFilm ->
            UserFilm(
                filmViewmodel = filmViewmodel,
                userFilmViewmodel = userFilmViewmodel,
                userFilmId = userFilm.uid,
                onFilmClick = onFilmClick
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        }
    }
}
