package com.example.alquranapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.alquranapp.GoogleSignIn.FirebaseUtils
import com.example.alquranapp.navigasi.QuranNavGraph
import com.example.alquranapp.ui.theme.AlQuranAppTheme
import com.example.alquranapp.viewmodel.AppSettingsViewModel
import com.example.alquranapp.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseUtils.initializeGoogleClient(this)

        //cek user yang sudah login sebelumnya (Google Account)
        val lastAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (lastAccount != null) {
            loginViewModel.currentUser.value = lastAccount
        }

        setContent {
            val settingsViewModel: AppSettingsViewModel = viewModel()
            val navController = rememberNavController()
            val isDarkTheme = settingsViewModel.isDarkTheme.value

            AlQuranAppTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuranNavGraph(
                        navController = navController,
                        settingsViewModel = settingsViewModel,
                        loginViewModel = loginViewModel
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val result = FirebaseUtils.getSignInResultFromIntent(data)
            result.addOnSuccessListener { account ->
                val idToken = account.idToken
                if (idToken != null) {
                    FirebaseUtils.firebaseAuthWithGoogle(idToken) {
                        loginViewModel.currentUser.value = account
                        Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
}