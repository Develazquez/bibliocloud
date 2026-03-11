package com.develazquez.bibliocloud.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.develazquez.bibliocloud.data.local.HardwareUtils
import com.develazquez.bibliocloud.domain.model.EstadoPrestamo
import com.develazquez.bibliocloud.domain.model.Prestamo
import com.develazquez.bibliocloud.presentation.state.LoanProcessState
import com.develazquez.bibliocloud.presentation.state.MyLoansState
import com.develazquez.bibliocloud.presentation.viewmodel.LoanProcessViewModel
import com.develazquez.bibliocloud.presentation.viewmodel.MyLoansViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLoansScreen(
    navController: NavController,
    viewModel: MyLoansViewModel = hiltViewModel(),
    loanProcessViewModel: LoanProcessViewModel = hiltViewModel()
) {
    val loansState by viewModel.loansState.collectAsState()
    val returnState by loanProcessViewModel.loanState.collectAsState()
    val context = LocalContext.current

    // Hardware #2: Vibración al devolver préstamo exitosamente
    LaunchedEffect(returnState) {
        if (returnState is LoanProcessState.Success) {
            HardwareUtils.vibrateSuccess(context)
            Toast.makeText(context, "Préstamo devuelto exitosamente", Toast.LENGTH_SHORT).show()
            loanProcessViewModel.resetState()
            viewModel.refresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Préstamos") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = loansState) {
            is MyLoansState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MyLoansState.Success -> {
                if (state.prestamos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes préstamos activos")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.prestamos) { prestamo ->
                            PrestamoCard(
                                prestamo = prestamo,
                                onDevolver = {
                                    loanProcessViewModel.devolverPrestamo(prestamo.id)
                                }
                            )
                        }
                    }
                }
            }
            is MyLoansState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            MyLoansState.Idle -> {}
        }
    }
}

@Composable
fun PrestamoCard(
    prestamo: Prestamo,
    onDevolver: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = prestamo.recurso?.titulo ?: "Recurso #${prestamo.recursoId}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fecha inicio: ${dateFormat.format(prestamo.fechaInicio)}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Fecha límite: ${dateFormat.format(prestamo.fechaFinPrevista)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text(prestamo.estado.name) }
                )

                if (prestamo.estado == EstadoPrestamo.ACTIVO || prestamo.estado == EstadoPrestamo.ATRASADO) {
                    Button(onClick = onDevolver) {
                        Text("Devolver")
                    }
                }
            }
        }
    }
}