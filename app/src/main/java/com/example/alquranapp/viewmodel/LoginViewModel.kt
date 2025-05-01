package com.example.alquranapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LoginViewModel : ViewModel() {
    val currentUser = mutableStateOf<GoogleSignInAccount?>(null)
}