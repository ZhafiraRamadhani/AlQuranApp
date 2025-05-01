package com.example.alquranapp.HomeScreen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alquranapp.GoogleSignIn.FirebaseUtils
import com.example.alquranapp.R
import com.example.alquranapp.utils.BookmarkManager
import com.example.alquranapp.viewmodel.AppSettingsViewModel
import com.example.alquranapp.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    settingsViewModel: AppSettingsViewModel,
    loginViewModel: LoginViewModel
) {
    val isDarkTheme = isSystemInDarkTheme()
    val isDarkMode by settingsViewModel.isDarkTheme
    val context = LocalContext.current
    val bookmark = BookmarkManager.getBookmark(context)
    val user = loginViewModel.currentUser.value
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
                            FirebaseUtils.signOut()
                            loginViewModel.currentUser.value = null
                            Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Al-Qur'an App",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\uD83C\uDF19 Mode Gelap",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { settingsViewModel.isDarkTheme.value = it }
                    )
                }

                QuranButton(
                    onClick = {
                        if (user != null) {
                            navController.navigate("surah_list")
                        } else {
                            Toast.makeText(context, "Harap login terlebih dahulu", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                if (user == null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Silakan login untuk mulai membaca Al-Qur'an",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = {
                                val signInIntent = googleSignInClient.signInIntent
                                (context as Activity).startActivityForResult(signInIntent, 100)
                            }) {
                                Text("Login dengan Google")
                            }
                        }
                    }
                }

                if (bookmark != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        navController.navigate("detail/${bookmark.first}")
                    }) {
                        Text("\uD83D\uDD16 Lanjutkan ke Surah ${bookmark.first}, Ayat ${bookmark.second}")
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Temukan surah dan dengarkan lantunan ayat",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}