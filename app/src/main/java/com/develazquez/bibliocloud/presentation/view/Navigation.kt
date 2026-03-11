package com.develazquez.bibliocloud.presentation.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Catalog : Screen("catalog")
    object Profile : Screen("profile")
    object RecursoDetail : Screen("recurso_detail/{recursoId}") {
        fun createRoute(recursoId: String) = "recurso_detail/$recursoId"
    }
    object ConfirmLoan : Screen("confirm_loan/{recursoId}") {
        fun createRoute(recursoId: String) = "confirm_loan/$recursoId"
    }
    object MyLoans : Screen("my_loans")
    object CapturePhoto : Screen("capture_photo")
}

@Composable
fun BiblioCloudApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Screen.Catalog.route) {
            CatalogScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.RecursoDetail.route,
            arguments = listOf(navArgument("recursoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recursoId = backStackEntry.arguments?.getString("recursoId") ?: ""
            RecursoDetailScreen(recursoId = recursoId, navController = navController)
        }

        composable(
            route = Screen.ConfirmLoan.route,
            arguments = listOf(navArgument("recursoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recursoId = backStackEntry.arguments?.getString("recursoId") ?: ""
            ConfirmLoanScreen(recursoId = recursoId, navController = navController)
        }

        composable(Screen.MyLoans.route) {
            MyLoansScreen(navController = navController)
        }

        composable(Screen.CapturePhoto.route) {
            CapturePhotoScreen(navController = navController)
        }
    }
}