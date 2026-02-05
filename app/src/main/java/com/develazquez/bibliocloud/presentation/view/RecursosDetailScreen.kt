package com.develazquez.bibliocloud.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.develazquez.bibliocloud.presentation.state.LoanProcessState
import com.develazquez.bibliocloud.presentation.viewmodel.LoanProcessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecursoDetailScreen(
    recursoId: String,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Recurso") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("ID del Recurso: $recursoId")

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(Screen.ConfirmLoan.createRoute(recursoId))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solicitar Préstamo")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmLoanScreen(
    recursoId: String,
    navController: NavController,
    viewModel: LoanProcessViewModel = hiltViewModel()
) {
    val loanState by viewModel.loanState.collectAsState()

    LaunchedEffect(loanState) {
        when (loanState) {
            is LoanProcessState.Success -> {
                navController.navigate(Screen.MyLoans.route) {
                    popUpTo(Screen.Catalog.route)
                }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Préstamo") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
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
            Text(
                text = "¿Confirmar préstamo del recurso?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "El préstamo tendrá una duración de 7 días",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = { viewModel.solicitarPrestamo(recursoId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = loanState !is LoanProcessState.Loading
            ) {
                if (loanState is LoanProcessState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirmar Préstamo")
                }
            }

            when (val state = loanState) {
                is LoanProcessState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is LoanProcessState.ValidationError -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = state.message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                is LoanProcessState.AvailabilityError -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = state.message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
                else -> {}
            }
        }
    }
}