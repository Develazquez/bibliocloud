package com.develazquez.bibliocloud.presentation.view

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.develazquez.bibliocloud.presentation.viewmodel.LoginViewModel
import com.develazquez.bibliocloud.presentation.viewmodel.RegisterViewModel

@Composable
fun BiblioCloudApp() { // Nombre exacto que pide tu MainActivity
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // Ruta para tu Login (Diseño tuyo + Lógica de él)
        composable("login") {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onIrARegistro = { navController.navigate("register") }
            )
        }

        // Ruta para tu Registro (Diseño tuyo + Lógica de él)
        composable("register") {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = viewModel,
                alVolverAlLogin = { navController.popBackStack() }
            )
        }
    }
}