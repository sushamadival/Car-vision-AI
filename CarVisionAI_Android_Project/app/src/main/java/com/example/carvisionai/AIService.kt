package com.example.carvisionai

import android.graphics.Bitmap
import com.example.carvisionai.models.CarDetectionResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AIService {

    // Retrieve API key securely injected via BuildConfig (from local.properties)
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3.8-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            temperature = 0.2f
        },
        systemInstruction = content {
            text(
                """
                You are CarVision AI, an automotive computer vision classifier.
                Analyze the provided image and output a valid JSON object with:
                - isCar: boolean (true if an automobile/passenger vehicle is visible)
                - make: string (e.g. BMW, Audi, Toyota, Honda, Mercedes-Benz, Porsche)
                - model: string (e.g. 3 Series, A4, Camry, Civic, 911). CRITICAL: If model confidence < 70%, set model to EXACTLY "Model could not be identified with high confidence." Do not invent or guess.
                - colour: string (e.g. Alpine White, Black, Red, Blue, Silver)
                - makeConfidence: integer (0 to 100)
                - modelConfidence: integer (0 to 100)
                - colourConfidence: integer (0 to 100)
                - overallConfidence: integer (0 to 100)
                - isModelConfident: boolean
                - bodyType: string (Sedan, Coupe, SUV, Wagon, etc.)
                - estimatedYear: string (e.g. 2021-2024)
                - errorMessage: string (if not a car, state "No car detected in the image.")
                """
            )
        }
    )

    suspend fun analyzeCarBitmap(bitmap: Bitmap): CarDetectionResult = withContext(Dispatchers.IO) {
        try {
            val inputContent = content {
                image(bitmap)
                text("Analyze this vehicle image and return the JSON automotive specification.")
            }

            val response = generativeModel.generateContent(inputContent)
            val jsonText = response.text ?: throw IllegalStateException("Empty response from AI")

            parseJsonResponse(jsonText)
        } catch (e: Exception) {
            throw Exception("AI Analysis Failed: ${e.localizedMessage ?: "Unknown error"}", e)
        }
    }

    private fun parseJsonResponse(jsonString: String): CarDetectionResult {
        val root = JSONObject(jsonString)
        val isCar = root.optBoolean("isCar", true)

        if (!isCar) {
            val errorMsg = root.optString("errorMessage", "No vehicle detected in the selected image.")
            return CarDetectionResult(
                make = "Unknown",
                model = "Model could not be identified with high confidence.",
                colour = "Unknown",
                makeConfidence = 0,
                modelConfidence = 0,
                colourConfidence = 0,
                overallConfidence = 0,
                isCar = false,
                isModelConfident = false,
                errorMessage = errorMsg
            )
        }

        val make = root.optString("make", "Unknown")
        val modelConfidence = root.optInt("modelConfidence", 0)
        var model = root.optString("model", "Model could not be identified with high confidence.")
        val isModelConfident = root.optBoolean("isModelConfident", modelConfidence >= 70)

        if (!isModelConfident || modelConfidence < 70) {
            model = "Model could not be identified with high confidence."
        }

        return CarDetectionResult(
            make = make,
            model = model,
            colour = root.optString("colour", "Unknown"),
            makeConfidence = root.optInt("makeConfidence", 85),
            modelConfidence = modelConfidence,
            colourConfidence = root.optInt("colourConfidence", 90),
            overallConfidence = root.optInt("overallConfidence", 88),
            isCar = true,
            isModelConfident = isModelConfident,
            bodyType = root.optString("bodyType", "Sedan"),
            estimatedYear = root.optString("estimatedYear", "Modern"),
            errorMessage = null
        )
    }
}
