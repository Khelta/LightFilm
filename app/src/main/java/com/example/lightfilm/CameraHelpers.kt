package com.example.lightfilm

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.round

fun getShutterSpeedFromAperture(f: Double, ev: Double, ISO: Int): Double {
    return (100 * f.pow(2)) / (ISO * 2.0.pow(ev))
    //return (f.pow(2)/2.0.pow(ev))/(ISO /100 )
}

fun roundToFirstSignificantDigit(number: Double): Double {
    if (number == 0.0) {
        return 0.0
    }

    val absNumber = abs(number)
    val magnitude = 10.0.pow(floor(log10(absNumber)))
    val shifted = absNumber / magnitude
    val roundedShifted = round(shifted)
    val result = roundedShifted * magnitude

    return if (number < 0) -result else result
}

fun findClosestNumber(numbers: List<Double>, target: Double): Double {
    var closestNumber = numbers[0]
    var minDifference = abs(numbers[0] - target)

    for (number in numbers) {
        val difference = abs(number - target)
        if (difference < minDifference) {
            minDifference = difference
            closestNumber = number
        }
    }

    return closestNumber
}

fun calculateEV(aperture: Double, exposureTime: Double, iso: Int): Double {
    if (aperture > 0.0 && exposureTime > 0.0)
        return log2(aperture.pow(2.0) / exposureTime) + log2(iso.toDouble() / 100)
    else
        return -999.0
}