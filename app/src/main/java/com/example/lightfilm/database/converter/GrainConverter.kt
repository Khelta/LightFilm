package com.example.lightfilm.database.converter

import androidx.room.TypeConverter
import com.example.lightfilm.Grain

class GrainConverter {
    @TypeConverter
    fun fromString(value: String?): Grain? {
        return fromReadable(value)
    }

    @TypeConverter
    fun toString(enum: Grain?): String? {
        return toReadable(enum)
    }
}

