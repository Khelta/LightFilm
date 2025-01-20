package com.example.lightfilm

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lightfilm.ui.theme.LightFilmTheme

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
    LazyColumn {
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

@Preview(showBackground = true)
@Composable
fun PicturePreview() {
    LightFilmTheme {
        Picture(Modifier.fillMaxSize())
    }
}
