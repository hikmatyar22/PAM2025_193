package com.example.tugasakhir.view.route

import com.example.tugasakhir.R

/**
 * Interface dasar untuk semua rute navigasi
 */
interface DestinasiNavigasi {
    val route: String
    val titleRes: Int
}

object DestinasiLogin : DestinasiNavigasi {
    override val route = "login"
    override val titleRes = R.string.login_title
}

object DestinasiRegister : DestinasiNavigasi {
    override val route = "register"
    override val titleRes = R.string.btn_register
}

object DestinasiSelamatDatang : DestinasiNavigasi {
    override val route = "welcome"
    override val titleRes = R.string.app_name
}

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = R.string.app_name
}

object DestinasiBuku : DestinasiNavigasi {
    override val route = "buku"
    override val titleRes = R.string.label_judul // Atau title_buku jika ada
}

object DestinasiTambahBuku : DestinasiNavigasi {
    override val route = "tambah_buku"
    override val titleRes = R.string.title_add_book
}

object DestinasiDetailBuku : DestinasiNavigasi {
    override val route = "detail_buku"
    override val titleRes = R.string.label_judul
    const val idBuku = "id_buku"
    val routeWithArgs = "$route/{$idBuku}"
}

object DestinasiPeminjaman : DestinasiNavigasi {
    override val route = "peminjaman"
    override val titleRes = R.string.title_detail_peminjaman
}

object DestinasiTambahPeminjaman : DestinasiNavigasi {
    override val route = "tambah_peminjaman"
    override val titleRes = R.string.title_add_loan
}

object DestinasiDetailPeminjaman : DestinasiNavigasi {
    override val route = "detail_peminjaman"
    override val titleRes = R.string.title_detail_peminjaman
    const val idPeminjaman = "id_peminjaman"
    val routeWithArgs = "$route/{$idPeminjaman}"
}

object DestinasiRiwayat : DestinasiNavigasi {
    override val route = "riwayat"
    override val titleRes = R.string.title_riwayat
}

object DestinasiDetailRiwayat : DestinasiNavigasi {
    override val route = "detail_riwayat"
    override val titleRes = R.string.title_riwayat
    const val idRiwayat = "id_riwayat"
    val routeWithArgs = "$route/{$idRiwayat}"
}