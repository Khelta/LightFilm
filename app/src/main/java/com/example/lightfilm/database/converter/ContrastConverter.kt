package com.example.lightfilm.database.converter

import androidx.room.TypeConverter
import com.example.lightfilm.Contrast

class ContrastConverter {
    @TypeConverter
    fun fromString(value: String?): Contrast? {
        return fromReadable(value)
    }

    @TypeConverter
    fun toString(enum: Contrast?): String? {
        return toReadable(enum)
    }
}

