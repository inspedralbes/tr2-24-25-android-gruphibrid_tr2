package com.example.gomath.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gomath.R
import com.example.gomath.ui.screens.LoginScreen

enum class GoMathApp(@StringRes val title: Int) {
    Login(title = R.string.login),
    Code(title = R.string.code),
    Control(title =R.string.control),
    Result(title = R.string.result)
}

@Composable
fun GoMathApp(navController: NavHostController, context: Context) {
    val viewModel: GoMathViewModel = viewModel();

    NavHost(navController, startDestination = GoMathApp.Login.name) {
        composable(route = GoMathApp.Login.name) {
            LoginScreen(viewModel)
        }
//        composable(route = GoMathApp.Code.name) {
//            CodeScreen()
//        }
//        composable(route = GoMathApp.Control.name) {
//            ControlScreen()
//        }
//        composable(route = GoMathApp.Result.name) {
//            ResultScreen()
//        }
    }
}