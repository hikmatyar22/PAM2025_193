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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugasakhir.R
import com.example.tugasakhir.data.modeldata.Peminjaman
import com.example.tugasakhir.viewmodel.PeminjamanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPeminjaman(
    viewModel: PeminjamanViewModel,
    onBack: () -> Unit,
    onTambahPeminjaman: () -> Unit,
    onDetailPeminjaman: (Int) -> Unit
) {
    val daftarPeminjaman by viewModel.daftarPeminjaman.collectAsState()
    val query by viewModel.query.collectAsState()

    val primaryColor = colorResource(id = R.color.primary_blue)
    val backgroundColor = colorResource(id = R.color.background)

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTambahPeminjaman,
                shape = CircleShape,
                containerColor = primaryColor,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.desc_tambah_peminjaman)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- HEADER MODERN ---
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
                                stringResource(R.string.desc_kembali),
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.menu_manage_loans),
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
                            Text(stringResource(R.string.hint_search_peminjam), color = Color.Gray)
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
                // --- LIST DATA ---
                if (viewModel.isLoading && daftarPeminjaman.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                } else if (viewModel.errorMessage != null && daftarPeminjaman.isEmpty()) {
                    val errorMsg = viewModel.errorMessage // Capture for smart cast
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
                } else if (daftarPeminjaman.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Inbox, null, Modifier.size(64.dp), Color.LightGray)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.empty_peminjaman_msg),
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = daftarPeminjaman,
                            key = { it.id_peminjaman }
                        ) { item ->
                            ItemPeminjaman(
                                peminjaman = item,
                                onClick = { onDetailPeminjaman(item.id_peminjaman) },
                                primaryColor = primaryColor,
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemPeminjaman(peminjaman: Peminjaman, onClick: () -> Unit, primaryColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon User/Peminjam
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(28.dp), tint = primaryColor)
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = peminjaman.nama_peminjam,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colorResource(R.color.text_primary)
                )

                // Menampilkan Judul Buku yang dipinjam
                Text(
                    text = peminjaman.judul_buku ?: "",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(Modifier.height(4.dp))

                // Menampilkan Jatuh Tempo dengan warna kontras dan Nilai Tanggalnya
                Text(
                    text = "${stringResource(R.string.label_jatuh_tempo)}: ${peminjaman.tanggal_jatuh_tempo}",
                    color = Color(0xFFD32F2F), // Red shade
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.desc_detail),
                tint = Color.LightGray
            )
        }
    }
}