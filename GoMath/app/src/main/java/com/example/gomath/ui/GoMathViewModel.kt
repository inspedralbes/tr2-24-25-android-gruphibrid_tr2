package com.example.gomath.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gomath.data.loginFromApi
import com.example.gomath.model.LoginRequest
import com.example.gomath.model.LoginResponse
import com.example.gomath.model.User
import com.example.gomath.model.Users
import kotlinx.coroutines.launch
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class GoMathViewModel() : ViewModel() {
    private val loginError = mutableStateOf<String?>(null)

    private val currentUser = MutableStateFlow(LoginResponse())

    // Llista de productes
    private val _users = MutableStateFlow(Users())
    val users: StateFlow<Users> get() = _users.asStateFlow()
    // Funció per establir la llista de productes

    private var codeActual: String = "";
//    val uiState: StateFlow<User> = currentUser.asStateFlow()

    lateinit var mSocket: Socket

    //Creació de socket
    init {
        viewModelScope.launch {
            try {
                mSocket = IO.socket("http://10.0.2.2:3010")
                // mSocket = IO.socket("http://juicengo.dam.inspedralbes.cat:20871")
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
                    currentUser.value = LoginResponse(
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
        codeActual = code
        mSocket.emit("joinRoom", data)
    }

    fun getLlista() {
        val data = JSONObject()
        data.put("room", codeActual) // Enviar el código de la sala al servidor

        // Emitir el evento `getRoomUserDetails` al servidor
        mSocket.emit("getRoomUserDetails", data)

        // Escuchar la respuesta del servidor
        mSocket.on("roomUserDetails") { args ->
            val response = args[0] as JSONObject
            val usersArray = response.optJSONArray("users")
            val roomName = response.optString("room")
            val message = response.optString("message", "No message")

            if (usersArray != null) {
                // Crear una lista de usuarios basada en los datos recibidos
                val userList = mutableListOf<User>()
                for (i in 0 until usersArray.length()) {
                    val userJson = usersArray.getJSONObject(i)
                    val user = User(
                        email = userJson.optString("email", ""),
                        username = userJson.optString("username", ""),
                        role = userJson.optString("role", "")
                    )
                    userList.add(user)
                }

                // Actualizar el flujo de usuarios
                _users.value = Users(users = userList)

                // Mostrar en Log para pruebas
                Log.d("SocketIO", "Sala: $roomName, Usuaris: ${_users.value.users}")
            } else {
                Log.w("SocketIO", "Missatge del servidor: $message")
            }
        }
    }

    fun pause() {
        Log.d("MandoScreen", "Pausando...")
        // Envía una señal al servidor si es necesario
        mSocket.emit("pause")
    }

}