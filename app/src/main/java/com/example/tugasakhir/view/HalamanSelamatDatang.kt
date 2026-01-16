package com.example.tugasakhir.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MenuBook
import com.example.tugasakhir.R
import com.example.tugasakhir.view.route.DestinasiLogin
import com.example.tugasakhir.view.route.DestinasiRegister

@Composable
fun HalamanSelamatDatang(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val primaryColor = colorResource(id = R.color.primary_blue)
    
    // Gradient Background for "Menarik" feel
    val backgroundBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            primaryColor,
            primaryColor.copy(alpha = 0.8f) // Slightly lighter/darker at bottom
        )
    )

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundBrush)
        ) {
            // Background Decoration (Subtle Circles)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // Outer glow
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = width * 0.8f,
                    center = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.4f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = width * 1.1f,
                    center = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.4f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Section: Title & Description
                Column(
                    modifier = Modifier.padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.login_title),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.app_title), 
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Akses ribuan buku dan jurnal digital\ndengan aman dan mudah.",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }

                // Middle Section: Main Illustration (Large Vector/Image)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 40.dp)
                ) {
                    // Using a large vector icon for cleanliness "another photo" interpretation
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.LocalLibrary,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(180.dp)
                    )
                }

                // Bottom Section: Buttons
                Column(
                    modifier = Modifier.padding(bottom = 50.dp, top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // LOGIN Button (Filled White)
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = primaryColor
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.btn_login).uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // REGISTER Button (Outlined White)
                    OutlinedButton(
                        onClick = onRegisterClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_register).uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
