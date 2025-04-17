package com.example.alquranapp.data

data class AyatResponse(
    val data: List<EditionResponse>
)

data class EditionResponse(
    val edition: Edition,
    val ayahs: List<AyahData>
)

data class AyahData(
    val number: Int,
    val numberInSurah: Int,
    val text: String,
    val audio: String? = null //hanya ada di edition 'ar.abdulbasitmurattal'
)