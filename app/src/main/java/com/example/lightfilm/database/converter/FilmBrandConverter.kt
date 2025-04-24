package com.example.lightfilm.database.converter

import androidx.room.TypeConverter
import com.example.lightfilm.FilmBrand

class FilmBrandConverter {
    @TypeConverter
    fun fromString(value: String?): FilmBrand? {
        return fromReadable(value)
    }

    @TypeConverter
    fun toString(enum: FilmBrand?): String? {
        return toReadable(enum)
    }
}

