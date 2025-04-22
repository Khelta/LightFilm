package com.example.lightfilm

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import java.io.File

fun imageFileToBitmap(context: Context, fileName: String): ImageBitmap {
    val file = File(context.filesDir, fileName)
    val filePath = file.absolutePath
    val bitmap = BitmapFactory.decodeFile(filePath).asImageBitmap()
    return bitmap
}

fun createPlaceholderBitmap(): ImageBitmap {
    val placeholder = createBitmap(1, 1)
    return placeholder.asImageBitmap()
}


fun apertureValueToString(value: Double?): String {
    value?.let {
        return "f/$value"
    }
    return noValueString
}

fun shutterSpeedValueToString(value: Double?): String {
    value?.let {
        return "$value"
    }
    return noValueString
}