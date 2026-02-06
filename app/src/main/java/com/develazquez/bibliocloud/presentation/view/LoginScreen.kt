package com.develazquez.bibliocloud.presentation.view // Paquete actualizado

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.develazquez.bibliocloud.presentation.viewmodel.LoginViewModel // ViewModel actualizado

@Composable
fun LoginScreen(viewModel: LoginViewModel, onIrARegistro: () -> Unit) {
    val state = viewModel.state
    val BrownDark = Color(0xFF4E342E)
    val BrownLight = Color(0xFF8D6E63)

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(bottomStart = 100.dp, bottomEnd = 100.dp))
                .background(Brush.verticalGradient(listOf(BrownDark, BrownLight))))

            Surface(modifier = Modifier.size(150.dp).align(Alignment.BottomCenter),
                shape = CircleShape, color = Color.White, shadowElevation = 8.dp) {
                Image(painter = painterResource(id = R.drawable.logo),
                    contentDescription = null, modifier = Modifier.padding(10.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Iniciar sesión", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
            Spacer(modifier = Modifier.height(24.dp))

            // Mostramos errores de la API (ej: "Credenciales inválidas")
            state.errorMessage?.let {
                Text(it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Usamos 'email' pero mantenemos tu etiqueta de 'Número telefónico' para tu diseño
            LoginField("Número telefónico / Email", state.email) { viewModel.onEmailChanged(it) }
            LoginField("Contraseña", state.password, true) { viewModel.onPasswordChanged(it) }

            Spacer(modifier = Modifier.height(40.dp))

            // Si está cargando, mostramos el progreso, si no, el botón
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = BrownLight
                )
            } else {
                Button(onClick = { viewModel.onLoginClick() },
                    modifier = Modifier.fillMaxWidth().height(55.dp).clip(RoundedCornerShape(25.dp))
                        .background(Brush.horizontalGradient(listOf(BrownLight, BrownDark))),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                    Text("Iniciar sesión", fontSize = 18.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("¿Aún no tienes cuenta? ", color = Color.Gray)
                Text("Regístrate", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onIrARegistro() })
            }
        }
    }
}

@Composable
fun LoginField(label: String, value: String, esPass: Boolean = false, onValue: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row { Text("*", color = Color.Red); Text(label, color = Color.Gray) }
        OutlinedTextField(value = value, onValueChange = onValue, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), visualTransformation = if(esPass) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None)
    }
}