package com.example.gomath.ui.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.FastForward
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
import com.example.gomath.ui.GoMathViewModel



@Composable
fun MandoScreen(viewModel: GoMathViewModel, navController: NavHostController) {
    var clickedButton by remember { mutableStateOf<ButtonType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ControlButton(
            text = "Retrocedir",
            icon = Icons.Filled.FastRewind,
            isClicked = clickedButton == ButtonType.REWIND
        ) {
            clickedButton = ButtonType.REWIND
            viewModel.rewind()
        }

        Spacer(modifier = Modifier.height(16.dp))

        ControlButton(
            text = "Pausar",
            icon = Icons.Filled.PauseCircle,
            isClicked = clickedButton == ButtonType.PAUSE
        ) {
            clickedButton = ButtonType.PAUSE
            viewModel.pause()
        }

        Spacer(modifier = Modifier.height(16.dp))

        ControlButton(
            text = "Adelantar",
            icon = Icons.Filled.FastForward,
            isClicked = clickedButton == ButtonType.FORWARD
        ) {
            clickedButton = ButtonType.FORWARD
            viewModel.forward()
        }
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
