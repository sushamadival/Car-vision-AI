package com.example.carvisionai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.carvisionai.models.CarUiState

@Composable
fun ResultScreen(
    uiState: CarUiState,
    onAnalyzeAnotherClick: () -> Unit
) {
    val successState = uiState as? CarUiState.Success
    val result = successState?.result
    val imageUri = successState?.imageUri

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Result Title Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp, bottom = 20.dp)
        ) {
            Text(
                text = "🚗 CAR DETECTION RESULT",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = Color(0xFF60A5FA)
            )
        }

        // Captured/Selected Car Image
        if (imageUri != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Analyzed Car",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (result != null) {
            // Main Specifications Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Make Field
                    DetectionRow(
                        label = "Make",
                        value = result.make,
                        confidence = result.makeConfidence
                    )

                    HorizontalDivider(
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    // Model Field
                    DetectionRow(
                        label = "Model",
                        value = result.model,
                        confidence = result.modelConfidence,
                        isWarning = !result.isModelConfident
                    )

                    HorizontalDivider(
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    // Colour Field
                    DetectionRow(
                        label = "Colour",
                        value = result.colour,
                        confidence = result.colourConfidence
                    )

                    HorizontalDivider(
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(vertical = 14.dp)
                    )

                    // Overall Confidence Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Confidence",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${result.overallConfidence}%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Analyze Another Image CTA Button
        Button(
            onClick = onAnalyzeAnotherClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Analyze Another Image",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DetectionRow(
    label: String,
    value: String,
    confidence: Int,
    isWarning: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWarning) Color(0xFFFBBF24) else Color.White
                )
            }

            Text(
                text = "$confidence%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (confidence >= 80) Color(0xFF34D399) else Color(0xFFF59E0B)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { confidence / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (isWarning) Color(0xFFF59E0B) else Color(0xFF3B82F6),
            trackColor = Color(0xFF1E293B)
        )
    }
}
