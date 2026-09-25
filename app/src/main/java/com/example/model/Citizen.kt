package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citizens")
data class Citizen(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val kkNumber: String,
    val nikHead: String,
    val fullName: String,
    val category: CitizenCategory,
    val phone: String,
    val email: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val addressStreet: String,
    val rtRw: String = "RT 03 / RW 07",
    val postalCode: String = "15412",
    val houseOwnership: String = "Milik Sendiri", // Milik Sendiri, Sewa/Kontrak, Menumpang, Rumah Dinas
    val settledSince: String = "2020", // misal: "Januari 2021"
    val familyMembersCount: Int = 1,
    val familyMembersNames: String = "", // misal: "1. Budi (Kepala), 2. Siti (Istri), 3. Doni (Anak)"
    val occupation: String = "Karyawan Swasta",
    val notes: String = "",
    val feeStatus: String = "Lunas", // Lunas, Belum Lunas, Bebas Iuran
    val lastBlockHash: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
