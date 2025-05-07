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
import java.math.RoundingMode
import java.text.DecimalFormat

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

fun shutterSpeedValueToString(value: Double?): String {
    if (value == null) return noValueString


    return if (value < 1) {
        val reverse = (1 / value)
        val isWholeNumber = reverse % 1 == 0.0

        val denominator = if (isWholeNumber) {
            reverse.toInt().toString()
        } else {
            val df = DecimalFormat("#.#").apply {
                roundingMode = RoundingMode.CEILING
            }
            df.format(reverse)
        }

        "1/$denominator"

    } else {
        val df = DecimalFormat("#.#").apply {
            roundingMode = RoundingMode.HALF_UP
        }

        "${df.format(value)}\""
    }
}

fun shutterSpeedStringToValue(value: String?): Double? {
    val input = value?.toString() ?: return null

    return when {
        "/" in input -> {
            val parts = input.split("/")
            if (parts.size == 2) {
                val numerator = parts[0].replace(",", ".").toDoubleOrNull()
                val denominator = parts[1].replace(",", ".").toDoubleOrNull()
                if (numerator != null && denominator != null && denominator != 0.0) {
                    numerator / denominator
                } else null
            } else null
        }

        else -> input.replace(",", ".").toDoubleOrNull()
    }
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
    val onlyAllowedChars = newValue.all { it.isDigit() || it == '.' || it == ',' || it == '/' }

    val commaCount = newValue.count { it == ',' }
    val dotCount = newValue.count { it == '.' }
    val hasDecimal = (commaCount + dotCount) > 0
    val slashCount = newValue.count { it == '/' }

    if (
        onlyAllowedChars &&
        commaCount <= 1 &&
        dotCount <= 1 &&
        slashCount <= 1 &&
        !(slashCount > 0 && hasDecimal)
    ) {
        return newValue
    } else
        return oldValue
}