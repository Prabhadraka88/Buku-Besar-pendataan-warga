package com.example.model

import androidx.compose.ui.graphics.Color

enum class CitizenCategory(
    val title: String,
    val shortLabel: String,
    val description: String,
    val lightContainerColor: Long = 0xFFDCFCE7,
    val lightContentColor: Long = 0xFF15803D,
    val darkContainerColor: Long = 0xFF064E3B,
    val darkContentColor: Long = 0xFF6EE7B7
) {
    WARGA_ASLI(
        title = "Warga Asli (Tetap)",
        shortLabel = "Warga Asli",
        description = "Warga dengan KTP & KK beralamat tetap di RT setempat dengan kepemilikan hunian sendiri.",
        lightContainerColor = 0xFFDCFCE7, // Emerald
        lightContentColor = 0xFF15803D,
        darkContainerColor = 0xFF064E3B,
        darkContentColor = 0xFF6EE7B7
    ),
    WARGA_PENDATANG(
        title = "Warga Pendatang (Domisili)",
        shortLabel = "Pendatang",
        description = "Warga dengan KTP luar daerah yang tinggal menetap sementara dengan surat domisili RT/RW.",
        lightContainerColor = 0xFFE0F2FE, // Sky
        lightContentColor = 0xFF0369A1,
        darkContainerColor = 0xFF082F49,
        darkContentColor = 0xFF7DD3FC
    ),
    WARGA_NGONTRAK(
        title = "Warga Ngontrak (Sewa/Kost)",
        shortLabel = "Ngontrak",
        description = "Warga yang menyewa rumah/kontrakan/kost dengan masa sewa berkala di wilayah RT.",
        lightContainerColor = 0xFFFEF3C7, // Amber
        lightContentColor = 0xFFB45309,
        darkContainerColor = 0xFF451A03,
        darkContentColor = 0xFFFCD34D
    );

    companion object {
        fun fromString(value: String): CitizenCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: WARGA_ASLI
        }
    }
}
