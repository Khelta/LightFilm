package com.example.lightfilm.database.converter

import com.example.lightfilm.ReadableEnum

inline fun <reified T> fromReadable(readable: String?): T
        where T : Enum<T>, T : ReadableEnum {
    return enumValues<T>().firstOrNull { it.readable == readable }
        ?: throw IllegalArgumentException("No enum constant with readable = '$readable'")
}

inline fun <reified T> toReadable(value: T?): String
        where T : Enum<T>, T : ReadableEnum {
    return value?.readable ?: ""
}