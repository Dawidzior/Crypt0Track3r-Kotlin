package dawidzior.crypt0track3r.dependencies

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dawidzior.crypt0track3r.CryptoModelRepository
import dawidzior.crypt0track3r.database.CryptoModelDatabase
import dawidzior.crypt0track3r.database.CryptosModelDao
import javax.inject.Singleton

@Module(includes = [RetrofitModule::class])
class AppModule constructor(private val context: Context) {

    @Provides
    @Singleton
    fun getDatabase(): CryptoModelDatabase = Room.databaseBuilder(context.applicationContext, CryptoModelDatabase::class.java, "CryptoDatabase").build()

    @Provides
    @Singleton
    fun getDao(database: CryptoModelDatabase): CryptosModelDao = database.cryptoModelDao()

    @Provides
    @Singleton
    fun getCryptoModelRepository(): CryptoModelRepository = CryptoModelRepository()

}