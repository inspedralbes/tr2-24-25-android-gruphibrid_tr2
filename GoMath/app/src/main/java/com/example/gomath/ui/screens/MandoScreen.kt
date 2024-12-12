package com.example.gomath.ui.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomath.model.User
import com.example.gomath.model.Users
import com.example.gomath.ui.GoMathViewModel



@Composable
fun MandoScreen(viewModel: GoMathViewModel, navController: NavHostController) {
    var clickedButton by remember { mutableStateOf<ButtonType?>(null) }
    val users by viewModel.users.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Control",
            fontSize = 40.sp,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        LlistaRoom(users, viewModel)
        ControlButton(
            text = "Pausar",
            icon = Icons.Filled.PauseCircle,
            isClicked = clickedButton == ButtonType.PAUSE
        ) {
            clickedButton = ButtonType.PAUSE
            viewModel.pause()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun LlistaRoom(users: Users, viewModel: GoMathViewModel){
    viewModel.getLlista()
    // Utilitzem un LazyColumn per crear una llista vertical
    LazyColumn(modifier = Modifier.size(550.dp)) {
        // Iterem per cada dos elements de la llista
        items(users.users.chunked(2)) { rowUsers ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                // Per cada element de la fila (2 per fila)
                for (user in rowUsers) {
                    UserIndividual(user = user, users = users, viewModel = viewModel, modifier = Modifier.weight(5f))
                }
            }
        }
    }
}

@Composable
fun UserIndividual(user: User, users: Users, viewModel: GoMathViewModel, modifier: Modifier) {
    Log.d("users", "user:" + user.username)
    Card (
        colors = CardDefaults.cardColors(
        disabledContainerColor = Color.Red,
        disabledContentColor = Color.Red,
        contentColor = Color.Cyan,
        containerColor = Color.LightGray)
    ) {
        Text(text = user.username, color = Color.Black)
        Text(text = "Hola", color = Color.Black)
    }
}

@Composable
fun ControlButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isClicked: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isClicked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    )

    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.2f else 1f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(backgroundColor, MaterialTheme.shapes.medium)
            .padding(16.dp)
            .scale(scale),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, fontSize = 18.sp)
    }
}

enum class ButtonType {
    REWIND, PAUSE, FORWARD
}
