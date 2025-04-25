package com.example.lightfilm.Helper

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.graphics.createBitmap
import com.example.lightfilm.noValueString
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

fun apertureStringToValue(value: String?): Double? {
    value?.let {
        return value.replace("/f", "").toDouble()
    }
    return null
}

//TODO Display as fractions or seconds
fun shutterSpeedValueToString(value: Double?): String {
    value?.let {
        return "$value"
    }
    return noValueString
}

fun shutterSpeedStringToValue(value: String?): Double? {
    value?.let {
        return value.toDouble()
    }
    return null
}

class FPrefixVisualTransformation : VisualTransformation {
    private val prefix = "f/"

    override fun filter(text: AnnotatedString): TransformedText {
        // Prepend the fixed prefix "f/" to the actual text.
        val transformedText = AnnotatedString(prefix + text.text)
        val prefixLength = prefix.length

        // Adjust the offset mapping to account for the prefix.
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Shift the cursor by the prefix length.
                return offset + prefixLength
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Make sure that the mapping doesn't return negative values.
                return if (offset - prefixLength < 0) 0 else offset - prefixLength
            }
        }
        return TransformedText(transformedText, offsetMapping)
    }
}

fun decimalStringClean(oldValue: String, newValue: String): String {
    val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' }

    val hasComma = filtered.contains(',')
    val hasDot = filtered.contains('.')

    val finalValue = when {
        hasComma && hasDot -> oldValue
        else -> {
            val separator = if (hasDot) '.' else if (hasComma) ',' else null
            if (separator != null) {
                val firstIndex = filtered.indexOf(separator)
                val cleaned = filtered.filterIndexed { index, c ->
                    c.isDigit() || (c == separator && index == firstIndex)
                }
                cleaned
            } else {
                filtered
            }
        }
    }
    return finalValue
}