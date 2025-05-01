package com.example.alquranapp.listScreen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alquranapp.GoogleSignIn.FirebaseUtils
import com.example.alquranapp.data.Surah
import com.example.alquranapp.viewmodel.SurahViewModel
import com.example.alquranapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahListScreen(
    navController: NavController,
    viewModel: SurahViewModel,
    loginViewModel: LoginViewModel
) {
    val user = loginViewModel.currentUser.value
    val surahList = viewModel.surahList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val googleSignInClient = remember { FirebaseUtils.getGoogleSignInClient(context) }

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
                    if (user != null) {
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
                            coroutineScope.launch {
                                FirebaseUtils.signOut()
                                loginViewModel.currentUser.value = null
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                                Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Daftar Surah",
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
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari surah...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredList = surahList.filter {
                    it.name.contains(searchQuery.text, true) ||
                            it.englishName.contains(searchQuery.text, true) ||
                            it.englishNameTranslation.contains(searchQuery.text, true)
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredList) { surah ->
                        SurahCard(surah = surah) {
                            navController.navigate("detail/${surah.number}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahCard(surah: Surah, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
        ,
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFBDBDBD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = surah.number.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = surah.englishName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${surah.englishNameTranslation} • Ayat: ${surah.numberOfAyahs}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
