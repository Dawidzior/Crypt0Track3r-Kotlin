package dawidzior.crypt0track3r.retrofit

import dawidzior.crypt0track3r.graph.CryptoDailyModel
import dawidzior.crypt0track3r.model.CryptoModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitService {

    //TODO wydzielić id nagłówka
    @Headers("X-CMC_PRO_API_KEY: d0cd281b-d7e1-4683-8658-a3e95c8180c0")
    @GET("ticker/")
    fun getCoins(@Query("limit") limit: Int): Call<List<CryptoModel>>

    @GET("histoday")
    fun getHistoryList(@Query("fsym") symbol: String?, @Query("tsym") currency: String?): Call<CryptoDailyModel>
}