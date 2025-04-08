package com.example.lightfilm.organizing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.database.viewmodel.PictureViewmodel
import com.example.lightfilm.ui.theme.LightFilmTheme
import androidx.compose.runtime.livedata.observeAsState

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
    viewmodel: PictureViewmodel, onPictureClick: (Int) -> Unit = {}
) {
    val pictures = viewmodel.allPictures.observeAsState(emptyList())

    LazyColumn {
        itemsIndexed(pictures.value) { index, picture ->
            Picture(pictureId = index, onPictureClick = onPictureClick)
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
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
