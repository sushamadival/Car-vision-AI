package com.example.carvisionai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carvisionai.ui.HomeScreen
import com.example.carvisionai.ui.CameraScreen
import com.example.carvisionai.ui.ResultScreen
import com.example.carvisionai.ui.theme.CarVisionAITheme

class MainActivity : ComponentActivity() {

    private val viewModel: CarDetectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarVisionAITheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CarVisionApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CarVisionApp(viewModel: CarDetectionViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                uiState = uiState,
                onTakePhotoClick = {
                    navController.navigate("camera")
                },
                onImageSelected = { uri ->
                    viewModel.onImageSelected(uri)
                },
                onAnalyzeClick = {
                    viewModel.analyzeCarImage(
                        onSuccess = {
                            navController.navigate("result")
                        }
                    )
                },
                onClearImage = {
                    viewModel.clearSelectedImage()
                }
            )
        }

        composable("camera") {
            CameraScreen(
                onImageCaptured = { uri ->
                    viewModel.onImageSelected(uri)
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("result") {
            ResultScreen(
                uiState = uiState,
                onAnalyzeAnotherClick = {
                    viewModel.resetState()
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}
