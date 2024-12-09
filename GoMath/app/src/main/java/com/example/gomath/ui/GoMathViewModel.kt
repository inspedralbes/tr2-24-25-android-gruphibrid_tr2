package com.example.gomath.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gomath.data.loginFromApi
import com.example.gomath.model.LoginRequest
import com.example.gomath.model.Usuari
import kotlinx.coroutines.launch

class GoMathViewModel() : ViewModel() {
    private val loginError = mutableStateOf<String?>(null)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val loginRequest = LoginRequest(email, password)
            loginError.value = null

            val result = loginFromApi(loginRequest)

            if (result.isSuccess) {
                Log.d("response", result.toString())
                val loginResponse = result.getOrNull()
                if (loginResponse != null) {
                    val user = Usuari(
                        loginResponse.email,
                        loginResponse.role
                    )
                    Log.d("Login", user.toString())
                }
                else {
                    Log.e("Login", "Resposta correcta però el cos és nul o mal format. Comproveu la resposta de l'API.")
                }
            } else {
                result.exceptionOrNull()?.let {
                    loginError.value = "Error de xarxa o servidor. Si us plau, torna-ho a provar més tard."
                }
            }
        }
    }
}