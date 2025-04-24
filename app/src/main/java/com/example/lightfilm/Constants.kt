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
    1, 2, 4, 8, 16, 32, 64, 100, 128, 256, 400, 512, 1024, 2048, 4096, 6310, 8192, 10000
)

val fNumbers: List<Double> = listOf(
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
    45.0
)

val shutterSpeeds: List<Double> = listOf(
    1.0 / 8000,
    1.0 / 6500,
    1.0 / 5500,
    1.0 / 4000,
    1.0 / 3200,
    1.0 / 2500,
    1.0 / 2000,
    1.0 / 1500,
    1.0 / 1250,
    1.0 / 1000,
    1.0 / 800,
    1.0 / 640,
    1.0 / 500,
    1.0 / 400,
    1.0 / 320,
    1.0 / 250,
    1.0 / 200,
    1.0 / 180,
    1.0 / 125,
    1.0 / 100,
    1.0 / 80,
    1.0 / 60,
    1.0 / 50,
    1.0 / 40,
    1.0 / 30,
    1.0 / 25,
    1.0 / 20,
    1.0 / 15,
    1.0 / 13,
    1.0 / 10,
    1.0 / 8,
    1.0 / 6,
    1.0 / 5,
    1.0 / 4,
    1.0 / 3,
    1.0 / 2.5,
    1.0 / 2,
    1.0 / 1.6,
    1.0 / 1.3,
    1.0,
    1.3,
    1.6,
    2.0,
    2.5,
    3.0,
    4.0,
    5.0,
    6.0,
    8.0,
    10.0,
    13.0,
    16.0,
    20.0,
    25.0,
    32.0,
    40.0,
    52.0,
    64.0
)

val noValueString = " - "

interface ReadableEnum {
    val readable: String
}

enum class Scene {
    FILMLIST,
    PICTURELIST,
    PICTUREDETAILS,
    MEASUREMENTS,
    FILMCREATION
}

enum class Contrast(override val readable: String) : ReadableEnum {
    CUSTOM("Custom"),
    LOW("Low"),
    LOW_MEDIUM("Low-Medium"),
    MEDIUM("Medium"),
    MEDIUM_HIGH("Medium-High"),
    HIGH("High"),
    VERY_HIGH("Very High")
}

enum class Grain(override val readable: String) : ReadableEnum {
    CUSTOM("Custom"),
    VERY_FINE("Very Fine"),
    FINE("Fine"),
    NORMAL("Normal"),
    MEDIUM("Medium"),
    STRONG("Strong")
}

enum class FilmType(override val readable: String) : ReadableEnum {
    CUSTOM("Custom"),
    COLOR_NEGATIVE("Color Negative"),
    BLACK_AND_WHITE("Black & White"),
    COLOR_SLIDE("Color Slide"),
    BLACK_AND_WHITE_SLIDE("Black & White Slide")
}

enum class FilmBrand(override val readable: String) : ReadableEnum {
    CUSTOM("Custom"),
    ADOX("ADOX"),
    AGFA("AGFA"),
    CINESTILL("Cinestill"),
    FOMAPAN("Fomapan"),
    FUJIFILM("Fujifilm"),
    ILFORD("Ilford"),
    KENTMERE("Kentmere"),
    KODAK("Kodak"),
    LOMOGRAPHY("Lomography")
}
