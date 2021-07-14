package dawidzior.crypt0track3r.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import dawidzior.crypt0track3r.model.CryptoModel


@Dao
interface CryptosModelDao {
    @Insert(onConflict = REPLACE)
    fun save(cryptoModelList: List<CryptoModel>?)

    @Query("SELECT * FROM cryptoModel")
    fun load(): LiveData<List<CryptoModel>>
}