package com.example.alquranapp.HomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alquranapp.R
import com.example.alquranapp.ui.theme.AlQuranAppTheme
import com.example.alquranapp.utils.BookmarkManager
import com.example.alquranapp.viewmodel.AppSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    settingsViewModel: AppSettingsViewModel
) {
    val isDarkMode by settingsViewModel.isDarkTheme
    val context = LocalContext.current
    val bookmark = BookmarkManager.getBookmark(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📖 Al-Qur'an Digital") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            //background image
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //mode gelap toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌙 Mode Gelap",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { settingsViewModel.isDarkTheme.value = it }
                    )
                }

                //tombol utama baca qur'an
                QuranButton {
                    navController.navigate("surah_list")
                }

                //bookmark terakhir
                if (bookmark != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        navController.navigate("detail/${bookmark.first}")
                    }) {
                        Text("🔖 Lanjutkan ke Surah ${bookmark.first}, Ayat ${bookmark.second}")
                    }
                }
            }
        }
    }
}

@Composable
fun QuranButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Baca Al-Qur'an",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Temukan surah dan dengarkan lantunan ayat",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuranButtonPreview() {
    AlQuranAppTheme {
        QuranButton(onClick = {})
    }
}