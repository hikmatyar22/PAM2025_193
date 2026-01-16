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
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.viewmodel.BukuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanBuku(
    viewModel: BukuViewModel,
    onBack: () -> Unit,
    onTambahBuku: () -> Unit,
    onDetailBuku: (Int) -> Unit
) {
    val daftarBuku by viewModel.daftarBuku.collectAsState()
    val query by viewModel.query.collectAsState()

    val backgroundColor = colorResource(id = R.color.background)
    val primaryColor = colorResource(id = R.color.primary_blue)
    val textColorPrimary = colorResource(id = R.color.text_primary)

    Scaffold(
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTambahBuku,
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.desc_tambah_buku),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.desc_kembali),
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.menu_manage_books),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- SEARCH BAR ---
                    OutlinedTextField(
                        value = query,
                        onValueChange = viewModel::updateQuery,
                        placeholder = {
                            Text(
                                stringResource(R.string.hint_search_buku),
                                color = Color.Gray
                            )
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
                // --- LIST BUKU ---
                if (viewModel.isLoading && daftarBuku.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                } else if (viewModel.errorMessage != null && daftarBuku.isEmpty()) {
                    val errorMsg = viewModel.errorMessage // Capture for smart cast
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                null,
                                Modifier.size(64.dp),
                                MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = errorMsg ?: "",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.refresh() },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                } else if (daftarBuku.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.MenuBook,
                                null,
                                Modifier.size(64.dp),
                                Color.LightGray
                            )
                            Text(stringResource(R.string.empty_buku_msg), color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = daftarBuku,
                            key = { it.id_buku }
                        ) { buku ->
                            BukuPremiumCard(
                                buku = buku,
                                primaryColor = primaryColor,
                                onClick = { onDetailBuku(buku.id_buku) },
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
fun BukuPremiumCard(
    buku: Buku,
    primaryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Buku dengan Background Kotak Lembut
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MenuBook,
                    null,
                    modifier = Modifier.size(28.dp),
                    tint = primaryColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buku.judul,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.text_primary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${buku.penulis} • ${buku.tahun_terbit}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Badge Stok menggunakan string resource label_stok
                Surface(
                    color = primaryColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${stringResource(R.string.label_stok)}: ${buku.jumlah_tersedia}/${buku.jumlah_stok}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.desc_detail_buku),
                tint = Color.LightGray
            )
        }
    }
}
