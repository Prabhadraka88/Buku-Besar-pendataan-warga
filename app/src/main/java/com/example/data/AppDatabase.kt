package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.blockchain.BlockchainEngine
import com.example.model.Block
import com.example.model.Citizen
import com.example.model.CitizenCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Citizen::class, Block::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun citizenDao(): CitizenDao
    abstract fun blockDao(): BlockDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rt_citizen_ledger_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialBlockchainAndCitizens(database)
                    }
                }
            }
        }

        suspend fun populateInitialBlockchainAndCitizens(database: AppDatabase) {
            val citizenDao = database.citizenDao()
            val blockDao = database.blockDao()

            // 1. Mine Genesis Block
            val genesisBlock = BlockchainEngine.createGenesisBlock()
            blockDao.insertBlock(genesisBlock)

            var lastHash = genesisBlock.hash
            var blockIndex = 1L

            // 2. Initial Citizen 1: Warga Asli
            val citizen1 = Citizen(
                id = 0,
                kkNumber = "3201011203850001",
                nikHead = "3201011505800002",
                fullName = "Bapak Joko Prasetyo",
                category = CitizenCategory.WARGA_ASLI,
                phone = "081234567890",
                email = "joko.prasetyo@email.com",
                emergencyContactName = "Ibu Siti Nurhaliza (Istri)",
                emergencyContactPhone = "081298765431",
                addressStreet = "Jl. Mawar No. 12",
                rtRw = "RT 03 / RW 07",
                postalCode = "15412",
                houseOwnership = "Milik Sendiri",
                settledSince = "2012",
                familyMembersCount = 4,
                familyMembersNames = "1. Joko Prasetyo (Kepala Keluarga)\n2. Siti Nurhaliza (Istri)\n3. Bagas Aditya (Anak)\n4. Nabila Putri (Anak)",
                occupation = "PNS / Guru",
                notes = "Koordinator ronda malam wilayah selatan RT 03.",
                feeStatus = "Lunas",
                lastBlockHash = ""
            )
            val block1 = BlockchainEngine.mineBlock(
                index = blockIndex++,
                previousHash = lastHash,
                actionType = "REGISTRASI_KK",
                citizenKk = citizen1.kkNumber,
                citizenName = citizen1.fullName,
                payloadSummary = "Pendaftaran KK Warga Asli: ${citizen1.fullName} - ${citizen1.addressStreet}",
                fullDataPayload = "{\"kk\":\"${citizen1.kkNumber}\",\"nama\":\"${citizen1.fullName}\",\"kategori\":\"WARGA_ASLI\",\"alamat\":\"${citizen1.addressStreet}\",\"jiwa\":4}",
                validatorNode = "Node-RT03-Ketua"
            )
            blockDao.insertBlock(block1)
            lastHash = block1.hash
            citizenDao.insertCitizen(citizen1.copy(lastBlockHash = block1.hash))

            // 3. Initial Citizen 2: Warga Pendatang
            val citizen2 = Citizen(
                id = 0,
                kkNumber = "3302154408900003",
                nikHead = "3302156209910004",
                fullName = "Ibu Rina Kusuma",
                category = CitizenCategory.WARGA_PENDATANG,
                phone = "081398765432",
                email = "rina.kusuma@tech.id",
                emergencyContactName = "Agus Santoso (Saudara)",
                emergencyContactPhone = "081765432109",
                addressStreet = "Jl. Melati Gang 2 No. 5B",
                rtRw = "RT 03 / RW 07",
                postalCode = "15412",
                houseOwnership = "Sewa/Kontrak",
                settledSince = "Maret 2023",
                familyMembersCount = 3,
                familyMembersNames = "1. Rina Kusuma (Kepala Keluarga)\n2. Hendra Wijaya (Suami)\n3. Kenzo Wijaya (Anak)",
                occupation = "Software Engineer",
                notes = "Pindahan dari Sleman, Yogyakarta. Surat domisili aktif s/d 2027.",
                feeStatus = "Lunas",
                lastBlockHash = ""
            )
            val block2 = BlockchainEngine.mineBlock(
                index = blockIndex++,
                previousHash = lastHash,
                actionType = "REGISTRASI_KK",
                citizenKk = citizen2.kkNumber,
                citizenName = citizen2.fullName,
                payloadSummary = "Pendaftaran KK Warga Pendatang: ${citizen2.fullName} (Asal Sleman)",
                fullDataPayload = "{\"kk\":\"${citizen2.kkNumber}\",\"nama\":\"${citizen2.fullName}\",\"kategori\":\"WARGA_PENDATANG\",\"alamat\":\"${citizen2.addressStreet}\",\"jiwa\":3}",
                validatorNode = "Node-RT03-Sekretaris"
            )
            blockDao.insertBlock(block2)
            lastHash = block2.hash
            citizenDao.insertCitizen(citizen2.copy(lastBlockHash = block2.hash))

            // 4. Initial Citizen 3: Warga Ngontrak
            val citizen3 = Citizen(
                id = 0,
                kkNumber = "3509121904940005",
                nikHead = "3509122501950006",
                fullName = "Mas Dimas Pratama",
                category = CitizenCategory.WARGA_NGONTRAK,
                phone = "085711223344",
                email = "dimas.pratama@gmail.com",
                emergencyContactName = "Bambang Pratama (Orang Tua)",
                emergencyContactPhone = "081512345678",
                addressStreet = "Jl. Anggrek Kontrakan Paviliun No. 8",
                rtRw = "RT 03 / RW 07",
                postalCode = "15412",
                houseOwnership = "Sewa/Kontrak",
                settledSince = "Juli 2024",
                familyMembersCount = 2,
                familyMembersNames = "1. Dimas Pratama (Kepala Keluarga)\n2. Annisa Larasati (Istri)",
                occupation = "Wiraswasta / Desainer Grafis",
                notes = "Kontrak 1 tahun per Juli 2024 di paviliun milik Bpk. H. Ahmad Fauzi.",
                feeStatus = "Lunas",
                lastBlockHash = ""
            )
            val block3 = BlockchainEngine.mineBlock(
                index = blockIndex++,
                previousHash = lastHash,
                actionType = "REGISTRASI_KK",
                citizenKk = citizen3.kkNumber,
                citizenName = citizen3.fullName,
                payloadSummary = "Pendaftaran KK Warga Ngontrak: ${citizen3.fullName} - Paviliun No. 8",
                fullDataPayload = "{\"kk\":\"${citizen3.kkNumber}\",\"nama\":\"${citizen3.fullName}\",\"kategori\":\"WARGA_NGONTRAK\",\"alamat\":\"${citizen3.addressStreet}\",\"jiwa\":2}",
                validatorNode = "Node-RT03-Bendahara"
            )
            blockDao.insertBlock(block3)
            lastHash = block3.hash
            citizenDao.insertCitizen(citizen3.copy(lastBlockHash = block3.hash))

            // 5. Initial Citizen 4: Warga Asli Sesepuh
            val citizen4 = Citizen(
                id = 0,
                kkNumber = "3201010101750007",
                nikHead = "3201011002700008",
                fullName = "Bapak H. Ahmad Fauzi",
                category = CitizenCategory.WARGA_ASLI,
                phone = "081122334455",
                email = "ahmad.fauzi@sukamaju.id",
                emergencyContactName = "Hj. Maryam (Istri)",
                emergencyContactPhone = "081199887766",
                addressStreet = "Jl. Flamboyan No. 1",
                rtRw = "RT 03 / RW 07",
                postalCode = "15412",
                houseOwnership = "Milik Sendiri",
                settledSince = "1998",
                familyMembersCount = 5,
                familyMembersNames = "1. H. Ahmad Fauzi (Kepala Keluarga)\n2. Hj. Maryam (Istri)\n3. Fikri Ahmad (Anak)\n4. Farhan Ahmad (Anak)\n5. Fatimah Azzahra (Anak)",
                occupation = "Pengusaha / Tokoh Agama",
                notes = "Ketua Dewan Kemakmuran Musholla Al-Ikhlas RT 03.",
                feeStatus = "Lunas",
                lastBlockHash = ""
            )
            val block4 = BlockchainEngine.mineBlock(
                index = blockIndex++,
                previousHash = lastHash,
                actionType = "REGISTRASI_KK",
                citizenKk = citizen4.kkNumber,
                citizenName = citizen4.fullName,
                payloadSummary = "Pendaftaran KK Warga Asli: ${citizen4.fullName} - Tokoh Masyarakat",
                fullDataPayload = "{\"kk\":\"${citizen4.kkNumber}\",\"nama\":\"${citizen4.fullName}\",\"kategori\":\"WARGA_ASLI\",\"alamat\":\"${citizen4.addressStreet}\",\"jiwa\":5}",
                validatorNode = "Node-RT03-Ketua"
            )
            blockDao.insertBlock(block4)
            citizenDao.insertCitizen(citizen4.copy(lastBlockHash = block4.hash))
        }
    }
}
