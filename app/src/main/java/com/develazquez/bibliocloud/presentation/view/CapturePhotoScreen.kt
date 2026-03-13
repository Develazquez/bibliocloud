package com.develazquez.bibliocloud.presentation.view

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.develazquez.bibliocloud.core.database.HardwareUtils
import com.develazquez.bibliocloud.presentation.viewmodel.CapturePhotoViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapturePhotoScreen(
    navController: NavController,
    viewModel: CapturePhotoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isPhotoCaptured by viewModel.isPhotoCaptured.collectAsState()
    val capturedPhotoUri by viewModel.capturedPhotoUri.collectAsState()

    var hasCameraPermission by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(capturedPhotoUri) {
        capturedPhotoUri?.let { uri ->
            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartir foto"))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotografiar portada") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetState()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!hasCameraPermission) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Se necesita permiso de cámara para fotografiar portadas")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                            Text("Conceder permiso")
                        }
                    }
                }
            } else if (isPhotoCaptured && capturedPhotoUri != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = capturedPhotoUri,
                        contentDescription = "Portada capturada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Foto capturada exitosamente",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "URI: ${capturedPhotoUri}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resetState() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tomar Otra")
                        }

                        Button(
                            onClick = {
                                HardwareUtils.vibrateSuccess(context)
                                navController.navigateUp()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Aceptar")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = {
                            capturedPhotoUri?.let { uri ->
                                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "image/jpeg"
                                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartir foto"))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Compartir foto")
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)

                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()

                                val preview = Preview.Builder().build().also {
                                    it.surfaceProvider = previewView.surfaceProvider
                                }

                                val imgCapture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()
                                imageCapture = imgCapture

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imgCapture
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(ctx, "Error al iniciar cámara", Toast.LENGTH_SHORT).show()
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Botón de captura
                Button(
                    onClick = {
                        imageCapture?.let { capture ->
                            takePhoto(context, capture) { uri ->
                                viewModel.onPhotoCaptured(uri)
                                HardwareUtils.vibrateOnAction(context)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Capturar foto", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onPhotoCaptured: (Uri) -> Unit
) {
    val photoFile = File(
        context.filesDir,
        "portada_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val authority = "${context.packageName}.fileprovider"
                val savedUri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    authority,
                    photoFile
                )
                onPhotoCaptured(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Error al capturar foto: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}
