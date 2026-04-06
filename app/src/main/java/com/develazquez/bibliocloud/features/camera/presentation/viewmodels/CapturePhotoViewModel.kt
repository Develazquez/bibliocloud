package com.develazquez.bibliocloud.features.camera.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CapturePhotoViewModel @Inject constructor() : ViewModel() {

    private val _capturedPhotoUri = MutableStateFlow<Uri?>(null)
    val capturedPhotoUri: StateFlow<Uri?> = _capturedPhotoUri.asStateFlow()

    private val _isPhotoCaptured = MutableStateFlow(false)
    val isPhotoCaptured: StateFlow<Boolean> = _isPhotoCaptured.asStateFlow()

    fun onPhotoCaptured(uri: Uri) {
        _capturedPhotoUri.value = uri
        _isPhotoCaptured.value = true
    }

    fun resetState() {
        _capturedPhotoUri.value = null
        _isPhotoCaptured.value = false
    }
}

