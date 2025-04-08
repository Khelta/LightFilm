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
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "datetime") val datetime: Int,
    @ColumnInfo(name = "aperture") val aperture: Double,
    @ColumnInfo(name = "shutterspeed") val shutterspeed: Double,
    @ColumnInfo(name = "iso") val iso: Int
)

 @Entity(tableName = "user_film")
 data class UserFilmModel(
     @PrimaryKey(autoGenerate = true) val uid: Int?,
     @ColumnInfo(name = "title") val title: String
 )

@Entity(tableName = "film")
data class FilmModel(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: FilmType,
    @ColumnInfo(name = "brand") val brand: FilmBrand,
    @ColumnInfo(name = "iso") val iso: Int,
    @ColumnInfo(name = "grain") val grain: Grain,
    @ColumnInfo(name = "contrast") val contrast: Contrast
)