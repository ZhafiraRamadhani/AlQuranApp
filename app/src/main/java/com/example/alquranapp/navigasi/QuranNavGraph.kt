package com.example.alquranapp.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alquranapp.HomeScreen.HomeScreen
import com.example.alquranapp.listScreen.DetailSurahScreen
import com.example.alquranapp.listScreen.SurahListScreen
import com.example.alquranapp.viewmodel.AppSettingsViewModel
import com.example.alquranapp.viewmodel.DetailViewModel
import com.example.alquranapp.viewmodel.SurahViewModel

@Composable
fun QuranNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    settingsViewModel: AppSettingsViewModel //terima parameter dari MainActivity
) {
    NavHost(navController = navController, startDestination = "home") {

        //home screen
        composable("home") {
            HomeScreen(
                navController = navController,
                settingsViewModel = settingsViewModel //diteruskan ke HomeScreen
            )
        }

        //surah list
        composable("surah_list") {
            val viewModel: SurahViewModel = viewModel()
            SurahListScreen(navController = navController, viewModel = viewModel)
        }

        //detail ayat by surah
        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            val viewModel: DetailViewModel = viewModel()
            DetailSurahScreen(
                surahId = id,
                viewModel = viewModel,
                isDarkTheme = settingsViewModel.isDarkTheme.value //kirim nilai dari ViewModel
            )
        }
    }
}