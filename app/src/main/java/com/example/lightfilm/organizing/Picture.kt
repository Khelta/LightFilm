package com.example.lightfilm.organizing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Iso
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.database.viewmodel.FilmViewmodel
import com.example.lightfilm.database.viewmodel.PictureViewmodel
import com.example.lightfilm.database.viewmodel.UserFilmViewmodel
import com.example.lightfilm.ui.theme.LightFilmTheme

@Composable
fun Picture(
    modifier: Modifier = Modifier, pictureId: Int = -1, onPictureClick: (Int) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onPictureClick(pictureId) }) {
        // Titel
        Text(
            text = "$pictureId - filmTitle",
        )
        // Untertitel
        Text(
            text = "filmTitle",
        )
    }
}

@Composable
fun PictureList(
    pictureViewmodel: PictureViewmodel,
    filmViewmodel: FilmViewmodel,
    userFilmViewmodel: UserFilmViewmodel,
    userFilmId: Int,
    onPictureClick: (Int) -> Unit = {}
) {
    val pictures = pictureViewmodel.allPictures.observeAsState(emptyList())
    val userFilms = userFilmViewmodel.allUserFilms.observeAsState(emptyList())

    val currentUserFilm = userFilms.value.find { it.uid == userFilmId }
    val currentFilm = filmViewmodel.allFilms.value?.find { it.uid == currentUserFilm?.film_id }

    val filmType = currentFilm?.type?.readable ?: ""
    val contrast = currentFilm?.contrast?.readable ?: ""
    val grain = currentFilm?.grain?.readable ?: ""
    val iso = currentFilm?.iso?.toString() ?: ""
    val brand = currentFilm?.brand?.readable ?: ""
    val name = currentFilm?.name ?: ""

    @Composable
    fun TestXXX(
        modifier: Modifier,
        imageVector: ImageVector,
        text: String,
        preText: String = "",
        postText: String = "",
        iconDescription: String = ""
    ) {
        Row(modifier = modifier) {
            Icon(imageVector, iconDescription)
            Text("$preText$text$postText")
        }
    }

    // TODO custom title

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("$brand - $name")
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TestXXX(Modifier.weight(1f), Icons.Default.Science, filmType, postText = " Film")
                TestXXX(Modifier.weight(1f), Icons.Default.Iso, iso, preText = "ISO: ")

            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TestXXX(
                    Modifier.weight(1f),
                    Icons.Default.Contrast,
                    contrast,
                    preText = "Contrast: "
                )
                TestXXX(Modifier.weight(1f), Icons.Default.Grain, grain, preText = "Grain: ")

            }
        }

        HorizontalDivider(Modifier, 2.dp, MaterialTheme.colorScheme.outline)

        LazyColumn {
            itemsIndexed(pictures.value) { index, picture ->
                Picture(pictureId = index, onPictureClick = onPictureClick)
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun PictureDetails(viewmodel: PictureViewmodel, pictureIndex: Int) {
    val pictures = viewmodel.allPictures.observeAsState(emptyList())

    val iso = pictures.value[pictureIndex].iso
    val uid = pictures.value[pictureIndex].uid
    Row {
        Column {
            Text("ISO: $iso")
            Text("uid: $uid")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PicturePreview() {
    LightFilmTheme {
        Picture(Modifier.fillMaxSize())
    }
}
