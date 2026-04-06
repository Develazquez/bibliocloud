package com.develazquez.bibliocloud.features.profile.presentation.screens

import androidx.compose.runtime.collectAsState
import com.develazquez.bibliocloud.core.ui.Screen
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.develazquez.bibliocloud.features.profile.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.deleteResult) {
        uiState.deleteResult?.let { result ->
            if (result.isSuccess) {
                Toast.makeText(context, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                onNavigateToLogin()
            } else {
                Toast.makeText(context, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Perfil") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Zona de Peligro", style = MaterialTheme.typography.headlineSmall, color = Color.Red)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Al eliminar tu cuenta, se borrarán todos tus préstamos y datos personales de Bibliocloud.")

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("BORRAR MI CUENTA PERMANENTEMENTE", color = Color.White)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Estás completamente seguro?") },
            text = { Text("Esta acción no se puede deshacer. Tu usuario será eliminado de nuestra base de datos.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAccount()
                    showDialog = false
                }) {
                    Text("SÍ, ELIMINAR", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("CANCELAR")
                }
            }
        )
    }
}
