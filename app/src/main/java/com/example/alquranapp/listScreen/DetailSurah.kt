package com.example.alquranapp.listScreen

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.alquranapp.data.Ayat
import com.example.alquranapp.utils.BookmarkManager
import com.example.alquranapp.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSurahScreen(surahId: Int, viewModel: DetailViewModel, isDarkTheme: Boolean) {
    val rawAyatList = viewModel.ayatList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val ayatTextColor = remember(isDarkTheme) { if (isDarkTheme) Color.White else Color(0xFF1E3A8A) }

    val bismillahHeader = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
    val bismillahRegex = Regex("^بِسْمِ\\s*اللَّهِ\\s*الرَّحْمَٰنِ\\s*الرَّحِيمِ")
    val bismillahTranslationRegex = Regex("^Dengan nama Allah.*?Maha Penyayang[\\.\\s–-]*")

    //potong bismillah dari ayat pertama (Arab & Indonesia), selain 1 dan 9
    val filteredList = rawAyatList.map { ayat ->
        if (ayat.number == 1 && surahId != 1 && surahId != 9) {
            val cleanedArabic = ayat.arabicText.replaceFirst(bismillahRegex, "").trim()
            val cleanedTranslation = ayat.translationText.replaceFirst(bismillahTranslationRegex, "").trim()
            ayat.copy(
                arabicText = cleanedArabic,
                translationText = cleanedTranslation
            )
        } else {
            ayat
        }
    }.filter {
        it.arabicText.contains(searchQuery.text, ignoreCase = true) ||
                it.translationText.contains(searchQuery.text, ignoreCase = true)
    }

    LaunchedEffect(surahId) {
        viewModel.getAyatBySurah(surahId)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Detail Surah") })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari ayat atau terjemahan...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    //header bismillah manual untuk selain surah 1 & 9
                    if (rawAyatList.isNotEmpty() && surahId != 1 && surahId != 9) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = bismillahHeader,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ayatTextColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    items(filteredList) { ayat ->
                        AyatCard(
                            ayat = ayat,
                            onPlayAudio = { url ->
                                try {
                                    mediaPlayer?.release()
                                    mediaPlayer = MediaPlayer().apply {
                                        setDataSource(url)
                                        prepare()
                                        start()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            surahId = surahId,
                            ayatTextColor = ayatTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AyatCard(ayat: Ayat, onPlayAudio: (String) -> Unit, surahId: Int, ayatTextColor: Color) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //nomor di kiri, teks Arab di kanan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${ayat.number}.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ayatTextColor,
                    modifier = Modifier.alignByBaseline()
                )
                Text(
                    text = ayat.arabicText,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Right,
                    color = ayatTextColor,
                    modifier = Modifier
                        .weight(1f)
                        .alignByBaseline()
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ayat.translationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Button(onClick = {
                    onPlayAudio(ayat.audioUrl)
                }) {
                    Text("▶️ Dengarkan")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    BookmarkManager.saveBookmark(context, surahId, ayat.number)
                }) {
                    Text("🔖 Bookmark")
                }
            }
        }
    }
}
