package com.example.gomath.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GoMathViewModel() : ViewModel() {

//    private val loginError = mutableStateOf<String?>(null)
//
//    fun loginViewModel(email: String, password: String) {
//        viewModelScope.launch {
//            val hashedPassword = hashPassword(password)
//            val loginRequest = LoginRequest(email, hashedPassword)
//
//            loginError.value = null
//
//            login(loginRequest) { loginResponse, throwable ->
//                if (throwable != null) {
//                    Log.e("login", "Error de xarxa: ${throwable.message}")
//                } else if (loginResponse != null && loginResponse.Confirmacio) {
//                    // Login successful
//                    val user = Usuari(
//                        loginResponse.idUser,
//                        loginResponse.Nom,
//                        loginResponse.Correu,
//                        loginResponse.Contrasenya
//                    )
//                    currentUser.value = user
//                    loginError.value = null
//                } else {
//                    loginError.value = "Correu o contrasenya incorrectes"
//                }
//            }
//        }
//    }
}