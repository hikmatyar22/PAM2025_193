package com.example.tugasakhir.view.riwayat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R
import com.example.tugasakhir.viewmodel.DetailRiwayatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailRiwayat(
    viewModel: DetailRiwayatViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.detailUiState.collectAsState()
    val primaryColor = colorResource(id = R.color.primary_blue)
    val backgroundColor = colorResource(id = R.color.background)
    val successColor = Color(0xFF2E7D32)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Riwayat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(padding)
        ) {
            if (viewModel.isLoading && uiState == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else if (viewModel.errorMessage != null && uiState == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            } else {
                uiState?.let { data ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // --- SUCCESS ICON ---
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(12.dp, CircleShape)
                                .background(Color.White, CircleShape)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = successColor
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- STATUS BADGE ---
                        Surface(
                            color = successColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "PEMINJAMAN SELESAI",
                                color = successColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // --- INFO CARD ---
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(28.dp)),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = data.nama_peminjam,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    thickness = 0.5.dp,
                                    color = Color.LightGray.copy(alpha = 0.5f)
                                )

                                DetailRow(label = "Buku Dipinjam", value = data.judul_buku ?: "-", icon = Icons.Default.MenuBook, color = primaryColor)
                                DetailRow(label = "Penulis", value = data.penulis ?: "-", icon = Icons.Default.Person, color = Color.Gray)
                                DetailRow(label = "Penerbit", value = data.penerbit ?: "-", icon = Icons.Default.Business, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- TIMELINE CARD ---
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(28.dp)),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text("Timeline Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                TimelineItem(label = "Tanggal Pinjam", value = data.tanggal_pinjam, icon = Icons.Default.CalendarToday, isLast = false)
                                TimelineItem(label = "Jatuh Tempo", value = data.tanggal_jatuh_tempo, icon = Icons.Default.EventBusy, isLast = false)
                                TimelineItem(label = "Dikembalikan", value = data.tanggal_kembali, icon = Icons.Default.CheckCircle, isLast = true, color = successColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = color)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        }
    }
}

@Composable
fun TimelineItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isLast: Boolean, color: Color = Color.Gray) {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = color)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(30.dp)
                        .background(Color.LightGray.copy(alpha = 0.5f))
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (color == Color.Gray) Color.Black else color)
            if (!isLast) Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
