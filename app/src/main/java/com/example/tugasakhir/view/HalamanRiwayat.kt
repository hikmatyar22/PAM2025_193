package com.example.tugasakhir.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R
// Tampilkan daftar riwayat peminjaman
import com.example.tugasakhir.viewmodel.RiwayatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRiwayat(
    viewModel: RiwayatViewModel,
    onBack: () -> Unit,
    onDetailRiwayat: (Int) -> Unit
) {
    // Observasi StateFlow dari ViewModel
    val daftarRiwayat by viewModel.riwayatUiState.collectAsState()
    val query by viewModel.query.collectAsState()

    val backgroundColor = colorResource(id = R.color.background)
    val primaryColor = colorResource(id = R.color.primary_blue)
    val successColor = Color(0xFF2E7D32) // Warna hijau premium

    Scaffold(containerColor = backgroundColor) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // --- HEADER SECTION ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "Riwayat Peminjaman",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- SEARCH BAR ---
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.updateQuery(it) },
                        placeholder = {
                            Text("Cari nama atau judul buku...", color = Color.Gray)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = primaryColor
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = primaryColor) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateQuery("") }) {
                                    Icon(Icons.Default.Close, null, tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- PULL TO REFRESH BOX ---
            PullToRefreshBox(
                isRefreshing = viewModel.isLoading,
                onRefresh = { viewModel.refresh() }
            ) {
                // --- LIST CONTENT ---
                if (viewModel.isLoading && daftarRiwayat.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                } else if (viewModel.errorMessage != null && daftarRiwayat.isEmpty()) {
                    val errorMsg = viewModel.errorMessage
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Icon(Icons.Default.ErrorOutline, null, Modifier.size(64.dp), MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Text(text = errorMsg ?: "", color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                } else if (daftarRiwayat.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(Modifier.height(8.dp))
                            Text(text = "Tidak ada riwayat ditemukan", color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = daftarRiwayat,
                            key = { it.id_riwayat }
                        ) { item ->
                            RiwayatPremiumCard(
                                riwayat = item,
                                primaryColor = primaryColor,
                                successColor = successColor,
                                modifier = Modifier.animateItem(),
                                onClick = { onDetailRiwayat(item.id_riwayat) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiwayatPremiumCard(
    riwayat: com.example.tugasakhir.data.modeldata.Riwayat,
    primaryColor: Color,
    successColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(successColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AssignmentTurnedIn, null, tint = successColor, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = riwayat.nama_peminjam,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = riwayat.judul_buku ?: "Buku tidak ditemukan",
                        fontSize = 14.sp,
                        color = primaryColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    LabelValueRow(label = "Tgl Pinjam", value = riwayat.tanggal_pinjam, icon = Icons.Default.CalendarToday)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Status Selesai",
                        fontSize = 10.sp,
                        color = successColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(successColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = riwayat.tanggal_kembali,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = successColor
                    )
                }
            }
        }
    }
}

@Composable
fun LabelValueRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column {
        Text(text = label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(12.dp), tint = Color.Gray)
            Spacer(Modifier.width(4.dp))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        }
    }
}
