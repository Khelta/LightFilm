package com.example.lightfilm.database.converter

import androidx.room.TypeConverter
import com.example.lightfilm.FilmType

class FilmTypeConverter {
    @TypeConverter
    fun fromString(value: String?): FilmType? {
        return fromReadable(value)
    }

    @TypeConverter
    fun toString(enum: FilmType?): String? {
        return toReadable(enum)
    }
}

