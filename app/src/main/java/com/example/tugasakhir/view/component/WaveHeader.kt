package com.example.tugasakhir.view.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaveHeader(
    modifier: Modifier = Modifier,
    height: Dp = 250.dp,
    primaryColor: Color = Color(0xFF3B5BDB),
    secondaryColor: Color? = null
) {
    val colorToUse = secondaryColor ?: primaryColor.copy(alpha = 0.8f)
    
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val width = size.width
        val heightPx = size.height

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, heightPx * 0.75f)
            cubicTo(
                width * 0.25f, heightPx, 
                width * 0.75f, heightPx * 0.5f, 
                width, heightPx * 0.8f
            )
            lineTo(width, 0f)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor, colorToUse)
            )
        )
        
        // Optional: Add some circles or decorations if needed to match the reference exactly
        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
            radius = 100f,
            center = Offset(width * 0.2f, heightPx * 0.3f)
        )
         drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = 200f,
            center = Offset(width * 0.85f, heightPx * 0.6f)
        )
    }
}
