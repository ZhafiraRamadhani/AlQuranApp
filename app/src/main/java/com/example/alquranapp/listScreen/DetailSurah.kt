package com.example.alquranapp.listScreen

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alquranapp.GoogleSignIn.FirebaseUtils
import com.example.alquranapp.data.Ayat
import com.example.alquranapp.utils.BookmarkManager
import com.example.alquranapp.viewmodel.DetailViewModel
import com.example.alquranapp.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSurahScreen(
    surahId: Int,
    viewModel: DetailViewModel,
    isDarkTheme: Boolean,
    loginViewModel: LoginViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val user = loginViewModel.currentUser.value
    val rawAyatList = viewModel.ayatList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val ayatTextColor = if (isDarkTheme) Color.White else Color(0xFF1E3A8A)
    val bismillah = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"

    if (user == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Harap login terlebih dahulu", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
        return
    }

    val filteredList = rawAyatList.filter {
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

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF8B5E3C))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(user.displayName ?: "", color = Color.White)
                        Text(user.email ?: "", color = Color.White.copy(alpha = 0.8f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "User Photo",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    IconButton(onClick = {
                        FirebaseUtils.signOut()
                        loginViewModel.currentUser.value = null
                        Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Detail Surah",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari ayat...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (rawAyatList.isNotEmpty() && surahId != 1 && surahId != 9) {
                        item {
                            Text(
                                text = bismillah,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = ayatTextColor
                            )
                        }
                    }

                    items(filteredList) { ayat ->
                        AyatCard(ayat, { url ->
                            try {
                                mediaPlayer?.release()
                                mediaPlayer = MediaPlayer().apply {
                                    setDataSource(url)
                                    prepare()
                                    start()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Gagal memutar audio", Toast.LENGTH_SHORT).show()
                            }
                        }, surahId, ayatTextColor)
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
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
        ,
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8B5E3C)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ayat.number.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = ayat.arabicText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ayatTextColor,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ayat.translationText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Button(onClick = { onPlayAudio(ayat.audioUrl) }) {
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
