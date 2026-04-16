package com.develazquez.bibliocloud.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.develazquez.bibliocloud.features.auth.presentation.screens.LoginScreen
import com.develazquez.bibliocloud.features.auth.presentation.screens.RegisterScreen
import com.develazquez.bibliocloud.features.catalog.presentation.screens.CatalogScreen
import com.develazquez.bibliocloud.features.catalog.presentation.screens.RecursoDetailScreen
import com.develazquez.bibliocloud.features.catalog.presentation.screens.ConfirmLoanScreen
import com.develazquez.bibliocloud.features.loans.presentation.screens.MyLoansScreen
import com.develazquez.bibliocloud.features.profile.presentation.screens.ProfileScreen
import com.develazquez.bibliocloud.features.camera.presentation.screens.CapturePhotoScreen
import com.develazquez.bibliocloud.features.admin.presentation.screens.AdminDashboardScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Catalog : Screen("catalog")
    object Profile : Screen("profile")
    object RecursoDetail : Screen("recurso_detail/{recursoId}?audioUrl={audioUrl}&isLoaned={isLoaned}") {
        fun createRoute(recursoId: String, audioUrl: String? = null, isLoaned: Boolean = false): String {
            val encodedAudio = if (!audioUrl.isNullOrEmpty()) java.net.URLEncoder.encode(audioUrl, "UTF-8") else ""
            return "recurso_detail/$recursoId?audioUrl=$encodedAudio&isLoaned=$isLoaned"
        }
    }
    object ConfirmLoan : Screen("confirm_loan/{recursoId}") {
        fun createRoute(recursoId: String) = "confirm_loan/$recursoId"
    }
    object MyLoans : Screen("my_loans")
    object CapturePhoto : Screen("capture_photo")
    object AdminDashboard : Screen("admin_dashboard")
}

@Composable
fun BiblioCloudApp(
    intentNavigateTo: String? = null,
    intentBookId: String? = null
) {
    val navController = rememberNavController()

    LaunchedEffect(intentNavigateTo, intentBookId) {
        if (intentNavigateTo == "loan_details" && intentBookId != null) {
            navController.navigate(Screen.RecursoDetail.createRoute(intentBookId, isLoaned = true))
        }
    }

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
            CatalogScreen(
                navController = navController,
                onBookClick = { bookId, audioUrl, isLoaned ->
                    navController.navigate(Screen.RecursoDetail.createRoute(bookId, audioUrl, isLoaned))
                }
            )
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
            arguments = listOf(
                navArgument("recursoId") { type = NavType.StringType },
                navArgument("audioUrl") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("isLoaned") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val recursoId = backStackEntry.arguments?.getString("recursoId") ?: ""
            val audioUrl = backStackEntry.arguments?.getString("audioUrl")?.takeIf { it.isNotEmpty() }
            val isLoaned = backStackEntry.arguments?.getBoolean("isLoaned") ?: false
            val decodedAudioUrl = audioUrl?.let { java.net.URLDecoder.decode(it, "UTF-8") }
            RecursoDetailScreen(
                recursoId = recursoId, 
                audioUrl = decodedAudioUrl,
                isLoaned = isLoaned,
                navController = navController
            )
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

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}