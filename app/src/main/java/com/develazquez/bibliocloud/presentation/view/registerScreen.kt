package com.develazquez.bibliocloud.presentation.view // Paquete actualizado

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.develazquez.bibliocloud.R // R de tu amigo
import com.develazquez.bibliocloud.presentation.viewmodel.RegisterViewModel // ViewModel actualizado

@Composable
fun RegisterScreen(viewModel: RegisterViewModel, alVolverAlLogin: () -> Unit) {
    val state = viewModel.state
    val brownDark = Color(0xFF4E342E)
    val brownLight = Color(0xFF8D6E63)

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        // Encabezado con diseño curvo
        Box(modifier = Modifier.fillMaxWidth().height(230.dp)) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(bottomStart = 100.dp, bottomEnd = 100.dp))
                .background(Brush.verticalGradient(listOf(brownDark, brownLight))))

            IconButton(onClick = alVolverAlLogin, modifier = Modifier.padding(16.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }

            Surface(modifier = Modifier.size(120.dp).align(Alignment.BottomCenter),
                shape = CircleShape, color = Color.White, shadowElevation = 8.dp) {
                Image(painter = painterResource(id = R.drawable.logo),
                    contentDescription = null, modifier = Modifier.padding(8.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Registrarse", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            // Mostramos error de la API si existe
            state.mensajeError?.let {
                Text(it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            }

            RegistroField("Nombre", state.nombre) { viewModel.onNombreChanged(it) }

            // CAMBIO: Ahora pedimos Correo pero se guarda en 'telefono' como quedamos
            RegistroField("Correo electrónico", state.telefono) { viewModel.onEmailChanged(it) }

            RegistroField("Contraseña", state.clave, true) { viewModel.onClaveChanged(it) }

            Spacer(modifier = Modifier.height(30.dp))

            if (state.estaCargando) {
                CircularProgressIndicator(color = brownLight)
            } else {
                Button(onClick = { viewModel.registrarse() },
                    modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(25.dp))
                        .background(Brush.horizontalGradient(listOf(brownLight, brownDark))),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                    Text("Regístrate", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun RegistroField(label: String, value: String, esPass: Boolean = false, onValue: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row { Text("*", color = Color.Red); Text(label, color = Color.Gray, fontSize = 14.sp) }
        OutlinedTextField(
            value = value,
            onValueChange = onValue,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if(esPass) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None
        )
    }
}