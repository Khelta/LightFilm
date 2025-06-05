package com.example.lightfilm.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lightfilm.Contrast
import com.example.lightfilm.FilmBrand
import com.example.lightfilm.FilmType
import com.example.lightfilm.Grain

@Entity(tableName = "picture")
data class PictureModel(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user_film_id") val userFilmId: Int,
    @ColumnInfo(name = "path_to_file") val pathToFile: String? = null,
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "capture_date") val captureDate: Long,
    @ColumnInfo(name = "internal_aperture") val internalAperture: Double? = null,
    @ColumnInfo(name = "internal_shutterspeed") val internalShutterSpeed: Double? = null,
    @ColumnInfo(name = "internal_iso") val internalIso: Int? = null,
    @ColumnInfo(name = "selected_aperture") val selectedAperture: Double? = null,
    @ColumnInfo(name = "selected_shutterspeed") val selectedShutterSpeed: Double? = null,
    @ColumnInfo(name = "selected_iso") val selectedIso: Int? = null,
    @ColumnInfo(name = "rotation") val rotation: Int = 0,
)

@Entity(tableName = "user_film")
data class UserFilmModel(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "film_id") val filmId: Int? = null
)

@Entity(tableName = "film")
data class FilmModel(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: FilmType,
    @ColumnInfo(name = "brand") val brand: FilmBrand,
    @ColumnInfo(name = "iso") val iso: Int,
    @ColumnInfo(name = "grain") val grain: Grain,
    @ColumnInfo(name = "contrast") val contrast: Contrast
)