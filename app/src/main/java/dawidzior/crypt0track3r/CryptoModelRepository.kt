package dawidzior.crypt0track3r

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import dawidzior.crypt0track3r.database.CryptosModelDao
import dawidzior.crypt0track3r.details.isDeviceOnline
import dawidzior.crypt0track3r.model.CryptoModel
import dawidzior.crypt0track3r.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

class CryptoModelRepository {

    @Inject
    lateinit var retrofitService: RetrofitService

    @Inject
    lateinit var cryptosModelDao: CryptosModelDao

    init {
        App.appComponent.inject(this)
    }

    fun getCryptoModels(): LiveData<List<CryptoModel>> {
        return cryptosModelDao.load()
    }

    companion object {
        const val REQUESTED_COINS = 10
        private val executor: Executor = Executors.newSingleThreadExecutor()
    }

    fun refreshCryptos() {
        if (App.getContext().isDeviceOnline()) {
            executor.execute {
                val retrofitResultCallback: Call<List<CryptoModel>> = retrofitService.getCoins(REQUESTED_COINS)
                retrofitResultCallback.enqueue(object : Callback<List<CryptoModel>> {
                    override fun onResponse(call: Call<List<CryptoModel>>, response: Response<List<CryptoModel>>) {
                        executor.execute {
                            response.body()?.let {
                                cryptosModelDao.save(it)
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
                        Log.d(CryptoModelRepository::class.java.simpleName, "Failure response: " + t.message)
                    }
                })
            }
        } else Toast.makeText(App.getContext(), R.string.connection_error, Toast.LENGTH_LONG).show()
    }

}