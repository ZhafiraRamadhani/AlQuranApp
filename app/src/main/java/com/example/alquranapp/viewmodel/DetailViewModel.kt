package com.example.alquranapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alquranapp.data.Ayat
import com.example.alquranapp.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val _ayatList = MutableStateFlow<List<Ayat>>(emptyList())
    val ayatList: StateFlow<List<Ayat>> = _ayatList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getAyatBySurah(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getAyatWithAudio(id)

                val arabicAyat = response.data.find { it.edition.identifier == "quran-uthmani" }?.ayahs ?: emptyList()
                val translationAyat = response.data.find { it.edition.identifier == "id.indonesian" }?.ayahs ?: emptyList()
                val audioAyat = response.data.find { it.edition.identifier == "ar.abdulbasitmurattal" }?.ayahs ?: emptyList()

                _ayatList.value = arabicAyat.mapIndexed { index, arab ->
                    val isFirstAyat = arab.numberInSurah == 1 && id != 1 && id != 9

                    val cleanArabic = if (isFirstAyat) {
                        arab.text.drop(39).trim()
                    } else arab.text

                    val cleanTranslation = if (isFirstAyat && translationAyat.getOrNull(index)?.text?.startsWith("Dengan nama Allah") == true) {
                        translationAyat[index].text.removePrefix("Dengan nama Allah Yang Maha Pengasih, Maha Penyayang.").trim()
                    } else translationAyat.getOrNull(index)?.text ?: ""

                    Ayat(
                        number = arab.numberInSurah,
                        arabicText = cleanArabic,
                        translationText = cleanTranslation,
                        audioUrl = audioAyat.getOrNull(index)?.audio ?: ""
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}