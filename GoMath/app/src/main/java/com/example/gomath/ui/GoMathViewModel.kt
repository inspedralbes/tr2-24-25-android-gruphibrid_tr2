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
import com.example.gomath.model.User
import com.example.gomath.model.Users
import kotlinx.coroutines.launch
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject

class GoMathViewModel() : ViewModel() {
    private val loginError = mutableStateOf<String?>(null)

    private val _currentUser = MutableStateFlow<UserSession?>(null)

    // Llista de productes
    private val _users = MutableStateFlow(Users())
    val users: StateFlow<Users> = _users.asStateFlow()

    private var codeActual: String = "";

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
                mSocket.on("userJoined", onUserJoined)
                mSocket.on("userLeft", onUserLeft)
                //mSocket.on("roomUserDetails", onRoomUsers)
            }
            mSocket.on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketIO", "Disconnected from socket")
            }
        }
    }

    private val onUserJoined = Emitter.Listener { args ->
        val response = args[0] as JSONObject
        val username = response.optString("username", "Desconocido")
        val roomName = response.optString("room", "")
        val usersArray = response.optJSONArray("users")
        Log.d("SocketIO", "Usuario $username se unió a la sala $roomName")

        if (usersArray != null) {
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
            _users.update { currentState ->
                currentState.copy(users = userList)
            }
            Log.d("SocketIO", "Usuarios actualizados tras unirse: ${_users.value.users}")
        } else {
            Log.w("SocketIO", "Lista de usuarios no proporcionada en el evento userJoined.")
        }
    }

    private val onUserLeft = Emitter.Listener { args->
        val response = args[0] as JSONObject
        val username = response.optString("username", "Desconocido")
        val email = response.optString("email", "")
        val roomName = response.optString("room", "")
        Log.d("SocketIO", "Usuario $username salió de la sala $roomName")

        _users.update { currentState ->
            currentState.copy(users = currentState.users.filter { it.email != email })
        }
    }

    fun getUserFromLocal(context: Context, onResult: (UserSession?) -> Unit) {
        viewModelScope.launch {
            val db = GoMathDB.getDatabase(context)
            val userSession = db.GoMathDao().getUser()
            _currentUser.value = userSession
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
            val db = GoMathDB.getDatabase(context)
            db.GoMathDao().deleteUser()
            _currentUser.value = null
            mSocket.disconnect()
        }
    }

    fun socket(code: String) {
        val data = JSONObject()
        data.put("email", _currentUser.value?.email)
        data.put("role", _currentUser.value?.role)
        data.put("room", code)
        codeActual = code
        Log.d("socket", data.toString())
        mSocket.emit("joinRoom", data)
    }

    fun kickUserFromRoom(user: User) {
        val data = JSONObject()
        data.put("targetEmail", user.email)
        data.put("room", codeActual)
        mSocket.emit("kickUser", data)
        Log.d("kick", "Emitiendo kickUser: $data")
        _users.update { currentState ->
            currentState.copy(users = currentState.users.filter { it.email != user.email })
        }
        Log.d("kick", "L'Usuari: ${user.username} Ha estat eliminat")
    }

    fun resetCode(){
        codeActual = ""
    }

    fun pause() {
        Log.d("MandoScreen", "Pausando...")
        // Envía una señal al servidor si es necesario
        mSocket.emit("pause")
    }
}