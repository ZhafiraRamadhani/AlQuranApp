package com.example.alquranapp.listScreen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alquranapp.viewmodel.SurahViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahListScreen(navController: NavController, viewModel: SurahViewModel) {
    val surahList = viewModel.surahList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📖 Daftar Surah") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            //search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari surah...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else {
                val filteredList = surahList.filter {
                    it.name.contains(searchQuery.text, ignoreCase = true) ||
                            it.englishName.contains(searchQuery.text, ignoreCase = true) ||
                            it.englishNameTranslation.contains(searchQuery.text, ignoreCase = true)
                }

                if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("❌ Tidak ada surah ditemukan")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        items(filteredList) { surah ->
                            SurahCard(surah = surah, onClick = {
                                navController.navigate("detail/${surah.number}")
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahCard(
    surah: com.example.alquranapp.data.Surah,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val detailTextColor = if (isDarkTheme) Color.White else Color.Gray

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = surah.englishName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${surah.englishNameTranslation} • Ayat: ${surah.numberOfAyahs}",
                style = MaterialTheme.typography.bodyMedium,
                color = detailTextColor
            )
        }
    }
}