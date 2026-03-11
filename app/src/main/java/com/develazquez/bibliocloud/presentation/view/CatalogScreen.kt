package com.develazquez.bibliocloud.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.develazquez.bibliocloud.data.local.NetworkMonitor
import com.develazquez.bibliocloud.domain.model.Recurso
import com.develazquez.bibliocloud.presentation.state.RecursoState
import com.develazquez.bibliocloud.presentation.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavController,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val recursoState by viewModel.recursoState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Recursos") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.CapturePhoto.route) }) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Fotografiar Portada"
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.MyLoans.route) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Mis Préstamos"
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Mi Perfil"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sensor de conexión
            if (!isConnected) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = "⚠ Sin conexión — mostrando datos locales",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            when (val state = recursoState) {
                is RecursoState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RecursoState.Success -> {
                    if (state.recursos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay recursos disponibles")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.recursos) { recurso ->
                                RecursoCard(
                                    recurso = recurso,
                                    onClick = {
                                        navController.navigate(Screen.RecursoDetail.createRoute(recurso.id))
                                    }
                                )
                            }
                        }
                    }
                }
                is RecursoState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }) { Text("Reintentar") }
                        }
                    }
                }
                RecursoState.Idle -> {}
            }
        }
    }
}

@Composable
fun RecursoCard(recurso: Recurso, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (recurso.imagenUrl != null) {
                AsyncImage(
                    model = recurso.imagenUrl,
                    contentDescription = recurso.titulo,
                    modifier = Modifier.size(80.dp).padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = recurso.titulo, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = recurso.categoria.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            AssistChip(onClick = {}, label = { Text(recurso.estado.name) })
        }
    }
}