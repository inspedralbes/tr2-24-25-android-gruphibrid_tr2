package com.example.gomath.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gomath.data.loginFromApi
import com.example.gomath.model.LoginRequest
import com.example.gomath.model.User
import kotlinx.coroutines.launch
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject

class GoMathViewModel() : ViewModel() {
    private val loginError = mutableStateOf<String?>(null)

    private val currentUser = MutableStateFlow(User())

    lateinit var mSocket: Socket

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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val loginRequest = LoginRequest(email, password)
            loginError.value = null

            val result = loginFromApi(loginRequest)

            if (result.isSuccess) {
                Log.d("response", result.toString())
                val loginResponse = result.getOrNull()
                if (loginResponse != null) {
                    currentUser.value = User(
                        loginResponse.email,
                        loginResponse.role
                    )
                    Log.d("Login", currentUser.value.toString())
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

    fun socket(code: String) {
        val data = JSONObject()
        data.put("email", currentUser.value.email)
        data.put("role", currentUser.value.role)
        data.put("room", code)
        mSocket.emit("joinRoom", data)
    }
}