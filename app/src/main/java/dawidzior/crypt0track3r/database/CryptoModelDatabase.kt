package dawidzior.crypt0track3r.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dawidzior.crypt0track3r.model.CryptoModel

@Database(entities = [CryptoModel::class], version = 1, exportSchema = false)
abstract class CryptoModelDatabase : RoomDatabase() {
    abstract fun cryptoModelDao(): CryptosModelDao
}






