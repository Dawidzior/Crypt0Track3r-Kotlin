package dawidzior.crypt0track3r.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dawidzior.crypt0track3r.App
import dawidzior.crypt0track3r.CryptoModelRepository
import dawidzior.crypt0track3r.database.CryptoModelDatabase
import javax.inject.Inject

class CryptoListViewModel
constructor(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var cryptoModelRepository: CryptoModelRepository

    @Inject
    lateinit var appDatabase: CryptoModelDatabase

    private val cryptosModels: LiveData<List<CryptoModel>> by lazy { cryptoModelRepository.getCryptoModels() }

    init {
        App.appComponent.inject(this)
    }

    fun init() {
        //Force refresh from Internet.
        cryptoModelRepository.refreshCryptos()
    }

    fun getCryptos(): LiveData<List<CryptoModel>> {
        return cryptosModels
    }

}