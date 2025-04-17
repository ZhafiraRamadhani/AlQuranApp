package com.example.alquranapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    //endpoint: https://api.alquran.cloud/v1/surah
    @GET("v1/surah")
    suspend fun getAllSurah(): SurahResponse

    //endpoint: https://api.alquran.cloud/v1/surah/{number}/editions/quran-uthmani,id.indonesian,ar.abdulbasitmurattal
    @GET("v1/surah/{number}/editions/quran-uthmani,id.indonesian,ar.abdulbasitmurattal")
    suspend fun getAyatWithAudio(
        @Path("number") surahNumber: Int
    ): AyatResponse

}