package com.develazquez.bibliocloud.features.admin.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.develazquez.bibliocloud.features.admin.presentation.viewmodels.AdminUiState
import com.develazquez.bibliocloud.features.admin.presentation.viewmodels.AdminViewModel
import com.develazquez.bibliocloud.features.auth.domain.entities.Usuario
import com.develazquez.bibliocloud.features.auth.domain.entities.RolUsuario
import com.develazquez.bibliocloud.features.auth.domain.entities.EstadoUsuario
import com.develazquez.bibliocloud.features.loans.domain.entities.Prestamo
import com.develazquez.bibliocloud.features.loans.domain.entities.EstadoPrestamo
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Usuarios", "Préstamos")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (val state = uiState) {
                is AdminUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AdminUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchAdminData() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                is AdminUiState.Success -> {
                    if (selectedTabIndex == 0) {
                        UsersList(
                            users = state.users,
                            onRoleChange = viewModel::updateUserRole,
                            onStatusChange = viewModel::updateUserStatus
                        )
                    } else {
                        LoansList(
                            loans = state.loans,
                            onMarkReturned = viewModel::markLoanAsReturned
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsersList(
    users: List<Usuario>,
    onRoleChange: (String, String) -> Unit,
    onStatusChange: (String, String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(users) { user ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = user.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = user.email)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rol:")
                        Button(onClick = {
                            val newRole = if (user.rol == RolUsuario.ADMIN) "USUARIO" else "ADMIN"
                            onRoleChange(user.id, newRole)
                        }) {
                            Text(user.rol.name)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Estado:")
                        Button(onClick = {
                            val newStatus = if (user.estado == EstadoUsuario.ACTIVO) "INACTIVO" else "ACTIVO"
                            onStatusChange(user.id, newStatus)
                        }) {
                            Text(user.estado.name)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoansList(
    loans: List<Prestamo>,
    onMarkReturned: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(loans) { loan ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Préstamo: ${loan.id}", fontWeight = FontWeight.Bold)
                    Text(text = "Estado: ${loan.estado.name}")
                    Text(text = "Vencimiento: ${dateFormat.format(loan.fechaFinPrevista)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    if (loan.estado == EstadoPrestamo.ACTIVO || loan.estado == EstadoPrestamo.VENCIDO || loan.estado == EstadoPrestamo.ATRASADO) {
                        Button(onClick = { onMarkReturned(loan.id) }) {
                            Text("Aprobar Devolución")
                        }
                    }
                }
            }
        }
    }
}
