package com.example.turkeyguide.data

data class Category(
    val label: String,
    val query: String,
    val includedType: String?
)

val CATEGORIES = listOf(
    Category("Lokanta", "lokanta", "restaurant"),
    Category("Kafe", "kafe", "cafe"),
    Category("Müze", "müze", "museum"),
    Category("Tarihi yer", "tarihi yerler", "tourist_attraction"),
    Category("Otel", "otel", "lodging"),
    Category("Park", "park", "park")
)

val CITIES = listOf(
    "Adana", "Ankara", "Antalya", "Bursa", "Diyarbakır", "Eskişehir",
    "Gaziantep", "İstanbul", "İzmir", "Kayseri", "Konya", "Mardin",
    "Samsun", "Şanlıurfa", "Trabzon", "Van"
)
