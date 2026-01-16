package com.example.tugasakhir.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R
import com.example.tugasakhir.viewmodel.HomeViewModel

@Composable
fun HalamanHome(
    viewModel: HomeViewModel,
    onKelolaBuku: () -> Unit,
    onPeminjaman: () -> Unit,
    onRiwayat: () -> Unit,
    onLogout: () -> Unit
) {
    // Observasi state dari HomeViewModel
    val uiState by viewModel.homeUiState.collectAsState()

    val textColorPrimary = colorResource(id = R.color.text_primary)
    val backgroundColor = colorResource(id = R.color.background)
    val primaryColor = colorResource(id = R.color.primary_blue)
    val grayLight = colorResource(id = R.color.gray_light)
    val buttonDanger = colorResource(id = R.color.error_red)

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        }
        uiState.isError -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = buttonDanger
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Gagal memuat data",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColorPrimary
                )
                Text(
                    text = "Periksa koneksi internet atau server Anda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.refresh() },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Coba Lagi")
                }
            }
        }
        else -> {
            Scaffold(
                containerColor = backgroundColor
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // --- 1. HEADER SECTION ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(primaryColor, primaryColor.copy(alpha = 0.9f))
                                ),
                                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 40.dp, bottom = 48.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.welcome_greeting),
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = uiState.namaAdmin,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Surface(
                                    modifier = Modifier.size(54.dp),
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // --- QUICK STATS CARD ---
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(12.dp, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    QuickStatItem(
                                        icon = Icons.Default.Book,
                                        value = uiState.jumlahBuku.toString(),
                                        label = stringResource(R.string.stat_buku),
                                        iconColor = primaryColor
                                    )
                                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.LightGray.copy(alpha = 0.3f)))
                                    QuickStatItem(
                                        icon = Icons.Default.TrendingUp,
                                        value = uiState.jumlahDipinjam.toString(),
                                        label = stringResource(R.string.stat_dipinjam),
                                        iconColor = Color(0xFF3F51B5)
                                    )
                                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.LightGray.copy(alpha = 0.3f)))
                                    QuickStatItem(
                                        icon = Icons.Default.Check,
                                        value = uiState.jumlahSelesai.toString(),
                                        label = stringResource(R.string.stat_selesai),
                                        iconColor = Color(0xFF4361EE)
                                    )
                                }
                            }
                        }
                    }

                    // --- 2. MAIN MENU SECTION ---
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Column {
                            Text(
                                text = stringResource(R.string.menu_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColorPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Blue underline indicator
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(primaryColor, RoundedCornerShape(2.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        PremiumMenuCard(
                            title = stringResource(R.string.menu_manage_books),
                            subtitle = stringResource(R.string.menu_manage_books_sub),
                            icon = Icons.Default.Book,
                            onClick = onKelolaBuku,
                            accentColor = Color(0xFF4CAF50) // Green
                        )

                        PremiumMenuCard(
                            title = stringResource(R.string.menu_manage_loans),
                            subtitle = stringResource(R.string.menu_manage_loans_sub),
                            icon = Icons.Default.MenuBook,
                            onClick = onPeminjaman,
                            accentColor = Color(0xFF2196F3) // Blue
                        )

                        PremiumMenuCard(
                            title = stringResource(R.string.menu_history_title),
                            subtitle = stringResource(R.string.menu_history_sub_new),
                            icon = Icons.Default.History,
                            onClick = onRiwayat,
                            accentColor = Color(0xFFFF9800) // Orange
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- 3. LOGOUT BUTTON ---
                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonDanger),
                        ) {
                            Icon(Icons.Default.Logout, null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.btn_logout), 
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun QuickStatItem(icon: ImageVector, value: String, label: String, iconColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = iconColor.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = iconColor, 
                modifier = Modifier.padding(10.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PremiumMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() }
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    modifier = Modifier.size(30.dp), 
                    tint = accentColor
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle, 
                    fontSize = 13.sp, 
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.padding(8.dp).size(20.dp)
                )
            }
        }
    }
}