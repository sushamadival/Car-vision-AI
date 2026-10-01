package com.example.carvisionai

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.carvisionai.models.CarUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class CarDetectionViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = AIService()

    private val _uiState = MutableStateFlow<CarUiState>(CarUiState.Idle)
    val uiState: StateFlow<CarUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri) {
        _uiState.value = CarUiState.ImageSelected(uri)
    }

    fun clearSelectedImage() {
        _uiState.value = CarUiState.Idle
    }

    fun resetState() {
        _uiState.value = CarUiState.Idle
    }

    fun analyzeCarImage(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val imageUri = when (currentState) {
            is CarUiState.ImageSelected -> currentState.imageUri
            is CarUiState.Error -> currentState.imageUri
            else -> null
        }

        if (imageUri == null) {
            _uiState.value = CarUiState.Error("No image selected. Please take a photo or select an image from your gallery.")
            return
        }

        _uiState.value = CarUiState.Analyzing(imageUri)

        viewModelScope.launch {
            try {
                val bitmap = decodeSampledBitmapFromUri(imageUri, 1024, 1024)
                    ?: throw IllegalArgumentException("Could not load image file.")

                val result = aiService.analyzeCarBitmap(bitmap)

                if (!result.isCar && result.errorMessage != null) {
                    _uiState.value = CarUiState.Error(result.errorMessage, imageUri)
                } else {
                    _uiState.value = CarUiState.Success(imageUri, result)
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.value = CarUiState.Error(
                    message = e.localizedMessage ?: "Failed to analyze car image. Please check your internet connection.",
                    imageUri = imageUri
                )
            }
        }
    }

    private suspend fun decodeSampledBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                var input: InputStream? = context.contentResolver.openInputStream(uri)

                // First decode with inJustDecodeBounds=true to check dimensions
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(input, null, options)
                input?.close()

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
                options.inJustDecodeBounds = false

                // Decode bitmap with inSampleSize set
                input = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input, null, options)
                input?.close()
                bitmap
            } catch (e: Exception) {
                null
            }
        }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
