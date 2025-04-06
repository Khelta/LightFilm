package com.example.lightfilm.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture")
data class PictureModel(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "datetime") val datetime: Int,
    @ColumnInfo(name = "aperture") val aperture: Double,
    @ColumnInfo(name = "shutterspeed") val shutterspeed: Double,
    @ColumnInfo(name = "iso") val iso: Int
)
