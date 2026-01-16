package com.example.tugasakhir.view.uicontroller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tugasakhir.view.*
import com.example.tugasakhir.view.halamanpeminjaman.HalamanDetailPeminjaman
import com.example.tugasakhir.view.halamanpeminjaman.HalamanTambahPeminjaman
import com.example.tugasakhir.view.riwayat.HalamanDetailRiwayat
import com.example.tugasakhir.view.route.* import com.example.tugasakhir.viewmodel.*
import com.example.tugasakhir.viewmodel.provider.PenyediaViewModel

@Composable
fun PetaNavigasi(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiSelamatDatang.route,
        modifier = modifier
    ) {
        // --- WELCOME ---
        composable(DestinasiSelamatDatang.route) {
            HalamanSelamatDatang(
                onLoginClick = { navController.navigate(DestinasiLogin.route) },
                onRegisterClick = { navController.navigate(DestinasiRegister.route) }
            )
        }

        // --- AUTHENTICATION ---
        composable(DestinasiLogin.route) {
            val viewModel: EntryViewModel = viewModel(factory = PenyediaViewModel())
            HalamanEntry(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(DestinasiHome.route) {
                        popUpTo(DestinasiSelamatDatang.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(DestinasiRegister.route)
                }
            )
        }

        composable(DestinasiRegister.route) {
            val viewModel: RegisterViewModel = viewModel(factory = PenyediaViewModel())
            HalamanRegister(
                viewModel = viewModel,
                onRegisterSuccess = { navController.navigate(DestinasiLogin.route) }, // Go to login after register
                onLoginClick = { navController.popBackStack() }
            )
        }

        // --- HOME ---
        composable(DestinasiHome.route) {
            val viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel())
            HalamanHome(
                viewModel = viewModel,
                onKelolaBuku = { navController.navigate(DestinasiBuku.route) },
                onPeminjaman = { navController.navigate(DestinasiPeminjaman.route) },
                onRiwayat = { navController.navigate(DestinasiRiwayat.route) },
                onLogout = {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(DestinasiHome.route) { inclusive = true }
                    }
                }
            )
        }

        // --- MANAJEMEN BUKU ---
        composable(DestinasiBuku.route) {
            val viewModel: BukuViewModel = viewModel(factory = PenyediaViewModel())
            HalamanBuku(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onTambahBuku = { navController.navigate(DestinasiTambahBuku.route) },
                onDetailBuku = { id ->
                    navController.navigate("${DestinasiDetailBuku.route}/$id")
                }
            )
        }

        composable(DestinasiTambahBuku.route) {
            val viewModel: TambahBukuViewModel = viewModel(factory = PenyediaViewModel())
            HalamanTambahBuku(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DestinasiDetailBuku.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailBuku.idBuku) { type = NavType.IntType })
        ) {
            // PERBAIKAN: Tidak perlu SavedStateHandle manual
            val viewModel: DetailBukuViewModel = viewModel(factory = PenyediaViewModel())
            HalamanDetailBuku(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- MANAJEMEN PEMINJAMAN ---
        composable(DestinasiPeminjaman.route) {
            val viewModel: PeminjamanViewModel = viewModel(factory = PenyediaViewModel())
            HalamanPeminjaman(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onTambahPeminjaman = { navController.navigate(DestinasiTambahPeminjaman.route) },
                onDetailPeminjaman = { id ->
                    navController.navigate("${DestinasiDetailPeminjaman.route}/$id")
                }
            )
        }

        composable(DestinasiTambahPeminjaman.route) {
            val viewModel: TambahPeminjamanViewModel = viewModel(factory = PenyediaViewModel())
            HalamanTambahPeminjaman(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DestinasiDetailPeminjaman.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailPeminjaman.idPeminjaman) { type = NavType.IntType })
        ) {
            // PERBAIKAN: Cukup panggil PenyediaViewModel()
            val viewModel: DetailPeminjamanViewModel = viewModel(factory = PenyediaViewModel())
            HalamanDetailPeminjaman(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- RIWAYAT ---
        composable(DestinasiRiwayat.route) {
            val viewModel: RiwayatViewModel = viewModel(factory = PenyediaViewModel())
            HalamanRiwayat(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onDetailRiwayat = { id ->
                    navController.navigate("${DestinasiDetailRiwayat.route}/$id")
                }
            )
        }

        composable(
            route = DestinasiDetailRiwayat.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailRiwayat.idRiwayat) { type = NavType.IntType })
        ) {
            val viewModel: DetailRiwayatViewModel = viewModel(factory = PenyediaViewModel())
            HalamanDetailRiwayat(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}