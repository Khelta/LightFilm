package com.example.lightfilm

val isoSensitivityOptions: List<Int> = listOf(
    3,
    4,
    5,
    6,
    8,
    10,
    12,
    16,
    20,
    25,
    32,
    40,
    50,
    64,
    80,
    100,
    125,
    160,
    200,
    250,
    320,
    400,
    500,
    640,
    800,
    1000,
    1250,
    1600,
    2000,
    2500,
    3200,
    4000,
    5000,
    6400,
    8000,
    10000,
    12800
)

val ndSensitivityOptions: List<Int> = listOf(
    0, 2, 4, 8, 16, 32, 64, 100, 128, 256, 400, 512, 1024, 2048, 4096, 6310, 8192, 10000
)

val fNumbers: List<Double> = listOf(
    0.7,
    1.0,
    1.4,
    2.0,
    2.8,
    4.0,
    5.6,
    8.0,
    11.0,
    16.0,
    22.0,
    32.0,
    45.0,
    64.0,
    90.0,
    128.0,
    180.0,
    256.0
)

enum class Scene {
    FILMLIST, PICTURELIST, PICTUREDETAILS, MEASUREMENTS, FILMCREATION
}
