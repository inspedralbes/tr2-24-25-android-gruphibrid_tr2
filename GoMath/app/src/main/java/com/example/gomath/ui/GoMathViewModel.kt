package com.example.gomath.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gomath.data.local.GoMathDB
import com.example.gomath.data.loginFromApi
import com.example.gomath.model.LoginRequest
import com.example.gomath.model.UserSession
import kotlinx.coroutines.launch
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

class GoMathViewModel() : ViewModel() {
    private val loginError = mutableStateOf<String?>(null)

    private val _currentUser = MutableStateFlow<UserSession?>(null)
    // val currentUser: StateFlow<UserSession?> = _currentUser

    private lateinit var mSocket: Socket

    //Creació de socket
    init {
        viewModelScope.launch {
            try {
                mSocket = IO.socket("http://10.0.2.2:3010")
                // mSocket = IO.socket("http://10.0.2.2:8000")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SocketIO", "Failed to connect to socket", e)
            }
            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT) {
                Log.d("SocketIO", "Connected to socket: ${mSocket.id()}")

            }
            mSocket.on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketIO", "Disconnected from socket")
            }
        }
    }

    fun getUserFromLocal(context: Context, onResult: (UserSession?) -> Unit) {
        viewModelScope.launch {
            val db = GoMathDB.getDatabase(context)
            val userSession = db.GoMathDao().getUser()
            onResult(userSession)
        }
    }

    private fun saveUserToLocal(context: Context, user: UserSession) {
        viewModelScope.launch {
            val db = GoMathDB.getDatabase(context)
            db.GoMathDao().insertUser(user)
        }
    }

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            val loginRequest = LoginRequest(email, password)
            loginError.value = null
            val result = loginFromApi(loginRequest)

            if (result.isSuccess) {
                Log.d("response", result.toString())
                val loginResponse = result.getOrNull()
                if (loginResponse != null) {
                    val user = UserSession(
                        loginResponse.email,
                        loginResponse.role
                    )
                    saveUserToLocal(context, user)
                    _currentUser.value = user
                    Log.d("Login", _currentUser.value.toString())
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

    fun logout(context: Context) {
        viewModelScope.launch {
            // Borrar los datos del usuario en la base de datos
            val db = GoMathDB.getDatabase(context)
            db.GoMathDao().deleteUser()

            // Restablecer el estado de currentUser
            _currentUser.value = null

            // Aquí también podrías hacer otras acciones necesarias como desconectar el socket si lo deseas
            mSocket.disconnect()
        }
    }

    fun socket(code: String) {
        val data = JSONObject()
        data.put("email", _currentUser.value?.email)
        data.put("role", _currentUser.value?.role)
        data.put("room", code)
        mSocket.emit("joinRoom", data)
    }
}