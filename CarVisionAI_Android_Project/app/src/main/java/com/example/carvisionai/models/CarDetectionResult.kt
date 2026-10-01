package com.example.carvisionai.models

import android.net.Uri

data class CarDetectionResult(
    val make: String,
    val model: String,
    val colour: String,
    val makeConfidence: Int,
    val modelConfidence: Int,
    val colourConfidence: Int,
    val overallConfidence: Int,
    val isCar: Boolean = true,
    val isModelConfident: Boolean = true,
    val bodyType: String? = null,
    val estimatedYear: String? = null,
    val distinctiveFeatures: List<String> = emptyList(),
    val quickFact: String? = null,
    val errorMessage: String? = null
)

sealed interface CarUiState {
    data object Idle : CarUiState
    data class ImageSelected(val imageUri: Uri) : CarUiState
    data class Analyzing(val imageUri: Uri) : CarUiState
    data class Success(val imageUri: Uri, val result: CarDetectionResult) : CarUiState
    data class Error(val message: String, val imageUri: Uri? = null) : CarUiState
}
