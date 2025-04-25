package com.example.lightfilm.organizing

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Iso
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lightfilm.Helper.apertureValueToString
import com.example.lightfilm.Helper.createPlaceholderBitmap
import com.example.lightfilm.database.viewmodel.FilmViewmodel
import com.example.lightfilm.database.viewmodel.PictureViewmodel
import com.example.lightfilm.database.viewmodel.UserFilmViewmodel
import com.example.lightfilm.Helper.imageFileToBitmap
import com.example.lightfilm.noValueString
import com.example.lightfilm.Helper.shutterSpeedValueToString
import java.text.SimpleDateFormat

@Composable
fun Picture(
    modifier: Modifier = Modifier,
    viewmodel: PictureViewmodel,
    pictureId: Int = -1,
    onPictureClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val picture = viewmodel.allPictures.value?.find { it.uid == pictureId }
    val bitmap =
        remember(picture?.pathToFile) {
            picture?.pathToFile?.let { imageFileToBitmap(context, it) }
                ?: createPlaceholderBitmap()
        }

    val density = LocalDensity.current
    var columnHeightPx by remember { mutableIntStateOf(0) }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.clickable { onPictureClick(pictureId) }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = modifier
                    .onGloballyPositioned { coordinates ->
                        columnHeightPx = coordinates.size.height
                    }
                    .padding(16.dp)
            ) {
                val title = picture?.title?.let { " - $it" } ?: ""
                val date = SimpleDateFormat("dd/MM/yyyy hh:mm").format(picture?.captureDate)
                Text(text = "$pictureId" + title)
                Text(text = "$date")
            }

            Column(Modifier.weight(1F)) { }

            val imageHeightDp = with(density) { columnHeightPx.toDp() }
            Image(
                bitmap = bitmap,
                contentDescription = "User-generated preview image",
                modifier = Modifier
                    .height(imageHeightDp)
                    .width(150.dp)
                    .align(Alignment.CenterVertically)
                    .graphicsLayer { alpha = 0.99f }
                    .drawWithContent {
                        drawContent()
                        val fadeBrush = Brush.horizontalGradient(
                            colors = listOf(Color.White, Color.Transparent),
                            startX = 0f,
                            endX = size.width - 0.2f * size.width
                        )
                        drawRect(brush = fadeBrush, blendMode = BlendMode.DstOut)
                    },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun IconTextBlock(
    modifier: Modifier,
    imageVector: ImageVector,
    label: String = "",
    value: String = "",
    iconDescription: String = ""
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector, iconDescription, Modifier.padding(end = 8.dp))
        Text(
            label, Modifier
                .weight(1F)
                .padding(end = 8.dp)
        )
        Text(value, textAlign = TextAlign.End)
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
    val allPictures = pictureViewmodel.allPictures.observeAsState(emptyList())
    val userFilms = userFilmViewmodel.allUserFilms.observeAsState(emptyList())

    val currentUserFilm = userFilms.value.find { it.uid == userFilmId }
    val currentFilm = filmViewmodel.allFilms.value?.find { it.uid == currentUserFilm?.filmId }

    val filmType = currentFilm?.type?.readable ?: ""
    val contrast = currentFilm?.contrast?.readable ?: ""
    val grain = currentFilm?.grain?.readable ?: ""
    val iso = currentFilm?.iso?.toString() ?: ""
    val brand = currentFilm?.brand?.readable ?: ""
    val name = currentFilm?.name ?: ""


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
                IconTextBlock(
                    Modifier.weight(1f),
                    Icons.Default.Science,
                    label = "$filmType Film",
                    value = ""
                )
                IconTextBlock(
                    Modifier.weight(1f),
                    Icons.Default.Iso,
                    label = "ISO: $iso",
                    value = ""
                )

            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconTextBlock(
                    Modifier.weight(1f),
                    Icons.Default.Contrast,
                    label = "Contrast: $contrast",
                    value = ""
                )
                IconTextBlock(
                    Modifier.weight(1f),
                    Icons.Default.Grain,
                    label = "Grain: $grain",
                    value = ""
                )

            }
        }

        HorizontalDivider(Modifier, 2.dp, MaterialTheme.colorScheme.outline)

        LazyColumn {
            val pictures = allPictures.value.filter { it.userFilmId == userFilmId }
            itemsIndexed(pictures) { index, picture ->
                Picture(
                    viewmodel = pictureViewmodel,
                    pictureId = picture.uid,
                    onPictureClick = onPictureClick
                )
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun PictureDetails(viewmodel: PictureViewmodel, pictureIndex: Int) {
    val picture = viewmodel.allPictures.value?.find { it.uid == pictureIndex }

    Column(modifier = Modifier.padding(16.dp)) {
        picture?.let {
            val bitmap = imageFileToBitmap(LocalContext.current, picture.pathToFile ?: "")
            Image(
                bitmap = bitmap,
                contentDescription = "User-generated preview image",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxHeight(0.5f),
                contentScale = ContentScale.Fit
            )
            HorizontalDivider(Modifier, 2.dp)
        }

        picture?.title?.let {
            Text(it)
            HorizontalDivider(Modifier, 2.dp)
        }

        IconTextBlock(
            Modifier,
            Icons.Default.Iso,
            label = "Iso: ",
            value = picture?.selectedIso?.toString() ?: noValueString
        )
        IconTextBlock(
            Modifier,
            Icons.Default.Camera,
            label = "Aperture: ",
            value = apertureValueToString(picture?.selectedAperture)
        )
        IconTextBlock(
            Modifier,
            Icons.Default.ShutterSpeed,
            label = "Shutter speed: ",
            value = shutterSpeedValueToString(picture?.selectedShutterSpeed)
        )

        HorizontalDivider(Modifier, 2.dp)


        IconTextBlock(
            Modifier,
            Icons.Default.Numbers,
            label = "Internal identifier: ",
            value = picture?.uid?.toString() ?: noValueString
        )
        IconTextBlock(
            Modifier,
            Icons.Default.Iso,
            label = "Iso: ",
            value = picture?.internalIso?.toString() ?: noValueString
        )
        IconTextBlock(
            Modifier,
            Icons.Default.Camera,
            label = "Aperture: ",
            value = apertureValueToString(picture?.internalAperture)
        )
        IconTextBlock(
            Modifier,
            Icons.Default.ShutterSpeed,
            label = "Shutter speed: ",
            value = shutterSpeedValueToString(picture?.internalShutterSpeed)
        )
    }
}
